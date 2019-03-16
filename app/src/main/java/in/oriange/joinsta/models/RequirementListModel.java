package in.oriange.joinsta.models;

public class RequirementListModel {

    private String requirement;
    private String name;
    private String time;
    private String location;

    public RequirementListModel(String requirement, String name, String time, String location) {
        this.requirement = requirement;
        this.name = name;
        this.time = time;
        this.location = location;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
