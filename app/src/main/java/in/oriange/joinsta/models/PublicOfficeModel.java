package in.oriange.joinsta.models;

import java.io.Serializable;
import java.util.List;

public class PublicOfficeModel implements Serializable {


    /**
     * type : success
     * message : get office successfully!
     * result : [{"id":"18","name":"Jsjsjekksks Ejwjjsjw","local_name":"जस एजसनसनस सजसज्ज","office_type_id":"1","office_sub_type_id":"1","city":"Latur","country":"India","district":"Latur","state":"Maharashtra","pincode":"413531","department_functions":"mems DnsksnsNskvog 4 rjiejejens ebebbebeb","other_info":"Ejejekwk enejsnrbjssbe jdksnf dNsks sNdjne jejenebebssj ebeebkekene","website":"","latitude":"18.4028509","longitude":"Siddhivinayak Marg, Netaji Nagar, Deep Jyoti Nagar, Latur, Maharashtra 413531, India","address":"Siddhivinayak Marg, Netaji Nagar, Deep Jyoti Nagar, Latur, Maharashtra 413531, India","is_approved":"0","approved_by":"0","created_by":"10","updated_by":"10","created_at":"2020-04-22 16:07:16","updated_at":"2020-04-22 16:07:16","type":"persional_office","sub_type_name":"home_office","assign_date":"2020-04-22 16:07:16","avg_rating":0,"review_title_by_user":"","review_description_by_user":"","rating_by_user":0,"total_number_review":"0","mobile_number":[{"id":"12","mobile":"89464816485","country_code":"91"}],"landline_number":[{"id":"11","landlinenumbers":"5949678594","country_code":"91"}],"image_url":[{"id":"16","images":"uplimg-20200415030803.png"}],"emails":[{"id":"18","office_id":"18","email":"jfjeksk@jfkei.com"}],"fax":[{"id":"15","office_id":"18","fax":"664997997979"}],"tags":[{"id":"1","tag_id":"2115","tag_name":"office_tag","is_approved":"1"}]}]
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

    public static class ResultBean implements Serializable {
        /**
         * id : 18
         * name : Jsjsjekksks Ejwjjsjw
         * local_name : जस एजसनसनस सजसज्ज
         * office_type_id : 1
         * office_sub_type_id : 1
         * city : Latur
         * country : India
         * district : Latur
         * state : Maharashtra
         * pincode : 413531
         * department_functions : mems DnsksnsNskvog 4 rjiejejens ebebbebeb
         * other_info : Ejejekwk enejsnrbjssbe jdksnf dNsks sNdjne jejenebebssj ebeebkekene
         * website :
         * latitude : 18.4028509
         * longitude : Siddhivinayak Marg, Netaji Nagar, Deep Jyoti Nagar, Latur, Maharashtra 413531, India
         * address : Siddhivinayak Marg, Netaji Nagar, Deep Jyoti Nagar, Latur, Maharashtra 413531, India
         * is_approved : 0
         * approved_by : 0
         * created_by : 10
         * updated_by : 10
         * created_at : 2020-04-22 16:07:16
         * updated_at : 2020-04-22 16:07:16
         * type : persional_office
         * sub_type_name : home_office
         * assign_date : 2020-04-22 16:07:16
         * avg_rating : 0
         * review_title_by_user :
         * review_description_by_user :
         * rating_by_user : 0
         * total_number_review : 0
         * mobile_number : [{"id":"12","mobile":"89464816485","country_code":"91"}]
         * landline_number : [{"id":"11","landlinenumbers":"5949678594","country_code":"91"}]
         * image_url : [{"id":"16","images":"uplimg-20200415030803.png"}]
         * emails : [{"id":"18","office_id":"18","email":"jfjeksk@jfkei.com"}]
         * fax : [{"id":"15","office_id":"18","fax":"664997997979"}]
         * tags : [{"id":"1","tag_id":"2115","tag_name":"office_tag","is_approved":"1"}]
         */

