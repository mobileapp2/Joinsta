package in.oriange.joinsta.models;

import java.util.List;

public class EventMemberStatusModel {

    /**
     * type : success
     * message : get paid events successfully!
     * result : [{"first_name":"Priyesh Pawar","mobile":"8149115089","email":"priyeshpawar07@gmail.com","user_id":"10","status":"unpaid"},{"first_name":"Saba","mobile":"8180012531","email":"ss@gmail.com","user_id":"271","status":"paid"},{"first_name":"Bhokare Sunita","mobile":"8830537273","email":"sonibhokare@gmail.com","user_id":"12","status":"unpaid"},{"first_name":"Mita","mobile":"8007140035","email":"sarukate100@gmail.com","user_id":"1745","status":"unpaid"},{"first_name":"First","mobile":"8536974254","email":"sak@","user_id":"1957","status":"unpaid"},{"first_name":"Jadhav","mobile":"9765065522","email":null,"user_id":"194","status":"unpaid"},{"first_name":"Bhokare Sunita","mobile":"9175326801","email":null,"user_id":"335","status":"unpaid"}]
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
         * first_name : Priyesh Pawar
         * mobile : 8149115089
         * email : priyeshpawar07@gmail.com
         * user_id : 10
         * status : unpaid
         */

        private String first_name;
        private String mobile;
        private String email;
        private String user_id;
        private String status;

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getEmail() {
            if (email != null) {
                return email;
            } else {
                return "";
            }
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getStatus() {
            if (status != null) {
                return status.trim();
            } else {
                return "";
            }
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
