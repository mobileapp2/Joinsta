package in.oriange.joinsta.models;

import java.io.Serializable;
import java.util.ArrayList;

public class AllGroupsListModel implements Serializable {


    /**
     * type : success
     * message : Groups returned successfully!
     * result : [{"id":"6","group_name":"New Group Admin By Namrata","group_code":"0006","group_description":"Test New Group","status":"requested","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin"},{"first_name":"Test Group Memeber","mobile":"8878765434","role":"group_member"},{"first_name":"Monika  ","mobile":"8830648438","role":"group_member"},{"first_name":" shagufta ","mobile":"7841095280","role":"group_member"},{"first_name":" shagufta ","mobile":"7841095280","role":"group_member"},{"first_name":" shagufta ","mobile":"7841095280","role":"group_member"},{"first_name":" shagufta ","mobile":"7841095280","role":"group_supervisor"},{"first_name":"Priyesh Pawar    ","mobile":"8149115089","role":"group_member"}]},{"id":"2","group_name":"Test group 1","group_code":"0002","group_description":"Group description 1","status":"left","Group_Member_Details":[{"first_name":"Radhika        ","mobile":"9237294738","role":"group_admin"},{"first_name":"Monika  ","mobile":"8830648438","role":"group_member"},{"first_name":"Monika  ","mobile":"8830648438","role":"group_member"},{"first_name":"Priyesh Pawar    ","mobile":"8149115089","role":"group_member"}]},{"id":"18","group_name":"New Group Admin","group_code":"0018","group_description":"Test New Group","status":"requested","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin"},{"first_name":"Priyesh Pawar    ","mobile":"8149115089","role":"group_member"}]},{"id":"1","group_name":"Test group 1","group_code":"0001","group_description":"Group description 1","status":"","Group_Member_Details":[{"first_name":"Radhika        ","mobile":"9237294738","role":"group_admin"},{"first_name":"Monika  ","mobile":"8830648438","role":"group_member"}]},{"id":"3","group_name":"Joinsta Admins","group_code":"0003","group_description":"Joinsta Admin","status":"","Group_Member_Details":[{"first_name":"Radhika          ","mobile":"9689282338","role":"group_admin"},{"first_name":"Test supervisor    ","mobile":"9032930247","role":"group_supervisor"},{"first_name":"sandesh","mobile":"9049396189","role":"group_member"},{"first_name":"Sarika","mobile":"9307728041","role":"group_member"},{"first_name":"Sarika","mobile":"9307728041","role":"group_supervisor"},{"first_name":"Varsharani","mobile":"8275460300","role":"group_supervisor"},{"first_name":"komal ","mobile":"7420901225","role":"group_member"},{"first_name":"qqqq","mobile":"","role":"group_member"}]},{"id":"4","group_name":"Joinsta Admins test","group_code":"0004","group_description":"Joinsta Admin test","status":"","Group_Member_Details":[{"first_name":"Radhika          ","mobile":"9689282338","role":"group_admin"},{"first_name":"komal","mobile":"8007140035","role":"group_member"},{"first_name":"komal","mobile":"8007140035","role":"group_supervisor"},{"first_name":"komal","mobile":"8007140035","role":"group_supervisor"},{"first_name":"Test supervisor 11","mobile":"9637617162","role":"group_supervisor"}]},{"id":"5","group_name":"namrata","group_code":"0005","group_description":"employee","status":"","Group_Member_Details":[{"first_name":"staff    ","mobile":"654655654","role":"group_admin"}]},{"id":"7","group_name":"wueqi","group_code":"0007","group_description":"kjqwe","status":"","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin"}]},{"id":"8","group_name":"wueqi","group_code":"0008","group_description":"kjqwe","status":"","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin"}]},{"id":"9","group_name":"wueqi","group_code":"0009","group_description":"kjqwe","status":"","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin"}]},{"id":"10","group_name":"New Group Admin","group_code":"0010","group_description":"Test New Group","status":"","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin"}]},{"id":"11","group_name":"New Group Admin","group_code":"0011","group_description":"Test New Group","status":"","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin"}]},{"id":"12","group_name":"New Group Admin","group_code":"0012","group_description":"Test New Group","status":"","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin"}]},{"id":"13","group_name":"New Group Admin","group_code":"0013","group_description":"Test New Group","status":"","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin"}]},{"id":"14","group_name":"New Group Admin","group_code":"0014","group_description":"Test New Group","status":"","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin"}]},{"id":"15","group_name":"New Group Admin","group_code":"0015","group_description":"Test New Group","status":"","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin"}]},{"id":"16","group_name":"New Group Admin","group_code":"0016","group_description":"Test New Group","status":"","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin"}]},{"id":"17","group_name":"New Group Admin","group_code":"0017","group_description":"Test New Group","status":"","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin"}]},{"id":"19","group_name":"New Group Admin By Namrata","group_code":"0019","group_description":"Test New Group","status":"","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin"}]},{"id":"20","group_name":"New Group Admin By Namrata","group_code":"0020","group_description":"Test New Group","status":"","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin"}]},{"id":"21","group_name":"New Group Admin By Namrata","group_code":"0021","group_description":"Test New Group","status":"","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin"}]},{"id":"22","group_name":"New Group Admin By Namrata","group_code":"0022","group_description":"Test New Group","status":"","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin"}]},{"id":"23","group_name":"New Group Admin By Namrata","group_code":"0023","group_description":"Test New Group","status":"","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin"}]},{"id":"24","group_name":"New Group Admin By Namrata","group_code":"0024","group_description":"Test New Group","status":"","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin"}]},{"id":"25","group_name":"New Group Admin","group_code":"0025","group_description":"Test New Group","status":"","Group_Member_Details":[{"first_name":"qqqq","mobile":"","role":"group_admin"}]},{"id":"26","group_name":"New Group Admin","group_code":"0026","group_description":"Test New Group","status":"","Group_Member_Details":[{"first_name":"qqqq","mobile":"","role":"group_admin"}]},{"id":"27","group_name":"New Group Admin","group_code":"0027","group_description":"Test New Group","status":"","Group_Member_Details":[{"first_name":"qqqq","mobile":"","role":"group_admin"}]},{"id":"28","group_name":"New Group Admin","group_code":"0028","group_description":"Test New Group","status":"","Group_Member_Details":[{"first_name":"qqqq","mobile":"","role":"group_admin"}]},{"id":"29","group_name":"New Group Admin","group_code":"0029","group_description":"Test New Group","status":"","Group_Member_Details":[{"first_name":"qqqq","mobile":"","role":"group_admin"}]}]
     */

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

