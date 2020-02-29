package in.oriange.joinsta.models;

import java.util.List;

public class DisclaimerModel {


    /**
     * type : success
     * message : Disclaimer string returned successfully!
     * result : [{"id":"1","title":"disclaimer","string":"Here will be the disclaimer string"}]
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
         * title : disclaimer
         * string : Here will be the disclaimer string
         */

        private String id;
        private String title;
        private String string;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }
    }
}
