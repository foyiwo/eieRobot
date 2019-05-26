package eie.robot.com.model;

public class IncomeRecordModel {
    private String appName;
    private double virtualCurrency;
    private double rmb;
    private String phoneName;
    private String phoneIMEA;


    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public double getVirtualCurrency() {
        return virtualCurrency;
    }

    public void setVirtualCurrency(double virtualCurrency) {
        this.virtualCurrency = virtualCurrency;
    }

    public double getRmb() {
        return rmb;
    }

    public void setRmb(double rmb) {
        this.rmb = rmb;
    }

    public String getPhoneName() {
        return phoneName;
    }

    public void setPhoneName(String phoneName) {
        this.phoneName = phoneName;
    }

    public String getPhoneIMEA() {
        return phoneIMEA;
    }

    public void setPhoneIMEA(String phoneIMEA) {
        this.phoneIMEA = phoneIMEA;
    }
}
