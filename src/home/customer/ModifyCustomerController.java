package home.customer;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Class that manages the modify-customer view's controls and events
 */
public class ModifyCustomerController
{
    @FXML
    private GridPane modifyCustomerPane;
    @FXML
    private TextField idTextField;
    @FXML
    private TextField name;
    @FXML
    private TextField address;
    @FXML
    private TextField postalCode;
    @FXML
    private TextField phoneNumber;
    @FXML
    private Button confirmButton;
    @FXML
    private Text headerText;
    @FXML
    private ComboBox<String> divisionList;
    @FXML
    private ComboBox<String> countryList;

    private static boolean isNew;

    private static Customer customer;

    /**
     * logic that will execute when a form is shown to add a user
     * @return the created user
     * @throws IOException
     */
    public Customer showAddCustomer() throws IOException {
        isNew = true;
        customer = null;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/resources/views/modify-customer.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 500);
        Stage stage = new Stage();
        stage.setTitle("Add Customer");
        stage.setScene(scene);
        stage.showAndWait();
        return customer;
    }

    /**
     * logic that will execute when a form is shown to edit a user
     * @param customer
     * @return the edited user
     * @throws IOException
     */
    public Customer showEditCustomer(Customer customer) throws IOException {
        isNew = false;
        this.customer = customer;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/resources/views/modify-customer.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 500);
        Stage stage = new Stage();
        stage.setTitle("Edit Customer");
        stage.setScene(scene);
        stage.showAndWait();
        return this.customer;
    }

    /**
     * method that will be called when the window is initialized by javafx framework
     * Lambda expression is used here to retrieve all countries by their names as a list
     * Lambda expression is used here to retrieve all divisions by their names as a list
     */
    @FXML
    public void initialize() {
        List<String> countries = DataManager.getInstance().countries.stream().map(c -> c.getCountryName()).collect(
            Collectors.toList());
        countryList.setItems(FXCollections.observableList(countries));
        countryList.getSelectionModel().selectFirst();

        List<String> divisionsByCountry = DataManager.getInstance().getDivisionsByCountryName(countryList.getValue()).stream()
            .map(d -> d.getDivisionName()).collect(Collectors.toList());
        divisionList.setItems(FXCollections.observableList(divisionsByCountry));
        divisionList.getSelectionModel().selectFirst();

        if (isNew) {
            headerText.setText("Add New Customer");
            idTextField.setText("Auto Generated");
            idTextField.setEditable(false);
            idTextField.setMouseTransparent(true);
            idTextField.setFocusTraversable(false);
        } else {
            headerText.setText("Edit Existing Customer");
            idTextField.setText(Integer.toString(customer.getId()));
            idTextField.setEditable(false);
            idTextField.setMouseTransparent(true);
            idTextField.setFocusTraversable(false);
            name.setText(customer.getName());
            address.setText(customer.getAddress());
            postalCode.setText(customer.getPostalCode());
            phoneNumber.setText(customer.getPhoneNumber());
            countryList.getSelectionModel().select(customer.getCountryName());
            divisionList.getSelectionModel().select(customer.getDivisionName());
        }

        countryList.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                String countryName = countryList.getValue().toString();
                List<String> divisionsByCountry = DataManager.getInstance().getDivisionsByCountryName(countryName).stream()
                    .map(d -> d.getDivisionName()).collect(Collectors.toList());
                divisionList.setItems(FXCollections.observableList(divisionsByCountry));
                divisionList.getSelectionModel().selectFirst();
            }
        });
    }

    /**
     * logic that will be executed when the user confirms to add or edit a customer
     */
    @FXML
    public void onConfirm() {
        String customerName = name.getText().trim();
        String customerAddress = address.getText().trim();
        String customerPostalCode = postalCode.getText().trim();
        String customerPhoneNumber = phoneNumber.getText().trim();
        String divisionName = divisionList.getSelectionModel().getSelectedItem();
        String countryName = countryList.getSelectionModel().getSelectedItem();
        Division division = DataManager.getInstance().getDivisionByNameAndCountryName(divisionName, countryName);

        if (customerName.isEmpty() || customerAddress.isEmpty() ||
            customerPostalCode.isEmpty() || customerPhoneNumber.isEmpty() ) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Missing Fields");
            errorAlert.setContentText("Please provide value for all fields");
            errorAlert.showAndWait();
        } else {
            if (isNew) {
                customer = new Customer(0,customerName, customerAddress, customerPostalCode, customerPhoneNumber, new ArrayList<>(), division);
                DataManager.getInstance().addCustomer(customer);
            } else {
                int id = Integer.parseInt(idTextField.getText());
                Customer existingCustomer = DataManager.getInstance().getCustomerById(id);
                customer = new Customer(id,customerName, customerAddress, customerPostalCode, customerPhoneNumber, new ArrayList<>(), division);
                customer.setAppointments(existingCustomer.getAppointments());
                customer.setCreateDate(existingCustomer.getCreateDate());
                customer.setCreatedBy(existingCustomer.getCreatedBy());
                customer.setLastUpdate(existingCustomer.getLastUpdate());
                customer.setLastUpdatedBy(existingCustomer.getLastUpdatedBy());
                DataManager.getInstance().editCustomer(customer);
            }
            Stage stage = (Stage) confirmButton.getScene().getWindow();
            stage.close();
        }
    }
}
