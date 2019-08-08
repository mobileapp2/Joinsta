package in.oriange.joinsta.models;

public class CategotyListModel {

    private String updated_at;

    private String level;

    private String parent_id;

    private String name;

    private String updated_by;

    private String created_at;

    private String id;

    private String category_typeid;

    private String created_by;

    private String category_icon;

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory_typeid() {
        return category_typeid;
    }

    public void setCategory_typeid(String category_typeid) {
        this.category_typeid = category_typeid;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getCategory_icon() {
        if (category_icon == null) {
            return "";
        } else {
            return category_icon;
        }
    }

    public void setCategory_icon(String category_icon) {
        this.category_icon = category_icon;
    }
}
