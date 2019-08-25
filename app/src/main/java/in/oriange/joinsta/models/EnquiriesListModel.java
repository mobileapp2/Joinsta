package in.oriange.joinsta.models;

import java.util.ArrayList;

public class EnquiriesListModel {
    
    private String type;
    private String message;
    private ArrayList<ResultBean> result;

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

    public ArrayList<ResultBean> getResult() {
        return result;
    }

    public void setResult(ArrayList<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        
        private String id;
        private String name;
        private String mobile;
        private String email;
        private String subject;
        private String message;
        private String category_type_id;
        private String created_by;
        private String created_at;
        private String is_attended;
        private String attended_by;
        private String is_delete;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getCategory_type_id() {
            return category_type_id;
        }

        public void setCategory_type_id(String category_type_id) {
            this.category_type_id = category_type_id;
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

        public String getIs_attended() {
            return is_attended;
        }

        public void setIs_attended(String is_attended) {
            this.is_attended = is_attended;
        }

        public String getAttended_by() {
            return attended_by;
        }

        public void setAttended_by(String attended_by) {
            this.attended_by = attended_by;
        }

        public String getIs_delete() {
            return is_delete;
        }

        public void setIs_delete(String is_delete) {
            this.is_delete = is_delete;
        }
    }
}
