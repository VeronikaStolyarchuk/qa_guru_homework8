package utils;

import java.util.List;

public class Product {

    public Integer id;
    public String name;
    public String brand;
    public Mass mass;
    public Dimensions dimensions;
    public List<Prices> prices;
    public Boolean is_active;

    public static class Mass{
        public Double value;
        public String unit;
    }

    public static  class Dimensions{
        public Double length;
        public Double width;
        public Double height;
        public String unit;
    }

    public static  class Prices{
        public String currency;
        public Integer amount;
        public String type;
    }

}
