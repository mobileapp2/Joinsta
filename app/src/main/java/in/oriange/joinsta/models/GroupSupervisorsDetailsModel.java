package in.oriange.joinsta.models;

import java.util.List;

public class GroupSupervisorsDetailsModel {


    /**
     * type : success
     * message : Group supervisor details returned successfully!
     * result : {"group_member_id":"44","first_name":"Test supervisor 11","mobile":"9637611111","email":"test@example.com","states":[{"state_id":"3","state_name":"Arunachal Pradesh"}],"districts":[{"district_id":"282","district_name":"Dibang Valley"},{"district_id":"283","district_name":"East Kameng"}],"role":"group_supervisor"}
     */

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
        /**
         * group_member_id : 44
         * first_name : Test supervisor 11
         * mobile : 9637611111
         * email : test@example.com
         * states : [{"state_id":"3","state_name":"Arunachal Pradesh"}]
         * districts : [{"district_id":"282","district_name":"Dibang Valley"},{"district_id":"283","district_name":"East Kameng"}]
         * role : group_supervisor
         */

        private String group_member_id;
        private String first_name;
        private String mobile;
        private String email;
        private String role;
        private String country_code;
        private List<StatesBean> states;
        private List<DistrictsBean> districts;

        public String getGroup_member_id() {
            return group_member_id;
        }

        public void setGroup_member_id(String group_member_id) {
            this.group_member_id = group_member_id;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getCountry_code() {
            if (country_code != null){
                return country_code;
            } else {
                return "+91";
            }
        }

        public void setCountry_code(String country_code) {
            this.country_code = country_code;
        }

        public List<StatesBean> getStates() {
            return states;
        }

        public void setStates(List<StatesBean> states) {
            this.states = states;
        }

        public List<DistrictsBean> getDistricts() {
            return districts;
        }

        public void setDistricts(List<DistrictsBean> districts) {
            this.districts = districts;
        }

        public static class StatesBean {
            /**
             * state_id : 3
             * state_name : Arunachal Pradesh
             */

            private String state_id;
            private String state_name;

            public String getState_id() {
                return state_id;
            }

            public void setState_id(String state_id) {
                this.state_id = state_id;
            }

            public String getState_name() {
                return state_name;
            }

            public void setState_name(String state_name) {
                this.state_name = state_name;
            }
        }

        public static class DistrictsBean {
            /**
             * district_id : 282
             * district_name : Dibang Valley
             */

            private String district_id;
            private String district_name;

            public String getDistrict_id() {
                return district_id;
            }

            public void setDistrict_id(String district_id) {
                this.district_id = district_id;
            }

            public String getDistrict_name() {
                return district_name;
            }

            public void setDistrict_name(String district_name) {
                this.district_name = district_name;
            }
        }
    }
}
