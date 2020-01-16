package in.oriange.joinsta.models;

import java.io.Serializable;

public class MutualGroupsModel implements Serializable {

    private String id;
    private String group_name;
    private String group_code;
    private String group_description;
    private String is_active;
    private String is_visible;
    private String is_public_group;
    private String is_members_post;
    private String is_deleted;
    private String created_by;
    private String updated_by;
    private String created_at;
    private String updated_at;

    public MutualGroupsModel() {
    }

    public MutualGroupsModel(String id, String group_name, String group_code) {
        this.id = id;
        this.group_name = group_name;
        this.group_code = group_code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_code() {
        return group_code;
    }

    public void setGroup_code(String group_code) {
        this.group_code = group_code;
    }

    public String getGroup_description() {
        return group_description;
    }

    public void setGroup_description(String group_description) {
        this.group_description = group_description;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    public String getIs_visible() {
        return is_visible;
    }

    public void setIs_visible(String is_visible) {
        this.is_visible = is_visible;
    }

    public String getIs_public_group() {
        return is_public_group;
    }

    public void setIs_public_group(String is_public_group) {
        this.is_public_group = is_public_group;
    }

    public String getIs_members_post() {
        return is_members_post;
    }

    public void setIs_members_post(String is_members_post) {
        this.is_members_post = is_members_post;
    }

    public String getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(String is_deleted) {
        this.is_deleted = is_deleted;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
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

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
