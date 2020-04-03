package in.oriange.joinsta.models;

public class PrimarySelectionModel {

    private String details;
    private String isPrimary;
    private String id;
    private String country_code;

    public PrimarySelectionModel(String details, String isPrimary, String id, String country_code) {
        this.details = details;
        this.isPrimary = isPrimary;
        this.id = id;
        this.country_code = country_code;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(String isPrimary) {
        this.isPrimary = isPrimary;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }
}
