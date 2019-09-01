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
        private String group_id;

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

        public String getGroup_id() {
            return group_id;
        }

        public void setGroup_id(String group_id) {
            this.group_id = group_id;
        }
    }
}
