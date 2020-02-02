package in.oriange.joinsta.models;

import java.util.List;

public class GroupPaymentAccountModel {


    /**
     * type : success
     * message : get payment account details successfully!
     * result : [{"nick_alias_name":"new details ","account_holder_name":"wait ","ifsc_code":"875764586456","bank_name":"SBI","account_number":"987654321234","user_id":"512","payment_account_id":"4"}]
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
         * nick_alias_name : new details
         * account_holder_name : wait
         * ifsc_code : 875764586456
         * bank_name : SBI
         * account_number : 987654321234
         * user_id : 512
         * payment_account_id : 4
         */

        private String nick_alias_name;
        private String account_holder_name;
        private String ifsc_code;
        private String bank_name;
        private String account_number;
        private String user_id;
        private String payment_account_id;

        public String getNick_alias_name() {
            return nick_alias_name;
        }

        public void setNick_alias_name(String nick_alias_name) {
            this.nick_alias_name = nick_alias_name;
        }

        public String getAccount_holder_name() {
            return account_holder_name;
        }

        public void setAccount_holder_name(String account_holder_name) {
            this.account_holder_name = account_holder_name;
        }

        public String getIfsc_code() {
            return ifsc_code;
        }

        public void setIfsc_code(String ifsc_code) {
            this.ifsc_code = ifsc_code;
        }

        public String getBank_name() {
            return bank_name;
        }

        public void setBank_name(String bank_name) {
            this.bank_name = bank_name;
        }

        public String getAccount_number() {
            return account_number;
        }

        public void setAccount_number(String account_number) {
            this.account_number = account_number;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getPayment_account_id() {
            return payment_account_id;
        }

        public void setPayment_account_id(String payment_account_id) {
            this.payment_account_id = payment_account_id;
        }
    }
}
