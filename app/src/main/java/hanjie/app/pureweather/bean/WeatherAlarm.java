package hanjie.app.pureweather.bean;

public class WeatherAlarm {

    private String cityName;
    private String alarmType;
    private String alarmDegree;
    private String alarmText;
    private String alarm_details;
    private String time;

    public WeatherAlarm() {

    }

    public WeatherAlarm(String cityName, String alarmType, String alarmDegree, String alarmText, String alarm_details, String time) {
        this.cityName = cityName;
        this.alarmType = alarmType;
        this.alarmDegree = alarmDegree;
        this.alarmText = alarmText;
        this.alarm_details = alarm_details;
        this.time = time;
    }


    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getAlarmDegree() {
        return alarmDegree;
    }

    public void setAlarmDegree(String alarmDegree) {
        this.alarmDegree = alarmDegree;
    }

    public String getAlarmText() {
        return alarmText;
    }

    public void setAlarmText(String alarmText) {
        this.alarmText = alarmText;
    }

    public String getAlarm_details() {
        return alarm_details;
    }

    public void setAlarm_details(String alarm_details) {
        this.alarm_details = alarm_details;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "WeatherAlarm{" +
                "cityName='" + cityName + '\'' +
                ", alarmType='" + alarmType + '\'' +
                ", alarmDegree='" + alarmDegree + '\'' +
                ", alarmText='" + alarmText + '\'' +
                ", alarm_details='" + alarm_details + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
