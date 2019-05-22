package in.oriange.joinsta.pojos;

import java.util.ArrayList;

import in.oriange.joinsta.models.RequirementsListModel;

public class RequirementsListPojo {

    private ArrayList<RequirementsListModel> result;

    private String type;

    private String message;

    public ArrayList<RequirementsListModel> getResult() {
        return result;
    }

    public void setResult(ArrayList<RequirementsListModel> result) {
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
