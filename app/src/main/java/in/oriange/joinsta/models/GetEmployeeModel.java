package in.oriange.joinsta.models;

import java.io.Serializable;
import java.util.ArrayList;

public class GetEmployeeModel implements Serializable {

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

    public static class ResultBean implements Serializable {

        private String id;
        private String employee_code;
        private String address;
        private String city;
        private String state;
        private String country;
        private String district;
        private String pincode;
        private String longitude;
        private String latitude;
        private String landmark;
        private String locality;
        private String email;
        private String type_id;
        private String sub_type_id;
        private String designation;
        private String organization_name;
        private String record_status_id;
        private String blood_group_id;
        private String website;
        private String is_active;
        private String is_verified;
        private String other_details;
        private String image_url;
        private String created_at;
        private String updated_at;
        private String created_by;
        private String updated_by;
        private String subtype_description;
        private String type_description;
        private String IsFavourite;
        private String UserIsFavourite;
        private String bank_id;
        private String account_holder_name;
        private String bank_alias;
        private String bank_name;
        private String ifsc_code;
        private String account_no;
        private String bank_status;
        private String tax_id;
        private String tax_name;
        private String tax_alias;
        private String pan_number;
        private String gst_number;
        private String tax_status;
        private String is_visible;
        private String offer_count;
        private String order_online;
        private String avg_rating;
        private String review_title_by_user;
        private String review_description_by_user;
        private String rating_by_user;
        private String total_number_review;
        private ArrayList<ArrayList<MobilesBean>> mobiles;
        private ArrayList<ArrayList<LandlineBean>> landline;
        private ArrayList<ArrayList<TagBean>> tag;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEmployee_code() {
            return employee_code;
        }

