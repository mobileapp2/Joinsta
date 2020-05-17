package in.oriange.joinsta.models;

import java.io.Serializable;
import java.util.List;

public class BookOrderGetMyOrdersModel implements Serializable {


    /**
     * type : success
     * message : Order details returned successfully!
     * result : [{"id":"1","order_id":"2147483647","order_type":"1","order_text":"","owner_business_id":"18","purchase_order_type":"1","business_id":"0","created_by":"10","updated_by":"10","created_at":"2020-05-03 19:05:42","updated_at":"0000-00-00 00:00:00","order_images":["text.pdf"],"owner_business_code":"L00018","owner_business_name":"business name","owner_id":"21","owner_name":"Radhesham Biyani","owner_country_code":"91","owner_mobile":"8888654567","product_details":[{"id":"1","order_details_id":"1","product_id":"2","quantity":"2","amount":"540","current_amount":"340","name":"Product 02","description":"Description of Product 02","business_id":"27","product_images":["test.jpg"]}],"status_details":[{"status":"1","date":"2020-05-03 19:05:42"},{"status":"2","date":"2020-05-05 20:16:02"}]}]
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
         * id : 1
         * order_id : 2147483647
         * order_type : 1
         * order_text :
         * owner_business_id : 18
         * purchase_order_type : 1
         * business_id : 0
         * created_by : 10
         * updated_by : 10
         * created_at : 2020-05-03 19:05:42
         * updated_at : 0000-00-00 00:00:00
         * order_images : ["text.pdf"]
         * owner_business_code : L00018
         * owner_business_name : business name
         * owner_id : 21
         * owner_name : Radhesham Biyani
         * owner_country_code : 91
         * owner_mobile : 8888654567
         * product_details : [{"id":"1","order_details_id":"1","product_id":"2","quantity":"2","amount":"540","current_amount":"340","name":"Product 02","description":"Description of Product 02","business_id":"27","product_images":["test.jpg"]}]
         * status_details : [{"status":"1","date":"2020-05-03 19:05:42"},{"status":"2","date":"2020-05-05 20:16:02"}]
         */

        private String id;
        private String order_id;
        private String order_type;
        private String order_text;
        private String owner_business_id;
        private String purchase_order_type;
        private String business_id;
        private String created_by;
        private String updated_by;
        private String created_at;
        private String updated_at;
        private String owner_business_code;
        private String owner_business_name;
        private String owner_id;
        private String owner_name;
        private String owner_country_code;
        private String owner_mobile;
        private String business_code;
        private String business_name;
        private List<String> order_images;
        private List<ProductDetailsBean> product_details;
        private List<StatusDetailsBean> status_details;

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

        public String getOwner_business_code() {
            return owner_business_code;
        }

        public void setOwner_business_code(String owner_business_code) {
            this.owner_business_code = owner_business_code;
        }

        public String getOwner_business_name() {
            return owner_business_name;
        }

        public void setOwner_business_name(String owner_business_name) {
            this.owner_business_name = owner_business_name;
        }

        public String getOwner_id() {
            return owner_id;
        }

        public void setOwner_id(String owner_id) {
            this.owner_id = owner_id;
        }

        public String getOwner_name() {
            return owner_name;
        }

        public void setOwner_name(String owner_name) {
            this.owner_name = owner_name;
        }

        public String getOwner_country_code() {
            return owner_country_code;
        }

        public void setOwner_country_code(String owner_country_code) {
            this.owner_country_code = owner_country_code;
        }

        public String getOwner_mobile() {
            return owner_mobile;
        }

        public void setOwner_mobile(String owner_mobile) {
            this.owner_mobile = owner_mobile;
        }

        public String getBusiness_code() {
            return business_code;
        }

        public void setBusiness_code(String business_code) {
            this.business_code = business_code;
        }

        public String getBusiness_name() {
            return business_name;
        }

        public void setBusiness_name(String business_name) {
            this.business_name = business_name;
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

        public static class ProductDetailsBean implements Serializable {
            /**
             * id : 1
             * order_details_id : 1
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

            public ProductDetailsBean() {

            }

            public ProductDetailsBean(String id, String product_id, String order_details_id, String quantity, String amount) {
                this.id = id;
                this.product_id = product_id;
                this.order_details_id = order_details_id;
                this.quantity = quantity;
                this.amount = amount;
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
                if (current_amount != null)
                    if (!current_amount.equals(""))
                        return current_amount;
                    else
                        return "0";
                else
                    return "0";
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
             * date : 2020-05-03 19:05:42
             */

            private String status;
            private String date;

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
    }
}
