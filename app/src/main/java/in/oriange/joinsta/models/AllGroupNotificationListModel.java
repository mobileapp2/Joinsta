package in.oriange.joinsta.models;

import java.util.List;

public class AllGroupNotificationListModel {


    /**
     * type : success
     * message : Notification details returned successfully!
     * result : [{"id":"11","group_name":"new test group","group_code":"0011","group_description":"software developers,software engireer,software and hardware all software related project hgggggggggggggggggggggggggggggggj","Group_Member_Details":[{"msg_details_id":"154","id":"89","subject":"Test","message":"Test","send_by":"1","is_deleted":"0","created_at":"2019-10-25 00:00:23","group_id":"11","user_id":"10","group_name":"new test group","groupnotification_id":"11","is_read":"1"}]}]
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
         * id : 11
         * group_name : new test group
         * group_code : 0011
         * group_description : software developers,software engireer,software and hardware all software related project hgggggggggggggggggggggggggggggggj
         * Group_Member_Details : [{"msg_details_id":"154","id":"89","subject":"Test","message":"Test","send_by":"1","is_deleted":"0","created_at":"2019-10-25 00:00:23","group_id":"11","user_id":"10","group_name":"new test group","groupnotification_id":"11","is_read":"1"}]
         */

        private String id;
        private String group_name;
        private String group_code;
        private String group_description;
        private List<GroupMemberDetailsBean> Group_Member_Details;

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

        public List<GroupMemberDetailsBean> getGroup_Member_Details() {
            return Group_Member_Details;
        }

        public void setGroup_Member_Details(List<GroupMemberDetailsBean> Group_Member_Details) {
            this.Group_Member_Details = Group_Member_Details;
        }

        public static class GroupMemberDetailsBean {
            /**
             * msg_details_id : 154
             * id : 89
             * subject : Test
             * message : Test
             * send_by : 1
             * is_deleted : 0
             * created_at : 2019-10-25 00:00:23
             * group_id : 11
             * user_id : 10
             * group_name : new test group
             * groupnotification_id : 11
             * is_read : 1
             */

            private String msg_details_id;
            private String id;
            private String subject;
            private String message;
            private String send_by;
            private String is_deleted;
            private String created_at;
            private String group_id;
            private String user_id;
            private String group_name;
            private String groupnotification_id;
            private String is_read;
            private String attachment;

            public String getMsg_details_id() {
                return msg_details_id;
            }

            public void setMsg_details_id(String msg_details_id) {
                this.msg_details_id = msg_details_id;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getSubject() {
                return subject;
            }

            public void setSubject(String subject) {
                this.subject = subject;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            public String getSend_by() {
                return send_by;
            }

            public void setSend_by(String send_by) {
                this.send_by = send_by;
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

            public String getGroup_id() {
                return group_id;
            }

            public void setGroup_id(String group_id) {
                this.group_id = group_id;
            }

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getGroup_name() {
                return group_name;
            }

            public void setGroup_name(String group_name) {
                this.group_name = group_name;
            }

            public String getGroupnotification_id() {
                return groupnotification_id;
            }

            public void setGroupnotification_id(String groupnotification_id) {
                this.groupnotification_id = groupnotification_id;
            }

            public String getIs_read() {
                return is_read;
            }

            public void setIs_read(String is_read) {
                this.is_read = is_read;
            }

            public String getAttachment() {
                if (attachment != null) {
                    return attachment;
                } else {
                    return "";
                }
            }

            public void setAttachment(String attachment) {
                this.attachment = attachment;
            }
        }
    }
}
