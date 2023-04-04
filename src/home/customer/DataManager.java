package home.customer;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import home.JDBC;
import home.LogInController;
import home.appointments.Appointment;
import home.appointments.Contact;
import home.appointments.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Class that encapsulates the logic for interacting with database data.
 */
public class DataManager
{
    public List<Customer> customers = new ArrayList<>();
    public List<Country> countries = getAllCountries();
    public List<Division> divisions = getAllDivisions();
    public List<Contact> contacts = getAllContacts();
    public List<User> users = getAllUsers();
    public List<Appointment> appointments = getAllAppointments();
    private static DataManager instance = new DataManager();

    /**
     * private constructor to define singleton
     */
    private DataManager() {
    }

    /**
     * creates an instance of this class only when its null
     * otherwise it will return the same instance
     *
     * @return
     */
    public static DataManager getInstance() {
        if (instance == null) {
            DataManager dataManager = new DataManager();
            return dataManager;
        } else {
            return instance;
        }
    }

    /**
     * Method that retrieves all appointments from database
     * Lambda expression used to find user from list of users
     * Lamdba expression used to find contact from list of contacts
     * @return list of appointments from database
     */
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        try {
            String query = "select appointment_id, title, description, location, type, " +
                "start, end, customer_id, user_id, contact_id, create_date, created_by, " +
                    "last_update, last_updated_by from appointments";
            PreparedStatement myStmt = JDBC.getConnection().prepareStatement(query);
            ResultSet resultSet = myStmt.executeQuery();
            while (resultSet.next()) {
                int appointmentId = resultSet.getInt("appointment_id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                String location = resultSet.getString("location");
                String type = resultSet.getString("type");
                String createdBy = resultSet.getString("created_by");
                String updatedBy = resultSet.getString("last_updated_by");
                int customerId = resultSet.getInt("customer_id");
                int userId = resultSet.getInt("user_id");
                int contactId = resultSet.getInt("contact_id");
                Timestamp startTimestamp = resultSet.getTimestamp("start");
                ZonedDateTime startZonedDateTime = ZonedDateTime.ofInstant(startTimestamp.toInstant(), ZoneId.systemDefault());
                Timestamp endTimestamp = resultSet.getTimestamp("end");
                ZonedDateTime endZonedDateTime = ZonedDateTime.ofInstant(endTimestamp.toInstant(), ZoneId.systemDefault());
                User user = users.stream().filter(u -> u.getId() == userId).findFirst().get();
                Contact contact = contacts.stream().filter(c -> c.getId() == contactId).findFirst().get();
                Appointment appointment = new Appointment(appointmentId, title, description, location, type, startZonedDateTime,
                    endZonedDateTime, customerId, contact, user);
                Timestamp created = resultSet.getTimestamp("create_date");
                ZonedDateTime createdZone = ZonedDateTime.ofInstant(created.toInstant(), ZoneId.systemDefault());
                Timestamp updated = resultSet.getTimestamp("last_update");
                ZonedDateTime updatedZone = ZonedDateTime.ofInstant(updated.toInstant(), ZoneId.systemDefault());
                appointment.setCreatedBy(createdBy);
                appointment.setCreateDate(createdZone);
                appointment.setLastUpdatedBy(updatedBy);
                appointment.setLastUpdate(updatedZone);
                appointments.add(appointment);
            }
            JDBC.getConnection().close();
        } catch (SQLException e) {
            System.out.println("Error with retrieving appointments " + e.getMessage());
        }
        return  appointments;
    }
    /**
     * Loads the customers from the database and stores a local copy
     */
    public void loadCustomers() throws SQLException {
        String query = "select customer_id, customer_name, address, postal_code, phone, division_id, create_date, " +
                "created_by, last_update, last_updated_by from customers";
        PreparedStatement myStmt = JDBC.getConnection().prepareStatement(query);

        ResultSet resultSet = myStmt.executeQuery();
        while (resultSet.next()) {
            int customerId = resultSet.getInt("customer_id");
            String customerName = resultSet.getString("customer_name");
            String customerAddress = resultSet.getString("address");
            String customerPhone = resultSet.getString("phone");
            String customerPostal = resultSet.getString("postal_code");
            String createdBy = resultSet.getString("created_by");
            String updatedBy = resultSet.getString("last_updated_by");
            int divisionId = resultSet.getInt("division_id");
            Division division = getDivisionById(divisionId);
            List<Appointment> customerApppointments = appointments.stream().
                filter(a -> a.getCustomerId() == customerId).collect(Collectors.toList());
            Customer customer = new Customer(customerId, customerName, customerAddress, customerPostal, customerPhone,
                customerApppointments, division);
            Timestamp created = resultSet.getTimestamp("create_date");
            ZonedDateTime createdZone = ZonedDateTime.ofInstant(created.toInstant(), ZoneId.systemDefault());
            Timestamp updated = resultSet.getTimestamp("last_update");
            ZonedDateTime updatedZone = ZonedDateTime.ofInstant(updated.toInstant(), ZoneId.systemDefault());
            customer.setCreatedBy(createdBy);
            customer.setCreateDate(createdZone);
            customer.setLastUpdatedBy(updatedBy);
            customer.setLastUpdate(updatedZone);
            customers.add(customer);
        }
        JDBC.getConnection().close();
    }

