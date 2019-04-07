package in.oriange.joinsta.models;

public class SubCategotyListModel {


    private int imageId;
    private String subCategoryName;

    public SubCategotyListModel(int imageId, String subCategoryName) {
        this.imageId = imageId;
        this.subCategoryName = subCategoryName;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }
}
