package in.oriange.joinsta.models;

import java.io.Serializable;
import java.util.ArrayList;

public class AllGroupsListModel implements Serializable {


    /**
     * type : success
     * message : Groups returned successfully!
     * result : [{"id":"1","group_name":"Test group 1","group_code":"0001","group_description":"Group description 1","Group_Member_Details":[{"first_name":"Radhika        ","mobile":"9237294738","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"},{"first_name":"Monika  ","mobile":"8830648438","role":"group_member","status":"left","is_hidden":"0","allow_notifiication":"1"}]},{"id":"2","group_name":"Test group 1","group_code":"0002","group_description":"Group description 1","Group_Member_Details":[{"first_name":"Radhika        ","mobile":"9237294738","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"},{"first_name":"Monika  ","mobile":"8830648438","role":"group_member","status":"requested","is_hidden":"0","allow_notifiication":"0"},{"first_name":"Monika  ","mobile":"8830648438","role":"group_member","status":"requested","is_hidden":"0","allow_notifiication":"0"}]},{"id":"3","group_name":"Joinsta Admins","group_code":"0003","group_description":"Joinsta Admin","Group_Member_Details":[{"first_name":"Radhika          ","mobile":"9689282338","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"},{"first_name":"Test supervisor    ","mobile":"9032930247","role":"group_supervisor","status":"accepted","is_hidden":"0","allow_notifiication":"0"},{"first_name":"sandesh","mobile":"9049396189","role":"group_member","status":"requested","is_hidden":"0","allow_notifiication":"0"},{"first_name":"Sarika","mobile":"9307728041","role":"group_member","status":"accepted","is_hidden":"0","allow_notifiication":"0"},{"first_name":"Sarika","mobile":"9307728041","role":"group_supervisor","status":"accepted","is_hidden":"0","allow_notifiication":"0"},{"first_name":"Varsharani","mobile":"8275460300","role":"group_supervisor","status":"accepted","is_hidden":"0","allow_notifiication":"0"}]},{"id":"4","group_name":"Joinsta Admins test","group_code":"0004","group_description":"Joinsta Admin test","Group_Member_Details":[{"first_name":"Radhika          ","mobile":"9689282338","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"},{"first_name":"komal","mobile":"8007140035","role":"group_member","status":"requested","is_hidden":"0","allow_notifiication":"0"},{"first_name":"komal","mobile":"8007140035","role":"group_supervisor","status":"accepted","is_hidden":"0","allow_notifiication":"0"},{"first_name":"komal","mobile":"8007140035","role":"group_supervisor","status":"accepted","is_hidden":"0","allow_notifiication":"0"}]},{"id":"5","group_name":"namrata","group_code":"0005","group_description":"employee","Group_Member_Details":[{"first_name":"staff    ","mobile":"654655654","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"}]},{"id":"6","group_name":"New Group Admin By Namrata","group_code":"0006","group_description":"Test New Group","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"},{"first_name":"Test Group Memeber","mobile":"8878765434","role":"group_member","status":"accepted","is_hidden":"0","allow_notifiication":"0"},{"first_name":"Monika  ","mobile":"8830648438","role":"group_member","status":"accepted","is_hidden":"0","allow_notifiication":"0"},{"first_name":" shagufta ","mobile":"7841095280","role":"group_member","status":"accepted","is_hidden":"0","allow_notifiication":"0"},{"first_name":" shagufta ","mobile":"7841095280","role":"group_member","status":"accepted","is_hidden":"0","allow_notifiication":"0"},{"first_name":" shagufta ","mobile":"7841095280","role":"group_member","status":"accepted","is_hidden":"0","allow_notifiication":"0"},{"first_name":" shagufta ","mobile":"7841095280","role":"group_supervisor","status":"accepted","is_hidden":"0","allow_notifiication":"0"}]},{"id":"7","group_name":"wueqi","group_code":"0007","group_description":"kjqwe","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"}]},{"id":"8","group_name":"wueqi","group_code":"0008","group_description":"kjqwe","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"}]},{"id":"9","group_name":"wueqi","group_code":"0009","group_description":"kjqwe","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"}]},{"id":"10","group_name":"New Group Admin","group_code":"0010","group_description":"Test New Group","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"}]},{"id":"11","group_name":"New Group Admin","group_code":"0011","group_description":"Test New Group","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"}]},{"id":"12","group_name":"New Group Admin","group_code":"0012","group_description":"Test New Group","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"}]},{"id":"13","group_name":"New Group Admin","group_code":"0013","group_description":"Test New Group","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"}]},{"id":"14","group_name":"New Group Admin","group_code":"0014","group_description":"Test New Group","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"}]},{"id":"15","group_name":"New Group Admin","group_code":"0015","group_description":"Test New Group","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"}]},{"id":"16","group_name":"New Group Admin","group_code":"0016","group_description":"Test New Group","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"}]},{"id":"17","group_name":"New Group Admin","group_code":"0017","group_description":"Test New Group","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"}]},{"id":"18","group_name":"New Group Admin","group_code":"0018","group_description":"Test New Group","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"}]},{"id":"19","group_name":"New Group Admin By Namrata","group_code":"0019","group_description":"Test New Group","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"}]},{"id":"20","group_name":"New Group Admin By Namrata","group_code":"0020","group_description":"Test New Group","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"}]},{"id":"21","group_name":"New Group Admin By Namrata","group_code":"0021","group_description":"Test New Group","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"}]},{"id":"22","group_name":"New Group Admin By Namrata","group_code":"0022","group_description":"Test New Group","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"}]},{"id":"23","group_name":"New Group Admin By Namrata","group_code":"0023","group_description":"Test New Group","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"}]},{"id":"24","group_name":"New Group Admin By Namrata","group_code":"0024","group_description":"Test New Group","Group_Member_Details":[{"first_name":"Monika  ","mobile":"8830648438","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"}]},{"id":"25","group_name":"New Group Admin","group_code":"0025","group_description":"Test New Group","Group_Member_Details":[{"first_name":"qqqq","mobile":"","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"}]},{"id":"26","group_name":"New Group Admin","group_code":"0026","group_description":"Test New Group","Group_Member_Details":[{"first_name":"qqqq","mobile":"","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"}]},{"id":"27","group_name":"New Group Admin","group_code":"0027","group_description":"Test New Group","Group_Member_Details":[{"first_name":"qqqq","mobile":"","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"}]},{"id":"28","group_name":"New Group Admin","group_code":"0028","group_description":"Test New Group","Group_Member_Details":[{"first_name":"qqqq","mobile":"","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"}]},{"id":"29","group_name":"New Group Admin","group_code":"0029","group_description":"Test New Group","Group_Member_Details":[{"first_name":"qqqq","mobile":"","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"}]}]
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
         * id : 1
         * group_name : Test group 1
         * group_code : 0001
         * group_description : Group description 1
         * Group_Member_Details : [{"first_name":"Radhika        ","mobile":"9237294738","role":"group_admin","status":"accepted","is_hidden":"0","allow_notifiication":"0"},{"first_name":"Monika  ","mobile":"8830648438","role":"group_member","status":"left","is_hidden":"0","allow_notifiication":"1"}]
         */

        private String id;
        private String group_name;
        private String group_code;
        private String group_description;
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

        public ArrayList<GroupMemberDetailsBean> getGroup_Member_Details() {
            return Group_Member_Details;
        }

        public void setGroup_Member_Details(ArrayList<GroupMemberDetailsBean> Group_Member_Details) {
            this.Group_Member_Details = Group_Member_Details;
        }

        public static class GroupMemberDetailsBean implements Serializable {
            /**
             * first_name : Radhika        
             * mobile : 9237294738
             * role : group_admin
             * status : accepted
             * is_hidden : 0
             * allow_notifiication : 0
             */

            private String first_name;
            private String mobile;
            private String role;
            private String status;
            private String is_hidden;
            private String allow_notifiication;

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

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getIs_hidden() {
                return is_hidden;
            }

            public void setIs_hidden(String is_hidden) {
                this.is_hidden = is_hidden;
            }

            public String getAllow_notifiication() {
                return allow_notifiication;
            }

            public void setAllow_notifiication(String allow_notifiication) {
                this.allow_notifiication = allow_notifiication;
            }
        }
    }
}
