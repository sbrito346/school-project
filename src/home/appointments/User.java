package home.appointments;

/**
 * Class that represents an appointment's user
 */
public class User
{
    private int id;
    private String username;
    private String password;

    /**
     * Constructor to initialize a user's object
     * @param id user id
     * @param username username of user
     * @param password password of user
     */
    public User(final int id, final String username, final String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    /**
     * retrieves the user's id
     * @return user id
     */
    public int getId()
    {
        return this.id;
    }

    /**
     * retrieves the username of user
     * @return username
     */
    public String getUsername()
    {
        return this.username;
    }

    /**
     * retrieves the password of the user
     * @return password of user
     */
    public String getPassword()
    {
        return this.password;
    }
}