    public static class ResultBean implements Serializable {
        /**
         * id : 6
         * group_name : New Group Admin By Namrata
         * group_code : 0006
         * group_description : Test New Group
         * status : requested
         * Group_Member_Details : [{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin"},{"first_name":"Test Group Memeber","mobile":"8878765434","role":"group_member"},{"first_name":"Monika  ","mobile":"8830648438","role":"group_member"},{"first_name":" shagufta ","mobile":"7841095280","role":"group_member"},{"first_name":" shagufta ","mobile":"7841095280","role":"group_member"},{"first_name":" shagufta ","mobile":"7841095280","role":"group_member"},{"first_name":" shagufta ","mobile":"7841095280","role":"group_supervisor"},{"first_name":"Priyesh Pawar    ","mobile":"8149115089","role":"group_member"}]
         */

        private String id;
        private String group_name;
        private String group_code;
        private String group_description;
        private String status;
        private String is_admin;
        private String is_visible;
        private String is_public_group;
        private ArrayList<GroupMemberDetailsBean> Group_Member_Details;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getGroup_name() {
            return group_name;
        }

        public void setGroup_name(String group_name) {
            this.group_name = group_name;
        }

        public String getGroup_code() {
            return group_code;
        }

        public void setGroup_code(String group_code) {
            this.group_code = group_code;
        }

        public String getGroup_description() {
            return group_description;
        }

        public void setGroup_description(String group_description) {
            this.group_description = group_description;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getIs_admin() {
            return is_admin;
        }

        public void setIs_admin(String is_admin) {
            this.is_admin = is_admin;
        }

        public String getIs_visible() {
            return is_visible;
        }

        public void setIs_visible(String is_visible) {
            this.is_visible = is_visible;
        }

        public String getIs_public_group() {
            return is_public_group;
        }

        public void setIs_public_group(String is_public_group) {
            this.is_public_group = is_public_group;
        }

        public ArrayList<GroupMemberDetailsBean> getGroup_Member_Details() {
            return Group_Member_Details;
        }

        public void setGroup_Member_Details(ArrayList<GroupMemberDetailsBean> Group_Member_Details) {
            this.Group_Member_Details = Group_Member_Details;
        }

        public static class GroupMemberDetailsBean implements Serializable {
            /**
             * first_name : Monika
             * mobile : 8830648438
             * role : group_admin
             */

            private String id;
            private String first_name;
            private String mobile;
            private String role;
            private String image_url;
            private String is_joinsta_member;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getFirst_name() {
                return first_name;
            }

            public void setFirst_name(String first_name) {
                this.first_name = first_name;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getRole() {
                return role;
            }

            public void setRole(String role) {
                this.role = role;
            }

            public String getImage_url() {
                return image_url;
            }

            public void setImage_url(String image_url) {
                this.image_url = image_url;
            }

            public String getIs_joinsta_member() {
                return is_joinsta_member;
            }

            public void setIs_joinsta_member(String is_joinsta_member) {
                this.is_joinsta_member = is_joinsta_member;
            }
        }
    }
}
