package home.customer;

/**
 * Class that represents a location's division
 */
public class Division {
    private int divisionId;
    private String divisionName;
    private Country country;

    /**
     * Constructor to initialize a division object
     * @param divisionId
     * @param divisionName
     */
    public Division(final int divisionId, final String divisionName, final Country country) {
        this.divisionId = divisionId;
        this.divisionName = divisionName;
        this.country = country;
    }

    /**
     * Retrieves the division identifier
     * @return the division id
     */
    public int getDivisionId() {
        return this.divisionId;
    }

    /**
     * Retrieves the division name
     * @return division name
     */
    public String getDivisionName() {
        return this.divisionName;
    }

    /**
     * Modifies the division name
     * @param divisionName division name to modify
     */
    public void setDivisionName(final String divisionName) {
        this.divisionName = divisionName;
    }

    /**
     * Retrieves the country associated with the division
     * @return country object
     */
    public Country getCountry()
    {
        return this.country;
    }

    /**
     * Modifies the country associated with the division
     * @param country country object to modify
     */
    public void setCountry(final Country country)
    {
        this.country = country;
    }
}
