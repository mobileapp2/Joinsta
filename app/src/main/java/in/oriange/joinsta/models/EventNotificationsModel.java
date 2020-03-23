package in.oriange.joinsta.models;

import java.util.List;

public class EventNotificationsModel {

    /**
     * type : success
     * message : Notification details returned successfully!
     * result : [{"event_notification_id":"8","event_not_details_id":"5","can_share":"1","sender_name":"test","event_id":"32","user_id":"512","is_read":"0","is_fav":"0"},{"event_notification_id":"7","event_not_details_id":"4","can_share":"1","sender_name":"test","event_id":"32","user_id":"512","is_read":"0","is_fav":"0"},{"event_notification_id":"2","event_not_details_id":"1","can_share":"1","sender_name":"test","event_id":"32","user_id":"512","is_read":"0","is_fav":"0"}]
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
         * event_notification_id : 8
         * event_not_details_id : 5
         * can_share : 1
         * sender_name : test
         * event_id : 32
         * user_id : 512
         * is_read : 0
         * is_fav : 0
         */

        private String event_notification_id;
        private String event_not_details_id;
        private String can_share;
        private String sender_name;
        private String created_at;
        private String subject;
        private String message;
        private String event_id;
        private String user_id;
        private String is_read;
        private String is_fav;

        public String getEvent_notification_id() {
            return event_notification_id;
        }

        public void setEvent_notification_id(String event_notification_id) {
            this.event_notification_id = event_notification_id;
        }

        public String getEvent_not_details_id() {
            return event_not_details_id;
        }

        public void setEvent_not_details_id(String event_not_details_id) {
            this.event_not_details_id = event_not_details_id;
        }

        public String getCan_share() {
            return can_share;
        }

        public void setCan_share(String can_share) {
            this.can_share = can_share;
        }

        public String getSender_name() {
            return sender_name;
        }

        public void setSender_name(String sender_name) {
            this.sender_name = sender_name;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
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

        public String getIs_read() {
            return is_read;
        }

        public void setIs_read(String is_read) {
            this.is_read = is_read;
        }

        public String getIs_fav() {
            return is_fav;
        }

        public void setIs_fav(String is_fav) {
            this.is_fav = is_fav;
        }
    }
}
