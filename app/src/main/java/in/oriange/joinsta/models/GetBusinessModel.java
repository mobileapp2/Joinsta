package in.oriange.joinsta.models;

import java.io.Serializable;
import java.util.ArrayList;

public class GetBusinessModel implements Serializable {

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
        private String business_code;
        private String address;
        private String city;
        private String country;
        private String business_name;
        private String district;
        private String pincode;
        private String longitude;
        private String latitude;
        private String landmark;
        private String locality;
        private String state;
        private String designation;
        private String email;
        private String website;
        private String record_statusid;
        private String is_verified;
        private String created_at;
        private String updated_at;
        private String type_id;
        private String sub_type_id;
        private String created_by;
        private String updated_by;
        private String subtype_description;
        private String type_description;
        private String IsFavourite;
        private String UserIsFavourite;
        private String image_url;
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
        private ArrayList<ArrayList<MobilesBean>> mobiles;
        private ArrayList<ArrayList<LandlineBean>> landline;
        private ArrayList<ArrayList<TagBean>> tag;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getBusiness_code() {
            return business_code;
        }

        public void setBusiness_code(String business_code) {
            this.business_code = business_code;
        }

        public String getIs_verified() {
            return is_verified;
        }

        public void setIs_verified(String is_verified) {
            this.is_verified = is_verified;
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

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getBusiness_name() {
            return business_name;
        }

        public void setBusiness_name(String business_name) {
            this.business_name = business_name;
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

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getDesignation() {
            return designation;
        }

        public void setDesignation(String designation) {
            this.designation = designation;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getRecord_statusid() {
            return record_statusid;
        }

        public void setRecord_statusid(String record_statusid) {
            this.record_statusid = record_statusid;
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
            if (UserIsFavourite != null) {
                return UserIsFavourite;
            } else {
                return "0";
            }
        }

        public void setUserIsFavourite(String userIsFavourite) {
            UserIsFavourite = userIsFavourite;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
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

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getMobile_number() {
                return mobile_number.replace("-", "");
            }

            public void setMobile_number(String mobile_number) {
                this.mobile_number = mobile_number;
            }
        }

        public static class LandlineBean implements Serializable {

            private String id;
            private String landline_number;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getLandline_number() {
                return landline_number.replace("-", "");
            }

            public void setLandline_number(String landline_number) {
                this.landline_number = landline_number;
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
