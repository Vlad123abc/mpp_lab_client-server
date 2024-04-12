package org.example.rpcProtocol;

import org.example.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServiceProxy implements IService
{
    private String host;
    private int port;

    private IObserver client;

    private ObjectInputStream input;
    private ObjectOutputStream output;
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
        if (response.getType() == ResponseType.NEW_REZERVARE)
        {
            Rezervare rezervare = (Rezervare) response.getData();
            try
            {
                client.rezervare(rezervare);
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
            output.writeObject(request);
            output.flush();
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
        } catch (InterruptedException e)
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
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (IOException e)
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
        } catch (IOException e)
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
        Request request = new Request.Builder().type(RequestType.LOGIN).data(user).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.getType() == ResponseType.OK) //aci
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
        Request request = new Request.Builder().type(RequestType.GET_USER_BY_USERNAME).data(username).build();
        sendRequest(request);
        Response response = readResponse();

        if (response.getType() == ResponseType.ERROR)
        {
            String err = response.getData().toString();
            closeConnection();
            throw new Exception(err);
        }

        return (User) response.getData();
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
        return response.getType() == ResponseType.NEW_REZERVARE;
    }

    private class ReaderThread implements Runnable
    {
        public void run()
        {
            while (!finished)
            {
                try
                {
                    Object response = input.readObject();
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
                } catch (IOException | ClassNotFoundException e)
                {
                    System.out.println("Reading error " + e);
                }
            }
        }
    }
}
