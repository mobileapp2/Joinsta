package in.oriange.joinsta.models;

public class DUMMYGroupMembers {

    private String name;
    private String mobile;
    private String email;
    private String isActive;

    public DUMMYGroupMembers(String name, String mobile, String email, String isActive) {
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.isActive = isActive;
    }

    public DUMMYGroupMembers() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }
}
