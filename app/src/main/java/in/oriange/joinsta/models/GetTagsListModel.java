package in.oriange.joinsta.models;

import java.util.ArrayList;

public class GetTagsListModel {

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

    public static class ResultBean {

        private String tagid;
        private String tag_name;
        private String is_approved;
        private String category_type_id;
        private String category_type_name;

        public String getTagid() {
            return tagid;
        }

        public void setTagid(String tagid) {
            this.tagid = tagid;
        }

        public String getTag_name() {
            return tag_name;
        }

        public void setTag_name(String tag_name) {
            this.tag_name = tag_name;
        }

        public String getIs_approved() {
            return is_approved;
        }

        public void setIs_approved(String is_approved) {
            this.is_approved = is_approved;
        }

        public String getCategory_type_id() {
            return category_type_id;
        }

        public void setCategory_type_id(String category_type_id) {
            this.category_type_id = category_type_id;
        }

        public String getCategory_type_name() {
            return category_type_name;
        }

        public void setCategory_type_name(String category_type_name) {
            this.category_type_name = category_type_name;
        }

        @Override
        public String toString() {
            return tag_name;
        }
    }
}
