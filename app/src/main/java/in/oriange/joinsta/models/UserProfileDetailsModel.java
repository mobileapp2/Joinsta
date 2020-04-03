package in.oriange.joinsta.models;

import java.util.List;

public class UserProfileDetailsModel {
    /**
     * type : success
     * message : You have logged in successfully!
     * result : [{"userid":"1","password":"72007983849f4fcb0ad565439834756b","blood_group_id":"1","blood_group_description":"O +","education_id":"1","education_description":"Graduate","first_name":"Test","gender_id":"1","gender_description":"Male","last_name":"test","country_code":"91","mobile":"8830648438","image_url":"","middle_name":"test","native_place":"latur","is_active":"1","referral_code":"qrwr","specific_education":"teetet","role_id":"2","mobile_numbers":[{"user_moblie_id":"1262","mobile":"1234567891","is_primary":"1","is_public":"1","country_code":"+91"}],"landline_numbers":[{"user_landline_id":"684","landline_number":"+9102323233220","country_code":""},{"user_landline_id":"685","landline_number":"02342330000","country_code":""}],"email":[{"user_email_id":"1360","email":"radhika.awad@outlook.com","is_primary":"1","email_verification":"0"}]}]
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
         * userid : 1
         * password : 72007983849f4fcb0ad565439834756b
         * blood_group_id : 1
         * blood_group_description : O +
         * education_id : 1
         * education_description : Graduate
         * first_name : Test
         * gender_id : 1
         * gender_description : Male
         * last_name : test
         * country_code : 91
         * mobile : 8830648438
         * image_url :
         * middle_name : test
         * native_place : latur
         * is_active : 1
         * referral_code : qrwr
         * specific_education : teetet
         * role_id : 2
         * mobile_numbers : [{"user_moblie_id":"1262","mobile":"1234567891","is_primary":"1","is_public":"1","country_code":"+91"}]
         * landline_numbers : [{"user_landline_id":"684","landline_number":"+9102323233220","country_code":""},{"user_landline_id":"685","landline_number":"02342330000","country_code":""}]
         * email : [{"user_email_id":"1360","email":"radhika.awad@outlook.com","is_primary":"1","email_verification":"0"}]
         */

        private String userid;
        private String password;
        private String blood_group_id;
        private String blood_group_description;
        private String education_id;
        private String education_description;
        private String first_name;
        private String gender_id;
        private String gender_description;
        private String last_name;
        private String country_code;
        private String mobile;
        private String image_url;
        private String middle_name;
        private String native_place;
        private String is_active;
        private String referral_code;
        private String specific_education;
        private String role_id;
        private String about;
        private String latitude;
        private String longitude;
        private List<MobileNumbersBean> mobile_numbers;
        private List<LandlineNumbersBean> landline_numbers;
        private List<EmailBean> email;

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getBlood_group_id() {
            return blood_group_id;
        }

        public void setBlood_group_id(String blood_group_id) {
            this.blood_group_id = blood_group_id;
        }

        public String getBlood_group_description() {
            if (blood_group_description != null) {
                return blood_group_description;
            } else {
                return "";
            }
        }

        public void setBlood_group_description(String blood_group_description) {
            this.blood_group_description = blood_group_description;
        }

        public String getEducation_id() {
            return education_id;
        }

        public void setEducation_id(String education_id) {
            this.education_id = education_id;
        }

        public String getEducation_description() {
            if (education_description != null) {
                return education_description;
            } else {
                return "";
            }
        }

