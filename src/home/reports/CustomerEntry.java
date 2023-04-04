package home.reports;

/**
 * Class that represents an entry on the customers report
 */
public class CustomerEntry {
    private String customerName;
    private String appointmentType;
    private int totalAppointments;

    /**
     * Constructor to initialize a report entry
     * @param customerName name of the customer
     * @param appointmentType the type of appointment
     * @param totalAppointments total number of appointments
     */
    public CustomerEntry(
        final String customerName,
        final String appointmentType,
        final int totalAppointments)
    {
        this.customerName = customerName;
        this.appointmentType = appointmentType;
        this.totalAppointments = totalAppointments;
    }

    /**
     * Retrieves the customer name
     * @return the name of the customer
     */
    public String getCustomerName()
    {
        return this.customerName;
    }

    /**
     * Sets the customer name
     * @param customerName new customer name
     */
    public void setCustomerName(final String customerName)
    {
        this.customerName = customerName;
    }

    /**
     * Retrieves the appointment type
     * @return the appointment's type
     */
    public String getAppointmentType()
    {
        return this.appointmentType;
    }

    /**
     * Sets the appointment's type
     * @param appointmentType new type of appointment
     */
    public void setAppointmentType(final String appointmentType)
    {
        this.appointmentType = appointmentType;
    }

    /**
     * Retrieves the total number of appointments of the report's entry
     * @return total number of appointments in report's entry
     */
    public int getTotalAppointments()
    {
        return this.totalAppointments;
    }

    /**
     * Sets the total number of appointments per entry
     * @param totalAppointments the total number of appointments in report's entry
     */
    public void setTotalAppointments(final int totalAppointments)
    {
        this.totalAppointments = totalAppointments;
    }
}
