package org.example.gui;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MessageWindow
{
    static void showMessage(Stage owner, Alert.AlertType type, String header, String text)
    {
        Alert message = new Alert(type);
        message.initOwner(owner);
        message.setHeaderText(header);
        message.setContentText(text);
        message.showAndWait();
    }
}
