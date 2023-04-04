package home.appointments;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class that represents a customer's scheduled appointment
 */
public class Appointment
{
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private String startDateString;
    private String endDateString;

    private int id;
    private String title;
    private String description;
    private String locationAppointment;
    private String type;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private int customerId;
    private int userId;
    private String contactName;
    private Contact contact;
    private User user;
    private ZonedDateTime createDate;
    private String createdBy;
    private ZonedDateTime lastUpdate;
    private String lastUpdatedBy;

    /**
     * Constructor to create a customer's appointment
     * @param id appointment unique identifier
     * @param title title for the appointment
     * @param description description for the appointment
     * @param location location of the appointment
     * @param type type of appointment
     * @param startDate date when appointment starts
     * @param endDate date when appointment ends
     * @param customerId customer identifier for the appointment
     * @param user user for the appointment
     * @param contact contact name of the appointment
     */
    public Appointment(
        final int id,
        final String title,
        final String description,
        final String location,
        final String type,
        final ZonedDateTime startDate,
        final ZonedDateTime endDate,
        final int customerId,
        final Contact contact,
        final User user)
    {
        this.id = id;
        this.title = title;
        this.description = description;
        this.locationAppointment = location;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.customerId = customerId;
        this.user = user;
        this.userId = user.getId();
        this.contact = contact;
        this.contactName = contact.getName();
        this.endDateString = getEndDateString();
        this.startDateString = getStartDateString();
    }

    /**
     * retrieves the appointment's id
     * @return
     */
    public int getId()
    {
        return this.id;
    }

    /**
     * modifies the appointment's id
     * @param id
     */
    public void setId(final int id)
    {
        this.id = id;
    }

    /**
     * retrieves the appointment's title
     * @return
     */
    public String getTitle()
    {
        return this.title;
    }

    /**
     * modifies the appointment's title
     * @param title
     */
    public void setTitle(final String title)
    {
        this.title = title;
    }

    /**
     * retrieves the appointment's description
     * @return
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * modifies the appointment's description
     * @param description
     */
    public void setDescription(final String description)
    {
        this.description = description;
    }

    /**
     * retrieves the appointment's location
     * @return
     */
    public String getLocationAppointment()
    {
        return this.locationAppointment;
    }

    /**
     * modifies the appointment's location
     * @param locationAppointment
     */
    public void setLocationAppointment(final String locationAppointment)
    {
        this.locationAppointment = locationAppointment;
    }

    /**
     * retrieves the appointment's type
     * @return
     */
    public String getType()
    {
        return this.type;
    }

    /**
     * modifies the appointment's type
     * @param type
     */
    public void setType(final String type)
    {
        this.type = type;
    }

    /**
     * retrieves the start date of the appointment
     * @return
     */
    public ZonedDateTime getStartDate()
    {
        return this.startDate;
    }

    /**
     * modifies the start date of the appointment
     * @param startDate
     */
    public void setStartDate(final ZonedDateTime startDate)
    {
        this.startDate = startDate;
    }

    /**
     * retrieves the end date of the appointment
     * @return
     */
    public ZonedDateTime getEndDate()
    {
        return this.endDate;
    }

    /**
     * modifies the end date of the appointment
     * @param endDate
     */
    public void setEndDate(final ZonedDateTime endDate)
    {
        this.endDate = endDate;
    }

    /**
     * retrieves the appointment's customer id
     * @return
     */
    public int getCustomerId()
    {
        return this.customerId;
    }

    /**
     * modifies the appointment's customer id
     * @param customerId
     */
    public void setCustomerId(final int customerId)
    {
        this.customerId = customerId;
    }

    /**
     * retrieves the appointment's user id
     * @return
     */
    public int getUserId()
    {
        return this.userId;
    }

    /**
     * modifies the appointment's user id
     * @param userId
     */
    public void setUserId(final int userId)
    {
        this.userId = userId;
    }

    /**
     * retrieves the contact name for the appointment
     * @return
     */
    public String getContactName()
    {
        return contactName;
    }

    /**
     * retrieves the contact for the appointment
     * @return
     */
    public Contact getContact()
    {
        return this.contact;
    }

    /**
     * retrieves the user for the appointment
     * @return
     */
    public User getUser() {
        return this.user;
    }

    /**
     * modifies the contact for the appointment
     * @param contact
     */
    public void setContact(final Contact contact)
    {
        this.contact = contact;
    }

    /**
     * retrieves the start date on local time zone as string
     * @return
     */
    public String getStartDateString()
    {
        return startDate.format(formatter);
    }

    /**
     * retrieves the end date on local time zone as string
     * @return
     */
    public String getEndDateString()
    {
        return endDate.format(formatter);
    }

    /**
     * Retrieves the created date of the appointment
     * @return the creation date of the appointment
     */
    public ZonedDateTime getCreateDate()
    {
        return this.createDate;
    }

    /**
     * Modifies the created date of the appointment
     * @param createDate the creation date of the appointment
     */
    public void setCreateDate(final ZonedDateTime createDate)
    {
        this.createDate = createDate;
    }

    /**
     * Retrieves the username of the user that created the appointment
     * @return username of the user who created the appointment
     */
    public String getCreatedBy()
    {
        return this.createdBy;
    }

    /**
     * Modifies who created the appointment
     * @param createdBy the date of appointment modification
     */
    public void setCreatedBy(final String createdBy)
    {
        this.createdBy = createdBy;
    }

    /**
     * Retrieves the last update time of the appointment
     * @return last update time of the appointment
     */
    public ZonedDateTime getLastUpdate()
    {
        return this.lastUpdate;
    }

    /**
     * Modifies the last update time of the appointment
     * @param lastUpdate last update time of the appointment
     */
    public void setLastUpdate(final ZonedDateTime lastUpdate)
    {
        this.lastUpdate = lastUpdate;
    }

    /**
     * Retrieves the username of the user that last modified the appointment
     * @return the username of the user that last modified the appointment
     */
    public String getLastUpdatedBy()
    {
        return this.lastUpdatedBy;
    }

    /**
     * Modifies the user that last updated the appointment
     * @param lastUpdatedBy the username of the user who last updated the appointment
     */
    public void setLastUpdatedBy(final String lastUpdatedBy)
    {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * Method to check if appointment is within business hours EST 8am - 10pm
     * @param newAppointment appointment to be checked
     * @return true if its within business hours otherwise false
     */
    public boolean isAppointmentWithinBusinessHours(Appointment newAppointment) {
        ZonedDateTime startLocalTime = newAppointment.getStartDate();
        ZonedDateTime endLocalTime = newAppointment.getEndDate();
        ZoneId zoneId = ZoneId.systemDefault();

        if (!zoneId.getId().equals("America/New_York")) {
            ZonedDateTime startZonedTime = startLocalTime.withZoneSameInstant(ZoneId.of("America/New_York"));
            ZonedDateTime endZonedTime = endLocalTime.withZoneSameInstant(ZoneId.of("America/New_York"));
            return startZonedTime.getHour() >= 8 && startZonedTime.getHour() <= 22 && endZonedTime.getHour() >=8 && endZonedTime.getHour() <= 22;
        }
        return startLocalTime.getHour() >= 8 && startLocalTime.getHour() <= 22 && endLocalTime.getHour() >=8 && endLocalTime.getHour() <= 22;
    }
}
