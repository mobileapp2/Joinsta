package in.oriange.joinsta.models;

import java.util.ArrayList;

public class EnquiriesListModel {
    
    /**
     * type : success
     * message : get enquiry successfully!
     * result : [{"id":"1","name":"sarika","mobile":"8006570089","email":"mk@gmail.com","subject":"enquiry","message":"its for profession","category_type_id":"3","record_id":"11","created_by":"1","created_at":"2019-08-01 00:00:00","is_attended":"1","attended_by":"rahika mam","is_delete":"0","organization_name":"","business_name":"Electronic Shop111111","profession_name":""},{"id":"5","name":"mansi","mobile":"90778667565","email":"sk@gmail.com","subject":"grbgr","message":"nytyrr","category_type_id":"1","record_id":"12","created_by":"74","created_at":"2019-08-20 00:00:00","is_attended":"1","attended_by":"malhar","is_delete":"0","organization_name":"Employee111","business_name":"","profession_name":""},{"id":"10","name":"namrata","mobile":"8830648438","email":"namrata@gmail.com","subject":"enquiry","message":"stydfcyu jdyfyu","category_type_id":"1","record_id":"17","created_by":"73","created_at":"2019-08-22 15:59:56","is_attended":"0","attended_by":"test","is_delete":"0","organization_name":"SOS BALGRA","business_name":"","profession_name":""},{"id":"17","name":"namrata","mobile":"8830648438","email":"namrata@gmail.com","subject":"enquiry","message":"stydfcyu jdyfyu","category_type_id":"3","record_id":"11","created_by":"1","created_at":"2019-08-29 13:04:42","is_attended":"0","attended_by":"","is_delete":"0","organization_name":"","business_name":"Electronic Shop111111","profession_name":""}]
     */

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
        /**
         * id : 1
         * name : sarika
         * mobile : 8006570089
         * email : mk@gmail.com
         * subject : enquiry
         * message : its for profession
         * category_type_id : 3
         * record_id : 11
         * created_by : 1
         * created_at : 2019-08-01 00:00:00
         * is_attended : 1
         * attended_by : rahika mam
         * is_delete : 0
         * organization_name : 
         * business_name : Electronic Shop111111
         * profession_name : 
         */

        private String id;
        private String name;
        private String mobile;
        private String email;
        private String subject;
        private String message;
        private String category_type_id;
        private String record_id;
        private String communication_mode;
        private String created_by;
        private String created_at;
        private String is_attended;
        private String attended_by;
        private String is_delete;
        private String organization_name;
        private String business_name;
        private String profession_name;

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

        public String getRecord_id() {
            return record_id;
        }

        public void setRecord_id(String record_id) {
            this.record_id = record_id;
        }

        public String getCommunication_mode() {
            return communication_mode;
        }

        public void setCommunication_mode(String communication_mode) {
            this.communication_mode = communication_mode;
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

        public String getOrganization_name() {
            return organization_name;
        }

        public void setOrganization_name(String organization_name) {
            this.organization_name = organization_name;
        }

        public String getBusiness_name() {
            return business_name;
        }

        public void setBusiness_name(String business_name) {
            this.business_name = business_name;
        }

        public String getProfession_name() {
            return profession_name;
        }

        public void setProfession_name(String profession_name) {
            this.profession_name = profession_name;
        }
    }
}
