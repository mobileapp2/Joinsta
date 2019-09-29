package in.oriange.joinsta.models;

import java.util.List;

public class BannerListModel {

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

        private String id;
        private String location;
        private String category_type_id;
        private String category_id;
        private String sub_category_id;
        private String start_date;
        private String end_date;
        private String created_by;
        private String created_at;
        private String banner_name;
        private String banner_description;
        private String banners_image;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getCategory_type_id() {
            return category_type_id;
        }

        public void setCategory_type_id(String category_type_id) {
            this.category_type_id = category_type_id;
        }

        public String getCategory_id() {
            return category_id;
        }

        public void setCategory_id(String category_id) {
            this.category_id = category_id;
        }

        public String getSub_category_id() {
            return sub_category_id;
        }

        public void setSub_category_id(String sub_category_id) {
            this.sub_category_id = sub_category_id;
        }

        public String getStart_date() {
            return start_date;
        }

        public void setStart_date(String start_date) {
            this.start_date = start_date;
        }

        public String getEnd_date() {
            return end_date;
        }

        public void setEnd_date(String end_date) {
            this.end_date = end_date;
        }

        public String getCreated_by() {
            return created_by;
        }

        public void setCreated_by(String created_by) {
            this.created_by = created_by;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getBanner_name() {
            return banner_name;
        }

        public void setBanner_name(String banner_name) {
            this.banner_name = banner_name;
        }

        public String getBanner_description() {
            return banner_description;
        }

        public void setBanner_description(String banner_description) {
            this.banner_description = banner_description;
        }

        public String getBanners_image() {
            return banners_image;
        }

        public void setBanners_image(String banners_image) {
            this.banners_image = banners_image;
        }

    }
}