    /**
     * Method to retrieve a user by id
     * Lambda expression used her to find a user by its id from list of users
     * @param id user id
     * @return user object found otherwise null
     */
    public User getUserById(int id) {
        Optional<User> user = users.stream().filter(u -> u.getId() == id).findFirst();
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    /**
     * Method to retrieve a customer by id
     * Lambda expression used here to find a customer by its id from list of customers
     *
     * @param id identifier to filter by
     * @return the customer found if nothing found will return null
     */
    public Customer getCustomerById(int id) {
        Optional<Customer> customer = customers.stream().filter(c -> c.getId() == id).findFirst();
        if (customer.isPresent()) {
            return customer.get();
        }
        return null;
    }

    /**
     * returns the list of customers in the application
     *
     * @return observable list of customers
     */
    public ObservableList<Customer> getCustomers()
    {
        return FXCollections.observableArrayList(customers);
    }

    /**
     * logic to add a customer to the list of customers
     *
     * @param customer
     */
    public void addCustomer(Customer customer) {
        customer.setCreateDate(ZonedDateTime.now());
        customer.setCreatedBy(LogInController.userLoggedOn.getUsername());
        customer.setLastUpdate(ZonedDateTime.now());
        customer.setLastUpdatedBy(LogInController.userLoggedOn.getUsername());
        int customerId = insertCustomer(customer);
        customer.setId(customerId);
        customers.add(customer);
    }

    /**
     * logic to remove a customer from the list of customers
     *
     * @param customer
     */
    public void deleteCustomer(Customer customer) {
        removeCustomer(customer);
        //appointments.removeAll(customer.getAppointments());
        customers.remove(customer);
    }

    /**
     * logic to edit a customer from the list of customers
     * Lambda expression used here to find a customer by its id from list of customers
     *
     * @param customer
     */
    public void editCustomer(Customer customer) {
        // TODO Convert Local Time Zone to UTC time zone for appointments dates
        customer.setLastUpdate(ZonedDateTime.now());
        customer.setLastUpdatedBy(LogInController.userLoggedOn.getUsername());
        updateCustomer(customer);
        Optional<Customer> existingCustomer = customers.stream().filter(c -> c.getId() == customer.getId()).findFirst();
        if (existingCustomer.isPresent()) {
            customers.set(customers.indexOf(existingCustomer.get()), customer);
        }
    }

    /**
     * method to retrieve a division by its id
     * Lambda expression heer used to find a division by its id from list of divisions
     *
     * @param divisionId division id
     * @return the division found otherwise null
     */
    public Division getDivisionById(int divisionId) {
        Optional<Division> division = divisions.stream().filter(d -> d.getDivisionId() == divisionId).findFirst();
        if (division.get() != null) {
            return division.get();
        }
        return null;
    }

    /**
     * method to retrieve list of divisions by country name
     * Lambda expression used here to find all divisions with a specified country name
     *
     * @param countryName country name to filter by
     * @return list of divisions by country
     */
    public List<Division> getDivisionsByCountryName(String countryName) {
        return divisions.stream()
            .filter(d -> d.getCountry().getCountryName().equals(countryName))
            .collect(Collectors.toList());
    }

    /**
     * method to retrieve a division by its name and country name
     * Lambda expression used here to find a division by its name and an specified country name
     *
     * @param divisionName division name
     * @param countryName  country name
     * @return the division found otherwise null
     */
    public Division getDivisionByNameAndCountryName(String divisionName, String countryName) {
        Optional<Division> division = divisions.stream().filter(d -> d.getDivisionName().equals(divisionName)
            && d.getCountry().getCountryName().equals(countryName)).findFirst();
        if (division.get() != null) {
            return division.get();
        }
        return null;
    }

    /**
     * method to retrieve all divisions from DB
     * Lambda expression used to retrieve country based on id from list of countries
     * @return list of divisions
     */
    public List<Division> getAllDivisions() {
        List<Division> divisionsFromDB = new ArrayList<>();
        try {
            String query = "select division_id, division, country_id from first_level_divisions";
            PreparedStatement myStmt = JDBC.getConnection().prepareStatement(query);
            ResultSet resultSet = myStmt.executeQuery();
            while (resultSet.next()) {
                int divsionId = resultSet.getInt("division_id");
                String divisionName = resultSet.getString("division");
                int countryId = resultSet.getInt("country_id");
                Country country = countries.stream().filter(c -> c.getCountryId() == countryId).findFirst().get();
                Division division = new Division(divsionId, divisionName, country);
                divisionsFromDB.add(division);
            }
            JDBC.getConnection().close();
        } catch (SQLException e) {
            System.out.println("Error with retrieving divisions " + e.getMessage());
        }
        return  divisionsFromDB;
    }

    /**
     * method to retrieve all countries from db
     *
     * @return list of all countries
     */
    public List<Country> getAllCountries() {
        List<Country> countriesFromDB = new ArrayList<>();
        try {
            String query = "select country_id, country from countries";
            PreparedStatement myStmt = JDBC.getConnection().prepareStatement(query);
            ResultSet resultSet = myStmt.executeQuery();
            while (resultSet.next()) {
                int countryId = resultSet.getInt("country_id");
                String countryName = resultSet.getString("country");
                Country country = new Country(countryId, countryName);
                countriesFromDB.add(country);
            }
            JDBC.getConnection().close();
        } catch (SQLException e) {
            System.out.println("Error with retrieving countries " + e.getMessage());
        }
        return  countriesFromDB;
    }

    /**
     * Method that helps retrieve all contacts from Database
     *
     * @return list of contacts from database
     */
    public List<Contact> getAllContacts(){
        List<Contact> contactsFromDB = new ArrayList<>();
        try {
           String query = "select contact_id, contact_name, email from contacts";
           PreparedStatement myStmt = JDBC.getConnection().prepareStatement(query);
           ResultSet resultSet = myStmt.executeQuery();
           while (resultSet.next()) {
               int contactId = resultSet.getInt("contact_id");
               String contactName = resultSet.getString("contact_name");
               String contactEmail = resultSet.getString("email");
               Contact contact = new Contact(contactId, contactName, contactEmail);
               contactsFromDB.add(contact);
           }
           JDBC.getConnection().close();
        } catch (SQLException e) {
              System.out.println("Error with retrieving contacts " + e.getMessage());
        }
        return  contactsFromDB;
    }

    /**
     * Method that helps retrieve all users from Database
     * @return  list of users from database
     */
    public List<User> getAllUsers() {
        List<User> usersFromDB = new ArrayList<>();
        try {
            String query = "select user_id, user_name, password from users";
            PreparedStatement myStmt = JDBC.getConnection().prepareStatement(query);
            ResultSet resultSet = myStmt.executeQuery();
            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String userName = resultSet.getString("user_name");
                String password = resultSet.getString("password");
                User user = new User(userId, userName, password);
                usersFromDB.add(user);
            }
            JDBC.getConnection().close();
        } catch (SQLException e) {
            System.out.println("Error with retrieving users " + e.getMessage());
        }
        return  usersFromDB;
    }

