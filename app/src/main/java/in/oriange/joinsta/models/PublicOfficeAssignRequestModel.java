package in.oriange.joinsta.models;

import java.util.List;

public class PublicOfficeAssignRequestModel {

    /**
     * type : success
     * message : Office successfully assign to user!
     * result : [{"office_name":"first","local_name":"test first","city":"Latur","country":"india","district":"latur","state":"","department_functions":"Contractor","other_info":"test","first_name":"Priyesh Pawar","mobile":"8149115089","image_url":"http://joinsta.in/images/10/uplimg-20190921012605.png","id":"17","office_id":"2","user_id":"10","is_deleted":"0","assign_date":"2020-04-10 16:55:02","status":"requested","sender_details":[{"office_name":"first","local_name":"test first","first_name":"saba","country_code":"91","mobile":"8180012531","image_url":"","sender_id":"271"}]},{"office_name":"first","local_name":"test first","city":"Latur","country":"india","district":"latur","state":"","department_functions":"Contractor","other_info":"test","first_name":"Priyesh Pawar","mobile":"8149115089","image_url":"http://joinsta.in/images/10/uplimg-20190921012605.png","id":"18","office_id":"2","user_id":"10","is_deleted":"0","assign_date":"2020-04-10 17:02:27","status":"requested","sender_details":[{"office_name":"first","local_name":"test first","first_name":"saba","country_code":"91","mobile":"8180012531","image_url":"","sender_id":"271"}]},{"office_name":"first","local_name":"test first","city":"Latur","country":"india","district":"latur","state":"","department_functions":"Contractor","other_info":"test","first_name":"Priyesh Pawar","mobile":"8149115089","image_url":"http://joinsta.in/images/10/uplimg-20190921012605.png","id":"19","office_id":"2","user_id":"10","is_deleted":"0","assign_date":"2020-04-10 17:12:22","status":"requested","sender_details":[{"office_name":"first","local_name":"test first","first_name":"saba","country_code":"91","mobile":"8180012531","image_url":"","sender_id":"271"}]}]
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
         * office_name : first
         * local_name : test first
         * city : Latur
         * country : india
         * district : latur
         * state :
         * department_functions : Contractor
         * other_info : test
         * first_name : Priyesh Pawar
         * mobile : 8149115089
         * image_url : http://joinsta.in/images/10/uplimg-20190921012605.png
         * id : 17
         * office_id : 2
         * user_id : 10
         * is_deleted : 0
         * assign_date : 2020-04-10 16:55:02
         * status : requested
         * sender_details : [{"office_name":"first","local_name":"test first","first_name":"saba","country_code":"91","mobile":"8180012531","image_url":"","sender_id":"271"}]
         */

        private String office_name;
        private String local_name;
        private String city;
        private String country;
        private String district;
        private String state;
        private String department_functions;
        private String other_info;
        private String first_name;
        private String mobile;
        private String image_url;
        private String id;
        private String office_id;
        private String user_id;
        private String is_deleted;
        private String assign_date;
        private String status;
        private List<SenderDetailsBean> sender_details;

        public String getOffice_name() {
            return office_name;
        }

        public void setOffice_name(String office_name) {
            this.office_name = office_name;
        }

        public String getLocal_name() {
            return local_name;
        }

        public void setLocal_name(String local_name) {
            this.local_name = local_name;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getDepartment_functions() {
            return department_functions;
        }

        public void setDepartment_functions(String department_functions) {
            this.department_functions = department_functions;
        }

        public String getOther_info() {
            return other_info;
        }

        public void setOther_info(String other_info) {
            this.other_info = other_info;
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

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getOffice_id() {
            return office_id;
        }

        public void setOffice_id(String office_id) {
            this.office_id = office_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getIs_deleted() {
            return is_deleted;
        }

        public void setIs_deleted(String is_deleted) {
            this.is_deleted = is_deleted;
        }

        public String getAssign_date() {
            return assign_date;
        }

        public void setAssign_date(String assign_date) {
            this.assign_date = assign_date;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<SenderDetailsBean> getSender_details() {
            return sender_details;
        }

        public void setSender_details(List<SenderDetailsBean> sender_details) {
            this.sender_details = sender_details;
        }

        public static class SenderDetailsBean {
            /**
             * office_name : first
             * local_name : test first
             * first_name : saba
             * country_code : 91
             * mobile : 8180012531
             * image_url :
             * sender_id : 271
             */

            private String office_name;
            private String local_name;
            private String first_name;
            private String country_code;
            private String mobile;
            private String image_url;
            private String sender_id;

            public String getOffice_name() {
                return office_name;
            }

            public void setOffice_name(String office_name) {
                this.office_name = office_name;
            }

            public String getLocal_name() {
                return local_name;
            }

            public void setLocal_name(String local_name) {
                this.local_name = local_name;
            }

            public String getFirst_name() {
                return first_name;
            }

            public void setFirst_name(String first_name) {
                this.first_name = first_name;
            }

            public String getCountry_code() {
                return "+" + country_code;
            }

            public void setCountry_code(String country_code) {
                this.country_code = country_code;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getImage_url() {
                return image_url;
            }

            public void setImage_url(String image_url) {
                this.image_url = image_url;
            }

            public String getSender_id() {
                return sender_id;
            }

            public void setSender_id(String sender_id) {
                this.sender_id = sender_id;
            }
        }
    }
}