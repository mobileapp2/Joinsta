package in.oriange.joinsta.models;

public class PrimaryPublicMobileSelectionModel {

    private String details;
    private String isPrimary;
    private String isPublic;
    private String id;

    public PrimaryPublicMobileSelectionModel(String details, String isPrimary, String isPublic, String id) {
        this.details = details;
        this.isPrimary = isPrimary;
        this.isPublic = isPublic;
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

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
