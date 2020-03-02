package in.oriange.joinsta.models;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static in.oriange.joinsta.utilities.Utilities.changeDateFormat;
import static in.oriange.joinsta.utilities.Utilities.getAmPmFrom24Hour;
import static in.oriange.joinsta.utilities.Utilities.ordinal;

public class EventsFreeModel implements Serializable {


    /**
     * type : success
     * message : get free event successfully!
     * result : [{"id":"12","event_code":"0012","event_type_id":"81","group_id":"88","name":"first event 1","description":"test 1","event_start_date":"2011-02-20","event_start_time":"04:00:00","event_end_time":"05:10:00","venue_address":"nanded","venue_longitude":"12.211","venue_latitude":"23.43","is_online_event":"1","is_displaytomembers":"1","is_confirmation_required":"1","event_city":"latur","display_in_city":"1","remark":"0","event_category_id":"1","created_by":"512","updated_by":"10","created_at":"2020-01-27 17:33:06","updated_at":"2020-01-27 18:12:54","group_name":null,"group_code":null,"event_type_name":null,"documents":[{"document_type":"invitationdocument","document_path":"password.jpg"},{"document_type":"invitationdocument","document_path":"username.jpg"},{"document_type":"invitationdocument","document_path":"abc.jpg"},{"document_type":"invitationdocument","document_path":"login.jpg"}]}]
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
         * id : 12
         * event_code : 0012
         * event_type_id : 81
         * group_id : 88
         * name : first event 1
         * description : test 1
         * event_start_date : 2011-02-20
         * event_start_time : 04:00:00
         * event_end_time : 05:10:00
         * venue_address : nanded
         * venue_longitude : 12.211
         * venue_latitude : 23.43
         * is_online_event : 1
         * is_displaytomembers : 1
         * is_confirmation_required : 1
         * event_city : latur
         * display_in_city : 1
         * remark : 0
         * event_category_id : 1
         * created_by : 512
         * updated_by : 10
         * created_at : 2020-01-27 17:33:06
         * updated_at : 2020-01-27 18:12:54
         * group_name : null
         * group_code : null
         * event_type_name : null
         * documents : [{"document_type":"invitationdocument","document_path":"password.jpg"},{"document_type":"invitationdocument","document_path":"username.jpg"},{"document_type":"invitationdocument","document_path":"abc.jpg"},{"document_type":"invitationdocument","document_path":"login.jpg"}]
         */

        private String id;
        private String event_code;
        private String event_type_id;
        private String group_id;
        private String name;
        private String description;
        private String event_start_date;
        private String event_end_date;
        private String event_start_time;
        private String event_end_time;
        private String venue_address;
        private String venue_longitude;
        private String venue_latitude;
        private String is_online_event;
        private String is_displaytomembers;
        private String is_confirmation_required;
        private String event_city;
        private String display_in_city;
        private String remark;
        private String event_category_id;
        private String created_by;
        private String updated_by;
        private String created_at;
        private String updated_at;
        private String group_name;
        private String group_code;
        private String event_type_name;
        private String mobile;
        private String organizer_name;
        private String is_active;
        private String status;
        private String created_by_name;
        private List<DocumentsBean> documents;

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
            return description + " ";
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getEvent_date() {
            return event_start_date;
        }

