package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.gui.LoginController;
import org.example.gui.UserController;
import org.example.rpcProtocol.ServiceProxy;

import java.io.IOException;
import java.util.Properties;

public class StartRpcClientFX extends Application
{
    private static int defaultChatPort = 55555;
    private static String defaultServer = "localhost";

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        System.out.println("In start");

        Properties clientProps = new Properties();
        try
        {
            clientProps.load(StartRpcClientFX.class.getResourceAsStream("/transportclient.properties"));
            System.out.println("Client properties set. ");
            clientProps.list(System.out);
        }
        catch (IOException e)
        {
            System.err.println("Cannot find transportclient.properties " + e);
            return;
        }

        String serverIP = clientProps.getProperty("transport.server.host", defaultServer);
        int serverPort = defaultChatPort;
        try
        {
            serverPort = Integer.parseInt(clientProps.getProperty("transport.server.port"));
        }
        catch (NumberFormatException ex)
        {
            System.err.println("Wrong port number " + ex.getMessage());
            System.out.println("Using default port: " + defaultChatPort);
        }
        System.out.println("Using server IP " + serverIP);
        System.out.println("Using server port " + serverPort);

        IService server = new ServiceProxy(serverIP, serverPort);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);

        LoginController loginController = fxmlLoader.getController();
        loginController.initController(server);

        FXMLLoader fxmlLoaderUser = new FXMLLoader(getClass().getClassLoader().getResource("user-view.fxml"));
        UserController userController = fxmlLoaderUser.getController();
        loginController.setUserController(userController);

        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
