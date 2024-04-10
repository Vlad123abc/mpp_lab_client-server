package org.example.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.IService;
import org.example.StartRpcClientFX;
import org.example.User;


public class LoginController
{
    private IService service;

    private UserController userController;

    @FXML
    private TextField textField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private Label incorrectLabel;

    public void initController(IService service)
    {
        this.service = service;
        incorrectLabel.setVisible(false);
    }

    public void setUserController(UserController uc)
    {
        this.userController = uc;
    }

    public void onLogin(ActionEvent actionEvent) throws Exception
    {
        String username = this.textField.getText();
        String password = this.passwordField.getText();

        if (username.isEmpty())
            MessageWindow.showMessage(null, Alert.AlertType.ERROR, "Error", "Completati Username!");
        if (password.isEmpty())
            MessageWindow.showMessage(null, Alert.AlertType.ERROR, "Error", "Completati Password!");

        if (this.service.login(username, password, this.userController))
        {
            this.incorrectLabel.setVisible(false);

            this.textField.clear();
            this.passwordField.clear();

            User user = this.service.getUserByUsername(username);

            // open user page
            FXMLLoader fxmlLoader = new FXMLLoader(StartRpcClientFX.class.getResource("user-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 800);

            UserController userController = fxmlLoader.getController();
            userController.init_controller(this.service, user);

            Stage stage = new Stage();
            stage.setTitle(username);
            stage.setScene(scene);
            stage.show();
        } else
        {
            this.incorrectLabel.setVisible(true);
            this.passwordField.clear();
        }
    }
}
