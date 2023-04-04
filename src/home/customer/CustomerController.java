package home.customer;

import java.io.IOException;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import home.appointments.Appointment;
import home.appointments.AppointmentController;
import home.reports.ReportController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

/**
 * Class that manages the customers view's controls and events
 */
public class CustomerController
{
    private ModifyCustomerController modifyCustomerController = new ModifyCustomerController();
    private AppointmentController appointmentController = new AppointmentController();

    @FXML
    private TableView customerTable;
    private static boolean loggedOnView = true;

    /**
     * Method that will initialize the javafx window
     */
    @FXML
    public void initialize() throws SQLException {
        DataManager.getInstance().loadCustomers();
        ObservableList<Customer> list = DataManager.getInstance().getCustomers();
        customerTable.setItems(list);
        addButtonToTable();

        if (loggedOnView) {
            String messageText = checkForUpcomingAppointment(list);
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.OK);
            confirmationAlert.setResizable(true);
            confirmationAlert.getDialogPane().setMinHeight(500);
            confirmationAlert.getDialogPane().setMinWidth(500);
            confirmationAlert.setTitle("Confirmation");
            confirmationAlert.setHeaderText("Upcoming Appointments");
            confirmationAlert.setContentText(messageText);
            confirmationAlert.showAndWait();
        }
    }

    /**
     * Event handler that will execute when user tries to add a customer
     * @throws IOException
     */
    @FXML
    public void onAddCustomer() throws IOException {
        Customer newCustomer = modifyCustomerController.showAddCustomer();
        if (newCustomer != null) {
            customerTable.getItems().add(newCustomer);
        }
    }

    /**
     * Event handler that will execute when user tries to edit a customer
     * Lambda expression is used here to find a customer by its id from list of customers
     * @throws IOException
     */
    @FXML
    public void onEditCustomer() throws IOException {
        Customer customer = (Customer)customerTable.getSelectionModel().getSelectedItem();
        if (customer != null) {
            customer = modifyCustomerController.showEditCustomer(customer);
            if (customer != null) {
                ObservableList<Customer> customers = customerTable.getItems();
                final Customer finalCustomer = customer;
                Optional<Customer> existingCustomer = customers.stream().filter(c -> c.getId() == finalCustomer.getId()).findFirst();
                if (existingCustomer.isPresent()) {
                    customerTable.getItems().set(customers.indexOf(existingCustomer.get()), customer);
                }
            }

        } else {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Select Customer");
            errorAlert.setContentText("Please select a customer to edit");
            errorAlert.showAndWait();
        }
    }

    /**
     * Event handler that will execute when user tries to delete a customer
     * @throws IOException
     */
    @FXML
    public void onDeleteCustomer() {
        Customer customer = (Customer)customerTable.getSelectionModel().getSelectedItem();
        if (customer != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation");
            confirmationAlert.setHeaderText("Delete Customer");
            confirmationAlert.setContentText("Customer with ID :" + customer.getId() + " will be deleted");
            confirmationAlert.showAndWait();
            if (confirmationAlert.getResult() == ButtonType.OK) {
                DataManager.getInstance().deleteCustomer(customer);
                customerTable.getItems().remove(customer);
            }
        } else {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Select Customer");
            errorAlert.setContentText("Please select a customer to delete");
            errorAlert.showAndWait();
        }
    }

    /**
     * Event handler that will call the report controller to show the appointments report
     */
    @FXML
    public void onAppointmentReport() {
        ReportController reportController = new ReportController();

        List<Appointment> allAppointments = new ArrayList<>();
        for (Object customer : customerTable.getItems()) {
            Customer existingCustomer = (Customer) customer;
            allAppointments.addAll(existingCustomer.getAppointments());
        }
        reportController.showAppointmentReport(allAppointments);
    }

    /**
     * Event handler that will call the report controller to show the contacts report
     */
    @FXML
    public void onContactReport() {
        ReportController reportController = new ReportController();
        List<Appointment> allAppointments = new ArrayList<>();
        for (Object customer : customerTable.getItems()) {
            Customer existingCustomer = (Customer) customer;
            allAppointments.addAll(existingCustomer.getAppointments());
        }
        reportController.showContactReport(allAppointments);
    }

    /**
     * Event handler that will call the report controller to show the customers report
     */
    @FXML
    public void onCustomerReport() {
        ReportController reportController = new ReportController();
        List<Appointment> allAppointments = new ArrayList<>();
        for (Object customer : customerTable.getItems()) {
            Customer existingCustomer = (Customer) customer;
            allAppointments.addAll(existingCustomer.getAppointments());
        }
        reportController.showCustomerReport(allAppointments);
    }

    /**
     * Helper method to place a button inside the table view columns
     */
    private void addButtonToTable() {
        TableColumn<Customer, Void> colBtn = new TableColumn();
        Callback<TableColumn<Customer, Void>, TableCell<Customer, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Customer, Void> call(final TableColumn<Customer, Void> param) {
                final TableCell<Customer, Void> cell = new TableCell<>() {
                    private final Button btn = new Button("Appointments");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Customer customer = getTableView().getItems().get(getIndex());
                            System.out.println("Customer: " + customer.getName());
                            try {
                                appointmentController.showAppointments(customer.getAppointments());
                            } catch (IOException e) {
                                int x = 0;
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        colBtn.setCellFactory(cellFactory);
        customerTable.getColumns().add(colBtn);
    }

    /**
     * Helper method that will check if the user has an upcoming appointment in the next 15 minutes.
     * @param customers list of customers
     * @return label to show upcoming appointments or label to show there are no upcoming appointments.
     */
    private String checkForUpcomingAppointment(List<Customer> customers) {
        ZonedDateTime currentTime = ZonedDateTime.now().withZoneSameInstant(ZoneId.systemDefault());
        List<Appointment> upcomingApps = new ArrayList<>();

        for (Customer customer : customers) {
            for(Appointment appointment : customer.getAppointments()) {
                long minuteDiff = ChronoUnit.SECONDS.between(currentTime, appointment.getStartDate());
                if (minuteDiff >= 0 && minuteDiff <=15) {
                    upcomingApps.add(appointment);
                }
            }
        }

        String upcomingAppointments = "";
        for (Appointment appointment : upcomingApps) {
            upcomingAppointments = upcomingAppointments + "Appointment ID: " + appointment.getId() + "--- Appointment Time: " + appointment.getStartDateString() + "\n";
            return upcomingAppointments;
        }

        return "There are no upcoming appointments at the moment!";
    }

}
