package in.oriange.joinsta.models;

import java.io.Serializable;
import java.util.List;

import static in.oriange.joinsta.utilities.Utilities.changeDateFormat;
import static in.oriange.joinsta.utilities.Utilities.getAmPmFrom24Hour;

public class EventsPaidModel implements Serializable {


    /**
     * type : success
     * message : get paid event successfully!
     * result : [{"id":"34","event_code":"0034","event_type_id":"2","group_id":"47","name":"Jdjsjsj","description":"Kdskkala dNsks sks r ybrje djdn dNsks dNsks s snsns ekskd dNsks kd ejks","event_date":"0000-00-00","event_start_time":"12:00:00","event_end_time":"18:30:00","venue_address":"Vighnaharta Colony, Hadapsar Gaon, Hadapsar, Pune, Maharashtra 411028, India","venue_longitude":"73.9376396","venue_latitude":"18.497812300000003","message_for_paidmember":"Jdksnf jdksnf kygog. F ho and ixi fsgbwgb of besibd s snsns sks t fkdkdodkfj","message_for_unpaidmember":"Bdhbd ddkskskwn disnF wkdndbsNskna djfnr frkd d sks dNsks nrje jdksnf djfnr djdksmwndndj","is_offline_payments_allowed":"1","is_online_events":"1","is_confirmation_required":"0","is_displaytomembers":"1","event_city":"Pune","display_in_city":"1","payment_account_id":"4","remark":"Jdksnf dNsks s ng ejebee and DnsksnsNskvog ndf d dNsks DnsksnsNskvog snsns sb","earlybird_price":"100","normal_price":"1000","earlybird_price_duedate":"2020-02-05","normal_price_duedate":"2020-02-08","created_by":"271","updated_by":"271","created_at":"2020-02-04 04:19:28","updated_at":"2020-02-04 04:19:28","group_name":"Yuva Association Latur ","group_code":"0047","event_type_name":"Knowledge","paidevents_paymentoptions":[{"payment_mode":"online","payment_link":"","id":"53"},{"payment_mode":"offline","payment_link":"","id":"54"},{"payment_mode":"paymentlink","payment_link":"www.google.com","id":"55"}],"documents":[{"document_type":"invitationdocument","document_path":"5-android-fundamentals-services-m5-slides-20200203104831.pdf"},{"document_type":"invitationimage","document_path":"uplimg-20200203104816.png"}]},{"id":"34","event_code":"0034","event_type_id":"2","group_id":"47","name":"Jdjsjsj","description":"Kdskkala dNsks sks r ybrje djdn dNsks dNsks s snsns ekskd dNsks kd ejks","event_date":"0000-00-00","event_start_time":"12:00:00","event_end_time":"18:30:00","venue_address":"Vighnaharta Colony, Hadapsar Gaon, Hadapsar, Pune, Maharashtra 411028, India","venue_longitude":"73.9376396","venue_latitude":"18.497812300000003","message_for_paidmember":"Jdksnf jdksnf kygog. F ho and ixi fsgbwgb of besibd s snsns sks t fkdkdodkfj","message_for_unpaidmember":"Bdhbd ddkskskwn disnF wkdndbsNskna djfnr frkd d sks dNsks nrje jdksnf djfnr djdksmwndndj","is_offline_payments_allowed":"1","is_online_events":"1","is_confirmation_required":"0","is_displaytomembers":"1","event_city":"Pune","display_in_city":"1","payment_account_id":"4","remark":"Jdksnf dNsks s ng ejebee and DnsksnsNskvog ndf d dNsks DnsksnsNskvog snsns sb","earlybird_price":"100","normal_price":"1000","earlybird_price_duedate":"2020-02-05","normal_price_duedate":"2020-02-08","created_by":"271","updated_by":"271","created_at":"2020-02-04 04:19:28","updated_at":"2020-02-04 04:19:28","group_name":"Yuva Association Latur ","group_code":"0047","event_type_name":"Knowledge","paidevents_paymentoptions":[{"payment_mode":"online","payment_link":"","id":"53"},{"payment_mode":"offline","payment_link":"","id":"54"},{"payment_mode":"paymentlink","payment_link":"www.google.com","id":"55"}],"documents":[{"document_type":"invitationdocument","document_path":"5-android-fundamentals-services-m5-slides-20200203104831.pdf"},{"document_type":"invitationimage","document_path":"uplimg-20200203104816.png"}]},{"id":"34","event_code":"0034","event_type_id":"2","group_id":"47","name":"Jdjsjsj","description":"Kdskkala dNsks sks r ybrje djdn dNsks dNsks s snsns ekskd dNsks kd ejks","event_date":"0000-00-00","event_start_time":"12:00:00","event_end_time":"18:30:00","venue_address":"Vighnaharta Colony, Hadapsar Gaon, Hadapsar, Pune, Maharashtra 411028, India","venue_longitude":"73.9376396","venue_latitude":"18.497812300000003","message_for_paidmember":"Jdksnf jdksnf kygog. F ho and ixi fsgbwgb of besibd s snsns sks t fkdkdodkfj","message_for_unpaidmember":"Bdhbd ddkskskwn disnF wkdndbsNskna djfnr frkd d sks dNsks nrje jdksnf djfnr djdksmwndndj","is_offline_payments_allowed":"1","is_online_events":"1","is_confirmation_required":"0","is_displaytomembers":"1","event_city":"Pune","display_in_city":"1","payment_account_id":"4","remark":"Jdksnf dNsks s ng ejebee and DnsksnsNskvog ndf d dNsks DnsksnsNskvog snsns sb","earlybird_price":"100","normal_price":"1000","earlybird_price_duedate":"2020-02-05","normal_price_duedate":"2020-02-08","created_by":"271","updated_by":"271","created_at":"2020-02-04 04:19:28","updated_at":"2020-02-04 04:19:28","group_name":"Yuva Association Latur ","group_code":"0047","event_type_name":"Knowledge","paidevents_paymentoptions":[{"payment_mode":"online","payment_link":"","id":"53"},{"payment_mode":"offline","payment_link":"","id":"54"},{"payment_mode":"paymentlink","payment_link":"www.google.com","id":"55"}],"documents":[{"document_type":"invitationdocument","document_path":"5-android-fundamentals-services-m5-slides-20200203104831.pdf"},{"document_type":"invitationimage","document_path":"uplimg-20200203104816.png"}]}]
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
         * id : 34
         * event_code : 0034
         * event_type_id : 2
         * group_id : 47
         * name : Jdjsjsj
         * description : Kdskkala dNsks sks r ybrje djdn dNsks dNsks s snsns ekskd dNsks kd ejks
         * event_date : 0000-00-00
         * event_start_time : 12:00:00
         * event_end_time : 18:30:00
         * venue_address : Vighnaharta Colony, Hadapsar Gaon, Hadapsar, Pune, Maharashtra 411028, India
         * venue_longitude : 73.9376396
         * venue_latitude : 18.497812300000003
         * message_for_paidmember : Jdksnf jdksnf kygog. F ho and ixi fsgbwgb of besibd s snsns sks t fkdkdodkfj
         * message_for_unpaidmember : Bdhbd ddkskskwn disnF wkdndbsNskna djfnr frkd d sks dNsks nrje jdksnf djfnr djdksmwndndj
         * is_offline_payments_allowed : 1
         * is_online_events : 1
         * is_confirmation_required : 0
         * is_displaytomembers : 1
         * event_city : Pune
         * display_in_city : 1
         * payment_account_id : 4
         * remark : Jdksnf dNsks s ng ejebee and DnsksnsNskvog ndf d dNsks DnsksnsNskvog snsns sb
         * earlybird_price : 100
         * normal_price : 1000
         * earlybird_price_duedate : 2020-02-05
         * normal_price_duedate : 2020-02-08
         * created_by : 271
         * updated_by : 271
         * created_at : 2020-02-04 04:19:28
         * updated_at : 2020-02-04 04:19:28
         * group_name : Yuva Association Latur
         * group_code : 0047
         * event_type_name : Knowledge
         * paidevents_paymentoptions : [{"payment_mode":"online","payment_link":"","id":"53"},{"payment_mode":"offline","payment_link":"","id":"54"},{"payment_mode":"paymentlink","payment_link":"www.google.com","id":"55"}]
         * documents : [{"document_type":"invitationdocument","document_path":"5-android-fundamentals-services-m5-slides-20200203104831.pdf"},{"document_type":"invitationimage","document_path":"uplimg-20200203104816.png"}]
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
        private String is_confirmation_required;
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
        private String group_name;
        private String group_code;
        private String event_type_name;
        private List<PaideventsPaymentoptionsBean> paidevents_paymentoptions;
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

        public String getIs_confirmation_required() {
            return is_confirmation_required;
        }

        public void setIs_confirmation_required(String is_confirmation_required) {
            this.is_confirmation_required = is_confirmation_required;
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

        public String getDateTime() {
            return "Event is held on " + changeDateFormat("yyyy-MM-dd", "dd-MMM-yyyy", event_date) +
                    " from " + getAmPmFrom24Hour(event_start_time) + " to " + getAmPmFrom24Hour(event_end_time);
        }

        public List<PaideventsPaymentoptionsBean> getPaidevents_paymentoptions() {
            return paidevents_paymentoptions;
        }

        public void setPaidevents_paymentoptions(List<PaideventsPaymentoptionsBean> paidevents_paymentoptions) {
            this.paidevents_paymentoptions = paidevents_paymentoptions;
        }

        public List<DocumentsBean> getDocuments() {
            return documents;
        }

        public void setDocuments(List<DocumentsBean> documents) {
            this.documents = documents;
        }

        public static class PaideventsPaymentoptionsBean implements Serializable {
            /**
             * payment_mode : online
             * payment_link :
             * id : 53
             */

            private String payment_mode;
            private String payment_link;
            private String id;

            public String getPayment_mode() {
                return payment_mode;
            }

            public void setPayment_mode(String payment_mode) {
                this.payment_mode = payment_mode;
            }

            public String getPayment_link() {
                return payment_link;
            }

            public void setPayment_link(String payment_link) {
                this.payment_link = payment_link;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }

        public static class DocumentsBean implements Serializable {
            /**
             * document_type : invitationdocument
             * document_path : 5-android-fundamentals-services-m5-slides-20200203104831.pdf
             */

            private String document_type;
            private String document_path;

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
        }
    }
}
