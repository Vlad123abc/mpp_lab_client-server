package org.example.rpcProtocol;

import org.example.*;

import java.io.IOException;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;


public class ServiceProxy implements IService
{
    private String host;
    private int port;

    private IObserver client;

    private java.io.InputStream input;
    private java.io.OutputStream output;
    private java.io.BufferedReader reader;
    private java.io.PrintWriter writer;
    private Socket connection;

    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    public ServiceProxy(String host, int port)
    {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<Response>();
    }

    private void handleUpdate(Response response)
    {
        if (response.getType() == ResponseType.REZERVARE)
        {
            Rezervare rezervare = (Rezervare) response.getData();
            try
            {
                client.rezervare(rezervare); //client is null
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void sendRequest(Request request) throws Exception
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            String request_string = mapper.writeValueAsString(request);
            request_string = request_string + "\n"; // is this even needed?
            System.out.println("Sending line:" + request_string);
            writer.println(request_string);            
            System.out.println("Sent line:" + request_string);            
        } catch (IOException e)
        {
            throw new Exception("Error sending object " + e);
        }

    }

    private Response readResponse() throws Exception
    {
        Response response = null;
        try
        {
            response = qresponses.take();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return response;
    }

    private void initializeConnection() throws Exception
    {
        try
        {
            connection = new Socket(host, port);
            output = connection.getOutputStream();
            output.flush();
            writer = new PrintWriter(output, true);            
            input = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));            
            
            finished = false;
            startReader();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void closeConnection()
    {
        finished = true;
        try
        {
            input.close();
            output.close();
            connection.close();
            client = null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private void startReader()
    {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    @Override
    public boolean login(String username, String password, IObserver client) throws Exception
    {
        initializeConnection();

        User user = new User(username, password);
        user.setId(1L);
        Request request = new Request.Builder().type(RequestType.LOGIN).data(user).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.getType() == ResponseType.OK)
        {
            this.client = client;
            return true;
        }
        if (response.getType() == ResponseType.ERROR)
        {
            String err = response.getData().toString();
            closeConnection();
            throw new Exception(err);
        }
        return false;
    }

    @Override
    public org.example.User getUserByUsername(String username) throws Exception
    {
        User user = new User(username, "nopass");
        return user;
    }

    @Override
    public List<org.example.Cursa> getAllCurse() throws Exception
    {
        Request request = new Request.Builder().type(RequestType.GET_ALL_CURSE).build();
        sendRequest(request);
        Response response = readResponse();

        if (response.getType() == ResponseType.ERROR)
        {
            String err = response.getData().toString();
            closeConnection();
            throw new Exception(err);
        }
        return (List<Cursa>) response.getData();
    }

    @Override
    public List<org.example.LocCursa> genereaza_lista_locuri(Long id_cursa) throws Exception
    {
        Request request = new Request.Builder().type(RequestType.GENEREAZA_LISTA_LOCURI).data(id_cursa).build();
        sendRequest(request);
        Response response = readResponse();

        if (response.getType() == ResponseType.ERROR)
        {
            String err = response.getData().toString();
            closeConnection();
            throw new Exception(err);
        }

        return (List<LocCursa>) response.getData();
    }

    @Override
    public org.example.Cursa cauta_cursa(String destinatie, Timestamp data) throws Exception
    {
        Cursa cursa = new Cursa(destinatie, data, 0);
        Request request = new Request.Builder().type(RequestType.CAUTA_CURSA).data(cursa).build();
        sendRequest(request);
        Response response = readResponse();

        if (response.getType() == ResponseType.ERROR)
        {
            String err = response.getData().toString();
            closeConnection();
            throw new Exception(err);
        }

        return (Cursa) response.getData();
    }

    @Override
    public void rezerva(String nume, Integer nr, Long id_cursa) throws Exception
    {
        Rezervare rezervare = new Rezervare(nume, nr, id_cursa);
        Request request = new Request.Builder().type(RequestType.REZERVARE).data(rezervare).build();
        sendRequest(request);
        Response response = readResponse();

        if (response.getType() == ResponseType.ERROR)
        {
            String err = response.getData().toString();
            closeConnection();
            throw new Exception(err);
        }
    }

    @Override
    public Integer getNrLocuriLibereCursa(org.example.Cursa cursa) throws Exception
    {
        Request request = new Request.Builder().type(RequestType.GET_NR_LOCURI_LIBERE_CURSA).data(cursa).build();
        sendRequest(request);
        Response response = readResponse();

        if (response.getType() == ResponseType.ERROR)
        {
            String err = response.getData().toString();
            closeConnection();
            throw new Exception(err);
        }

        return (Integer) response.getData();
    }

    private boolean isUpdate(Response response)
    {
        return response.getType() == ResponseType.REZERVARE;
    }
    private class ReaderThread implements Runnable
    {
        public void run()
        {
            while (!finished)
            {
                try
                {
                    System.out.println("Reading response ");
                    String response_in = reader.readLine();
                    System.out.println(String.format("Got response string:[%s]",response_in));
                    String json_in = response_in.replace("\uFEFF", "");
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(json_in);
                    String response_type = root.get("type").asText();

                    Response response = mapper.readValue(json_in, Response.class);
                    System.out.println(String.format("Response type is:[%s]",response_type));
                    
                    if (response_type.equals("GET_ALL_CURSE")) {
                        Cursa[] curse = mapper.treeToValue(root.get("data"), Cursa[].class);
                        response.setData(Arrays.asList(curse));
                    }
                    else if(response_type.equals("GET_ALL_CURSE")) {
                    }
                    
                    System.out.println("response received " + response);
                    if (isUpdate((Response) response))
                    {
                        handleUpdate((Response) response);
                    }
                    else
                    {
                        try
                        {
                            qresponses.put((Response) response);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e)
                {
                    System.out.println("Reading error " + e.toString());
                }
            }
        }
    }
}
