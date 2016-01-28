package hanjie.app.pureweather.bean;

public class WeatherYesterday {

    private String date;
    private String tempMin;
    private String tempMax;
    private String weatherStart;
    private String weatherEnd;

    public WeatherYesterday() {

    }

    public WeatherYesterday(String date, String tempMin, String tempMax, String weatherStart, String weatherEnd) {
        this.date = date;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.weatherStart = weatherStart;
        this.weatherEnd = weatherEnd;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    @Override
    public String toString() {
        return "WeatherYesterday{" +
                "date='" + date + '\'' +
                ", tempMin='" + tempMin + '\'' +
                ", tempMax='" + tempMax + '\'' +
                ", weatherStart='" + weatherStart + '\'' +
                ", weatherEnd='" + weatherEnd + '\'' +
                '}';
    }
}