    /**
     * Inserts a new customer record on database
     * @param customer customer to insert into database
     * @return new customer id generated from database
     */
    public int insertCustomer(Customer customer) {
        int customerId = 0;
        try {
            String query = "insert into customers " +
                "(customer_name, address, postal_code, phone, create_date, created_by, last_update, " +
                "last_updated_by, division_id) values (?,?,?,?,?,?,?,?,?)";
            PreparedStatement myStmt = JDBC.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            myStmt.setString(1, customer.getName());
            myStmt.setString(2, customer.getAddress());
            myStmt.setString(3, customer.getPostalCode());
            myStmt.setString(4, customer.getPhoneNumber());
            ZonedDateTime utcCreate = customer.getCreateDate().withZoneSameInstant(ZoneId.of("UTC"));
            myStmt.setTimestamp(5, Timestamp.from(utcCreate.toInstant()));
            myStmt.setString(6, customer.getCreatedBy());
            ZonedDateTime utcLastUpdate = customer.getLastUpdate().withZoneSameInstant(ZoneId.of("UTC"));
            myStmt.setTimestamp(7, Timestamp.from(utcLastUpdate.toInstant()));
            myStmt.setString(8, customer.getLastUpdatedBy());
            myStmt.setInt(9, customer.getDivision().getDivisionId());
            myStmt.executeUpdate();
            ResultSet res = myStmt.getGeneratedKeys();
            if (res.next()) {
                customerId = res.getInt(1);
            }
            JDBC.getConnection().close();
        } catch (SQLException e) {
            System.out.println("Error with inserting a new customer " + e.getMessage());
        }
        return customerId;
    }

