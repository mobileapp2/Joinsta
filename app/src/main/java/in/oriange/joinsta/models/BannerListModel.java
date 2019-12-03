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


        private String banner_name;
        private String banner_description;
        private String banners_image;

        public ResultBean() {
        }

        public ResultBean(String banner_name, String banner_description, String banners_image) {
            this.banner_name = banner_name;
            this.banner_description = banner_description;
            this.banners_image = banners_image;
        }

        public String getBanner_description() {
            return banner_description;
        }

        public void setBanner_description(String banner_description) {
            this.banner_description = banner_description;
        }

        public String getBanner_name() {
            return banner_name;
        }

        public void setBanner_name(String banner_name) {
            this.banner_name = banner_name;
        }

        public String getBanners_image() {
            return banners_image;
        }

        public void setBanners_image(String banners_image) {
            this.banners_image = banners_image;
        }

    }
}
