package in.oriange.joinsta.models;

import java.util.List;

public class EventsModel {

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
        private List<EventsFreeModel.ResultBean> free_events;
        private List<EventsPaidModel.ResultBean> paid_events;

        public List<EventsFreeModel.ResultBean> getFree_events() {
            return free_events;
        }

        public void setFree_events(List<EventsFreeModel.ResultBean> free_events) {
            this.free_events = free_events;
        }

        public List<EventsPaidModel.ResultBean> getPaid_events() {
            return paid_events;
        }

        public void setPaid_events(List<EventsPaidModel.ResultBean> paid_events) {
            this.paid_events = paid_events;
        }
    }
}
