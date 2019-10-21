package in.oriange.joinsta.fragments;

import java.io.Serializable;
import java.util.List;

public class GetDistrictListModel implements Serializable {


    /**
     * type : success
     * message : get districts successfully!
     * result : [{"districtId":"532","district":"Ahmed Nagar","stateId":"21","state":"Maharashtra"},{"districtId":"533","district":"Akola","stateId":"21","state":"Maharashtra"},{"districtId":"534","district":"Amravati","stateId":"21","state":"Maharashtra"},{"districtId":"535","district":"Aurangabad","stateId":"21","state":"Maharashtra"},{"districtId":"536","district":"Beed","stateId":"21","state":"Maharashtra"},{"districtId":"537","district":"Bhandara","stateId":"21","state":"Maharashtra"},{"districtId":"538","district":"Buldhana","stateId":"21","state":"Maharashtra"},{"districtId":"539","district":"Chandrapur","stateId":"21","state":"Maharashtra"},{"districtId":"540","district":"Dhule","stateId":"21","state":"Maharashtra"},{"districtId":"541","district":"Gadchiroli","stateId":"21","state":"Maharashtra"},{"districtId":"542","district":"Gondia","stateId":"21","state":"Maharashtra"},{"districtId":"543","district":"Hingoli","stateId":"21","state":"Maharashtra"},{"districtId":"544","district":"Jalgaon","stateId":"21","state":"Maharashtra"},{"districtId":"545","district":"Jalna","stateId":"21","state":"Maharashtra"},{"districtId":"546","district":"Kolhapur","stateId":"21","state":"Maharashtra"},{"districtId":"547","district":"Latur","stateId":"21","state":"Maharashtra"},{"districtId":"548","district":"Mumbai","stateId":"21","state":"Maharashtra"},{"districtId":"549","district":"Mumbai Suburban","stateId":"21","state":"Maharashtra"},{"districtId":"550","district":"Nagpur","stateId":"21","state":"Maharashtra"},{"districtId":"551","district":"Nanded","stateId":"21","state":"Maharashtra"},{"districtId":"552","district":"Nandurbar","stateId":"21","state":"Maharashtra"},{"districtId":"553","district":"Nashik","stateId":"21","state":"Maharashtra"},{"districtId":"554","district":"Osmanabad","stateId":"21","state":"Maharashtra"},{"districtId":"555","district":"Parbhani","stateId":"21","state":"Maharashtra"},{"districtId":"556","district":"Pune","stateId":"21","state":"Maharashtra"},{"districtId":"557","district":"Raigarh (Maharashtra)","stateId":"21","state":"Maharashtra"},{"districtId":"558","district":"Ratnagiri","stateId":"21","state":"Maharashtra"},{"districtId":"559","district":"Sangli","stateId":"21","state":"Maharashtra"},{"districtId":"560","district":"Satara","stateId":"21","state":"Maharashtra"},{"districtId":"561","district":"Sindhudurg","stateId":"21","state":"Maharashtra"},{"districtId":"562","district":"Solapur","stateId":"21","state":"Maharashtra"},{"districtId":"563","district":"Thane","stateId":"21","state":"Maharashtra"},{"districtId":"564","district":"Wardha","stateId":"21","state":"Maharashtra"},{"districtId":"565","district":"Washim","stateId":"21","state":"Maharashtra"},{"districtId":"566","district":"Yavatmal","stateId":"21","state":"Maharashtra"},{"districtId":"568","district":"Anantapur","stateId":"2","state":"Andhra Pradesh"},{"districtId":"569","district":"Chittoor","stateId":"2","state":"Andhra Pradesh"},{"districtId":"570","district":"East Godavari","stateId":"2","state":"Andhra Pradesh"},{"districtId":"571","district":"Guntur","stateId":"2","state":"Andhra Pradesh"},{"districtId":"573","district":"Kadapa (Cuddapah)","stateId":"2","state":"Andhra Pradesh"},{"districtId":"576","district":"Krishna","stateId":"2","state":"Andhra Pradesh"},{"districtId":"577","district":"Kurnool","stateId":"2","state":"Andhra Pradesh"},{"districtId":"581","district":"Nellore","stateId":"2","state":"Andhra Pradesh"},{"districtId":"583","district":"Prakasam","stateId":"2","state":"Andhra Pradesh"},{"districtId":"585","district":"Srikakulam","stateId":"2","state":"Andhra Pradesh"},{"districtId":"586","district":"Visakhapatnam","stateId":"2","state":"Andhra Pradesh"},{"districtId":"587","district":"Vizianagaram","stateId":"2","state":"Andhra Pradesh"},{"districtId":"589","district":"West Godavari","stateId":"2","state":"Andhra Pradesh"}]
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
         * districtId : 532
         * district : Ahmed Nagar
         * stateId : 21
         * state : Maharashtra
         */

        private String districtId;
        private String district;
        private String stateId;
        private String state;
        private boolean isChecked;

        public String getDistrictId() {
            return districtId;
        }

        public void setDistrictId(String districtId) {
            this.districtId = districtId;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getStateId() {
            return stateId;
        }

        public void setStateId(String stateId) {
            this.stateId = stateId;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }
}