    /**
     * Updates a customer record on database
     * @param customer customer to update
     */
    public void updateCustomer(Customer customer) {
        try {
            String query = "update customers set customer_name = ?, address = ?, postal_code = ?, " +
                "phone = ?, create_date = ?, created_by = ?, last_update = ?, " +
                "last_updated_by = ?, division_id = ? where customer_id = ?";
            PreparedStatement myStmt = JDBC.getConnection().prepareStatement(query);
            myStmt.setString(1, customer.getName());
            myStmt.setString(2, customer.getAddress());
            myStmt.setString(3, customer.getPostalCode());
            myStmt.setString(4, customer.getPhoneNumber());
            ZonedDateTime utcCreate = customer.getCreateDate().withZoneSameInstant(ZoneId.of("UTC"));
            myStmt.setTimestamp(5, Timestamp.from(utcCreate.toInstant()));
            myStmt.setString(6, customer.getCreatedBy());
            ZonedDateTime utcLastUpdate = customer.getLastUpdate().withZoneSameInstant(ZoneId.of("UTC"));
            myStmt.setTimestamp(7, Timestamp.from(utcLastUpdate.toInstant()));
            myStmt.setString(8, customer.getLastUpdatedBy());
            myStmt.setInt(9, customer.getDivision().getDivisionId());
            myStmt.setInt(10, customer.getId());
            myStmt.executeUpdate();
            JDBC.getConnection().close();
        } catch (SQLException e) {
            System.out.println("Error with updating a customer " + e.getMessage());
        }
    }

    /**
     * Deletes a customer record from database
     * @param customer customer to delete from database
     */
    public void removeCustomer(Customer customer) {
        List<Appointment> customerAppointments = customer.getAppointments();

        for (Appointment appointment : customerAppointments) {
            try {
                String query = "delete from appointments where appointment_id = ?";
                PreparedStatement myStmt = JDBC.getConnection().prepareStatement(query);
                myStmt.setInt(1, appointment.getId());
                myStmt.executeUpdate();
                JDBC.getConnection().close();
            } catch (SQLException e) {
                System.out.println("Error with deleting appointments" + e.getMessage());
            }
        }
        try {
            String query = "delete from customers where customer_id = ?";
            PreparedStatement myStmt = JDBC.getConnection().prepareStatement(query);
            myStmt.setInt(1, customer.getId());
            myStmt.executeUpdate();
            JDBC.getConnection().close();
        } catch (SQLException e) {
            System.out.println("Error with deleting customer " + e.getMessage());
        }
    }

