package in.oriange.joinsta.models;

import java.io.Serializable;
import java.util.List;

public class BookOrderProductsListModel implements Serializable {

    /**
     * type : success
     * message : Product data returned successfully!
     * result : [{"id":"1","name":"Product 01","description":"Description of Product 01","unit_of_measure":"BGS","max_retail_price":"270","selling_price":"270","remark":"Remark for Product 01","business_id":"18","created_by":"21","updated_by":"21","created_at":"2020-04-30 23:26:36","updated_at":"2020-04-30 23:26:36","product_images":["https://www.gstkhata.com/joinsta_test/joinsta_updated_live/images/product/test.jpg","https://www.gstkhata.com/joinsta_test/joinsta_updated_live/images/product/test.jpg"]}]
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
         * name : Product 01
         * description : Description of Product 01
         * unit_of_measure : BGS
         * max_retail_price : 270
         * selling_price : 270
         * remark : Remark for Product 01
         * business_id : 18
         * created_by : 21
         * updated_by : 21
         * created_at : 2020-04-30 23:26:36
         * updated_at : 2020-04-30 23:26:36
         * product_images : ["https://www.gstkhata.com/joinsta_test/joinsta_updated_live/images/product/test.jpg","https://www.gstkhata.com/joinsta_test/joinsta_updated_live/images/product/test.jpg"]
         */

        private String id;
        private String name;
        private String description;
        private String unit_of_measure;
        private String max_retail_price;
        private String selling_price;
        private String remark;
        private String business_id;
        private String created_by;
        private String updated_by;
        private String created_at;
        private String updated_at;
        private List<String> product_images;

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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUnit_of_measure() {
            return unit_of_measure;
        }

        public void setUnit_of_measure(String unit_of_measure) {
            this.unit_of_measure = unit_of_measure;
        }

        public String getMax_retail_price() {
            if (max_retail_price != null)
                if (!max_retail_price.equals(""))
                    return max_retail_price;
                else
                    return "0";
            else
                return "0";

//            return "100";
        }

        public void setMax_retail_price(String max_retail_price) {
            this.max_retail_price = max_retail_price;
        }

        public String getSelling_price() {
            if (selling_price != null)
                if (!selling_price.equals(""))
                    return selling_price;
                else
                    return "0";
            else
                return "0";

//            return "70";
        }

        public void setSelling_price(String selling_price) {
            this.selling_price = selling_price;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
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

        public List<String> getProduct_images() {
            return product_images;
        }

        public void setProduct_images(List<String> product_images) {
            this.product_images = product_images;
        }
    }
}
