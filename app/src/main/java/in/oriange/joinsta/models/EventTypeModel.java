package in.oriange.joinsta.models;

import java.util.List;

public class EventTypeModel {


    /**
     * type : success
     * message : get event types successfully!
     * result : [{"id":"1","event_type":"Business"},{"id":"2","event_type":"Knowledge"},{"id":"3","event_type":"Sharing"},{"id":"4","event_type":"Social"},{"id":"5","event_type":"Entertainment"},{"id":"6","event_type":"Group Event"},{"id":"7","event_type":"Personal"},{"id":"8","event_type":"Kid"},{"id":"9","event_type":"Health"},{"id":"10","event_type":"Others"}]
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
         * id : 1
         * event_type : Business
         */

        private String id;
        private String event_type;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEvent_type() {
            return event_type;
        }

        public void setEvent_type(String event_type) {
            this.event_type = event_type;
        }
    }
}
