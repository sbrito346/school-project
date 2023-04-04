package home.appointments;

/**
 * Class the represents an appointment's contact
 */
public class Contact
{
    private int id;
    private String name;
    private String email;

    /**
     * Constructor that create a new contact object
     * @param id contact's unique identifier
     * @param name contact's name
     * @param email contact's email
     */
    public Contact(final int id, final String name, final String email)
    {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    /**
     * Retrieves the contact's id
     * @return
     */
    public int getId()
    {
        return this.id;
    }

    /**
     * Retrieves the contact's name
     * @return
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Retrieves the contact's email
     * @return
     */
    public String getEmail()
    {
        return this.email;
    }
}
