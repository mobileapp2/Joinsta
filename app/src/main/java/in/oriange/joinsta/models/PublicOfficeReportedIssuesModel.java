package in.oriange.joinsta.models;

import java.util.List;

public class PublicOfficeReportedIssuesModel {

    /**
     * type : success
     * message : Office reported issues return successfully!
     * result : [{"first_name":"saba","country_code":"91","usermobile":"8180012531","id":"1","name":"abcd","title":"aa","description":"sjkdaskdjaskdjaskldj","user_id":"271","office_id":"2","is_attended":"1","attended_by":"271","mobile":"2147483647","created_at":"2020-04-13 16:13:24","updated_at":"2020-04-13 16:13:24"},{"first_name":"Priyesh Pawar","country_code":"91","usermobile":"8149115089","id":"2","name":"abcd","title":"qqqqqqqqqq","description":"zzzzzzzzz","user_id":"10","office_id":"2","is_attended":"0","attended_by":"","mobile":"2147483647","created_at":"2020-04-14 14:53:56","updated_at":"2020-04-14 14:53:56"},{"first_name":"Priyesh Pawar","country_code":"91","usermobile":"8149115089","id":"3","name":"abcd","title":"qqqqqqqqqq","description":"zzzzzzzzz","user_id":"10","office_id":"2","is_attended":"0","attended_by":"","mobile":"2147483647","created_at":"2020-04-14 16:28:25","updated_at":"2020-04-14 16:28:25"},{"first_name":"Priyesh Pawar","country_code":"91","usermobile":"8149115089","id":"4","name":"abcd","title":"qqqqqqqqqq","description":"zzzzzzzzz","user_id":"10","office_id":"2","is_attended":"0","attended_by":"","mobile":"2147483647","created_at":"2020-04-14 16:28:46","updated_at":"2020-04-14 16:28:46"},{"first_name":"saba","country_code":"91","usermobile":"8180012531","id":"5","name":"abcd","title":"aa","description":"sjkdaskdjaskdjaskldj","user_id":"271","office_id":"2","is_attended":"0","attended_by":"","mobile":"8180012531","created_at":"2020-04-14 16:34:08","updated_at":"2020-04-14 16:34:08"},{"first_name":"Priyesh Pawar","country_code":"91","usermobile":"8149115089","id":"6","name":"abcd","title":"qqqqqqqqqq","description":"zzzzzzzzz","user_id":"10","office_id":"2","is_attended":"0","attended_by":"","mobile":"8149115089","created_at":"2020-04-14 16:39:07","updated_at":"2020-04-14 16:39:07"}]
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
         * first_name : saba
         * country_code : 91
         * usermobile : 8180012531
         * id : 1
         * name : abcd
         * title : aa
         * description : sjkdaskdjaskdjaskldj
         * user_id : 271
         * office_id : 2
         * is_attended : 1
         * attended_by : 271
         * mobile : 2147483647
         * created_at : 2020-04-13 16:13:24
         * updated_at : 2020-04-13 16:13:24
         */

        private String first_name;
        private String country_code;
        private String usermobile;
        private String id;
        private String name;
        private String title;
        private String description;
        private String user_id;
        private String office_id;
        private String is_attended;
        private String attended_by;
        private String mobile;
        private String created_at;
        private String updated_at;

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getCountry_code() {
            return country_code;
        }

        public void setCountry_code(String country_code) {
            this.country_code = country_code;
        }

        public String getUsermobile() {
            return usermobile;
        }

        public void setUsermobile(String usermobile) {
            this.usermobile = usermobile;
        }

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

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getOffice_id() {
            return office_id;
        }

        public void setOffice_id(String office_id) {
            this.office_id = office_id;
        }

        public String getIs_attended() {
            return is_attended;
        }

        public void setIs_attended(String is_attended) {
            this.is_attended = is_attended;
        }

        public String getAttended_by() {
            return attended_by;
        }

        public void setAttended_by(String attended_by) {
            this.attended_by = attended_by;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
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
    }
}
