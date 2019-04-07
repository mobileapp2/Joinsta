package in.oriange.joinsta.models;

public class CategotyListModel {

    private int imageId;
    private String categoryName;

    public CategotyListModel(int imageId, String categoryName) {
        this.imageId = imageId;
        this.categoryName = categoryName;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
