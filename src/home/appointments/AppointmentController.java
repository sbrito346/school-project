package home.appointments;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import home.customer.Customer;
import home.customer.DataManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Class that manages the appointment view
 * Lambda expression is used in a static member of this class to retrieve all contacts by their name as a list.
 */
public class AppointmentController
{
    @FXML
    private TableView<Appointment> appointmentTable;
    @FXML
    private Text headerText;
    @FXML
    private TextField idTextField;
    @FXML
    private TextField title;
    @FXML
    private TextField description;
    @FXML
    private TextField locationAppointment;
    @FXML
    private ComboBox<String> contactList;
    @FXML
    private TextField type;
    @FXML
    private TextField startTime;
    @FXML
    private TextField endTime;
    @FXML
    private TextField customerId;
    @FXML
    private TextField userId;
    @FXML
    private Button confirmButton;
    @FXML
    private ToggleGroup filterToggleGroup;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static List<Appointment> appointments = new ArrayList<>();
    public static Stage stage = new Stage();
    private static Appointment appointment = null;
    private static boolean isShowingAppointments = false;
    private static boolean isAddingAppointment = false;
    private static List<String> contacts = DataManager.getInstance().contacts.stream().map(c -> c.getName()).collect(Collectors.toList());

    /**
     * Displays on the stage the appointments
     * @param appointments list of appointments to show
     * @throws IOException
     */
    public void showAppointments(List<Appointment> appointments) throws IOException
    {
        isShowingAppointments = true;
        this.appointments = appointments;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/resources/views/appointments-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 400);
        this.stage.setTitle("Appointments");
        this.stage.setScene(scene);

