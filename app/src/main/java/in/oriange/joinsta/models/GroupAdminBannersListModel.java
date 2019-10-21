package in.oriange.joinsta.models;

import java.io.Serializable;
import java.util.List;

public class GroupAdminBannersListModel implements Serializable {


    /**
     * type : success
     * message : Group banner details returned successfully!
     * result : [{"id":"39","banner_name":"testing","banner_description":"testing demo","banners_image":"download1-20191014095700.jpg","start_date":"2019-10-10","end_date":"2019-10-15","created_at":"2019-10-18 15:29:05","created_by":"0","group_id":"2","group_name":"Group by saba "},{"id":"38","banner_name":"test banner 1111","banner_description":"hk111","banners_image":"11-20191003022816-20191010122319.png","start_date":"2019-10-15","end_date":"2019-10-30","created_at":"2019-10-10 17:53:46","created_by":"0","group_id":"2","group_name":"Group by saba "},{"id":"35","banner_name":"bannernew","banner_description":"hipune","banners_image":"cir-20191004062720.jpg","start_date":"2019-10-01","end_date":"2019-10-24","created_at":"2019-10-04 11:57:20","created_by":"0","group_id":"7","group_name":"mita"},{"id":"32","banner_name":"Test group banners 22","banner_description":"jkljfekq","banners_image":"2-20191002030422.jpg","start_date":"2019-10-03","end_date":"2019-10-04","created_at":"2019-10-04 11:49:27","created_by":"0","group_id":"3","group_name":"Test group"},{"id":"26","banner_name":"bannernavratri","banner_description":"its a navrartri banner","banners_image":"2018-02-13-20191002062554.jpg","start_date":"2019-10-02","end_date":"2019-10-02","created_at":"2019-10-02 19:30:31","created_by":"0","group_id":"3","group_name":"Test group"},{"id":"22","banner_name":"banner4","banner_description":"tfhgf","banners_image":"grwpl-20191002053133.jpg","start_date":"2019-10-01","end_date":"2019-10-31","created_at":"2019-10-04 11:49:38","created_by":"0","group_id":"3","group_name":"Test group"},{"id":"17","banner_name":"groups related banners","banner_description":"gb","banners_image":"lib-20191002052254.jpg","start_date":"2019-10-01","end_date":"2019-09-30","created_at":"2019-10-02 11:00:54","created_by":"0","group_id":"4","group_name":"Test group"},{"id":"15","banner_name":"Test group banners 1122","banner_description":"Test group banners 1122","banners_image":"ads7-20191001031227.jpg","start_date":"2019-10-02","end_date":"2019-10-13","created_at":"2019-10-04 11:52:28","created_by":"0","group_id":"3","group_name":"Test group"},{"id":"14","banner_name":"Test group banners 22","banner_description":"TEst descc 2433y5y","banners_image":"flag-gm-20191001024605.png","start_date":"2019-10-02","end_date":"2019-10-05","created_at":"2019-10-01 20:16:07","created_by":"0","group_id":"4","group_name":"Test group"},{"id":"10","banner_name":"Test group banners 44","banner_description":"test group banners 44","banners_image":"fl-en-20191001024700.png","start_date":"2019-10-02","end_date":"2019-10-11","created_at":"2019-10-01 20:17:02","created_by":"0","group_id":"2","group_name":"Group by saba "},{"id":"5","banner_name":"advertisement for group","banner_description":"groups information ","banners_image":"login-20191001125758.jpg","start_date":"2019-10-31","end_date":"2019-11-30","created_at":"2019-10-01 18:27:59","created_by":"0","group_id":"3","group_name":"Test group"},{"id":"4","banner_name":"sarika","banner_description":"adversiment ","banners_image":"grwpl-20191001125644.jpg","start_date":"2019-10-01","end_date":"2019-10-31","created_at":"2019-10-01 18:26:45","created_by":"0","group_id":"7","group_name":"mita"}]
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

    public static class ResultBean implements Serializable{
        /**
         * id : 39
         * banner_name : testing
         * banner_description : testing demo
         * banners_image : download1-20191014095700.jpg
         * start_date : 2019-10-10
         * end_date : 2019-10-15
         * created_at : 2019-10-18 15:29:05
         * created_by : 0
         * group_id : 2
         * group_name : Group by saba
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
