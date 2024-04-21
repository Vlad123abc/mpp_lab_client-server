package org.example.rpcProtocol;

import org.example.*;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

import java.net.Socket;
import java.util.List;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientWorker implements Runnable, IObserver
{
    private IService server;
    private Socket connection;

    private java.io.InputStream input;
    private java.io.OutputStream output;
    private java.io.BufferedReader reader;
    private java.io.BufferedWriter writer;
    
    private volatile boolean connected;
    private User user;

    public ClientWorker(IService server, Socket connection)
    {
        this.server = server;
        this.connection = connection;

        try
        {
            output = connection.getOutputStream();
            output.flush();
            writer = new BufferedWriter(new OutputStreamWriter(output));            
            input = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));            
            connected = true;
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        while (this.connected)
        {
            try
            {
                String request_in = reader.readLine();
                ObjectMapper mapper = new ObjectMapper();
                Request request = mapper.readValue(request_in, Request.class);
                
                Response response = handleRequest((Request) request);
                if (response != null)
                {
                    sendResponse(response);
                }
            } catch (IOException | ClassNotFoundException e)
            {
                e.printStackTrace();
            }
            try
            {
                Thread.sleep(1000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e)
        {
            System.out.println("Error " + e);
        }
    }

    private static Response okResponse = new Response.Builder().type(ResponseType.OK).build();
    private Response handleRequest(Request request)
    {
        Response response = null;
        if (request.getType() == RequestType.LOGIN)
        {
            System.out.println("Login request ..." + request.getType());
            User user = (User) request.getData();
            this.user = user;
            try
            {
                Boolean ok = server.login(user.getUsername(), user.getPassword(), this);
                if (ok == true)
                    return okResponse;
                return new Response.Builder().type(ResponseType.LOGIN).data(ok).build();
            } catch (Exception e)
            {
                connected = false;
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.getType() == RequestType.REZERVARE)
        {
            System.out.println(user.getUsername() + ": SendRezervareRequest ...");
            Rezervare rezervare = (Rezervare) request.getData();
            try
            {
                server.rezerva(rezervare.getNume_client(), rezervare.getNr_locuri(), rezervare.getId_cursa());
                return okResponse;
            } catch (Exception e)
            {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.getType() == RequestType.GET_USER_BY_USERNAME)
        {
            System.out.println(user.getUsername() + ": SendGetUserByUsernameRequest ...");
            String username = (String) request.getData();
            try
            {
                User user = server.getUserByUsername(username);
                return new Response.Builder().type(ResponseType.GET_USER_BY_USERNAME).data(user).build();
            } catch (Exception e)
            {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.getType() == RequestType.GET_ALL_CURSE)
        {
            System.out.println(user.getUsername() + ": SendGetAllCurseRequest ...");
            try
            {
                List<Cursa> curse = server.getAllCurse();
                return new Response.Builder().type(ResponseType.GET_ALL_CURSE).data(curse).build();
            } catch (Exception e)
            {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.getType() == RequestType.GENEREAZA_LISTA_LOCURI)
        {
            System.out.println(user.getUsername() + ": SendGenereazaListaLocuriRequest ...");
            Long id_cursa = (Long) request.getData();
            try
            {
                List<LocCursa> locuri = server.genereaza_lista_locuri(id_cursa);
                return new Response.Builder().type(ResponseType.GENEREAZA_LISTA_LOCURI).data(locuri).build();
            } catch (Exception e)
            {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.getType() == RequestType.CAUTA_CURSA)
        {
            System.out.println(user.getUsername() + ": SendCautaCursaRequest ...");
            Cursa cursa = (Cursa) request.getData();
            try
            {
                Cursa cursa1 = server.cauta_cursa(cursa.getDestinatie(), cursa.getPlecare());
                return new Response.Builder().type(ResponseType.CAUTA_CURSA).data(cursa1).build();
            } catch (Exception e)
            {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.getType() == RequestType.GET_NR_LOCURI_LIBERE_CURSA)
        {
            System.out.println(user.getUsername() + ": SendGetNrLocuriLibereCursaRequest ...");
            Cursa cursa = (Cursa) request.getData();
            try
            {
                Integer nr = server.getNrLocuriLibereCursa(cursa);
                return new Response.Builder().type(ResponseType.GET_NR_LOCURI_LIBERE_CURSA).data(nr).build();
            } catch (Exception e)
            {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        return response;
    }

    private void sendResponse(Response response) throws IOException
    {
        System.out.println(user.getUsername() + ": sending response " + response);
        synchronized (output)
        {
            ObjectMapper mapper = new ObjectMapper();
            String response_string = mapper.writeValueAsString(response);
            response_string = response_string + "\n"; // is this even needed?
            writer.write(response_string);
            output.flush();
        }
    }

    @Override
    public void rezervare(Rezervare rezervare) throws Exception
    {
        Response resp = new Response.Builder().type(ResponseType.REZERVARE).data(rezervare).build();
        try
        {
            sendResponse(resp);
        }
        catch (IOException e)
        {
            throw new Exception("Sending error: " + e);
        }
    }
}