    /**
     * Updates an appointment record on database
     * @param appointment appointment to update
     */
    public void updateAppointment(Appointment appointment) {
        try {
            String query = "update appointments set title = ?, description = ?, location = ?, " +
                "type = ?, start = ?, end = ?, create_date = ?, " +
                "created_by = ?, last_update= ?, last_updated_by = ?, " +
                "customer_id = ?, user_id = ?, contact_id = ? where appointment_id = ?";
            PreparedStatement myStmt = JDBC.getConnection().prepareStatement(query);
            myStmt.setString(1, appointment.getTitle());
            myStmt.setString(2, appointment.getDescription());
            myStmt.setString(3, appointment.getLocationAppointment());
            myStmt.setString(4, appointment.getType());
            ZonedDateTime utcStart = appointment.getStartDate().withZoneSameInstant(ZoneId.of("UTC"));
            myStmt.setTimestamp(5, Timestamp.from(utcStart.toInstant()));
            ZonedDateTime utcEnd = appointment.getEndDate().withZoneSameInstant(ZoneId.of("UTC"));
            myStmt.setTimestamp(6, Timestamp.from(utcEnd.toInstant()));
            ZonedDateTime utcCreate = appointment.getCreateDate().withZoneSameInstant(ZoneId.of("UTC"));
            myStmt.setTimestamp(7, Timestamp.from(utcCreate.toInstant()));
            myStmt.setString(8, appointment.getCreatedBy());
            ZonedDateTime utcLastUpdate = appointment.getLastUpdate().withZoneSameInstant(ZoneId.of("UTC"));
            myStmt.setTimestamp(9, Timestamp.from(utcLastUpdate.toInstant()));
            myStmt.setString(10, appointment.getLastUpdatedBy());
            myStmt.setInt(11, appointment.getCustomerId());
            myStmt.setInt(12, appointment.getUserId());
            myStmt.setInt(13, appointment.getContact().getId());
            myStmt.setInt(14, appointment.getId());
            myStmt.executeUpdate();
            JDBC.getConnection().close();
        } catch (SQLException e) {
            System.out.println("Error with updating an appointment " + e.getMessage());
        }
    }

    /**
     * Deletes an appointment record on database
     * @param appointment appointment to remove
     */
    public void removeAppointment(Appointment appointment) {
        try {
            String query = "delete from appointments where appointment_id = ?";
            PreparedStatement myStmt = JDBC.getConnection().prepareStatement(query);
            myStmt.setInt(1, appointment.getId());
            myStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error with deleting one appointment" + e.getMessage());
        }
    }

    /**
     * Inserts a new record appointment in database
     * @param appointment apppointment to insert in database
     * @return new generated appointment id
     */
    public int insertAppointment(Appointment appointment) {
        int appointmentId = 0;
        try {
            String query = "insert into appointments " +
                "(title, description, location, type, start, end, create_date, created_by, last_update, " +
                "last_updated_by, customer_id, user_id, contact_id) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement myStmt = JDBC.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            myStmt.setString(1, appointment.getTitle());
            myStmt.setString(2, appointment.getDescription());
            myStmt.setString(3, appointment.getLocationAppointment());
            myStmt.setString(4, appointment.getType());
            ZonedDateTime utcStart = appointment.getStartDate().withZoneSameInstant(ZoneId.of("UTC"));
            myStmt.setTimestamp(5, Timestamp.from(utcStart.toInstant()));
            ZonedDateTime utcEnd = appointment.getEndDate().withZoneSameInstant(ZoneId.of("UTC"));
            myStmt.setTimestamp(6, Timestamp.from(utcEnd.toInstant()));
            ZonedDateTime utcCreate = appointment.getCreateDate().withZoneSameInstant(ZoneId.of("UTC"));
            myStmt.setTimestamp(7, Timestamp.from(utcCreate.toInstant()));
            myStmt.setString(8, appointment.getCreatedBy());
            ZonedDateTime utcLastUpdate = appointment.getLastUpdate().withZoneSameInstant(ZoneId.of("UTC"));
            myStmt.setTimestamp(9, Timestamp.from(utcLastUpdate.toInstant()));
            myStmt.setString(10, appointment.getLastUpdatedBy());
            myStmt.setInt(11, appointment.getCustomerId());
            myStmt.setInt(12, appointment.getUserId());
            myStmt.setInt(13, appointment.getContact().getId());
            appointmentId = myStmt.executeUpdate();
            ResultSet res = myStmt.getGeneratedKeys();
            if (res.next()) {
                appointmentId = res.getInt(1);
            }
            JDBC.getConnection().close();
        } catch (SQLException e) {
            System.out.println("Error with inserting a new appointment " + e.getMessage());
        }
        return appointmentId;
    }
}