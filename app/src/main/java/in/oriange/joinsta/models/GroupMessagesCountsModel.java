package in.oriange.joinsta.models;

public class GroupMessagesCountsModel {


    /**
     * type : success
     * message : Message count returned successfully!
     * result : {"smscount":10,"whatsappcount":10,"emailcount":0,"notificationcount":3}
     */

    private String type;
    private String message;
    private ResultBean result;

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

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * smscount : 10
         * whatsappcount : 10
         * emailcount : 0
         * notificationcount : 3
         */

        private String smscount;
        private String whatsappcount;
        private String emailcount;
        private String notificationcount;

        public String getSmscount() {
            return smscount;
        }

        public void setSmscount(String smscount) {
            this.smscount = smscount;
        }

        public String getWhatsappcount() {
            return whatsappcount;
        }

        public void setWhatsappcount(String whatsappcount) {
            this.whatsappcount = whatsappcount;
        }

        public String getEmailcount() {
            return emailcount;
        }

        public void setEmailcount(String emailcount) {
            this.emailcount = emailcount;
        }

        public String getNotificationcount() {
            return notificationcount;
        }

        public void setNotificationcount(String notificationcount) {
            this.notificationcount = notificationcount;
        }
    }
}
