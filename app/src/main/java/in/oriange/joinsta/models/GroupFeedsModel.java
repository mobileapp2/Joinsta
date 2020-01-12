package in.oriange.joinsta.models;

import java.io.Serializable;
import java.util.List;

public class GroupFeedsModel implements Serializable {


    /**
     * type : success
     * message : Offer details returned successfully!
     * result : [{"id":"32","group_id":"92","type_id":"1","feed_text":"Djjsjakakka jsnansks snsan dNsks wskwnw\nDkdkskks nrje ejebee enwkeenen ejebee e e e\nDkdkskks ekdjsnsjre rktenwnjr ejebee jrbe \nwww.google.com","feed_doc":"uplimg-20200107071409.png","is_admin":"1","is_private":"0","is_hidden":"0","is_deleted":"0","created_by":"271","updated_by":"271","created_at":"2020-01-07 23:35:58","updated_at":"2020-01-08 00:44:16","can_share":"0","type":"personal","first_name":"Saba","image_url":"","feed_documents":[{"id":"2","feed_details_id":"39","documents":"5_6053197452373131396-20200107071039-20200112125712.pdf"},{"id":"3","feed_details_id":"39","documents":"9-android-fundamentals-services-m9-slides-20200112125722.pdf"}],"is_favourite":0,"feed_comments":[{"id":"65","feed_id":"32","message":"ndjskakaw","is_private":"0","is_deleted":"0","created_by":"271","created_at":"2020-01-12 18:36:47","first_name":"Saba","image_url":"","comment_reply":[{"id":"21","comment_id":"65","message":"djjsjakakka dNsks","is_private":"0","is_deleted":"0","created_by":"271","created_at":"2020-01-12 18:37:16","first_name":"Saba","image_url":""}]}]},{"id":"33","group_id":"92","type_id":"1","feed_text":"Djjskak dNsks dNsks dNsks snsns","feed_doc":"","is_admin":"1","is_private":"0","is_hidden":"0","is_deleted":"0","created_by":"271","updated_by":"271","created_at":"2020-01-07 23:36:30","updated_at":"2020-01-07 23:36:30","can_share":"0","type":"personal","first_name":"Saba","image_url":"","feed_documents":[],"is_favourite":0,"feed_comments":[]},{"id":"38","group_id":"92","type_id":"1","feed_text":"Nsjskkakwk","feed_doc":"","is_admin":"1","is_private":"0","is_hidden":"0","is_deleted":"0","created_by":"271","updated_by":"271","created_at":"2020-01-12 18:04:05","updated_at":"2020-01-12 18:04:05","can_share":"0","type":"personal","first_name":"Saba","image_url":"","feed_documents":[{"id":"1","feed_details_id":"38","documents":""}],"is_favourite":0,"feed_comments":[]},{"id":"39","group_id":"92","type_id":"1","feed_text":"Dksksksk","feed_doc":"uplimg-20200112125654.png","is_admin":"1","is_private":"0","is_hidden":"0","is_deleted":"0","created_by":"271","updated_by":"271","created_at":"2020-01-12 18:27:23","updated_at":"2020-01-12 18:27:23","can_share":"1","type":"personal","first_name":"Saba","image_url":"","feed_documents":[{"id":"2","feed_details_id":"39","documents":"5_6053197452373131396-20200107071039-20200112125712.pdf"},{"id":"3","feed_details_id":"39","documents":"9-android-fundamentals-services-m9-slides-20200112125722.pdf"}],"is_favourite":0,"feed_comments":[]}]
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
         * id : 32
         * group_id : 92
         * type_id : 1
         * feed_text : Djjsjakakka jsnansks snsan dNsks wskwnw
         * Dkdkskks nrje ejebee enwkeenen ejebee e e e
         * Dkdkskks ekdjsnsjre rktenwnjr ejebee jrbe
         * www.google.com
         * feed_doc : uplimg-20200107071409.png
         * is_admin : 1
         * is_private : 0
         * is_hidden : 0
         * is_deleted : 0
         * created_by : 271
         * updated_by : 271
         * created_at : 2020-01-07 23:35:58
         * updated_at : 2020-01-08 00:44:16
         * can_share : 0
         * type : personal
         * first_name : Saba
         * image_url :
         * feed_documents : [{"id":"2","feed_details_id":"39","documents":"5_6053197452373131396-20200107071039-20200112125712.pdf"},{"id":"3","feed_details_id":"39","documents":"9-android-fundamentals-services-m9-slides-20200112125722.pdf"}]
         * is_favourite : 0
         * feed_comments : [{"id":"65","feed_id":"32","message":"ndjskakaw","is_private":"0","is_deleted":"0","created_by":"271","created_at":"2020-01-12 18:36:47","first_name":"Saba","image_url":"","comment_reply":[{"id":"21","comment_id":"65","message":"djjsjakakka dNsks","is_private":"0","is_deleted":"0","created_by":"271","created_at":"2020-01-12 18:37:16","first_name":"Saba","image_url":""}]}]
         */

