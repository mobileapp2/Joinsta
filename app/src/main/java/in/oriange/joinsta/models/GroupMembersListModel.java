package in.oriange.joinsta.models;

import java.io.Serializable;
import java.util.List;

public class GroupMembersListModel implements Serializable {


    /**
     * type : success
     * message : Group member details returned successfully!
     * result : [{"first_name":"Nikita  ","mobile":"9860392913","email":null,"is_active":"0","id":"45"},{"first_name":"saba bagwan","mobile":"8180012531","email":"sababagwan01@gmail.com","is_active":"0","id":"163"},{"first_name":"Test Group Memeber","mobile":"8878765434","email":"","is_active":"0","id":"162"},{"first_name":"Varsharani","mobile":"8275460300","email":"varsha@oriange.in","is_active":"0","id":"160"}]
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

    public static class ResultBean implements Serializable {
        /**
         * first_name : Nikita
         * mobile : 9860392913
         * email : null
         * is_active : 0
         * id : 45
         */

        private String first_name;
        private String mobile;
        private String email;
        private String is_active;
        private String id;

        public String getFirst_name() {
            if (first_name != null) {
                return first_name;
            } else {
                return "";
            }
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getMobile() {
            if (mobile != null) {
                return mobile;
            } else {
                return "";
            }
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

        public String getIs_active() {
            if (is_active != null) {
                return is_active;
            } else {
                return "";
            }
        }

        public void setIs_active(String is_active) {
            this.is_active = is_active;
        }

        public String getId() {
            if (id != null) {
                return id;
            } else {
                return "";
            }
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
