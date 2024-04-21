package org.example;

import org.example.jdbc.CursaDBRepository;
import org.example.jdbc.RezervareDBRepository;
import org.example.jdbc.UtilizatorDBRepository;
import org.example.repository.CursaRepository;
import org.example.repository.RezervareRepository;
import org.example.repository.UtilizatorRepository;
import org.example.utils.AbstractServer;
import org.example.utils.RpcConcurrentServer;

import java.io.IOException;
import java.rmi.ServerException;
import java.util.Properties;

public class StartRpcServer
{
    private static int defaultPort = 55555;

    public static void main(String[] args)
    {
        Properties serverProps = new Properties();
        try
        {
            serverProps.load(StartRpcServer.class.getResourceAsStream("/chatserver.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        }
        catch (IOException e)
        {
            System.err.println("Cannot find chatserver.properties "+e);
            return;
        }

        UtilizatorRepository utilizatorRepository = new UtilizatorDBRepository(serverProps);
        CursaRepository cursaRepository = new CursaDBRepository(serverProps);
        RezervareRepository rezervareRepository = new RezervareDBRepository(serverProps);

        IService service = new Service(utilizatorRepository, cursaRepository, rezervareRepository);

        int chatServerPort = defaultPort;
        try
        {
            chatServerPort = Integer.parseInt(serverProps.getProperty("transport.server.port"));
        }
        catch (NumberFormatException nef)
        {
            System.err.println("Wrong  Port Number"+nef.getMessage());
            System.err.println("Using default port "+defaultPort);
        }
        System.out.println("Starting server on port: "+chatServerPort);

        AbstractServer server = new RpcConcurrentServer(chatServerPort, service);
        try
        {
            server.start();
        }
        catch (Exception e)
        {
            System.err.println("Error starting the server" + e.getMessage());
        }
        finally
        {
            try
            {
                server.stop();
            }
            catch(Exception e)
            {
                System.err.println("Error stopping server "+e.getMessage());
            }
        }
    }
}

// dracie
