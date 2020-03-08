package in.oriange.joinsta.models;

import java.util.List;

public class MemberCategoryModel {


    /**
     * type : success
     * message : Category return successfully!
     * result : [{"id":"1","member_category":"demo","is_active":"1","is_deleted":"1","created_by":"271","created_at":"2020-03-06 18:00:08","updated_by":"271","updated_at":"2020-03-06 18:00:08"},{"id":"3","member_category":"demo2","is_active":"1","is_deleted":"0","created_by":"271","created_at":"2020-03-08 23:47:23","updated_by":"271","updated_at":"2020-03-08 23:47:23"}]
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
         * id : 1
         * member_category : demo
         * is_active : 1
         * is_deleted : 1
         * created_by : 271
         * created_at : 2020-03-06 18:00:08
         * updated_by : 271
         * updated_at : 2020-03-06 18:00:08
         */

        private String id;
        private String member_category;
        private String is_active;
        private String is_deleted;
        private String created_by;
        private String created_at;
        private String updated_by;
        private String updated_at;
        private boolean isChecked;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMember_category() {
            return member_category;
        }

        public void setMember_category(String member_category) {
            this.member_category = member_category;
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

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_by() {
            return updated_by;
        }

        public void setUpdated_by(String updated_by) {
            this.updated_by = updated_by;
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
