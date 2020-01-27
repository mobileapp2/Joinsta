package in.oriange.joinsta.models;

import java.io.Serializable;
import java.util.List;

public class EventsPaidModel implements Serializable {


    /**
     * type : success
     * message : get paid event successfully!
     * result : [{"id":"2","event_code":"","event_type_id":"2","group_id":"88","name":"third event","description":"test","event_date":"2012-02-20","event_start_time":"00:00:12","event_end_time":"00:00:30","venue_address":"latur","venue_longitude":"12.2","venue_latitude":"23.43","message_for_paidmember":"plz pay bill","message_for_unpaidmember":"plz paid","is_offline_payments_allowed":"1","is_online_events":"0","is_displaytomembers":"1","event_city":"latur","display_in_city":"0","payment_account_id":"45","remark":"1","earlybird_price":"200","normal_price":"122","earlybird_price_duedate":"0000-00-00","normal_price_duedate":"0000-00-00","created_by":"512","updated_by":"512","created_at":"2020-01-20 15:39:15","updated_at":"2020-01-20 15:39:15"},{"id":"5","event_code":"","event_type_id":"5","group_id":"88","name":"final event","description":"paid events","event_date":"2012-02-20","event_start_time":"04:00:00","event_end_time":"06:00:00","venue_address":"latur","venue_longitude":"3.4","venue_latitude":"90.9","message_for_paidmember":"thanks","message_for_unpaidmember":"plz pay bill","is_offline_payments_allowed":"1","is_online_events":"1","is_displaytomembers":"1","event_city":"pune","display_in_city":"0","payment_account_id":"40","remark":"1","earlybird_price":"10","normal_price":"20","earlybird_price_duedate":"0003-04-20","normal_price_duedate":"2010-04-20","created_by":"512","updated_by":"512","created_at":"2020-01-21 13:50:52","updated_at":"2020-01-21 13:50:52"},{"id":"4","event_code":"","event_type_id":"5","group_id":"88","name":"final event","description":"paid events","event_date":"2012-02-20","event_start_time":"04:00:00","event_end_time":"06:00:00","venue_address":"latur","venue_longitude":"3.4","venue_latitude":"90.9","message_for_paidmember":"thanks","message_for_unpaidmember":"plz pay bill","is_offline_payments_allowed":"1","is_online_events":"1","is_displaytomembers":"1","event_city":"pune","display_in_city":"0","payment_account_id":"40","remark":"1","earlybird_price":"10","normal_price":"20","earlybird_price_duedate":"0003-04-20","normal_price_duedate":"2010-04-20","created_by":"512","updated_by":"512","created_at":"2020-01-20 15:50:43","updated_at":"2020-01-20 15:50:43"}]
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

    public static class ResultBean implements Serializable{
        /**
         * id : 2
         * event_code :
         * event_type_id : 2
         * group_id : 88
         * name : third event
         * description : test
         * event_date : 2012-02-20
         * event_start_time : 00:00:12
         * event_end_time : 00:00:30
         * venue_address : latur
         * venue_longitude : 12.2
         * venue_latitude : 23.43
         * message_for_paidmember : plz pay bill
         * message_for_unpaidmember : plz paid
         * is_offline_payments_allowed : 1
         * is_online_events : 0
         * is_displaytomembers : 1
         * event_city : latur
         * display_in_city : 0
         * payment_account_id : 45
         * remark : 1
         * earlybird_price : 200
         * normal_price : 122
         * earlybird_price_duedate : 0000-00-00
         * normal_price_duedate : 0000-00-00
         * created_by : 512
         * updated_by : 512
         * created_at : 2020-01-20 15:39:15
         * updated_at : 2020-01-20 15:39:15
         */