        public void setEvent_date(String event_start_date) {
            this.event_start_date = event_start_date;
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

        public String getIs_confirmation_required() {
            return is_confirmation_required;
        }

        public void setIs_confirmation_required(String is_confirmation_required) {
            this.is_confirmation_required = is_confirmation_required;
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

        public String getGroup_name() {
            if (group_name != null) {
                return group_name;
            } else {
                return "";
            }
        }

        public void setGroup_name(String group_name) {
            this.group_name = group_name;
        }

        public String getGroup_code() {
            if (group_code != null) {
                return group_code;
            } else {
                return "";
            }
        }

        public void setGroup_code(String group_code) {
            this.group_code = group_code;
        }

        public String getEvent_type_name() {
            if (event_type_name != null) {
                return event_type_name;
            } else {
                return "";
            }
        }

        public void setEvent_type_name(String event_type_name) {
            this.event_type_name = event_type_name;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getOrganizer_name() {
            return organizer_name;
        }

        public void setOrganizer_name(String organizer_name) {
            this.organizer_name = organizer_name;
        }

        public String getEvent_end_date() {
            return event_end_date;
        }

        public void setEvent_end_date(String event_end_date) {
            this.event_end_date = event_end_date;
        }

        public String getIs_active() {
            return is_active;
        }

        public void setIs_active(String is_active) {
            this.is_active = is_active;
        }

        public String getCreated_by_name() {
            if (created_by_name != null) {
                return created_by_name;
            } else {
                return "";
            }
        }

        public void setCreated_by_name(String created_by_name) {
            this.created_by_name = created_by_name;
        }

        public String getStatus() {
            if (status != null) {
                return status.trim();
            } else {
                return "";
            }
        }

        public boolean isEndDatePassed() {
            try {

                String currentDateStr = Calendar.getInstance().get(Calendar.YEAR) + "-" +
                        Calendar.getInstance().get(Calendar.MONTH) + "-" +
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

                Date currentDate = new SimpleDateFormat("yyyy-MM-dd").parse(currentDateStr);
                Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(event_end_date);

                return currentDate.after(endDate);
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @SuppressLint("SimpleDateFormat")
        public String getDateTime() {

            try {
                Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(event_start_date);
                Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(event_end_date);

                String startDay = changeDateFormat("yyyy-MM-dd", "dd", event_start_date);
                String startMonth = changeDateFormat("yyyy-MM-dd", "MMM", event_start_date);
                String startYear = changeDateFormat("yyyy-MM-dd", "yyyy", event_start_date);
                String endDay = changeDateFormat("yyyy-MM-dd", "dd", event_end_date);
                String endMonth = changeDateFormat("yyyy-MM-dd", "MMM", event_end_date);
                String endYear = changeDateFormat("yyyy-MM-dd", "yyyy", event_end_date);


                String startDateStr = ordinal(Integer.parseInt(startDay)) + " " + startMonth + " " + startYear;
                String endDateStr = ordinal(Integer.parseInt(endDay)) + " " + endMonth + " " + endYear;

                boolean areDatesEqual = startDate.equals(endDate);

                if (areDatesEqual)
                    return "On " + startDateStr +
                            " Time " + getAmPmFrom24Hour(event_start_time) + " to " + getAmPmFrom24Hour(event_end_time);
                else
                    return startDateStr + " to " + endDateStr +
                            " Time " + getAmPmFrom24Hour(event_start_time) + " to " + getAmPmFrom24Hour(event_end_time);

            } catch (ParseException e) {
                e.printStackTrace();
                return "";
            }
        }

        public List<DocumentsBean> getDocuments() {
            return documents;
        }

        public void setDocuments(List<DocumentsBean> documents) {
            this.documents = documents;
        }

        public static class DocumentsBean implements Serializable {
            /**
             * document_type : invitationdocument
             * document_path : password.jpg
             */

            private String document_type;
            private String document_path;
            private String document_id;

            public DocumentsBean(String document_type, String document_path, String document_id) {
                this.document_type = document_type;
                this.document_path = document_path;
                this.document_id = document_id;
            }

            public DocumentsBean() {
            }

            public String getDocument_type() {
                return document_type;
            }

            public void setDocument_type(String document_type) {
                this.document_type = document_type;
            }

            public String getDocument_path() {
                return document_path;
            }

            public void setDocument_path(String document_path) {
                this.document_path = document_path;
            }

            public String getDocument_id() {
                return document_id;
            }

            public void setDocument_id(String document_id) {
                this.document_id = document_id;
            }
        }
    }
}
