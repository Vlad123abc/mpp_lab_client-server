package org.example.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.*;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.ResourceBundle;

public class UserController implements Initializable, IObserver
{
    private IService service;
    private User user;

    @FXML
    private TableView<Cursa> cursaTableView;
    private ObservableList<Cursa> modelCurse = FXCollections.observableArrayList();
    @FXML
    private TableColumn<Cursa, String> destinatieTableColumn;
    @FXML
    private TableColumn<Cursa, Timestamp> plecareTableColumn;
    @FXML
    private TableColumn<Cursa, Integer> locuriTableColumn;

    @FXML
    private TextField destinatie;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField ora;

    @FXML
    private TableView<LocCursa> locCursaTableView;
    private ObservableList<LocCursa> modelLocCurse = FXCollections.observableArrayList();
    @FXML
    private TableColumn<LocCursa, Integer> locTableColumn;
    @FXML
    private TableColumn<LocCursa, String> locClientTableColumn;

    @FXML
    private TextField nume_rezervare;
    @FXML
    private Spinner<Integer> nr_locuri_rezervare;

    @FXML
    private Button logoutButton;

    public void init_controller(IService service, User user) throws Exception
    {
        this.service = service;
        this.user = user;
        initModel();

        // Spinner
        this.nr_locuri_rezervare.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 18));
    }

    @FXML
    public void initialize()
    {
        destinatieTableColumn.setCellValueFactory(new PropertyValueFactory<>("destinatie"));
        plecareTableColumn.setCellValueFactory(new PropertyValueFactory<>("plecare"));
        locuriTableColumn.setCellValueFactory(new PropertyValueFactory<>("nr_locuri"));
        cursaTableView.setItems(modelCurse);

        locTableColumn.setCellValueFactory(new PropertyValueFactory<>("nr"));
        locClientTableColumn.setCellValueFactory(new PropertyValueFactory<>("client"));
        locCursaTableView.setItems(modelLocCurse);
    }

    private void initModel() throws Exception
    {
        modelCurse.setAll(this.service.getAllCurse());
    }

    public void onCauta(ActionEvent actionEvent) throws Exception
    {
        String destinatie = this.destinatie.getText();

        if (Objects.equals(destinatie, ""))
            MessageWindow.showMessage(null, Alert.AlertType.ERROR, "Error", "Introduceti o destinatie!");
        else
        {
            LocalDate selectedDate = datePicker.getValue();

            if (selectedDate != null)
            {
                String ora = this.ora.getText();

                if (Objects.equals(ora, ""))
                    ora = "00:00:00";

                LocalTime defaultTime = LocalTime.parse(ora);
                LocalDateTime localDateTime = selectedDate.atTime(defaultTime); // Combining date and time
                Timestamp data = Timestamp.valueOf(localDateTime);

                Cursa cursa = this.service.cauta_cursa(destinatie, data);

                if (cursa == null)
                    MessageWindow.showMessage(null, Alert.AlertType.INFORMATION, "Information", "Nu exista cursa");
                else
                    modelLocCurse.setAll(this.service.genereaza_lista_locuri(cursa.getId())); // completare tabel locuri
            }
            else
                MessageWindow.showMessage(null, Alert.AlertType.ERROR, "Error", "Introduceti o data!");
        }
    }

    public void onRezerva(ActionEvent actionEvent) throws Exception
    {
        Cursa cursa = this.cursaTableView.getSelectionModel().getSelectedItem();

        if (cursa == null)
            MessageWindow.showMessage(null, Alert.AlertType.ERROR, "Error", "Alegeti o cursa din tabel!");
        else
        {
            String client = this.nume_rezervare.getText();
            if (Objects.equals(client, ""))
                MessageWindow.showMessage(null, Alert.AlertType.ERROR, "Error", "Introduceti numele clientului!");
            else
            {
                Integer nr_locuri = this.nr_locuri_rezervare.getValue();
                if (nr_locuri > this.service.getNrLocuriLibereCursa(cursa))
                    MessageWindow.showMessage(null, Alert.AlertType.ERROR, "Error", "Nu sunt destule locuri libere!");
                else
                {
                    this.service.rezerva(client, nr_locuri, cursa.getId());
                    MessageWindow.showMessage(null, Alert.AlertType.CONFIRMATION, "Confirmation", "Rezervarea a fost facuta cu succes!");
                }
            }
        }
    }

    public void onLogout(ActionEvent actionEvent)
    {
        Stage stage = (Stage) this.logoutButton.getScene().getWindow();
        stage.close(); // Close the stage
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        destinatieTableColumn.setCellValueFactory(new PropertyValueFactory<>("destinatie"));
        plecareTableColumn.setCellValueFactory(new PropertyValueFactory<>("plecare"));
        locuriTableColumn.setCellValueFactory(new PropertyValueFactory<>("nr_locuri"));
        cursaTableView.setItems(modelCurse);

        locTableColumn.setCellValueFactory(new PropertyValueFactory<>("nr"));
        locClientTableColumn.setCellValueFactory(new PropertyValueFactory<>("client"));
        locCursaTableView.setItems(modelLocCurse);
    }

    @Override
    public void rezervare(Rezervare rezervare) throws Exception
    {

    }
}
