package in.oriange.joinsta.models;

import java.io.Serializable;
import java.util.List;

public class EventsFreeModel implements Serializable {


    /**
     * type : success
     * message : get free event successfully!
     * result : [{"id":"1","event_code":"L00001","event_type_id":"81","group_id":"88","name":"first event 1","description":"test 1","event_date":"2011-02-20","event_start_time":"04:00:00","event_end_time":"05:10:00","venue_address":"nanded","venue_longitude":"12.211","venue_latitude":"4.4","is_confirmation_required":"1","is_online_event":"1","is_displaytomembers":"1","event_city":"latur","display_in_city":"1","remark":"0","event_category_id":"1","created_by":"512","updated_by":"10","created_at":"2020-01-24 02:06:10","updated_at":"2020-01-25 13:27:20"},{"id":"3","event_code":"0003","event_type_id":"2","group_id":"88","name":"first event","description":"test","event_date":"2012-02-20","event_start_time":"04:20:00","event_end_time":"07:20:00","venue_address":"pune","venue_longitude":"12.2","venue_latitude":"23.43","is_confirmation_required":"1","is_online_event":"1","is_displaytomembers":"1","event_city":"latur","display_in_city":"1","remark":"1","event_category_id":"1","created_by":"512","updated_by":"512","created_at":"2020-01-25 13:25:18","updated_at":"2020-01-25 13:25:18"}]
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
         * id : 1
         * event_code : L00001
         * event_type_id : 81
         * group_id : 88
         * name : first event 1
         * description : test 1
         * event_date : 2011-02-20
         * event_start_time : 04:00:00
         * event_end_time : 05:10:00
         * venue_address : nanded
         * venue_longitude : 12.211
         * venue_latitude : 4.4
         * is_confirmation_required : 1
         * is_online_event : 1
         * is_displaytomembers : 1
         * event_city : latur
         * display_in_city : 1
         * remark : 0
         * event_category_id : 1
         * created_by : 512
         * updated_by : 10
         * created_at : 2020-01-24 02:06:10
         * updated_at : 2020-01-25 13:27:20
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
        private String is_confirmation_required;
        private String is_online_event;
        private String is_displaytomembers;
        private String event_city;
        private String display_in_city;
        private String remark;
        private String event_category_id;
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

        public String getIs_confirmation_required() {
            return is_confirmation_required;
        }

        public void setIs_confirmation_required(String is_confirmation_required) {
            this.is_confirmation_required = is_confirmation_required;
        }

        public String getIs_online_event() {
            return is_online_event;
        }

        public void setIs_online_event(String is_online_event) {
            this.is_online_event = is_online_event;
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

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getEvent_category_id() {
            return event_category_id;
        }

        public void setEvent_category_id(String event_category_id) {
            this.event_category_id = event_category_id;
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
