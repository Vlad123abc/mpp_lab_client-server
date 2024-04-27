package org.example;

import javafx.application.Application;
import org.example.gui.LoginController;
import org.example.gui.UserController;
import org.example.rpcProtocol.ServiceProxy;

import java.io.IOException;
import java.util.Properties;
import java.util.List;

import javax.management.RuntimeErrorException;

class TestObserver implements IObserver {

    @Override
    public void rezervare(Rezervare rezervare) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'rezervare'");
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