        private String id;
        private String name;
        private String local_name;
        private String office_type_id;
        private String office_sub_type_id;
        private String city;
        private String country;
        private String district;
        private String state;
        private String pincode;
        private String department_functions;
        private String other_info;
        private String website;
        private String latitude;
        private String longitude;
        private String address;
        private String is_approved;
        private String approved_by;
        private String created_by;
        private String updated_by;
        private String created_at;
        private String updated_at;
        private String type;
        private String sub_type_name;
        private String assign_date;
        private String avg_rating;
        private String review_title_by_user;
        private String review_description_by_user;
        private String rating_by_user;
        private String total_number_review;
        private List<MobileNumberBean> mobile_number;
        private List<LandlineNumberBean> landline_number;
        private List<ImageUrlBean> image_url;
        private List<EmailsBean> emails;
        private List<FaxBean> fax;
        private List<TagsBean> tags;
        private List<SenderDetailsBean> sender_details;

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

        public String getLocal_name() {
            return local_name;
        }

        public void setLocal_name(String local_name) {
            this.local_name = local_name;
        }

        public String getOffice_type_id() {
            return office_type_id;
        }

        public void setOffice_type_id(String office_type_id) {
            this.office_type_id = office_type_id;
        }

        public String getOffice_sub_type_id() {
            return office_sub_type_id;
        }

