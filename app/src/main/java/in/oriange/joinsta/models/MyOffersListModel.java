package in.oriange.joinsta.models;

import java.io.Serializable;
import java.util.List;

public class MyOffersListModel implements Serializable {


    /**
     * type : success
     * message : Offer details returned successfully!
     * result : [{"id":"17","category_type_id":"1","record_id":"129","title":"Ththth","description":"Dufufiuf","start_date":"2019-12-03","end_date":"2020-01-01","url":"www.gbgbgb.com","promo_code":"fvjtth","created_by":"10","updated_by":"10","created_at":"2019-11-28 18:24:47","updated_at":"2019-11-28 18:24:47","documents":[{"document":"uplimg-20191128125430.png"}]},{"id":"16","category_type_id":"1","record_id":"129","title":"Jvjvjv","description":"Jcjv vjvjjv. Jcjv. Jvvjv","start_date":"2019-11-28","end_date":"2019-12-19","url":"","promo_code":"","created_by":"10","updated_by":"10","created_at":"2019-11-28 18:22:16","updated_at":"2019-11-28 18:22:16","documents":[]},{"id":"15","category_type_id":"1","record_id":"129","title":"Jvjvjv","description":"Jcjv vjvjjv. Jcjv. Jvvjv","start_date":"2019-11-28","end_date":"2019-12-19","url":"","promo_code":"","created_by":"10","updated_by":"10","created_at":"2019-11-28 18:21:44","updated_at":"2019-11-28 18:21:44","documents":[{"document":"uplimg-20191128125100.png"}]},{"id":"14","category_type_id":"1","record_id":"129","title":"Jvjvjv","description":"Jcjv vjvjjv. Jcjv. Jvvjv","start_date":"2019-11-28","end_date":"2019-12-19","url":"","promo_code":"","created_by":"10","updated_by":"10","created_at":"2019-11-28 18:21:17","updated_at":"2019-11-28 18:21:17","documents":[]},{"id":"13","category_type_id":"1","record_id":"129","title":"Hygbgb","description":"Gbhgb gvgbg bggbgb hddhhd tcyvvg","start_date":"2019-11-28","end_date":"2019-12-11","url":"","promo_code":"","created_by":"10","updated_by":"10","created_at":"2019-11-28 18:20:18","updated_at":"2019-11-28 18:20:18","documents":[]},{"id":"10","category_type_id":"1","record_id":"129","title":"Jfdhdh","description":"Hddhhd dgdgdg dvdgsg","start_date":"2019-11-28","end_date":"2019-12-11","url":"www.dgdggs.com","promo_code":"","created_by":"10","updated_by":"10","created_at":"2019-11-28 18:17:49","updated_at":"2019-11-28 18:17:49","documents":[]},{"id":"9","category_type_id":"1","record_id":"129","title":"Jfdhdh","description":"Hddhhd dgdgdg dvdgsg","start_date":"2019-11-28","end_date":"2019-12-11","url":"www.dgdggs.com","promo_code":"","created_by":"10","updated_by":"10","created_at":"2019-11-28 18:15:42","updated_at":"2019-11-28 18:15:42","documents":[]},{"id":"8","category_type_id":"1","record_id":"129","title":"Jfdhdh","description":"Hddhhd dgdgdg dvdgsg","start_date":"2019-11-28","end_date":"2019-12-11","url":"www.dgdggs.com","promo_code":"","created_by":"10","updated_by":"10","created_at":"2019-11-28 18:15:11","updated_at":"2019-11-28 18:15:11","documents":[]},{"id":"7","category_type_id":"1","record_id":"129","title":"Tedt","description":"Vtgtgvvt vrtv dcfvnt dgcvhbrgrvsvtnny fvnynyhyvt dcfvnt brrb f fdv","start_date":"2019-11-28","end_date":"2019-12-11","url":"tctvtc","promo_code":"","created_by":"10","updated_by":"10","created_at":"2019-11-28 18:08:28","updated_at":"2019-11-28 18:08:28","documents":[]},{"id":"6","category_type_id":"1","record_id":"129","title":"Tedt","description":"Vtgtgvvt vrtv dcfvnt dgcvhbrgrvsvtnny fvnynyhyvt dcfvnt brrb f fdv","start_date":"2019-11-28","end_date":"2019-12-11","url":"tctvtc","promo_code":"","created_by":"10","updated_by":"10","created_at":"2019-11-28 18:06:57","updated_at":"2019-11-28 18:06:57","documents":[]},{"id":"5","category_type_id":"1","record_id":"129","title":"Tedt","description":"Vtgtgvvt vrtv dcfvnt dgcvhbrgrvsvtnny fvnynyhyvt dcfvnt brrb f fdv","start_date":"2019-11-28","end_date":"2019-12-11","url":"tctvtc","promo_code":"","created_by":"10","updated_by":"10","created_at":"2019-11-28 18:06:55","updated_at":"2019-11-28 18:06:55","documents":[]}]
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
         * id : 17
         * category_type_id : 1
         * record_id : 129
         * title : Ththth
         * description : Dufufiuf
         * start_date : 2019-12-03
         * end_date : 2020-01-01
         * url : www.gbgbgb.com
         * promo_code : fvjtth
         * created_by : 10
         * updated_by : 10
         * created_at : 2019-11-28 18:24:47
         * updated_at : 2019-11-28 18:24:47
         * documents : [{"document":"uplimg-20191128125430.png"}]
         */

        private String id;
        private String category_type_id;
        private String record_id;
        private String title;
        private String description;
        private String start_date;
        private String end_date;
        private String url;
        private String promo_code;
        private String created_by;
        private String updated_by;
        private String created_at;
        private String updated_at;
        private List<DocumentsBean> documents;

        public ResultBean() {
        }

        public ResultBean(String id, String category_type_id, String record_id, String title, String description, String start_date, String end_date, String url, String promo_code, String created_by, String updated_by, String created_at, String updated_at, List<DocumentsBean> documents) {
            this.id = id;
            this.category_type_id = category_type_id;
            this.record_id = record_id;
            this.title = title;
            this.description = description;
            this.start_date = start_date;
            this.end_date = end_date;
            this.url = url;
            this.promo_code = promo_code;
            this.created_by = created_by;
            this.updated_by = updated_by;
            this.created_at = created_at;
            this.updated_at = updated_at;
            this.documents = documents;
        }

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

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
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

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPromo_code() {
            return promo_code;
        }

        public void setPromo_code(String promo_code) {
            this.promo_code = promo_code;
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

        public List<DocumentsBean> getDocuments() {
            return documents;
        }

        public void setDocuments(List<DocumentsBean> documents) {
            this.documents = documents;
        }

        public static class DocumentsBean implements Serializable{

            /**
             * document : uplimg-20191128125430.png
             */

            public DocumentsBean() {
            }

            public DocumentsBean(String document) {
                this.document = document;
            }

            private String document;

            public String getDocument() {
                return document;
            }

            public void setDocument(String document) {
                this.document = document;
            }
        }
    }
}
