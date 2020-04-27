package in.oriange.joinsta.models;

import java.io.Serializable;
import java.util.List;

import static in.oriange.joinsta.utilities.Utilities.changeDateFormat;

public class PublicRatingAndReviewModel {

    /**
     * type : success
     * message : get  profile reviews ratings  successfully!
     * result : [{"id":"2","category_type_id":"1","record_id":"5","rating":"4","review_title":"","review_description":"bad","user_id":"271","is_active":"0","is_deleted":"0","created_at":"2020-03-27 11:16:45","updated_at":"2020-03-27 11:16:45","category_type":"Business","avg_rating":4,"name":"IT Services","code":"00005","user_name":"saba","image_url":""},{"id":"3","category_type_id":"1","record_id":"5","rating":"4","review_title":"","review_description":"bad","user_id":"271","is_active":"0","is_deleted":"0","created_at":"2020-03-27 11:33:38","updated_at":"2020-03-27 11:33:38","category_type":"Business","avg_rating":4,"name":"IT Services","code":"00005","user_name":"saba","image_url":""},{"id":"10","category_type_id":"1","record_id":"5","rating":"4","review_title":"good","review_description":"good","user_id":"12","is_active":"1","is_deleted":"0","created_at":"2020-03-28 17:06:20","updated_at":"2020-03-28 17:06:20","category_type":"Business","avg_rating":4,"name":"IT Services","code":"00005","user_name":"saba","image_url":""},{"id":"11","category_type_id":"1","record_id":"5","rating":"4","review_title":"good","review_description":"good","user_id":"12","is_active":"1","is_deleted":"0","created_at":"2020-03-28 17:06:43","updated_at":"2020-03-28 17:06:43","category_type":"Business","avg_rating":4,"name":"IT Services","code":"00005","user_name":"saba","image_url":""}]
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
         * category_type_id : 1
         * record_id : 5
         * rating : 4
         * review_title :
         * review_description : bad
         * user_id : 271
         * is_active : 0
         * is_deleted : 0
         * created_at : 2020-03-27 11:16:45
         * updated_at : 2020-03-27 11:16:45
         * category_type : Business
         * avg_rating : 4
         * name : IT Services
         * code : 00005
         * user_name : saba
         * image_url :
         */

        private String id;
        private String category_type_id;
        private String record_id;
        private String rating;
        private String review_title;
        private String review_description;
        private String user_id;
        private String is_active;
        private String is_deleted;
        private String created_at;
        private String updated_at;
        private String category_type;
        private String avg_rating;
        private String name;
        private String code;
        private String user_name;
        private String image_url;
        private String public_name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCategory_type_id() {
            return category_type_id;
        }

        public void setCategory_type_id(String category_type_id) {
            this.category_type_id = category_type_id;
        }

        public String getRecord_id() {
            return record_id;
        }

        public void setRecord_id(String record_id) {
            this.record_id = record_id;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public String getReview_title() {
            return review_title;
        }

        public void setReview_title(String review_title) {
            this.review_title = review_title;
        }

        public String getReview_description() {
            return review_description;
        }

        public void setReview_description(String review_description) {
            this.review_description = review_description;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getIs_active() {
            return is_active;
        }

        public void setIs_active(String is_active) {
            this.is_active = is_active;
        }

        public String getIs_deleted() {
            return is_deleted;
        }

        public void setIs_deleted(String is_deleted) {
            this.is_deleted = is_deleted;
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

        public String getCategory_type() {
            return category_type;
        }

        public void setCategory_type(String category_type) {
            this.category_type = category_type;
        }

        public String getAvg_rating() {
            return avg_rating;
        }

        public void setAvg_rating(String avg_rating) {
            this.avg_rating = avg_rating;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public String getPublic_name() {
            return public_name;
        }

        public void setPublic_name(String public_name) {
            this.public_name = public_name;
        }

        public String getFormattedDate() {
            return changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd-MM-yyyy", getCreated_at());
        }

    }
}
