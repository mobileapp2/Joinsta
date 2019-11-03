package in.oriange.joinsta.models;

import java.util.ArrayList;

public class GroupNotificationListModel {


    /**
     * type : success
     * message : Groups notifications returned successfully!
     * result : [{"id":"32","subject":"tester","message":"testing","send_by":"67","group_id":"1"},{"id":"41","subject":"gm","message":"gn","send_by":"67","group_id":"1"}]
     */

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
        /**
         * id : 32
         * subject : tester
         * message : testing
         * send_by : 67
         * group_id : 1
         */

        private String id;
        private String subject;
        private String message;
        private String send_by;
        private String created_at;
        private String group_id;
        private String msg_details_id;
        private String is_read;

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

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getGroup_id() {
            return group_id;
        }

        public void setGroup_id(String group_id) {
            this.group_id = group_id;
        }

        public String getMsg_details_id() {
            return msg_details_id;
        }

        public void setMsg_details_id(String msg_details_id) {
            this.msg_details_id = msg_details_id;
        }

        public String getIs_read() {
            return is_read;
        }

        public void setIs_read(String is_read) {
            this.is_read = is_read;
        }
    }
}
