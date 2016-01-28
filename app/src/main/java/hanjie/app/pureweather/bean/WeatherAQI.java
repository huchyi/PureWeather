package hanjie.app.pureweather.bean;

public class WeatherAQI {

    private String aqi;
    private String pm25;
    private String pm10;
    private String time;
    private String so2;
    private String no2;
    private String src;
    private String quality;

    public WeatherAQI() {

    }

    public WeatherAQI(String aqi, String pm25, String pm10, String time, String so2, String no2, String src, String quality) {
        this.aqi = aqi;
        this.pm25 = pm25;
        this.pm10 = pm10;
        this.time = time;
        this.so2 = so2;
        this.no2 = no2;
        this.src = src;
        this.quality = quality;
    }

    public String getAqi() {
        return aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getPm10() {
        return pm10;
    }

    public void setPm10(String pm10) {
        this.pm10 = pm10;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSo2() {
        return so2;
    }

    public void setSo2(String so2) {
        this.so2 = so2;
    }

    public String getNo2() {
        return no2;
    }

    public void setNo2(String no2) {
        this.no2 = no2;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    @Override
    public String toString() {
        return "WeatherAQI{" +
                "aqi='" + aqi + '\'' +
                ", pm25='" + pm25 + '\'' +
                ", pm10='" + pm10 + '\'' +
                ", time='" + time + '\'' +
                ", so2='" + so2 + '\'' +
                ", no2='" + no2 + '\'' +
                ", src='" + src + '\'' +
                ", quality='" + quality + '\'' +
                '}';
    }
}
