package org.example.rpcProtocol;

import org.example.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientWorker implements Runnable, IObserver
{
    private IService server;
    private Socket connection;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    public ClientWorker(IService server, Socket connection)
    {
        this.server = server;
        this.connection = connection;

        try
        {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
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
                Object request = input.readObject();
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
            System.out.println("SendRezervareRequest ...");
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
            System.out.println("SendGetUserByUsernameRequest ...");
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
            System.out.println("SendGetAllCurseRequest ...");
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
            System.out.println("SendGenereazaListaLocuriRequest ...");
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
            System.out.println("SendCautaCursaRequest ...");
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
            System.out.println("SendGetNrLocuriLibereCursaRequest ...");
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
        System.out.println("sending response " + response);
        synchronized (output)
        {
            output.writeObject(response);
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
