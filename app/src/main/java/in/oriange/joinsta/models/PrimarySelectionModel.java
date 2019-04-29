package in.oriange.joinsta.models;

public class PrimarySelectionModel {

    private String details;
    private String isPrimary;

    public PrimarySelectionModel(String details, String isPrimary) {
        this.details = details;
        this.isPrimary = isPrimary;
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
}