        private String id;
        private String event_code;
        private String event_type_id;
        private String group_id;
        private String name;
        private String description;
        private String event_date;
        private String event_start_time;
        private String event_end_time;
        private String venue_address;
        private String venue_longitude;
        private String venue_latitude;
        private String message_for_paidmember;
        private String message_for_unpaidmember;
        private String is_offline_payments_allowed;
        private String is_online_events;
        private String is_displaytomembers;
        private String event_city;
        private String display_in_city;
        private String payment_account_id;
        private String remark;
        private String earlybird_price;
        private String normal_price;
        private String earlybird_price_duedate;
        private String normal_price_duedate;
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

        public String getEvent_code() {
            return event_code;
        }

        public void setEvent_code(String event_code) {
            this.event_code = event_code;
        }

        public String getEvent_type_id() {
            return event_type_id;
        }

        public void setEvent_type_id(String event_type_id) {
            this.event_type_id = event_type_id;
        }

        public String getGroup_id() {
            return group_id;
        }

        public void setGroup_id(String group_id) {
            this.group_id = group_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getEvent_date() {
            return event_date;
        }

        public void setEvent_date(String event_date) {
            this.event_date = event_date;
        }

        public String getEvent_start_time() {
            return event_start_time;
        }

        public void setEvent_start_time(String event_start_time) {
            this.event_start_time = event_start_time;
        }

        public String getEvent_end_time() {
            return event_end_time;
        }

        public void setEvent_end_time(String event_end_time) {
            this.event_end_time = event_end_time;
        }

        public String getVenue_address() {
            return venue_address;
        }

        public void setVenue_address(String venue_address) {
            this.venue_address = venue_address;
        }

        public String getVenue_longitude() {
            return venue_longitude;
        }

        public void setVenue_longitude(String venue_longitude) {
            this.venue_longitude = venue_longitude;
        }

        public String getVenue_latitude() {
            return venue_latitude;
        }

        public void setVenue_latitude(String venue_latitude) {
            this.venue_latitude = venue_latitude;
        }

        public String getMessage_for_paidmember() {
            return message_for_paidmember;
        }

        public void setMessage_for_paidmember(String message_for_paidmember) {
            this.message_for_paidmember = message_for_paidmember;
        }

        public String getMessage_for_unpaidmember() {
            return message_for_unpaidmember;
        }

        public void setMessage_for_unpaidmember(String message_for_unpaidmember) {
            this.message_for_unpaidmember = message_for_unpaidmember;
        }

        public String getIs_offline_payments_allowed() {
            return is_offline_payments_allowed;
        }

        public void setIs_offline_payments_allowed(String is_offline_payments_allowed) {
            this.is_offline_payments_allowed = is_offline_payments_allowed;
        }

        public String getIs_online_events() {
            return is_online_events;
        }

        public void setIs_online_events(String is_online_events) {
            this.is_online_events = is_online_events;
        }

        public String getIs_displaytomembers() {
            return is_displaytomembers;
        }

        public void setIs_displaytomembers(String is_displaytomembers) {
            this.is_displaytomembers = is_displaytomembers;
        }

        public String getEvent_city() {
            return event_city;
        }

        public void setEvent_city(String event_city) {
            this.event_city = event_city;
        }

        public String getDisplay_in_city() {
            return display_in_city;
        }

        public void setDisplay_in_city(String display_in_city) {
            this.display_in_city = display_in_city;
        }

        public String getPayment_account_id() {
            return payment_account_id;
        }

        public void setPayment_account_id(String payment_account_id) {
            this.payment_account_id = payment_account_id;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getEarlybird_price() {
            return earlybird_price;
        }

        public void setEarlybird_price(String earlybird_price) {
            this.earlybird_price = earlybird_price;
        }

        public String getNormal_price() {
            return normal_price;
        }

        public void setNormal_price(String normal_price) {
            this.normal_price = normal_price;
        }

        public String getEarlybird_price_duedate() {
            return earlybird_price_duedate;
        }

        public void setEarlybird_price_duedate(String earlybird_price_duedate) {
            this.earlybird_price_duedate = earlybird_price_duedate;
        }

        public String getNormal_price_duedate() {
            return normal_price_duedate;
        }

        public void setNormal_price_duedate(String normal_price_duedate) {
            this.normal_price_duedate = normal_price_duedate;
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
