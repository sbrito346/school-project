package home.customer;

/**
 * Class to represent a country
 */
public class Country {
    private int countryId;
    private String countryName;

    /**
     * Constructor to instantiate a country's object
     * @param countryId country id
     * @param countryName country name
     */
    public Country(final int countryId, final String countryName) {
        this.countryId = countryId;
        this.countryName = countryName;
    }

    /**
     * Retrieves the country id
     * @return the country id
     */
    public int getCountryId() {
        return this.countryId;
    }

    /**
     * Retrieves the country name
     * @return the country name
     */
    public String getCountryName() {
        return this.countryName;
    }

    /**
     * Modifies the country name
     * @param countryName the country name
     */
    public void setCountryName(final String countryName) {
        this.countryName = countryName;
    }
}
