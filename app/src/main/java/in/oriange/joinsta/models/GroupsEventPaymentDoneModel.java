package in.oriange.joinsta.models;

import java.util.List;

public class GroupsEventPaymentDoneModel {


    /**
     * type : success
     * message : Event returns successfully!
     * result : [[{"id":"1","event_id":"15","user_id":"271","payment_mode":"online","gateway_configuration_id":"0","transaction_id":"2147483647","transaction_date":"2020-02-18","paid_to":"","quantity":"1","created_by":"271","updated_by":"271","created_at":"2020-02-18 20:47:59","updated_at":"2020-02-18 20:47:59"}]]
     */

    private String type;
    private String message;
    private List<List<ResultBean>> result;

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

    public List<List<ResultBean>> getResult() {
        return result;
    }

    public void setResult(List<List<ResultBean>> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * id : 1
         * event_id : 15
         * user_id : 271
         * payment_mode : online
         * gateway_configuration_id : 0
         * transaction_id : 2147483647
         * transaction_date : 2020-02-18
         * paid_to :
         * quantity : 1
         * created_by : 271
         * updated_by : 271
         * created_at : 2020-02-18 20:47:59
         * updated_at : 2020-02-18 20:47:59
         */

        private String id;
        private String event_id;
        private String user_id;
        private String payment_mode;
        private String gateway_configuration_id;
        private String transaction_id;
        private String transaction_date;
        private String paid_to;
        private String quantity;
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

        public String getEvent_id() {
            return event_id;
        }

        public void setEvent_id(String event_id) {
            this.event_id = event_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getPayment_mode() {
            return payment_mode;
        }

        public void setPayment_mode(String payment_mode) {
            this.payment_mode = payment_mode;
        }

        public String getGateway_configuration_id() {
            return gateway_configuration_id;
        }

        public void setGateway_configuration_id(String gateway_configuration_id) {
            this.gateway_configuration_id = gateway_configuration_id;
        }

        public String getTransaction_id() {
            return transaction_id;
        }

        public void setTransaction_id(String transaction_id) {
            this.transaction_id = transaction_id;
        }

        public String getTransaction_date() {
            return transaction_date;
        }

        public void setTransaction_date(String transaction_date) {
            this.transaction_date = transaction_date;
        }

        public String getPaid_to() {
            return paid_to;
        }

        public void setPaid_to(String paid_to) {
            this.paid_to = paid_to;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
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
