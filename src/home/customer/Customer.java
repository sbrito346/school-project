package home.customer;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import home.LogInController;
import home.appointments.Appointment;

/**
 * Class that represents a customer record
 */
public class Customer {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private int id;
    private String name;
    private String address;
    private String postalCode;
    private String phoneNumber;
    private List<Appointment> appointments;
    private Division division;
    private String divisionName;
    private String countryName;
    private ZonedDateTime createDate;
    private String createdBy;
    private ZonedDateTime lastUpdate;
    private String lastUpdatedBy;

    /**
     * Constructor to instantiate a customer object
     * @param id id of the customer
     * @param name name of the customer
     * @param address address of the customer
     * @param postalCode postal code of the customer
     * @param phoneNumber phone number of the customer
     * @param appointments list of appointments for the customer
     * @param division division of customer
     */
    public Customer(
        final int id,
        final String name,
        final String address,
        final String postalCode,
        final String phoneNumber,
        final List<Appointment> appointments,
        final Division division) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        this.appointments = appointments;
        this.division = division;
    }

    /**
     *  returns the id of the customer
     * @return
     */
    public int getId()
    {
        return this.id;
    }

    /**
     * sets the id of the customer
     * @param id
     */
    public void setId(final int id)
    {
        this.id = id;
    }

    /**
     * returns the name of the customer
     * @return
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * sets the name of the customer
     * @param name
     */
    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * returns the address of the customer
     * @return
     */
    public String getAddress()
    {
        return this.address;
    }

    /**
     * sets the address of the customer
     * @param address
     */
    public void setAddress(final String address)
    {
        this.address = address;
    }

    /**
     * returns the postal code of the customer
     * @return
     */
    public String getPostalCode()
    {
        return this.postalCode;
    }

    /**
     * sets the postal code of the customer
     * @param postalCode
     */
    public void setPostalCode(final String postalCode)
    {
        this.postalCode = postalCode;
    }

    /**
     * returns the phone number of the customer
     * @return
     */
    public String getPhoneNumber()
    {
        return this.phoneNumber;
    }

    /**
     * sets the phone number of the customer
     * @param phoneNumber
     */
    public void setPhoneNumber(final String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Retrieves the customer's division
     * @return the division object
     */
    public Division getDivision() {
        return this.division;
    }

    /**
     * Modifies teh country's division
     * @param division division object
     */
    public void setDivision(final Division division) {
        this.division = division;
    }

    /**
     * returns the list of appointments for the customer
     * @return list of appointments
     */
    public List<Appointment> getAppointments() {
        return appointments;
    }

    /**
     * sets the list of appointments for the customer
     * @param appointments list of appointments
     */
    public void setAppointments(final List<Appointment> appointments) {
        this.appointments = appointments;
    }

    /**
     * adds an appointment for the customer
     * @param appointment appointment to add
     */
    public void addAppointment(Appointment appointment) {
        appointment.setCreateDate(ZonedDateTime.now());
        appointment.setCreatedBy(LogInController.userLoggedOn.getUsername());
        appointment.setLastUpdate(ZonedDateTime.now());
        appointment.setLastUpdatedBy(LogInController.userLoggedOn.getUsername());
        int newId = DataManager.getInstance().insertAppointment(appointment);
        appointment.setId(newId);
        this.appointments.add(appointment);
        DataManager.getInstance().appointments.add(appointment);
    }

    /**
     * removes an appointment from the customer
     * @param appointment appointment to remove
     */
    public void removeAppointment(Appointment appointment) {
        DataManager.getInstance().removeAppointment(appointment);
        this.appointments.remove(appointment);
        DataManager.getInstance().appointments.remove(appointment);
    }

    /**
     * edits an existing customer's appointment
     * Lambda expression is used in this method to find the appointment to edit from list of appointments
     * @param appointment appointment to update
     */
    public void editAppointment(Appointment appointment) {
        appointment.setLastUpdate(ZonedDateTime.now());
        appointment.setLastUpdatedBy(LogInController.userLoggedOn.getUsername());
        DataManager.getInstance().updateAppointment(appointment);
        //update local lists of appointments
        Optional<Appointment> existingAppointment = this.appointments.stream().filter(c -> c.getId() == appointment.getId()).findFirst();
        if (existingAppointment.isPresent()) {
            appointments.set(appointments.indexOf(existingAppointment.get()), appointment);
        }
        Optional<Appointment> globalAppointment = DataManager.getInstance().appointments.stream().filter(c -> c.getId() == appointment.getId()).findFirst();
        if (globalAppointment.isPresent()) {
            DataManager.getInstance().appointments.set(DataManager.getInstance().appointments.indexOf(globalAppointment.get()), appointment);
        }
    }

    /**
     * Method to check for overlapping appointments
     * Lambda expression is used in this method to filter out the same appointment from list of appointments.
     * @param newAppointment new appointment
     * @param existingAppointments list of customer existing appointments
     * @return true if new appointment overlaps else false
     *  (StartA <= EndB) and (EndA >= StartB) function to check overlaps
     *  if yes return false otherwise true
     *
     */
    public boolean isAppointmentOverlapping(Appointment newAppointment, List<Appointment> existingAppointments) {
        ZonedDateTime newAppStart = newAppointment.getStartDate();
        ZonedDateTime newAppEnd = newAppointment.getEndDate();
        List<Appointment> appointmentsToCheck = existingAppointments.stream()
            .filter(a -> a.getId() != newAppointment.getId()).collect(Collectors.toList());

        for (Appointment appointment : appointmentsToCheck) {
            int diff1 = newAppEnd.compareTo(appointment.getStartDate());
            int diff2 = appointment.getEndDate().compareTo(newAppStart);

            if (diff1 > 0 && diff2 > 0) return true;
        }
        return false;
    }

    /**
     * Retrieves the division name
     * @return the division name as string
     */
    public String getDivisionName() {
        return division.getDivisionName();
    }

    /**
     * Retrieves the country name
     * @return the country name
     */
    public String getCountryName() {
        return division.getCountry().getCountryName();
    }

    /**
     * Retrieves the created date of the customer
     * @return the creation date of the customer
     */
    public ZonedDateTime getCreateDate()
    {
        return this.createDate;
    }

    /**
     * Modifies the created date of the customer
     * @param createDate the creation date of the customer
     */
    public void setCreateDate(final ZonedDateTime createDate)
    {
        this.createDate = createDate;
    }

    /**
     * Retrieves the username of the user that created the customer
     * @return username of the user who created the customer
     */
    public String getCreatedBy()
    {
        return this.createdBy;
    }

    /**
     * Modifies who created the customer
     * @param createdBy the date of customer modification
     */
    public void setCreatedBy(final String createdBy)
    {
        this.createdBy = createdBy;
    }

    /**
     * Retrieves the last update time of the customer
     * @return last update time of the customer
     */
    public ZonedDateTime getLastUpdate()
    {
        return this.lastUpdate;
    }

    /**
     * Modifies the last update time of the customer
     * @param lastUpdate last update time of the customer
     */
    public void setLastUpdate(final ZonedDateTime lastUpdate)
    {
        this.lastUpdate = lastUpdate;
    }

    /**
     * Retrieves the username of the user that last modified the customer
     * @return the username of the user that last modified the customer
     */
    public String getLastUpdatedBy()
    {
        return this.lastUpdatedBy;
    }

    /**
     * Modifies the user that last updated the customer
     * @param lastUpdatedBy the username of the user who last updated the customer
     */
    public void setLastUpdatedBy(final String lastUpdatedBy)
    {
        this.lastUpdatedBy = lastUpdatedBy;
    }
}
