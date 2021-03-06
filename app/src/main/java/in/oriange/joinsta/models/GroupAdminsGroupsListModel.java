package in.oriange.joinsta.models;

import java.util.List;

public class GroupAdminsGroupsListModel {


    /**
     * type : success
     * message : Group details returned successfully!
     * result : [{"id":"11","group_name":"new test group","group_code":"0011","group_description":"software developers","is_active":"1","is_deleted":"0","created_by":"6","updated_by":"6","created_at":"2019-10-05 10:46:16","updated_at":"2019-10-05 10:46:16"}]
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
         * id : 11
         * group_name : new test group
         * group_code : 0011
         * group_description : software developers
         * is_active : 1
         * is_deleted : 0
         * created_by : 6
         * updated_by : 6
         * created_at : 2019-10-05 10:46:16
         * updated_at : 2019-10-05 10:46:16
         */

        private String id;
        private String group_name;
        private String group_code;
        private String group_description;
        private String is_active;
        private String is_deleted;
        private String created_by;
        private String updated_by;
        private String created_at;
        private String updated_at;
        private boolean isChecked;

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

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }
}
