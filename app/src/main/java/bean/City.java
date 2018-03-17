package bean;

/**
 * Created by 小米笔记本Pro on 2018/3/17.
 */
public class City {
    private String number;
    private String firstPY;
    private String province;
    private String city;
    private String allPY;
    private String allFristPY;

    public City(String province, String city, String number, String firstPY, String allPY, String allFristPY) {
        this.allFristPY = allFristPY;
        this.allPY = allPY;
        this.city = city;
        this.firstPY = firstPY;
        this.number = number;
        this.province = province;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getNumber() {
        return number;
    }

    public  String getFirstPY() {
        return firstPY;
    }

    public  String getAllPY() {
        return allPY;
    }

    public  String getAllFristPY() {
        return allFristPY;
    }
}
