package org.example;

import javafx.application.Application;
import org.example.gui.LoginController;
import org.example.gui.UserController;
import org.example.rpcProtocol.ServiceProxy;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Properties;
import java.util.List;

import javax.management.RuntimeErrorException;

class TestObserver implements IObserver {

    @Override
    public void rezervare(Rezervare rezervare) throws Exception {
        System.out.println(String.format("aranjez la rezervare bine %s", rezervare.toString()));
    }
}

public class StartClientConsole {
    private static int defaultChatPort = 55556;
    private static String defaultServer = "localhost";

    public void start() throws Exception {
        System.out.println("In start");

        String serverIP = defaultServer;
        int serverPort = defaultChatPort;
        System.out.println("Using server IP " + serverIP);
        System.out.println("Using server port " + serverPort);

        IService server = new ServiceProxy(serverIP, serverPort);
        TestObserver observer = new TestObserver();
        var result = server.login("vlad", "parola", observer);
        System.out.println(String.format("login result is: [%b]", result));
        if (!result) {
            throw new RuntimeException("bad login. bye");
        }
        var user = server.getUserByUsername("vlad");
        if (user == null) {
            throw new RuntimeException("bad user. bye");            
        }
        List<Cursa> curse = server.getAllCurse();
        if (curse == null) {
            throw new RuntimeException("bad all curse. bye");            
        }
        System.out.println(String.format("ConsoleClient Recieved lista curse: [%s], type: [%s], element type:[%s]", curse.toString(), curse.getClass().getName(),curse.toArray()[0].getClass().getName()));


        var lista_locuri = server.genereaza_lista_locuri(curse.get(0));
        if (lista_locuri == null) {
            throw new RuntimeException("bad lista locuri. bye");            
        }
        System.out.println(String.format("ConsoleClient Recieved lista locuri: [%s], type: [%s], element type:[%s]", lista_locuri.toString(), lista_locuri.getClass().getName(),lista_locuri.toArray()[0].getClass().getName())); 

        Instant instant = Instant.parse( "2020-01-27T00:00:00+02:00" );

        var cursa_gasita = server.cauta_cursa("cluj", new Timestamp(instant.getEpochSecond()* 1000));
        if (cursa_gasita == null) {
            throw new RuntimeException("bad cursa_gasita. bye");            
        }
        System.out.println(String.format("ConsoleClient Recieved cursa_gasita: [%s], type: [%s], element type:[%s]", cursa_gasita.toString(), cursa_gasita.getClass().getName(),cursa_gasita.getClass().getName())); 

        server.rezerva("vlad", 1,1L);
        System.out.println(String.format("Am rezervat in PLM")); 
        
    }

    public static void main(String [] args) {
        StartClientConsole app = new StartClientConsole();
        try {
            app.start();
        }
        catch (Exception e) {
            System.out.println("error hahaha" + e.toString());
        }
    }
}
