package home.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import home.appointments.Appointment;
import home.customer.DataManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import static java.util.stream.Collectors.*;

/**
 * Class that manages the generation of report data as well as the display of the reports
 */
public class ReportController
{
    public static Stage stage = new Stage();

    /**
     * Method that will filter appointments by type and month and display the information
     * Lambda expression is used to group appointments by type and month. This makes is easier to do so.
     * @param appointments list of appointments to filter
     */
    public void showAppointmentReport(List<Appointment> appointments) {

        List<AppointmentEntry> appointmentEntries = new ArrayList<>();
        // by type and month
        Map<Pair<String, String>, List<Appointment>> apptsByTypeAndMonth = appointments.stream()
            .collect(groupingBy(appt -> new Pair<>(appt.getType(), appt.getStartDate().getMonth().toString())));

        for (Map.Entry<Pair<String, String>, List<Appointment>> typeAndMonthEntry : apptsByTypeAndMonth.entrySet()) {
            Pair<String, String> typeAndMonth = typeAndMonthEntry.getKey();
            List<Appointment> apppointments = typeAndMonthEntry.getValue();
            AppointmentEntry newEntry = new AppointmentEntry(typeAndMonth.getKey(), typeAndMonth.getValue(), apppointments.size());
            appointmentEntries.add(newEntry);
        }

        ObservableList<AppointmentEntry> apptsToShow = FXCollections.observableArrayList(appointmentEntries);

        Scene scene = new Scene(new Group());
        stage.setWidth(630);
        stage.setHeight(700);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 10, 10, 10));

        TableView<AppointmentEntry> table = new TableView<>();
        TableColumn typeColumn = new TableColumn("Appointment Type");
        typeColumn.setMinWidth(200);
        typeColumn.setCellValueFactory(
            new PropertyValueFactory<>("appointmentType"));

        TableColumn monthColumn = new TableColumn("Appointment Month");
        monthColumn.setMinWidth(200);
        monthColumn.setCellValueFactory(
            new PropertyValueFactory<>("appointmentMonth"));

        TableColumn totalCol = new TableColumn("Total Appointments");
        totalCol.setMinWidth(200);
        totalCol.setCellValueFactory(
            new PropertyValueFactory<>("appointmentTotal"));

        table.setItems(apptsToShow);
        table.getColumns().addAll(typeColumn, monthColumn, totalCol);
        vbox.getChildren().addAll(table);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);
        stage.setTitle("Appointment By Month And Type Report");
        stage.setScene(scene);

        if (!this.stage.isShowing()) {
            this.stage.showAndWait();
        }
    }

    /**
     * Method that will filter appointments by customer and type and display the report
     * Lambda expression is used in this method to group appointments by customer id and type.
     * @param appointments list of appointments to filter
     */
    public void showCustomerReport(List<Appointment> appointments) {
        List<CustomerEntry> customerEntries = new ArrayList<>();
        // by customer and type
        Map<Pair<Integer, String>, List<Appointment>> apptsByCustomerAndType = appointments.stream()
            .collect(groupingBy(appt -> new Pair<>(appt.getCustomerId(), appt.getType())));

        for (Map.Entry<Pair<Integer, String>, List<Appointment>> customerAndTypeEntry : apptsByCustomerAndType.entrySet()) {
            Pair<Integer, String> customerAndType = customerAndTypeEntry.getKey();
            List<Appointment> apppointments = customerAndTypeEntry.getValue();
            String customerName = DataManager.getInstance().getCustomerById(customerAndType.getKey()).getName();
            CustomerEntry newEntry = new CustomerEntry(customerName, customerAndType.getValue(), apppointments.size());
            customerEntries.add(newEntry);
        }

        ObservableList<CustomerEntry> apptsToShow = FXCollections.observableArrayList(customerEntries);

        Scene scene = new Scene(new Group());
        stage.setWidth(630);
        stage.setHeight(700);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 10, 10, 10));

        TableView<CustomerEntry> table = new TableView<>();
        TableColumn nameColumn = new TableColumn("Customer");
        nameColumn.setMinWidth(200);
        nameColumn.setCellValueFactory(
            new PropertyValueFactory<>("customerName"));

        TableColumn typeColumn = new TableColumn("Appointment Type");
        typeColumn.setMinWidth(200);
        typeColumn.setCellValueFactory(
            new PropertyValueFactory<>("appointmentType"));

        TableColumn totalCol = new TableColumn("Total Appointments");
        totalCol.setMinWidth(200);
        totalCol.setCellValueFactory(
            new PropertyValueFactory<>("totalAppointments"));

        table.setItems(apptsToShow);
        table.getColumns().addAll(nameColumn, typeColumn, totalCol);
        vbox.getChildren().addAll(table);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);
        stage.setTitle("Total Appointments By Customer and Type Report");
        stage.setScene(scene);

        if (!this.stage.isShowing()) {
            this.stage.showAndWait();
        }
    }

    /**
     * Method to filter appointments by contact and show all appoinments per contact
     * Lambda expression is used inside this method to group appointments by contact name.
     * @param appointments list of appointments to filter
     */
    public void showContactReport(List<Appointment> appointments) {

        Map<String, List<Appointment>> apptsByContact = appointments.stream()
            .collect(groupingBy(app -> app.getContactName()));

        Scene scene = new Scene(new Group());
        stage.setWidth(1500);
        stage.setHeight(700);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 10, 10, 10));

        for (Map.Entry<String, List<Appointment>> entry : apptsByContact.entrySet()) {
            String contact = entry.getKey();
            List<Appointment> contactAppts = entry.getValue();

            TableView<Appointment> table = new TableView<>();
            Label label = new Label();
            label.setText(contact);

            TableColumn idColumn = new TableColumn("Appointment ID");
            idColumn.setMinWidth(200);
            idColumn.setCellValueFactory(
                new PropertyValueFactory<>("id"));

            TableColumn titleColumn = new TableColumn("Appointment Title");
            titleColumn.setMinWidth(200);
            titleColumn.setCellValueFactory(
                new PropertyValueFactory<>("title"));

            TableColumn typeColumn = new TableColumn("Appointment Type");
            typeColumn.setMinWidth(200);
            typeColumn.setCellValueFactory(
                new PropertyValueFactory<>("type"));

            TableColumn descColumn = new TableColumn("Description");
            descColumn.setMinWidth(200);
            descColumn.setCellValueFactory(
                new PropertyValueFactory<>("description"));

            TableColumn startColumn = new TableColumn("Start Date");
            startColumn.setMinWidth(200);
            startColumn.setCellValueFactory(
                new PropertyValueFactory<>("startDateString"));

            TableColumn endColumn = new TableColumn("End Date");
            endColumn.setMinWidth(200);
            endColumn.setCellValueFactory(
                new PropertyValueFactory<>("endDateString"));

            TableColumn customerColumn = new TableColumn("Customer ID");
            customerColumn.setMinWidth(200);
            customerColumn.setCellValueFactory(
                new PropertyValueFactory<>("customerId"));

            table.setItems(FXCollections.observableList(contactAppts));
            table.getColumns().addAll(idColumn, titleColumn, typeColumn, descColumn, startColumn, endColumn, customerColumn);
            vbox.getChildren().addAll(label, table);
        }

        ScrollPane scroll = new ScrollPane();
        scroll.setPrefSize(1500, 700);
        scroll.setPadding(new Insets(10,10,10,10));
        //Setting content to the scroll pane
        scroll.setContent(vbox);

        ((Group) scene.getRoot()).getChildren().addAll(scroll);
        stage.setTitle("Contacts Report");
        stage.setScene(scene);

        if (!this.stage.isShowing()) {
            this.stage.showAndWait();
        }
    }
}
