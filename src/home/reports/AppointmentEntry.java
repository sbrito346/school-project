package home.reports;

/**
 * Class that represents an entry on the appointments report
 */
public class AppointmentEntry {
    private String appointmentType;
    private String appointmentMonth;
    private int appointmentTotal;

    /**
     * Constructor to initialize a report's entry
     * @param appointmentType type of appointment
     * @param appointmentMonth the month of the appointment
     * @param appointmentTotal the total number of appointments per report entry
     */
    public AppointmentEntry(final String appointmentType, final String appointmentMonth, final int appointmentTotal) {
        this.appointmentType = appointmentType;
        this.appointmentMonth = appointmentMonth;
        this.appointmentTotal = appointmentTotal;
    }

    /**
     * Retrieves the appointment type
     * @return the type of appointment
     */
    public String getAppointmentType() {
        return this.appointmentType;
    }

    /**
     * Sets the appointment type
     * @param appointmentType new type of appointment
     */
    public void setAppointmentType(final String appointmentType) {
        this.appointmentType = appointmentType;
    }

    /**
     * Retrieves the month of the appointment
     * @return the month of the appointment
     */
    public String getAppointmentMonth() {
        return this.appointmentMonth;
    }

    /**
     * Sets the month of the appointment
     * @param appointmentMonth month of the appointment
     */
    public void setAppointmentMonth(final String appointmentMonth) {
        this.appointmentMonth = appointmentMonth;
    }

    /**
     * Retrieves the total number of appointments of the report's entry
     * @return total number of appointments in report's entry
     */
    public int getAppointmentTotal() {
        return this.appointmentTotal;
    }

    /**
     * Sets the total number of appointments per entry
     * @param appointmentTotal the total number of appointments in report's entry
     */
    public void setAppointmentTotal(final int appointmentTotal) {
        this.appointmentTotal = appointmentTotal;
    }
}