        public void setEducation_description(String education_description) {
            this.education_description = education_description;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getGender_id() {
            return gender_id;
        }

        public void setGender_id(String gender_id) {
            this.gender_id = gender_id;
        }

        public String getGender_description() {
            return gender_description;
        }

        public void setGender_description(String gender_description) {
            this.gender_description = gender_description;
        }

        public String getLast_name() {
            return last_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
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

        public String getMiddle_name() {
            return middle_name;
        }

        public void setMiddle_name(String middle_name) {
            this.middle_name = middle_name;
        }

        public String getNative_place() {
            return native_place;
        }

        public void setNative_place(String native_place) {
            this.native_place = native_place;
        }

        public String getIs_active() {
            return is_active;
        }

        public void setIs_active(String is_active) {
            this.is_active = is_active;
        }

        public String getReferral_code() {
            if (referral_code != null) {
                return referral_code;
            } else {
                return "";
            }
        }

        public void setReferral_code(String referral_code) {
            this.referral_code = referral_code;
        }

        public String getSpecific_education() {
            return specific_education;
        }

        public void setSpecific_education(String specific_education) {
            this.specific_education = specific_education;
        }

        public String getRole_id() {
            return role_id;
        }

        public void setRole_id(String role_id) {
            this.role_id = role_id;
        }

        public String getAbout() {
            if (about != null) {
                return about;
            } else {
                return "";
            }
        }

        public void setAbout(String about) {
            this.about = about;
        }

        public String getLatitude() {
            if (latitude != null) {
                return latitude;
            } else {
                return "0.0";
            }
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            if (longitude != null) {
                return longitude;
            } else {
                return "0.0";
            }
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public List<MobileNumbersBean> getMobile_numbers() {
            return mobile_numbers;
        }

        public void setMobile_numbers(List<MobileNumbersBean> mobile_numbers) {
            this.mobile_numbers = mobile_numbers;
        }

        public List<LandlineNumbersBean> getLandline_numbers() {
            return landline_numbers;
        }

        public void setLandline_numbers(List<LandlineNumbersBean> landline_numbers) {
            this.landline_numbers = landline_numbers;
        }

        public List<EmailBean> getEmail() {
            return email;
        }

        public void setEmail(List<EmailBean> email) {
            this.email = email;
        }

        public static class MobileNumbersBean {
            /**
             * user_moblie_id : 1262
             * mobile : 1234567891
             * is_primary : 1
             * is_public : 1
             * country_code : +91
             */

            private String user_moblie_id;
            private String mobile;
            private String is_primary;
            private String is_public;
            private String country_code;

            public String getUser_moblie_id() {
                return user_moblie_id;
            }

            public void setUser_moblie_id(String user_moblie_id) {
                this.user_moblie_id = user_moblie_id;
            }

            public String getMobile() {
                return mobile.replace("-", "").replace("+", "");
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getIs_primary() {
                return is_primary;
            }

            public void setIs_primary(String is_primary) {
                this.is_primary = is_primary;
            }

            public String getIs_public() {
                return is_public;
            }

            public void setIs_public(String is_public) {
                this.is_public = is_public;
            }

            public String getCountry_code() {
                if (country_code != null) {
                    if (!country_code.equals("")) {
                        if (country_code.startsWith("+")) {
                            return country_code;
                        } else {
                            return "+" + country_code;
                        }
                    } else {
                        return "+91";
                    }
                } else {
                    return "+91";
                }
            }

            public void setCountry_code(String country_code) {
                this.country_code = country_code;
            }
        }

        public static class LandlineNumbersBean {
            /**
             * user_landline_id : 684
             * landline_number : +9102323233220
             * country_code :
             */

            private String user_landline_id;
            private String landline_number;
            private String country_code;

            public String getUser_landline_id() {
                return user_landline_id.replace("-", "").replace("+", "");
            }

            public void setUser_landline_id(String user_landline_id) {
                this.user_landline_id = user_landline_id;
            }

            public String getLandline_number() {
                return landline_number.replace("-", "").replace("+", "");
            }

            public void setLandline_number(String landline_number) {
                this.landline_number = landline_number;
            }

            public String getCountry_code() {
                if (country_code != null) {
                    if (!country_code.equals("")) {
                        if (country_code.startsWith("+")) {
                            return country_code;
                        } else {
                            return "+" + country_code;
                        }
                    } else {
                        return "+91";
                    }
                } else {
                    return "+91";
                }
            }

            public void setCountry_code(String country_code) {
                this.country_code = country_code;
            }
        }

        public static class EmailBean {
            /**
             * user_email_id : 1360
             * email : radhika.awad@outlook.com
             * is_primary : 1
             * email_verification : 0
             */

            private String user_email_id;
            private String email;
            private String is_primary;
            private String email_verification;

            public String getUser_email_id() {
                return user_email_id;
            }

            public void setUser_email_id(String user_email_id) {
                this.user_email_id = user_email_id;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getIs_primary() {
                return is_primary;
            }

            public void setIs_primary(String is_primary) {
                this.is_primary = is_primary;
            }

            public String getEmail_verification() {
                return email_verification;
            }

            public void setEmail_verification(String email_verification) {
                this.email_verification = email_verification;
            }
        }
    }
}
