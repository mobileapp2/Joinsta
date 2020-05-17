package in.oriange.joinsta.models;

import java.io.Serializable;
import java.util.List;

public class BookOrderBusinessOwnerModel implements Serializable {

    /**
     * type : success
     * message : Order details returned successfully!
     * result : [{"id":"2","order_id":"202005081230129","order_type":"1","order_text":"","owner_business_id":"18","purchase_order_type":"2","business_id":"1027","is_deleted":"0","created_by":"9","updated_by":"9","created_at":"2020-05-08 12:30:12","updated_at":"2020-05-08 12:30:12","order_images":["uplimg-20200508045152.png"],"customer_id":"9","customer_name":"Arkay","customer_country_code":"91","customer_mobile":"7066755627","customer_business_code":"L01027","customer_business_name":"Anvita creations","customer_business_address":"test address","customer_business_email":"radhika.awad@gmail.com","customer_business_city":"Latur","product_details":[{"id":"2","order_details_id":"2","product_id":"2","quantity":"2","amount":"540","current_amount":"340","name":"Product 02","description":"Description of Product 02","business_id":"27","product_images":["test.jpg"]}],"status_details":[{"status":"1","date":"2020-05-08 12:30:12"}],"customer_business_mobile":[[{"id":"1441","mobile_number":"9320323232","country_code":"+91"}]],"customer_business_landline":[[{"id":"175","landline_number":"0233233232","country_code":"+91"}]]}]
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
         * id : 2
         * order_id : 202005081230129
         * order_type : 1
         * order_text :
         * owner_business_id : 18
         * purchase_order_type : 2
         * business_id : 1027
         * is_deleted : 0
         * created_by : 9
         * updated_by : 9
         * created_at : 2020-05-08 12:30:12
         * updated_at : 2020-05-08 12:30:12
         * order_images : ["uplimg-20200508045152.png"]
         * customer_id : 9
         * customer_name : Arkay
         * customer_country_code : 91
         * customer_mobile : 7066755627
         * customer_business_code : L01027
         * customer_business_name : Anvita creations
         * customer_business_address : test address
         * customer_business_email : radhika.awad@gmail.com
         * customer_business_city : Latur
         * product_details : [{"id":"2","order_details_id":"2","product_id":"2","quantity":"2","amount":"540","current_amount":"340","name":"Product 02","description":"Description of Product 02","business_id":"27","product_images":["test.jpg"]}]
         * status_details : [{"status":"1","date":"2020-05-08 12:30:12"}]
         * customer_business_mobile : [[{"id":"1441","mobile_number":"9320323232","country_code":"+91"}]]
         * customer_business_landline : [[{"id":"175","landline_number":"0233233232","country_code":"+91"}]]
         */

        private String id;
        private String order_id;
        private String order_type;
        private String order_text;
        private String owner_business_id;
        private String purchase_order_type;
        private String business_id;
        private String is_deleted;
        private String created_by;
        private String updated_by;
        private String created_at;
        private String updated_at;
        private String customer_id;
        private String customer_name;
        private String customer_country_code;
        private String customer_mobile;
        private String customer_email;
        private String customer_business_code;
        private String customer_business_name;
        private String customer_business_address;
        private String customer_business_email;
        private String customer_business_city;
        private List<String> order_images;
        private List<ProductDetailsBean> product_details;
        private List<StatusDetailsBean> status_details;
        private List<List<CustomerBusinessMobileBean>> customer_business_mobile;
        private List<List<CustomerBusinessLandlineBean>> customer_business_landline;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getOrder_id() {
            return order_id;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }

        public String getOrder_type() {
            return order_type;
        }

        public void setOrder_type(String order_type) {
            this.order_type = order_type;
        }

        public String getOrder_text() {
            return order_text;
        }

        public void setOrder_text(String order_text) {
            this.order_text = order_text;
        }

        public String getOwner_business_id() {
            return owner_business_id;
        }

        public void setOwner_business_id(String owner_business_id) {
            this.owner_business_id = owner_business_id;
        }

        public String getPurchase_order_type() {
            return purchase_order_type;
        }

        public void setPurchase_order_type(String purchase_order_type) {
            this.purchase_order_type = purchase_order_type;
        }

        public String getBusiness_id() {
            return business_id;
        }

        public void setBusiness_id(String business_id) {
            this.business_id = business_id;
        }

        public String getIs_deleted() {
            return is_deleted;
        }

