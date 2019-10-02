package in.oriange.joinsta.utilities;

public class ConstantData {

    public static ConstantData _instance;

    private ConstantData() {
    }

    public static ConstantData getInstance() {
        if (_instance == null) {
            _instance = new ConstantData();
        }
        return _instance;
    }

    public static ConstantData get_instance() {
        return _instance;
    }

    public static void set_instance(ConstantData _instance) {
        ConstantData._instance = _instance;
    }

    private int isAppRunning = 0;       // 0 if app is not running  1  if app is running

    public int getIsAppRunning() {
        return isAppRunning;
    }

    public void setIsAppRunning(int isAppRunning) {
        this.isAppRunning = isAppRunning;
    }

}