        public void setOffice_sub_type_id(String office_sub_type_id) {
            this.office_sub_type_id = office_sub_type_id;
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

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
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

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getIs_approved() {
            return is_approved;
        }

        public void setIs_approved(String is_approved) {
            this.is_approved = is_approved;
        }

        public String getApproved_by() {
            return approved_by;
        }

        public void setApproved_by(String approved_by) {
            this.approved_by = approved_by;
        }

        public String getCreated_by() {
            return created_by;
        }

        public void setCreated_by(String created_by) {
            this.created_by = created_by;
        }

        public String getUpdated_by() {
            return updated_by;
        }

        public void setUpdated_by(String updated_by) {
            this.updated_by = updated_by;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getType() {
            if (type != null) {
                return type;
            } else {
                return "";
            }
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSub_type_name() {
            if (sub_type_name != null) {
                return sub_type_name;
            } else {
                return "";
            }
        }

        public void setSub_type_name(String sub_type_name) {
            this.sub_type_name = sub_type_name;
        }

        public String getAssign_date() {
            return assign_date;
        }

        public void setAssign_date(String assign_date) {
            this.assign_date = assign_date;
        }

        public String getAvg_rating() {
            return avg_rating;
        }

        public void setAvg_rating(String avg_rating) {
            this.avg_rating = avg_rating;
        }

        public String getReview_title_by_user() {
            return review_title_by_user;
        }

        public void setReview_title_by_user(String review_title_by_user) {
            this.review_title_by_user = review_title_by_user;
        }

        public String getReview_description_by_user() {
            return review_description_by_user;
        }

        public void setReview_description_by_user(String review_description_by_user) {
            this.review_description_by_user = review_description_by_user;
        }

        public String getRating_by_user() {
            return rating_by_user;
        }

        public void setRating_by_user(String rating_by_user) {
            this.rating_by_user = rating_by_user;
        }

        public String getTotal_number_review() {
            return total_number_review;
        }

        public void setTotal_number_review(String total_number_review) {
            this.total_number_review = total_number_review;
        }

        public List<MobileNumberBean> getMobile_number() {
            return mobile_number;
        }

        public void setMobile_number(List<MobileNumberBean> mobile_number) {
            this.mobile_number = mobile_number;
        }

        public List<LandlineNumberBean> getLandline_number() {
            return landline_number;
        }

        public void setLandline_number(List<LandlineNumberBean> landline_number) {
            this.landline_number = landline_number;
        }

        public List<ImageUrlBean> getImage_url() {
            return image_url;
        }

        public void setImage_url(List<ImageUrlBean> image_url) {
            this.image_url = image_url;
        }

        public List<EmailsBean> getEmails() {
            return emails;
        }

        public void setEmails(List<EmailsBean> emails) {
            this.emails = emails;
        }

        public List<FaxBean> getFax() {
            return fax;
        }

        public void setFax(List<FaxBean> fax) {
            this.fax = fax;
        }

        public List<TagsBean> getTags() {
            return tags;
        }

        public void setTags(List<TagsBean> tags) {
            this.tags = tags;
        }

        public List<SenderDetailsBean> getSender_details() {
            return sender_details;
        }

        public void setSender_details(List<SenderDetailsBean> sender_details) {
            this.sender_details = sender_details;
        }

        public String getTypeSubType() {
            if (!getType().isEmpty() && !getSub_type_name().isEmpty()) {
                return getType() + " | " + getSub_type_name();
            } else if (!getType().isEmpty() && getSub_type_name().isEmpty()) {
                return getType();
            } else if (getType().isEmpty() && !getSub_type_name().isEmpty()) {
                return getSub_type_name();
            } else {
                return "";
            }
        }

        public static class MobileNumberBean implements Serializable {
            /**
             * id : 12
             * mobile : 89464816485
             * country_code : 91
             */

            private String id;
            private String mobile;
            private String country_code;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getCountry_code() {
                return "+" + country_code;
            }

            public void setCountry_code(String country_code) {
                this.country_code = country_code;
            }
        }

        public static class LandlineNumberBean implements Serializable {
            /**
             * id : 11
             * landlinenumbers : 5949678594
             * country_code : 91
             */

            private String id;
            private String landlinenumbers;
            private String country_code;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getLandlinenumbers() {
                return landlinenumbers;
            }

            public void setLandlinenumbers(String landlinenumbers) {
                this.landlinenumbers = landlinenumbers;
            }

            public String getCountry_code() {
                return "+" + country_code;
            }

            public void setCountry_code(String country_code) {
                this.country_code = country_code;
            }
        }

        public static class ImageUrlBean implements Serializable {
            /**
             * id : 16
             * images : uplimg-20200415030803.png
             */

            private String id;
            private String images;
            private String document_type;

            public ImageUrlBean(String document_type, String images, String id) {
                this.document_type = document_type;
                this.images = images;
                this.id = id;
            }

            public ImageUrlBean() {
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getImages() {
                return images;
            }

            public void setImages(String images) {
                this.images = images;
            }

            public String getDocument_type() {
                return document_type;
            }

            public void setDocument_type(String document_type) {
                this.document_type = document_type;
            }
        }

        public static class EmailsBean implements Serializable {
            /**
             * id : 18
             * office_id : 18
             * email : jfjeksk@jfkei.com
             */

            private String id;
            private String office_id;
            private String email;

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

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }
        }

        public static class FaxBean implements Serializable {
            /**
             * id : 15
             * office_id : 18
             * fax : 664997997979
             */

            private String id;
            private String office_id;
            private String fax;

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

            public String getFax() {
                return fax;
            }

            public void setFax(String fax) {
                this.fax = fax;
            }
        }

        public static class TagsBean implements Serializable {
            /**
             * id : 1
             * tag_id : 2115
             * tag_name : office_tag
             * is_approved : 1
             */

            private String id;
            private String tag_id;
            private String tag_name;
            private String is_approved;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getTag_id() {
                return tag_id;
            }

            public void setTag_id(String tag_id) {
                this.tag_id = tag_id;
            }

            public String getTag_name() {
                return tag_name;
            }

            public void setTag_name(String tag_name) {
                this.tag_name = tag_name;
            }

            public String getIs_approved() {
                return is_approved;
            }

            public void setIs_approved(String is_approved) {
                this.is_approved = is_approved;
            }
        }

        public static class SenderDetailsBean implements Serializable {
            /**
             * id : 1
             * tag_id : 2115
             * tag_name : office_tag
             * is_approved : 1
             */

            private String sender_id;
            private String first_name;
            private String image_url;
            private String mobile;
            private String country_code;

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

            public String getImage_url() {
                return image_url;
            }

            public void setImage_url(String image_url) {
                this.image_url = image_url;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getCountry_code() {
                return "+" + country_code;
            }

            public void setCountry_code(String country_code) {
                this.country_code = country_code;
            }
        }
    }
}