        private String id;
        private String group_id;
        private String type_id;
        private String feed_text;
        private String feed_doc;
        private String is_admin;
        private String is_private;
        private String is_hidden;
        private String is_deleted;
        private String created_by;
        private String updated_by;
        private String created_at;
        private String updated_at;
        private String can_share;
        private String type;
        private String first_name;
        private String image_url;
        private int is_favourite;
        private List<FeedDocumentsBean> feed_documents;
        private List<FeedCommentsBean> feed_comments;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getGroup_id() {
            return group_id;
        }

        public void setGroup_id(String group_id) {
            this.group_id = group_id;
        }

        public String getType_id() {
            return type_id;
        }

        public void setType_id(String type_id) {
            this.type_id = type_id;
        }

        public String getFeed_text() {
            return feed_text;
        }

        public void setFeed_text(String feed_text) {
            this.feed_text = feed_text;
        }

        public String getFeed_doc() {
            return feed_doc;
        }

        public void setFeed_doc(String feed_doc) {
            this.feed_doc = feed_doc;
        }

        public String getIs_admin() {
            return is_admin;
        }

        public void setIs_admin(String is_admin) {
            this.is_admin = is_admin;
        }

        public String getIs_private() {
            return is_private;
        }

        public void setIs_private(String is_private) {
            this.is_private = is_private;
        }

        public String getIs_hidden() {
            return is_hidden;
        }

        public void setIs_hidden(String is_hidden) {
            this.is_hidden = is_hidden;
        }

        public String getIs_deleted() {
            return is_deleted;
        }

