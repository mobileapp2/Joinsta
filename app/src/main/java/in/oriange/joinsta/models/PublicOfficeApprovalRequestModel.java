package in.oriange.joinsta.models;

import java.util.List;

public class PublicOfficeApprovalRequestModel {


    /**
     * type : success
     * message : Office approval request return successfully!
     * result : [{"office_id":"16","status":"approval","office_name":"first","local_name":"test first","city":"Latur","sender_details":[{"sender_id":"512","first_name":"test","mobile":"9307728041","country_code":"91","image_url":""}]},{"office_id":"17","status":"approval","office_name":"first","local_name":"test first","city":"Latur","sender_details":[{"sender_id":"512","first_name":"test","mobile":"9307728041","country_code":"91","image_url":""}]}]
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
         * office_id : 16
         * status : approval
         * office_name : first
         * local_name : test first
         * city : Latur
         * sender_details : [{"sender_id":"512","first_name":"test","mobile":"9307728041","country_code":"91","image_url":""}]
         */

        private String office_id;
        private String status;
        private String office_name;
        private String local_name;
        private String city;
        private List<SenderDetailsBean> sender_details;

        public String getOffice_id() {
            return office_id;
        }

        public void setOffice_id(String office_id) {
            this.office_id = office_id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

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

        public List<SenderDetailsBean> getSender_details() {
            return sender_details;
        }

        public void setSender_details(List<SenderDetailsBean> sender_details) {
            this.sender_details = sender_details;
        }

        public static class SenderDetailsBean {
            /**
             * sender_id : 512
             * first_name : test
             * mobile : 9307728041
             * country_code : 91
             * image_url :
             */

            private String sender_id;
            private String first_name;
            private String mobile;
            private String country_code;
            private String image_url;

            public String getSender_id() {
                return sender_id;
            }

            public void setSender_id(String sender_id) {
                this.sender_id = sender_id;
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

            public String getCountry_code() {
                return country_code;
            }

            public void setCountry_code(String country_code) {
                this.country_code = country_code;
            }

            public String getImage_url() {
                return image_url;
            }

            public void setImage_url(String image_url) {
                this.image_url = image_url;
            }
        }
    }
}
