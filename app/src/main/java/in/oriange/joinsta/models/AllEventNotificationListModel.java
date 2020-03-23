package in.oriange.joinsta.models;

import java.util.List;

public class AllEventNotificationListModel {

    /**
     * type : success
     * message : Notification details returned successfully!
     * result : [{"event_id":"32","event_name":"sarika kate","event_code":"0032","event_description":"Test","event_member_details":[{"event_not_details_id":"5","can_share":"1","id":"5","subject":"Hii","message":"Plz pay","send_by":"512","is_deleted":"0","created_at":"2020-03-21 10:25:52","event_id":"32","user_id":"512","sender_name":"test","event_name":"sarika kate","event_notification_id":"8","is_read":"1","is_fav":"1"},{"event_not_details_id":"4","can_share":"1","id":"4","subject":"Hi","message":"Plz pay","send_by":"512","is_deleted":"0","created_at":"2020-03-20 21:01:21","event_id":"32","user_id":"512","sender_name":"test","event_name":"sarika kate","event_notification_id":"7","is_read":"0","is_fav":"0"},{"event_not_details_id":"1","can_share":"1","id":"1","subject":"Hi","message":"Plz pay","send_by":"512","is_deleted":"0","created_at":"2020-03-20 19:06:51","event_id":"32","user_id":"512","sender_name":"test","event_name":"sarika kate","event_notification_id":"2","is_read":"0","is_fav":"0"}]},{"event_id":"32","event_name":"sarika kate","event_code":"0032","event_description":"Test","event_member_details":[{"event_not_details_id":"5","can_share":"1","id":"5","subject":"Hii","message":"Plz pay","send_by":"512","is_deleted":"0","created_at":"2020-03-21 10:25:52","event_id":"32","user_id":"512","sender_name":"test","event_name":"sarika kate","event_notification_id":"8","is_read":"1","is_fav":"1"},{"event_not_details_id":"4","can_share":"1","id":"4","subject":"Hi","message":"Plz pay","send_by":"512","is_deleted":"0","created_at":"2020-03-20 21:01:21","event_id":"32","user_id":"512","sender_name":"test","event_name":"sarika kate","event_notification_id":"7","is_read":"0","is_fav":"0"},{"event_not_details_id":"1","can_share":"1","id":"1","subject":"Hi","message":"Plz pay","send_by":"512","is_deleted":"0","created_at":"2020-03-20 19:06:51","event_id":"32","user_id":"512","sender_name":"test","event_name":"sarika kate","event_notification_id":"2","is_read":"0","is_fav":"0"}]}]
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
         * event_id : 32
         * event_name : sarika kate
         * event_code : 0032
         * event_description : Test
         * event_member_details : [{"event_not_details_id":"5","can_share":"1","id":"5","subject":"Hii","message":"Plz pay","send_by":"512","is_deleted":"0","created_at":"2020-03-21 10:25:52","event_id":"32","user_id":"512","sender_name":"test","event_name":"sarika kate","event_notification_id":"8","is_read":"1","is_fav":"1"},{"event_not_details_id":"4","can_share":"1","id":"4","subject":"Hi","message":"Plz pay","send_by":"512","is_deleted":"0","created_at":"2020-03-20 21:01:21","event_id":"32","user_id":"512","sender_name":"test","event_name":"sarika kate","event_notification_id":"7","is_read":"0","is_fav":"0"},{"event_not_details_id":"1","can_share":"1","id":"1","subject":"Hi","message":"Plz pay","send_by":"512","is_deleted":"0","created_at":"2020-03-20 19:06:51","event_id":"32","user_id":"512","sender_name":"test","event_name":"sarika kate","event_notification_id":"2","is_read":"0","is_fav":"0"}]
         */

        private String event_id;
        private String event_name;
        private String event_code;
        private String event_description;
        private List<EventMemberDetailsBean> event_member_details;

        public String getEvent_id() {
            return event_id;
        }

        public void setEvent_id(String event_id) {
            this.event_id = event_id;
        }

        public String getEvent_name() {
            return event_name;
        }

        public void setEvent_name(String event_name) {
            this.event_name = event_name;
        }

        public String getEvent_code() {
            return event_code;
        }

        public void setEvent_code(String event_code) {
            this.event_code = event_code;
        }

        public String getEvent_description() {
            return event_description;
        }

        public void setEvent_description(String event_description) {
            this.event_description = event_description;
        }

        public List<EventMemberDetailsBean> getEvent_member_details() {
            return event_member_details;
        }

        public void setEvent_member_details(List<EventMemberDetailsBean> event_member_details) {
            this.event_member_details = event_member_details;
        }

        public static class EventMemberDetailsBean {
            /**
             * event_not_details_id : 5
             * can_share : 1
             * id : 5
             * subject : Hii
             * message : Plz pay
             * send_by : 512
             * is_deleted : 0
             * created_at : 2020-03-21 10:25:52
             * event_id : 32
             * user_id : 512
             * sender_name : test
             * event_name : sarika kate
             * event_notification_id : 8
             * is_read : 1
             * is_fav : 1
             */

            private String event_not_details_id;
            private String can_share;
            private String id;
            private String subject;
            private String message;
            private String send_by;
            private String is_deleted;
            private String created_at;
            private String event_id;
            private String user_id;
            private String sender_name;
            private String event_name;
            private String event_notification_id;
            private String is_read;
            private String is_fav;

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

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
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

            public String getSend_by() {
                return send_by;
            }

            public void setSend_by(String send_by) {
                this.send_by = send_by;
            }

            public String getIs_deleted() {
                return is_deleted;
            }

            public void setIs_deleted(String is_deleted) {
                this.is_deleted = is_deleted;
            }

            public String getCreated_at() {
                return created_at;
            }

            public void setCreated_at(String created_at) {
                this.created_at = created_at;
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

            public String getSender_name() {
                return sender_name;
            }

            public void setSender_name(String sender_name) {
                this.sender_name = sender_name;
            }

            public String getEvent_name() {
                return event_name;
            }

            public void setEvent_name(String event_name) {
                this.event_name = event_name;
            }

            public String getEvent_notification_id() {
                return event_notification_id;
            }

            public void setEvent_notification_id(String event_notification_id) {
                this.event_notification_id = event_notification_id;
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
}
