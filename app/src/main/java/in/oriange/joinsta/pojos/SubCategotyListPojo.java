package in.oriange.joinsta.pojos;

import java.util.ArrayList;

import in.oriange.joinsta.models.SubCategotyListModel;

public class SubCategotyListPojo {

    private ArrayList<SubCategotyListModel> result;

    private String type;

    private String message;

    public ArrayList<SubCategotyListModel> getResult() {
        return result;
    }

    public void setResult(ArrayList<SubCategotyListModel> result) {
        this.result = result;
    }

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

}
