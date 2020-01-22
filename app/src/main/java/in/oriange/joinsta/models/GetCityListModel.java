package in.oriange.joinsta.models;

import java.util.ArrayList;

public class GetCityListModel {


    /**
     * type : success
     * message : City details return successfull!
     * result : [{"city_id":"1","state_id":null,"country_id":null,"city_name":"Online","state_name":null,"country_name":null,"created_at":"01-01-2020"},{"city_id":"2","state_id":"1","country_id":"1","city_name":"Ahmednagar","state_name":"Maharashtra","country_name":"India","created_at":"01-01-2020"},{"city_id":"4","state_id":"1","country_id":"1","city_name":"Ambajogai","state_name":"Maharashtra","country_name":"India","created_at":"01-01-2020"},{"city_id":"5","state_id":"2","country_id":"1","city_name":"Ahmedabad","state_name":"Gujarat","country_name":"India","created_at":"01-01-2020"},{"city_id":"6","state_id":"1","country_id":"1","city_name":"Latur","state_name":"Maharashtra","country_name":"India","created_at":"07-01-2020"},{"city_id":"7","state_id":"1","country_id":"1","city_name":"Pune","state_name":"Maharashtra","country_name":"India","created_at":"13-01-2020"},{"city_id":"8","state_id":"1","country_id":"1","city_name":"Nanded","state_name":"Maharashtra","country_name":"India","created_at":"13-01-2020"},{"city_id":"9","state_id":"3","country_id":"2","city_name":"New York","state_name":"New York","country_name":"United States","created_at":"13-01-2020"}]
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
         * city_id : 1
         * state_id : null
         * country_id : null
         * city_name : Online
         * state_name : null
         * country_name : null
         * created_at : 01-01-2020
         */

        private String city_id;
        private String state_id;
        private String country_id;
        private String city_name;
        private String state_name;
        private String country_name;
        private String created_at;

        public String getCity_id() {
            return city_id;
        }

        public void setCity_id(String city_id) {
            this.city_id = city_id;
        }

        public String getState_id() {
            return state_id;
        }

        public void setState_id(String state_id) {
            this.state_id = state_id;
        }

        public String getCountry_id() {
            return country_id;
        }

        public void setCountry_id(String country_id) {
            this.country_id = country_id;
        }

        public String getCity_name() {
            return city_name;
        }

        public void setCity_name(String city_name) {
            this.city_name = city_name;
        }

        public String getState_name() {
            return state_name;
        }

        public void setState_name(String state_name) {
            this.state_name = state_name;
        }

        public String getCountry_name() {
            return country_name;
        }

        public void setCountry_name(String country_name) {
            this.country_name = country_name;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        @Override
        public String toString() {
            return city_name;
        }
    }
}