        if (!this.stage.isShowing()) {
            this.stage.showAndWait();
        }
    }

    /**
     * Initializes the stage based on different scenarios
     */
    @FXML
    public void initialize() {
        if (isShowingAppointments) {
            List<Appointment> appointmentsToShow = cloneAppointmentsList(appointments);
            appointmentTable.setItems(FXCollections.observableList(appointmentsToShow));
            // add a change listener

            filterToggleGroup.selectedToggleProperty().addListener((ob, o, n) -> {
                RadioButton rb = (RadioButton)filterToggleGroup.getSelectedToggle();
                if (rb.getId().equals("filterWeek")) {
                    List<Appointment> appointmentsNoFilter = cloneAppointmentsList(appointments);
                    List<Appointment> appointmentsByCurrentWeek = filterByCurrentWeek(appointmentsNoFilter);
                    appointmentTable.setItems(FXCollections.observableList(appointmentsByCurrentWeek));
                } else if (rb.getId().equals("filterMonth")) {
                    List<Appointment> appointmentsNoFilter = cloneAppointmentsList(appointments);
                    List<Appointment> appointmentsByCurrentMonth = filterByCurrentMonth(appointmentsNoFilter);
                    appointmentTable.setItems(FXCollections.observableList(appointmentsByCurrentMonth));
                } else {
                    List<Appointment> appointmentsNoFilter = cloneAppointmentsList(appointments);
                    appointmentTable.setItems(FXCollections.observableList(appointmentsNoFilter));
                }
            });

        } else if (isAddingAppointment) {
            contactList.setItems(FXCollections.observableList(contacts));
            contactList.getSelectionModel().selectFirst();

            idTextField.setText("Auto Generated");
            idTextField.setEditable(false);
            idTextField.setMouseTransparent(true);
            idTextField.setFocusTraversable(false);
            headerText.setText("Add New Appointment");
        } else {

            contactList.setItems(FXCollections.observableList(contacts));
            headerText.setText("Edit Existing Appointment");
            idTextField.setText(Integer.toString(appointment.getId()));
            idTextField.setEditable(false);
            idTextField.setMouseTransparent(true);
            idTextField.setFocusTraversable(false);
            title.setText(appointment.getTitle());
            description.setText(appointment.getDescription());
            locationAppointment.setText(appointment.getLocationAppointment());
            contactList.getSelectionModel().select(appointment.getContactName());
            type.setText(appointment.getType());
            startTime.setText(appointment.getStartDateString());
            endTime.setText(appointment.getEndDateString());
            customerId.setText(Integer.toString(appointment.getCustomerId()));
            userId.setText(Integer.toString(appointment.getUserId()));
        }
    }

    /**
     * Method that handles the logic to add an appointment
     * @throws IOException
     */
    @FXML
    public void onAdd() throws IOException {
        isShowingAppointments = false;
        isAddingAppointment = true;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/resources/views/modify-appointment.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 600);
        Stage stage = new Stage();
        stage.setTitle("Add Appointment");
        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * Method that handles the logic to edit an appointment
     * @throws IOException
     */
    @FXML
    public void onEdit() throws IOException {
        isShowingAppointments = false;
        isAddingAppointment = false;
        appointment = appointmentTable.getSelectionModel().getSelectedItem();
        if (appointment == null) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Select Appointment");
            errorAlert.setContentText("Please select an appointment to edit");
            errorAlert.showAndWait();
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/resources/views/modify-appointment.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 600);
            Stage stage = new Stage();
            stage.setTitle("Edit Appointment");
            stage.setScene(scene);
            stage.showAndWait();
        }
    }

    /**
     * Method that handles the removal of an appointment
     */
    @FXML
    public void onDelete() {
        appointment = appointmentTable.getSelectionModel().getSelectedItem();
        if (appointment == null) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Select Appointment");
            errorAlert.setContentText("Please select an appointment to delete");
            errorAlert.showAndWait();
        } else {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation");
            confirmationAlert.setHeaderText("Cancel Appointment");
            confirmationAlert.setContentText("Appointment Type :" + appointment.getType() + " with ID:" + appointment.getId() + " will be canceled");
            confirmationAlert.showAndWait();
            if (confirmationAlert.getResult() == ButtonType.OK) {
                Customer customer = DataManager.getInstance().getCustomerById(appointment.getCustomerId());
                customer.removeAppointment(appointment);
                appointmentTable.getItems().remove(appointment);
            }
        }
    }

    /**
     * Method that handles the logic to execute when the button to confirm action is pressed
     * Lambda expression is used in this method to find the contact specified on UI form from the list of contacts based on name
     * Lambda expression is used in this method to find the user specified on UI form from the list of users based on id
     * @throws IOException
     */
    @FXML
    public void onConfirm() throws IOException {
        if (isAddingAppointment) {
            Optional<Contact> contact = DataManager.getInstance().contacts.stream().filter(c -> c.getName().equals(contactList.getSelectionModel().getSelectedItem())).findFirst();
            Optional<User> user = DataManager.getInstance().users.stream().filter(u -> u.getId() == Integer.parseInt(this.userId.getText().trim())).findFirst();

            if (!user.isPresent()) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Non Existing User");
                errorAlert.setContentText("Please provide a valid user id");
                errorAlert.showAndWait();
                return;
            }

            LocalDateTime startDate = LocalDateTime.parse(this.startTime.getText().trim(), formatter);
            ZonedDateTime startDateZoned = ZonedDateTime.of(startDate, ZoneId.systemDefault());

            LocalDateTime endDate = LocalDateTime.parse(this.endTime.getText().trim(), formatter);
            ZonedDateTime endDateZoned = ZonedDateTime.of(endDate, ZoneId.systemDefault());

            Appointment appointment = new Appointment(0, this.title.getText().trim(), this.description.getText().trim(),
                this.locationAppointment.getText().trim(), this.type.getText().trim(), startDateZoned, endDateZoned,
                Integer.parseInt(this.customerId.getText().trim()), contact.get(), user.get());
            Customer customer = DataManager.getInstance().getCustomerById(Integer.parseInt(this.customerId.getText().trim()));

            if (fieldsValid(appointment, customer)) {
                customer.addAppointment(appointment);
                showAppointments(customer.getAppointments());
                Stage stage = (Stage) confirmButton.getScene().getWindow();
                stage.close();
            }
        } else {
            Optional<Contact> contact = DataManager.getInstance().contacts.stream().filter(c -> c.getName().equals(contactList.getSelectionModel().getSelectedItem())).findFirst();
            Optional<User> user = DataManager.getInstance().users.stream().filter(u -> u.getId() == Integer.parseInt(this.userId.getText().trim())).findFirst();

            if (!user.isPresent()) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Non Existing User");
                errorAlert.setContentText("Please provide a valid user id");
                errorAlert.showAndWait();
                return;
            }

            LocalDateTime startDate = LocalDateTime.parse(this.startTime.getText().trim(), formatter);
            ZonedDateTime startDateZoned = ZonedDateTime.of(startDate, ZoneId.systemDefault());

            LocalDateTime endDate = LocalDateTime.parse(this.endTime.getText().trim(), formatter);
            ZonedDateTime endDateZoned = ZonedDateTime.of(endDate, ZoneId.systemDefault());
            int id  = Integer.parseInt(this.idTextField.getText().trim());
            Appointment appointment = new Appointment( id, this.title.getText().trim(), this.description.getText().trim(),
                this.locationAppointment.getText().trim(), this.type.getText().trim(), startDateZoned ,
                endDateZoned, Integer.parseInt(this.customerId.getText().trim()), contact.get(), user.get());
            Customer customer = DataManager.getInstance().getCustomerById(Integer.parseInt(this.customerId.getText().trim()));
            Appointment existingAppointment = DataManager.getInstance().appointments.stream().filter(a -> a.getId() == id)
                    .findFirst().get();
            if (fieldsValid(appointment, customer)) {
                appointment.setCreateDate(existingAppointment.getCreateDate());
                appointment.setCreatedBy(existingAppointment.getCreatedBy());
                appointment.setLastUpdate(existingAppointment.getLastUpdate());
                appointment.setLastUpdatedBy(existingAppointment.getLastUpdatedBy());
                customer.editAppointment(appointment);
                showAppointments(customer.getAppointments());
                Stage stage = (Stage) confirmButton.getScene().getWindow();
                stage.close();
            }
        }
    }

    /**
     * Helper method that checks if fields on UI are valid
     * @return true if valid otherwise false
     */
    private boolean fieldsValid(Appointment appointment, Customer customer) {
        String title = this.title.getText().trim();
        String description = this.description.getText().trim();
        String location = this.locationAppointment.getText().trim();
        String contact = contactList.getSelectionModel().getSelectedItem();
        String type = this.type.getText().trim();
        String startTime = this.startTime.getText().trim();
        String endTime = this.endTime.getText().trim();
        String customerId = this.customerId.getText().trim();
        String userId = this.userId.getText().trim();

        if (title.isEmpty() || description.isEmpty() || location.isEmpty() || contact.isEmpty() || type.isEmpty()
            || startTime.isEmpty() || endTime.isEmpty() || customerId.isEmpty() || userId.isEmpty() ) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Missing Fields");
            errorAlert.setContentText("Please provide value for all fields");
            errorAlert.showAndWait();
        } else if(!isDateValid(startTime) || !isDateValid(endTime)) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Incorrect Fields");
            errorAlert.setContentText("Please provide valid value for dates");
            errorAlert.showAndWait();
        } else if (DataManager.getInstance().getCustomerById(Integer.parseInt(customerId)) == null) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Non Existing Customer");
            errorAlert.setContentText("Please provide a valid customer id");
            errorAlert.showAndWait();
        } else {
            ZonedDateTime start = appointment.getStartDate();
            ZonedDateTime end = appointment.getEndDate();
            if (start.isAfter(end)) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Incorrect Dates");
                errorAlert.setContentText("Start Date of appointment needs to be before End Date");
                errorAlert.showAndWait();
            } else {
                if (!appointment.isAppointmentWithinBusinessHours(appointment)) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Incorrect Dates");
                    errorAlert.setContentText("Appointment is not scheduled within business hours of 8am to 10pm EST time");
                    errorAlert.showAndWait();
                } else {
                    if (customer.isAppointmentOverlapping(appointment, customer.getAppointments())) {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Error");
                        errorAlert.setHeaderText("Incorrect Dates");
                        errorAlert.setContentText("Appointment is overlapping with current existing appointments. Please correct");
                        errorAlert.showAndWait();
                    } else {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Helper method that verifies if a string date is valid
     * @param date string date to verify
     * @return true if valid otherwise false
     */
    private boolean isDateValid(String date) {
        try {
            this.formatter.parse(date);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    /**
     * Helper method to deep clone a list of appointments
     * @param appointments list of appointments to copy
     * @return list of appointments
     */
    private List<Appointment> cloneAppointmentsList(List<Appointment> appointments) {
        List<Appointment> appointmentList = new ArrayList<>();

        for(Appointment appointment : appointments) {
            Appointment appointmentCopy = new Appointment(appointment.getId(), appointment.getTitle(), appointment.getDescription(),
                appointment.getLocationAppointment(), appointment.getType(), appointment.getStartDate(), appointment.getEndDate(),
                appointment.getCustomerId(), appointment.getContact(), appointment.getUser());
            appointmentList.add(appointmentCopy);
        }
        return appointmentList;
    }

    /**
     * Helper method to filter a list of appointments by current week
     * Lambda expression is used in this method to filter appointments by current week
     * @param appointments appointments to filter
     * @return filtered list of appointments
     */
    private List<Appointment> filterByCurrentWeek(List<Appointment> appointments) {
        LocalDate date = LocalDate.now();
        int weekOfYear = date.get(WeekFields.of(Locale.getDefault()).weekOfYear());
        List<Appointment> filteredList = appointments.stream().filter(a -> a.getStartDate()
            .get(WeekFields.of(Locale.getDefault()).weekOfYear()) == weekOfYear).collect(Collectors.toList());
        return filteredList;
    }

    /**
     * Helper method to filter a list of appointments by current month
     * Lambda expression is used in this method to filter appointments by current month
     * @param appointments appointments to filter
     * @return filtered list of appointments
     */
    private List<Appointment> filterByCurrentMonth(List<Appointment> appointments) {
        LocalDate date = LocalDate.now();
        List<Appointment> filteredList = appointments.stream().filter(a -> a.getStartDate().getMonthValue() == date.getMonthValue())
            .collect(Collectors.toList());
        return filteredList;
    }

}
