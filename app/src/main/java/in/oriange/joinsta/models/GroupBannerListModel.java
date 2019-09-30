package in.oriange.joinsta.models;

import java.util.List;

public class GroupBannerListModel {


    /**
     * type : success
     * message : getGroupBanners successfully!
     * result : [{"id":"1","banner_name":"sarika","banner_description":"banner for group","banners_image":"avengers-endgame-sign-2-20190930045108.jpg","start_date":"2019-09-01 00:00:00","end_date":"2019-09-30 00:00:00","created_at":"2019-09-30 22:21:12","created_by":"0","group_id":"3","group_name":"Test group"}]
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

    public static class ResultBean {
        /**
         * id : 1
         * banner_name : sarika
         * banner_description : banner for group
         * banners_image : avengers-endgame-sign-2-20190930045108.jpg
         * start_date : 2019-09-01 00:00:00
         * end_date : 2019-09-30 00:00:00
         * created_at : 2019-09-30 22:21:12
         * created_by : 0
         * group_id : 3
         * group_name : Test group
         */

        private String id;
        private String banner_name;
        private String banner_description;
        private String banners_image;
        private String start_date;
        private String end_date;
        private String created_at;
        private String created_by;
        private String group_id;
        private String group_name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getCreated_by() {
            return created_by;
        }

        public void setCreated_by(String created_by) {
            this.created_by = created_by;
        }

        public String getGroup_id() {
            return group_id;
        }

        public void setGroup_id(String group_id) {
            this.group_id = group_id;
        }

        public String getGroup_name() {
            return group_name;
        }

        public void setGroup_name(String group_name) {
            this.group_name = group_name;
        }
    }
}
