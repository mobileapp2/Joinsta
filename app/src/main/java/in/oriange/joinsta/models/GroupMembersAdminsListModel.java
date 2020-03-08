package in.oriange.joinsta.models;

import java.io.Serializable;
import java.util.List;

public class GroupMembersAdminsListModel implements Serializable {


    /**
     * type : success
     * message : Group admin details returned successfully!
     * result : [{"role":"group_member","can_members_post":"1","first_name":"aaaaaa","password":"","country_code":"91","mobile":"9860392456","email":"aaaa@gmail.com","is_active":"0","is_hidden":"0","id":"1608","can_pos":"1","member_categories":[{"id":"11","group_member_id":"1608","member_category_id":"1","member_category":"demo"},{"id":"12","group_member_id":"1608","member_category_id":"2","member_category":"demo1"}],"is_joinsta_member":"0"},{"role":"group_member","can_members_post":"1","first_name":"by","password":"123456","country_code":"91","mobile":"9545114055","email":"varshaoriange@gmail.com","is_active":"0","is_hidden":"0","id":"1590","can_pos":"1","member_categories":[],"is_joinsta_member":"1"},{"role":"group_member","can_members_post":"1","first_name":"sendbuzz","password":"1111","country_code":"91","mobile":"9002943757","email":null,"is_active":"1","is_hidden":"0","id":"1579","can_pos":"1","member_categories":[],"is_joinsta_member":"1"},{"role":"group_admin","can_members_post":"1","first_name":"Tester","password":"11223344","country_code":"91","mobile":"8275460300","email":"ss@gmail.com","is_active":"1","is_hidden":"0","id":"1578","can_pos":"1","member_categories":[],"is_joinsta_member":"1"}]
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
         * role : group_member
         * can_members_post : 1
         * first_name : aaaaaa
         * password :
         * country_code : 91
         * mobile : 9860392456
         * email : aaaa@gmail.com
         * is_active : 0
         * is_hidden : 0
         * id : 1608
         * can_pos : 1
         * member_categories : [{"id":"11","group_member_id":"1608","member_category_id":"1","member_category":"demo"},{"id":"12","group_member_id":"1608","member_category_id":"2","member_category":"demo1"}]
         * is_joinsta_member : 0
         */

        private String role;
        private String can_members_post;
        private String first_name;
        private String password;
        private String country_code;
        private String mobile;
        private String email;
        private String is_active;
        private String is_hidden;
        private String id;
        private String can_pos;
        private String is_joinsta_member;
        private List<MemberCategoriesBean> member_categories;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getCan_members_post() {
            return can_members_post;
        }

        public void setCan_members_post(String can_members_post) {
            this.can_members_post = can_members_post;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getCountry_code() {
            return country_code;
        }

        public void setCountry_code(String country_code) {
            this.country_code = country_code;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getIs_active() {
            return is_active;
        }

        public void setIs_active(String is_active) {
            this.is_active = is_active;
        }

        public String getIs_hidden() {
            return is_hidden;
        }

        public void setIs_hidden(String is_hidden) {
            this.is_hidden = is_hidden;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCan_pos() {
            return can_pos;
        }

        public void setCan_pos(String can_pos) {
            this.can_pos = can_pos;
        }

        public String getIs_joinsta_member() {
            return is_joinsta_member;
        }

        public void setIs_joinsta_member(String is_joinsta_member) {
            this.is_joinsta_member = is_joinsta_member;
        }

        public List<MemberCategoriesBean> getMember_categories() {
            return member_categories;
        }

        public void setMember_categories(List<MemberCategoriesBean> member_categories) {
            this.member_categories = member_categories;
        }

        public static class MemberCategoriesBean implements Serializable {
            /**
             * id : 11
             * group_member_id : 1608
             * member_category_id : 1
             * member_category : demo
             */

            private String id;
            private String group_member_id;
            private String member_category_id;
            private String member_category;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getGroup_member_id() {
                return group_member_id;
            }

            public void setGroup_member_id(String group_member_id) {
                this.group_member_id = group_member_id;
            }

            public String getMember_category_id() {
                return member_category_id;
            }

            public void setMember_category_id(String member_category_id) {
                this.member_category_id = member_category_id;
            }

            public String getMember_category() {
                return member_category;
            }

            public void setMember_category(String member_category) {
                this.member_category = member_category;
            }
        }
    }
}
