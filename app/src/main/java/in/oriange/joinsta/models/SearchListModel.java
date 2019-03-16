package in.oriange.joinsta.models;

public class SearchListModel {

    private String type;
    private String subType;
    private String subSubType;
    private String isFavourite;

    public SearchListModel(String type, String subType, String subSubType, String isFavourite) {
        this.type = type;
        this.subType = subType;
        this.subSubType = subSubType;
        this.isFavourite = isFavourite;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getSubSubType() {
        return subSubType;
    }

    public void setSubSubType(String subSubType) {
        this.subSubType = subSubType;
    }

    public String getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(String isFavourite) {
        this.isFavourite = isFavourite;
    }
}
