package in.oriange.joinsta.models;

import java.io.Serializable;
import java.util.List;

import static in.oriange.joinsta.utilities.Utilities.changeDateFormat;

public class PublicOfficeRatingAndReviewModel implements Serializable {
    /**
     * type : success
     * message : Office reviews & ratings return successfully!
     * result : [{"user_name":"saba","public_name":"test name","image_url":"","id":"1","office_id":"2","rating":"3","review_title":"aaa","review_description":"abcd","user_id":"271","is_active":"1","is_deleted":"0","created_at":"2020-04-13 12:32:40","updated_at":"2020-04-13 12:32:40","avg_rating":3,"name":"first"},{"user_name":"saba","public_name":"test name","image_url":"","id":"2","office_id":"2","rating":"3","review_title":"aaa","review_description":"abcd","user_id":"271","is_active":"1","is_deleted":"0","created_at":"0000-00-00 00:00:00","updated_at":"0000-00-00 00:00:00","avg_rating":3,"name":"first"},{"user_name":"saba","public_name":"test name","image_url":"","id":"3","office_id":"2","rating":"3","review_title":"aaa","review_description":"abcd","user_id":"271","is_active":"1","is_deleted":"0","created_at":"0000-00-00 00:00:00","updated_at":"0000-00-00 00:00:00","avg_rating":3,"name":"first"}]
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
         * user_name : saba
         * public_name : test name
         * image_url :
         * id : 1
         * office_id : 2
         * rating : 3
         * review_title : aaa
         * review_description : abcd
         * user_id : 271
         * is_active : 1
         * is_deleted : 0
         * created_at : 2020-04-13 12:32:40
         * updated_at : 2020-04-13 12:32:40
         * avg_rating : 3
         * name : first
         */

        private String user_name;
        private String public_name;
        private String image_url;
        private String id;
        private String office_id;
        private String rating;
        private String review_title;
        private String review_description;
        private String user_id;
        private String is_active;
        private String is_deleted;
        private String created_at;
        private String updated_at;
        private String avg_rating;
        private String name;

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getPublic_name() {
            return public_name;
        }

        public void setPublic_name(String public_name) {
            this.public_name = public_name;
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

        public String getFormattedDate() {
            return changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd-MM-yyyy", getCreated_at());
        }

    }
}