        public void setIs_deleted(String is_deleted) {
            this.is_deleted = is_deleted;
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

        public String getCustomer_id() {
            return customer_id;
        }

        public void setCustomer_id(String customer_id) {
            this.customer_id = customer_id;
        }

        public String getCustomer_name() {
            return customer_name;
        }

        public void setCustomer_name(String customer_name) {
            this.customer_name = customer_name;
        }

        public String getCustomer_country_code() {
            return customer_country_code;
        }

        public void setCustomer_country_code(String customer_country_code) {
            this.customer_country_code = customer_country_code;
        }

        public String getCustomer_mobile() {
            return customer_mobile;
        }

        public void setCustomer_mobile(String customer_mobile) {
            this.customer_mobile = customer_mobile;
        }

        public String getCustomer_email() {
            return customer_email;
        }

        public void setCustomer_email(String customer_email) {
            this.customer_email = customer_email;
        }

        public String getCustomer_business_code() {
            return customer_business_code;
        }

        public void setCustomer_business_code(String customer_business_code) {
            this.customer_business_code = customer_business_code;
        }

        public String getCustomer_business_name() {
            return customer_business_name;
        }

        public void setCustomer_business_name(String customer_business_name) {
            this.customer_business_name = customer_business_name;
        }

        public String getCustomer_business_address() {
            return customer_business_address;
        }

        public void setCustomer_business_address(String customer_business_address) {
            this.customer_business_address = customer_business_address;
        }

        public String getCustomer_business_email() {
            return customer_business_email;
        }

        public void setCustomer_business_email(String customer_business_email) {
            this.customer_business_email = customer_business_email;
        }

        public String getCustomer_business_city() {
            return customer_business_city;
        }

        public void setCustomer_business_city(String customer_business_city) {
            this.customer_business_city = customer_business_city;
        }

        public List<String> getOrder_images() {
            return order_images;
        }

        public void setOrder_images(List<String> order_images) {
            this.order_images = order_images;
        }

        public List<ProductDetailsBean> getProduct_details() {
            return product_details;
        }

        public void setProduct_details(List<ProductDetailsBean> product_details) {
            this.product_details = product_details;
        }

        public List<StatusDetailsBean> getStatus_details() {
            return status_details;
        }

        public void setStatus_details(List<StatusDetailsBean> status_details) {
            this.status_details = status_details;
        }

        public List<List<CustomerBusinessMobileBean>> getCustomer_business_mobile() {
            return customer_business_mobile;
        }

        public void setCustomer_business_mobile(List<List<CustomerBusinessMobileBean>> customer_business_mobile) {
            this.customer_business_mobile = customer_business_mobile;
        }

        public List<List<CustomerBusinessLandlineBean>> getCustomer_business_landline() {
            return customer_business_landline;
        }

        public void setCustomer_business_landline(List<List<CustomerBusinessLandlineBean>> customer_business_landline) {
            this.customer_business_landline = customer_business_landline;
        }

        public static class ProductDetailsBean implements Serializable {
            /**
             * id : 2
             * order_details_id : 2
             * product_id : 2
             * quantity : 2
             * amount : 540
             * current_amount : 340
             * name : Product 02
             * description : Description of Product 02
             * business_id : 27
             * product_images : ["test.jpg"]
             */

            private String id;
            private String order_details_id;
            private String product_id;
            private String quantity;
            private String amount;
            private String current_amount;
            private String name;
            private String description;
            private String business_id;
            private String unit_of_measure;
            private List<String> product_images;

            public ProductDetailsBean(String id, String order_details_id, String product_id, String quantity, String amount, String current_amount, String name, String description, String business_id, String unit_of_measure, List<String> product_images) {
                this.id = id;
                this.order_details_id = order_details_id;
                this.product_id = product_id;
                this.quantity = quantity;
                this.amount = amount;
                this.current_amount = current_amount;
                this.name = name;
                this.description = description;
                this.business_id = business_id;
                this.unit_of_measure = unit_of_measure;
                this.product_images = product_images;
            }

            public ProductDetailsBean() {
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getOrder_details_id() {
                return order_details_id;
            }

            public void setOrder_details_id(String order_details_id) {
                this.order_details_id = order_details_id;
            }

            public String getProduct_id() {
                return product_id;
            }

            public void setProduct_id(String product_id) {
                this.product_id = product_id;
            }

            public String getQuantity() {
                return quantity;
            }

            public void setQuantity(String quantity) {
                this.quantity = quantity;
            }

            public String getAmount() {
                if (amount != null)
                    if (!amount.equals(""))
                        return amount;
                    else
                        return "0";
                else
                    return "0";
            }

            public void setAmount(String amount) {
                this.amount = amount;
            }

            public String getCurrent_amount() {
                return current_amount;
            }

            public void setCurrent_amount(String current_amount) {
                this.current_amount = current_amount;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getBusiness_id() {
                return business_id;
            }

            public void setBusiness_id(String business_id) {
                this.business_id = business_id;
            }

            public String getUnit_of_measure() {
                return unit_of_measure;
            }

            public void setUnit_of_measure(String unit_of_measure) {
                this.unit_of_measure = unit_of_measure;
            }

            public List<String> getProduct_images() {
                return product_images;
            }

            public void setProduct_images(List<String> product_images) {
                this.product_images = product_images;
            }
        }

        public static class StatusDetailsBean implements Serializable {
            /**
             * status : 1
             * date : 2020-05-08 12:30:12
             */

            private String status;
            private String date;

            public StatusDetailsBean(String status, String date) {
                this.status = status;
                this.date = date;
            }

            public StatusDetailsBean() {
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }
        }

        public static class CustomerBusinessMobileBean implements Serializable {
            /**
             * id : 1441
             * mobile_number : 9320323232
             * country_code : +91
             */

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
                return mobile_number;
            }

            public void setMobile_number(String mobile_number) {
                this.mobile_number = mobile_number;
            }

            public String getCountry_code() {
                return country_code;
            }

            public void setCountry_code(String country_code) {
                this.country_code = country_code;
            }
        }

        public static class CustomerBusinessLandlineBean implements Serializable {
            /**
             * id : 175
             * landline_number : 0233233232
             * country_code : +91
             */

            private String id;
            private String landline_number;
            private String country_code;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getLandline_number() {
                return landline_number;
            }

            public void setLandline_number(String landline_number) {
                this.landline_number = landline_number;
            }

            public String getCountry_code() {
                return country_code;
            }

            public void setCountry_code(String country_code) {
                this.country_code = country_code;
            }
        }
    }
}
