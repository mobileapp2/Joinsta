package in.oriange.joinsta.models;

public class PrimaryPublicMobileSelectionModel {

    private String country_code;
    private String mobile;
    private String isPrimary;
    private String isPublic;
    private String id;

    public PrimaryPublicMobileSelectionModel(String country_code, String mobile, String isPrimary, String isPublic, String id) {
        this.country_code = country_code;
        this.mobile = mobile;
        this.isPrimary = isPrimary;
        this.isPublic = isPublic;
        this.id = id;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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
