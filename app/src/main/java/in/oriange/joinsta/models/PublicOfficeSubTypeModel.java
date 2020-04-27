package in.oriange.joinsta.models;

import java.util.List;

public class PublicOfficeSubTypeModel {


    /**
     * type : success
     * message : get sub type successfully!
     * result : [{"id":"1","type_id":"1","sub_type_name":"home_office"}]
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
         * type_id : 1
         * sub_type_name : home_office
         */

        private String id;
        private String type_id;
        private String sub_type_name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType_id() {
            return type_id;
        }

        public void setType_id(String type_id) {
            this.type_id = type_id;
        }

        public String getSub_type_name() {
            return sub_type_name;
        }

        public void setSub_type_name(String sub_type_name) {
            this.sub_type_name = sub_type_name;
        }
    }
}
