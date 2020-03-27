package in.oriange.joinsta.models;

import java.util.List;

public class BizProfEmpProfileDesignationsModel {

    /**
     * type : success
     * message : get designation successfully!
     * result : [{"id":"1","designation_name":"employee","is_applicable_to_business":"1","is_applicable_to_employee":"1","is_applicable_to_professional":"1","created_by":"1","updated_by":"1","created_at":"2020-03-24 03:00:00","updated_at":"2020-03-24 02:03:02"},{"id":"3","designation_name":"Project Manager","is_applicable_to_business":"1","is_applicable_to_employee":"1","is_applicable_to_professional":"1","created_by":"1","updated_by":"1","created_at":"2020-03-27 10:15:07","updated_at":"2020-03-27 10:15:38"}]
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
         * designation_name : employee
         * is_applicable_to_business : 1
         * is_applicable_to_employee : 1
         * is_applicable_to_professional : 1
         * created_by : 1
         * updated_by : 1
         * created_at : 2020-03-24 03:00:00
         * updated_at : 2020-03-24 02:03:02
         */

        private String id;
        private String designation_name;
        private String is_applicable_to_business;
        private String is_applicable_to_employee;
        private String is_applicable_to_professional;
        private String created_by;
        private String updated_by;
        private String created_at;
        private String updated_at;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDesignation_name() {
            return designation_name;
        }

        public void setDesignation_name(String designation_name) {
            this.designation_name = designation_name;
        }

        public String getIs_applicable_to_business() {
            return is_applicable_to_business;
        }

        public void setIs_applicable_to_business(String is_applicable_to_business) {
            this.is_applicable_to_business = is_applicable_to_business;
        }

        public String getIs_applicable_to_employee() {
            return is_applicable_to_employee;
        }

        public void setIs_applicable_to_employee(String is_applicable_to_employee) {
            this.is_applicable_to_employee = is_applicable_to_employee;
        }

        public String getIs_applicable_to_professional() {
            return is_applicable_to_professional;
        }

        public void setIs_applicable_to_professional(String is_applicable_to_professional) {
            this.is_applicable_to_professional = is_applicable_to_professional;
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
}
