package in.oriange.joinsta.models;

public class PrimarySelectionModel {

    private String details;
    private String isPrimary;
    private String id;

    public PrimarySelectionModel(String details, String isPrimary, String id) {
        this.details = details;
        this.isPrimary = isPrimary;
        this.id = id;
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
}
