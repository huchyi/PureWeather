package hanjie.app.pureweather.bean;

public class WeatherForecast {

    private int date_id;
    private String week;
    private String weatherStart;
    private String weatherEnd;
    private String tempMin;
    private String tempMax;
    private String fx;
    private String fl;

    public WeatherForecast() {

    }

    public WeatherForecast(int date_id, String week, String weatherStart, String weatherEnd, String tempMin, String tempMax, String fx, String fl) {
        this.date_id = date_id;
        this.week = week;
        this.weatherStart = weatherStart;
        this.weatherEnd = weatherEnd;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.fx = fx;
        this.fl = fl;
    }

    public int getDate_id() {
        return date_id;
    }

    public void setDate_id(int date_id) {
        this.date_id = date_id;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getWeatherStart() {
        return weatherStart;
    }

    public void setWeatherStart(String weatherStart) {
        this.weatherStart = weatherStart;
    }

    public String getWeatherEnd() {
        return weatherEnd;
    }

    public void setWeatherEnd(String weatherEnd) {
        this.weatherEnd = weatherEnd;
    }

    public String getTempMin() {
        return tempMin;
    }

    public void setTempMin(String tempMin) {
        this.tempMin = tempMin;
    }

    public String getTempMax() {
        return tempMax;
    }

    public void setTempMax(String tempMax) {
        this.tempMax = tempMax;
    }

    public String getFx() {
        return fx;
    }

    public void setFx(String fx) {
        this.fx = fx;
    }

    public String getFl() {
        return fl;
    }

    public void setFl(String fl) {
        this.fl = fl;
    }

    @Override
    public String toString() {
        return "WeatherForecast{" +
                "date_id=" + date_id +
                ", week='" + week + '\'' +
                ", weatherStart='" + weatherStart + '\'' +
                ", weatherEnd='" + weatherEnd + '\'' +
                ", tempMin='" + tempMin + '\'' +
                ", tempMax='" + tempMax + '\'' +
                ", fx='" + fx + '\'' +
                ", fl='" + fl + '\'' +
                '}';
    }
}
