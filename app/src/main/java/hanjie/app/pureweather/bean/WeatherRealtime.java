package hanjie.app.pureweather.bean;

public class WeatherRealtime {

    private String updatetime;
    private String wendu;
    private String fengli;
    private String shidu;
    private String fengxiang;
    private String sunrise;
    private String sunset;
    private String weather;

    public WeatherRealtime() {

    }

    public WeatherRealtime(String updatetime, String wendu, String fengli, String shidu, String fengxiang, String sunrise, String sunset, String weather) {
        this.updatetime = updatetime;
        this.wendu = wendu;
        this.fengli = fengli;
        this.shidu = shidu;
        this.fengxiang = fengxiang;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.weather = weather;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getWendu() {
        return wendu;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }

    public String getFengli() {
        return fengli;
    }

    public void setFengli(String fengli) {
        this.fengli = fengli;
    }

    public String getShidu() {
        return shidu;
    }

    public void setShidu(String shidu) {
        this.shidu = shidu;
    }

    public String getFengxiang() {
        return fengxiang;
    }

    public void setFengxiang(String fengxiang) {
        this.fengxiang = fengxiang;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    @Override
    public String toString() {
        return "WeatherRealtime{" +
                "updatetime='" + updatetime + '\'' +
                ", wendu='" + wendu + '\'' +
                ", fengli='" + fengli + '\'' +
                ", shidu='" + shidu + '\'' +
                ", fengxiang='" + fengxiang + '\'' +
                ", sunrise='" + sunrise + '\'' +
                ", sunset='" + sunset + '\'' +
                ", weather='" + weather + '\'' +
                '}';
    }
}
