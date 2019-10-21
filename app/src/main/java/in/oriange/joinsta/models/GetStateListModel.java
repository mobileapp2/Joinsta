package in.oriange.joinsta.models;

import java.util.List;

public class GetStateListModel {


    /**
     * type : success
     * message : get states successfully!
     * result : [{"id":"1","name":"Andaman & Nicobar Islands"},{"id":"2","name":"Andhra Pradesh"},{"id":"3","name":"Arunachal Pradesh"},{"id":"4","name":"Assam"},{"id":"5","name":"Bihar"},{"id":"6","name":"Chandigarh"},{"id":"7","name":"Chhattisgarh"},{"id":"8","name":"Dadra & Nagar Haveli"},{"id":"9","name":"Daman & Diu"},{"id":"10","name":"Delhi"},{"id":"11","name":"Goa"},{"id":"12","name":"Gujarat"},{"id":"13","name":"Haryana"},{"id":"14","name":"Himachal Pradesh"},{"id":"15","name":"Jammu & Kashmir"},{"id":"16","name":"Jharkhand"},{"id":"17","name":"Karnataka"},{"id":"18","name":"Kerala"},{"id":"19","name":"Lakshadweep"},{"id":"20","name":"Madhya Pradesh"},{"id":"21","name":"Maharashtra"},{"id":"22","name":"Manipur"},{"id":"23","name":"Meghalaya"},{"id":"24","name":"Mizoram"},{"id":"25","name":"Nagaland"},{"id":"26","name":"Odisha"},{"id":"27","name":"Puducherry"},{"id":"28","name":"Punjab"},{"id":"29","name":"Rajasthan"},{"id":"30","name":"Sikkim"},{"id":"31","name":"Tamil Nadu"},{"id":"32","name":"Tripura"},{"id":"33","name":"Uttar Pradesh"},{"id":"34","name":"Uttarakhand"},{"id":"35","name":"West Bengal"},{"id":"36","name":"Telengana"},{"id":"37","name":"Other Territory"}]
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
         * name : Andaman & Nicobar Islands
         */

        private String id;
        private String name;
        private boolean isChecked;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }
}
