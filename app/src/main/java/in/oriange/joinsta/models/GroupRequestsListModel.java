package in.oriange.joinsta.models;

import java.util.List;

public class GroupRequestsListModel {

    /**
     * type : success
     * message : Requested Groups returned successfully!
     * result : [{"id":"92","group_name":"sasidhar physics classes","group_code":"0092","group_description":"sasidhar physics classes group","is_active":"1","is_visible":"1","is_public_group":"1","is_members_post":"1","is_deleted":"0","created_by":"1","updated_by":"1","created_at":"2019-10-25 13:25:47","updated_at":"2019-12-13 13:19:26"}]
     */

    private String type;
    private String message;
    private List<ResultBean> result;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * id : 92
         * group_name : sasidhar physics classes
         * group_code : 0092
         * group_description : sasidhar physics classes group
         * is_active : 1
         * is_visible : 1
         * is_public_group : 1
         * is_members_post : 1
         * is_deleted : 0
         * created_by : 1
         * updated_by : 1
         * created_at : 2019-10-25 13:25:47
         * updated_at : 2019-12-13 13:19:26
         */

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
        private String sender_name;

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

        public String getSender_name() {
            return sender_name;
        }

        public void setSender_name(String sender_name) {
            this.sender_name = sender_name;
        }
    }
}
