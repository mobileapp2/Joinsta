package in.oriange.joinsta.models;

import java.util.ArrayList;

public class UserFeedbackListModel {

    private String type;
    private String message;
    private ArrayList<ResultBean> result;

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

    public ArrayList<ResultBean> getResult() {
        return result;
    }

    public void setResult(ArrayList<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {

        private String user_feedback_id;
        private String user_name;
        private String feedback_text;
        private String is_attended;
        private String attended_by;
        private String mobile;
        private String userid;
        private String Rating;

        public String getUser_feedback_id() {
            return user_feedback_id;
        }

        public void setUser_feedback_id(String user_feedback_id) {
            this.user_feedback_id = user_feedback_id;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getFeedback_text() {
            return feedback_text;
        }

        public void setFeedback_text(String feedback_text) {
            this.feedback_text = feedback_text;
        }

        public String getIs_attended() {
            return is_attended;
        }

        public void setIs_attended(String is_attended) {
            this.is_attended = is_attended;
        }

        public String getAttended_by() {
            return attended_by;
        }

        public void setAttended_by(String attended_by) {
            this.attended_by = attended_by;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getRating() {
            return Rating;
        }

        public void setRating(String Rating) {
            this.Rating = Rating;
        }
    }
}
