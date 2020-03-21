package in.oriange.joinsta.models;

import java.io.Serializable;
import java.util.List;

public class EventPaidMemberStatusModel implements Serializable {


    /**
     * type : success
     * message : get paid events successfully!
     * result : [{"first_name":"Priyesh Pawar","mobile":"8149115089","image_url":"http://joinsta.in/images/10/uplimg-20190921012605.png","email":"priyeshpawar07@gmail.com","password":"123456","user_id":"10","status":"paid","is_early_bird_availed":"1","payment_mode":"online","quantity":"1","amount":"1","is_joinsta_member":"1"},{"first_name":"Bhokare Sunita","mobile":"8830537273","image_url":"http://joinsta.in/images/12/uplimg-20190610070558.png","email":"sonibhokare@gmail.com","password":"1234","user_id":"12","status":"unpaid","is_early_bird_availed":"0","payment_mode":"","quantity":"0","amount":"0","is_joinsta_member":"1"},{"first_name":"Pandurang Jadhav","mobile":"7588611350","image_url":"http://joinsta.in/images/18/uplimg-20191111073014.png","email":"pandu.jadhav42@gmail.com","password":"1234","user_id":"18","status":"paid","is_early_bird_availed":"0","payment_mode":"pay_at_counter","quantity":"0","amount":"0","is_joinsta_member":"1"},{"first_name":"Machindra","mobile":"9767580040","image_url":"http://joinsta.in/images/29/uplimg-20191012084822.png","email":"machindraams08@gmail.com","password":"12345","user_id":"29","status":"paid","is_early_bird_availed":"0","payment_mode":"offline","quantity":"2","amount":"6","is_joinsta_member":"1"},{"first_name":"Pandurang Jadhav          ","mobile":"9960622138","image_url":"http://joinsta.in/images/18/uplimg-20191111073014.png","email":"ss","password":"1234","user_id":"77","status":"unpaid","is_early_bird_availed":"0","payment_mode":"","quantity":"0","amount":"0","is_joinsta_member":"1"}]
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
         * first_name : Priyesh Pawar
         * mobile : 8149115089
         * image_url : http://joinsta.in/images/10/uplimg-20190921012605.png
         * email : priyeshpawar07@gmail.com
         * password : 123456
         * user_id : 10
         * status : paid
         * is_early_bird_availed : 1
         * payment_mode : online
         * quantity : 1
         * amount : 1
         * is_joinsta_member : 1
         */

        private String first_name;
        private String mobile;
        private String image_url;
        private String email;
        private String password;
        private String user_id;
        private String status;
        private String is_early_bird_availed;
        private String payment_mode;
        private String quantity;
        private String amount;
        private String is_joinsta_member;
        private String is_group_member;

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

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getIs_early_bird_availed() {
            return is_early_bird_availed;
        }

        public void setIs_early_bird_availed(String is_early_bird_availed) {
            this.is_early_bird_availed = is_early_bird_availed;
        }

        public String getPayment_mode() {
            return payment_mode;
        }

        public void setPayment_mode(String payment_mode) {
            this.payment_mode = payment_mode;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getIs_joinsta_member() {
            return is_joinsta_member;
        }

        public void setIs_joinsta_member(String is_joinsta_member) {
            this.is_joinsta_member = is_joinsta_member;
        }

        public String getIs_group_member() {
            return is_group_member;
        }

        public void setIs_group_member(String is_group_member) {
            this.is_group_member = is_group_member;
        }
    }
}
