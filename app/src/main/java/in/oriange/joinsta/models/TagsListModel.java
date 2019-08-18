package in.oriange.joinsta.models;

public class TagsListModel {

    private String tag_id;
    private String tag_name;
    private String is_approved;

    public TagsListModel() {
    }

    public TagsListModel(String tag_id, String tag_name, String is_approved) {
        this.tag_id = tag_id;
        this.tag_name = tag_name;
        this.is_approved = is_approved;
    }

    public String getTag_id() {
        return tag_id;
    }

    public void setTag_id(String tag_id) {
        this.tag_id = tag_id;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    public String getIs_approved() {
        return is_approved;
    }

    public void setIs_approved(String is_approved) {
        this.is_approved = is_approved;
    }
}
