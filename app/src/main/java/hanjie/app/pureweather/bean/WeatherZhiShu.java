package hanjie.app.pureweather.bean;

public class WeatherZhiShu {

    private String name;
    private int name_id;
    private String value;

    public WeatherZhiShu() {
    }

    public WeatherZhiShu(String name, int name_id, String value) {
        this.name = name;
        this.name_id = name_id;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getName_id() {
        return name_id;
    }

    public void setName_id(int name_id) {
        this.name_id = name_id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "WeatherZhiShu{" +
                "name='" + name + '\'' +
                ", name_id=" + name_id +
                ", value='" + value + '\'' +
                '}';
    }
}