        public void setIs_deleted(String is_deleted) {
            this.is_deleted = is_deleted;
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

        public String getCan_share() {
            return can_share;
        }

        public void setCan_share(String can_share) {
            this.can_share = can_share;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public int getIs_favourite() {
            return is_favourite;
        }

        public void setIs_favourite(int is_favourite) {
            this.is_favourite = is_favourite;
        }

        public List<FeedDocumentsBean> getFeed_documents() {
            return feed_documents;
        }

        public void setFeed_documents(List<FeedDocumentsBean> feed_documents) {
            this.feed_documents = feed_documents;
        }

        public List<FeedCommentsBean> getFeed_comments() {
            return feed_comments;
        }

        public void setFeed_comments(List<FeedCommentsBean> feed_comments) {
            this.feed_comments = feed_comments;
        }

        public static class FeedDocumentsBean implements Serializable {
            /**
             * id : 2
             * feed_details_id : 39
             * documents : 5_6053197452373131396-20200107071039-20200112125712.pdf
             */

            private String id;
            private String feed_details_id;
            private String documents;

            public FeedDocumentsBean(String documents, String id) {
                this.documents = documents;
                this.id = id;
            }

            public FeedDocumentsBean() {
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getFeed_details_id() {
                return feed_details_id;
            }

            public void setFeed_details_id(String feed_details_id) {
                this.feed_details_id = feed_details_id;
            }

            public String getDocuments() {
                return documents;
            }

            public void setDocuments(String documents) {
                this.documents = documents;
            }
        }

        public static class FeedCommentsBean implements Serializable {
            /**
             * id : 65
             * feed_id : 32
             * message : ndjskakaw
             * is_private : 0
             * is_deleted : 0
             * created_by : 271
             * created_at : 2020-01-12 18:36:47
             * first_name : Saba
             * image_url :
             * comment_reply : [{"id":"21","comment_id":"65","message":"djjsjakakka dNsks","is_private":"0","is_deleted":"0","created_by":"271","created_at":"2020-01-12 18:37:16","first_name":"Saba","image_url":""}]
             */

            private String id;
            private String feed_id;
            private String message;
            private String is_private;
            private String is_deleted;
            private String created_by;
            private String created_at;
            private String first_name;
            private String image_url;
            private List<CommentReplyBean> comment_reply;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getFeed_id() {
                return feed_id;
            }

            public void setFeed_id(String feed_id) {
                this.feed_id = feed_id;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            public String getIs_private() {
                return is_private;
            }

            public void setIs_private(String is_private) {
                this.is_private = is_private;
            }

            public String getIs_deleted() {
                return is_deleted;
            }

            public void setIs_deleted(String is_deleted) {
                this.is_deleted = is_deleted;
            }

            public String getCreated_by() {
                return created_by;
            }

            public void setCreated_by(String created_by) {
                this.created_by = created_by;
            }

            public String getCreated_at() {
                return created_at;
            }

            public void setCreated_at(String created_at) {
                this.created_at = created_at;
            }

            public String getFirst_name() {
                return first_name;
            }

            public void setFirst_name(String first_name) {
                this.first_name = first_name;
            }

            public String getImage_url() {
                return image_url;
            }

            public void setImage_url(String image_url) {
                this.image_url = image_url;
            }

            public List<CommentReplyBean> getComment_reply() {
                return comment_reply;
            }

            public void setComment_reply(List<CommentReplyBean> comment_reply) {
                this.comment_reply = comment_reply;
            }

            public static class CommentReplyBean implements Serializable {
                /**
                 * id : 21
                 * comment_id : 65
                 * message : djjsjakakka dNsks
                 * is_private : 0
                 * is_deleted : 0
                 * created_by : 271
                 * created_at : 2020-01-12 18:37:16
                 * first_name : Saba
                 * image_url :
                 */

                private String id;
                private String comment_id;
                private String message;
                private String is_private;
                private String is_deleted;
                private String created_by;
                private String created_at;
                private String first_name;
                private String image_url;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getComment_id() {
                    return comment_id;
                }

                public void setComment_id(String comment_id) {
                    this.comment_id = comment_id;
                }

                public String getMessage() {
                    return message;
                }

                public void setMessage(String message) {
                    this.message = message;
                }

                public String getIs_private() {
                    return is_private;
                }

                public void setIs_private(String is_private) {
                    this.is_private = is_private;
                }

                public String getIs_deleted() {
                    return is_deleted;
                }

                public void setIs_deleted(String is_deleted) {
                    this.is_deleted = is_deleted;
                }

                public String getCreated_by() {
                    return created_by;
                }

                public void setCreated_by(String created_by) {
                    this.created_by = created_by;
                }

                public String getCreated_at() {
                    return created_at;
                }

                public void setCreated_at(String created_at) {
                    this.created_at = created_at;
                }

                public String getFirst_name() {
                    return first_name;
                }

                public void setFirst_name(String first_name) {
                    this.first_name = first_name;
                }

                public String getImage_url() {
                    return image_url;
                }

                public void setImage_url(String image_url) {
                    this.image_url = image_url;
                }
            }
        }
    }
}