        public void setEmployee_code(String employee_code) {
            this.employee_code = employee_code;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
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

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLandmark() {
            return landmark;
        }

        public void setLandmark(String landmark) {
            this.landmark = landmark;
        }

        public String getLocality() {
            return locality;
        }

        public void setLocality(String locality) {
            this.locality = locality;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getDesignation() {
            return designation;
        }

        public void setDesignation(String designation) {
            this.designation = designation;
        }

        public String getOrganization_name() {
            return organization_name;
        }

        public void setOrganization_name(String organization_name) {
            this.organization_name = organization_name;
        }

        public String getRecord_status_id() {
            return record_status_id;
        }

        public void setRecord_status_id(String record_status_id) {
            this.record_status_id = record_status_id;
        }

        public String getBlood_group_id() {
            return blood_group_id;
        }

        public void setBlood_group_id(String blood_group_id) {
            this.blood_group_id = blood_group_id;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getIs_active() {
            return is_active;
        }

        public void setIs_active(String is_active) {
            this.is_active = is_active;
        }

        public String getIs_verified() {
            return is_verified;
        }

        public void setIs_verified(String is_verified) {
            this.is_verified = is_verified;
        }

        public String getOther_details() {
            return other_details;
        }

        public void setOther_details(String other_details) {
            this.other_details = other_details;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
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

        public String getSubtype_description() {
            return subtype_description;
        }

        public void setSubtype_description(String subtype_description) {
            this.subtype_description = subtype_description;
        }

        public String getType_description() {
            return type_description;
        }

        public void setType_description(String type_description) {
            this.type_description = type_description;
        }

        public String getIsFavourite() {
            return IsFavourite;
        }

        public void setIsFavourite(String IsFavourite) {
            this.IsFavourite = IsFavourite;
        }

        public String getUserIsFavourite() {
            return UserIsFavourite;
        }

        public void setUserIsFavourite(String userIsFavourite) {
            UserIsFavourite = userIsFavourite;
        }

        public String getType_id() {
            return type_id;
        }

        public void setType_id(String type_id) {
            this.type_id = type_id;
        }

        public String getSub_type_id() {
            return sub_type_id;
        }

        public void setSub_type_id(String sub_type_id) {
            this.sub_type_id = sub_type_id;
        }

        public String getBank_id() {
            if (bank_id != null) {
                return bank_id;
            } else {
                return "";
            }
        }

        public void setBank_id(String bank_id) {
            this.bank_id = bank_id;
        }

        public String getAccount_holder_name() {
            if (account_holder_name != null) {
                return account_holder_name;
            } else {
                return "";
            }
        }

        public void setAccount_holder_name(String account_holder_name) {
            this.account_holder_name = account_holder_name;
        }

        public String getBank_alias() {
            if (bank_alias != null) {
                return bank_alias;
            } else {
                return "";
            }
        }

        public void setBank_alias(String bank_alias) {
            this.bank_alias = bank_alias;
        }

        public String getBank_name() {
            if (bank_name != null) {
                return bank_name;
            } else {
                return "";
            }
        }

        public void setBank_name(String bank_name) {
            this.bank_name = bank_name;
        }

        public String getIfsc_code() {
            if (ifsc_code != null) {
                return ifsc_code;
            } else {
                return "";
            }
        }

        public void setIfsc_code(String ifsc_code) {
            this.ifsc_code = ifsc_code;
        }

        public String getAccount_no() {
            if (account_no != null) {
                return account_no;
            } else {
                return "";
            }
        }

        public void setAccount_no(String account_no) {
            this.account_no = account_no;
        }

        public String getBank_status() {
            if (bank_status != null) {
                return bank_status;
            } else {
                return "";
            }
        }

        public void setBank_status(String bank_status) {
            this.bank_status = bank_status;
        }

        public String getTax_id() {
            if (tax_id != null) {
                return tax_id;
            } else {
                return "";
            }
        }

        public void setTax_id(String tax_id) {
            this.tax_id = tax_id;
        }

        public String getTax_name() {
            if (tax_name != null) {
                return tax_name;
            } else {
                return "";
            }
        }

        public void setTax_name(String tax_name) {
            this.tax_name = tax_name;
        }

        public String getTax_alias() {
            if (tax_alias != null) {
                return tax_alias;
            } else {
                return "";
            }
        }

        public void setTax_alias(String tax_alias) {
            this.tax_alias = tax_alias;
        }

        public String getPan_number() {
            if (pan_number != null) {
                return pan_number;
            } else {
                return "";
            }
        }

        public void setPan_number(String pan_number) {
            this.pan_number = pan_number;
        }

        public String getGst_number() {
            if (gst_number != null) {
                return gst_number;
            } else {
                return "";
            }
        }

        public void setGst_number(String gst_number) {
            this.gst_number = gst_number;
        }

        public String getTax_status() {
            if (tax_status != null) {
                return tax_status;
            } else {
                return "";
            }
        }

        public void setTax_status(String tax_status) {
            this.tax_status = tax_status;
        }


        public String getIs_visible() {
            if (is_visible != null) {
                return is_visible;
            } else {
                return "0";
            }
        }

        public void setIs_visible(String is_visible) {
            this.is_visible = is_visible;
        }

        public String getOffer_count() {
            if (offer_count != null) {
                return offer_count;
            } else {
                return "0";
            }
        }

        public void setOffer_count(String offer_count) {
            this.offer_count = offer_count;
        }

        public String getOrder_online() {
            if (order_online != null) {
                return order_online;
            } else {
                return "";
            }
        }

        public void setOrder_online(String order_online) {
            this.order_online = order_online;
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

        public ArrayList<ArrayList<MobilesBean>> getMobiles() {
            return mobiles;
        }

        public void setMobiles(ArrayList<ArrayList<MobilesBean>> mobiles) {
            this.mobiles = mobiles;
        }

        public ArrayList<ArrayList<LandlineBean>> getLandline() {
            return landline;
        }

        public void setLandline(ArrayList<ArrayList<LandlineBean>> landline) {
            this.landline = landline;
        }

        public ArrayList<ArrayList<TagBean>> getTag() {
            return tag;
        }

        public void setTag(ArrayList<ArrayList<TagBean>> tag) {
            this.tag = tag;
        }

        public static class MobilesBean implements Serializable {

            private String id;
            private String mobile_number;
            private String country_code;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getMobile_number() {
                return mobile_number.replace("-", "").replace("+", "");
            }

            public void setMobile_number(String mobile_number) {
                this.mobile_number = mobile_number;
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

        public static class LandlineBean implements Serializable {

            private String id;
            private String landline_numbers;
            private String country_code;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getLandline_numbers() {
                return landline_numbers.replace("-", "").replace("+", "");
            }

            public void setLandline_numbers(String landline_numbers) {
                this.landline_numbers = landline_numbers;
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

        public static class TagBean implements Serializable {
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

            public String getTag_name() {
                if (tag_name == null) {
                    return "";
                } else {
                    return tag_name;
                }
            }

            public void setTag_name(String tag_name) {
                this.tag_name = tag_name;
            }

            public String getTag_id() {
                return tag_id;
            }

            public void setTag_id(String tag_id) {
                this.tag_id = tag_id;
            }

            public String getIs_approved() {
                if (is_approved == null) {
                    return "0";
                } else {
                    return is_approved;
                }
            }

            public void setIs_approved(String is_approved) {
                this.is_approved = is_approved;
            }
        }
    }
}
