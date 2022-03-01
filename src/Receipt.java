import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;


class Product{
    private String name;
    private boolean domestic;
    private double price;
    private int weight;
    private String description;

    public Product(){}
    public Product(String name, boolean domestic, double price, int weight, String description) {
        this.name = name;
        this.domestic = domestic;
        this.price = price;
        this.weight = weight;
        this.description = description;
    }

    public Product(String name, boolean domestic, double price, String description) {
        this.name = name;
        this.domestic = domestic;
        this.price = price;
        this.description = description;
    }

    public String getName() {
        return name;
    }


    public boolean isDomestic() {
        return domestic;
    }


    public double getPrice() {
        return price;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return domestic == product.domestic && Double.compare(product.price, price) == 0 && weight == product.weight && Objects.equals(name, product.name) && Objects.equals(description, product.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, domestic, price, weight, description);
    }

    @Override
    public String toString() {

        StringBuilder sb= new StringBuilder();
        sb.append('\t' + name+'\n');
        sb.append('\t' +"Price: $"+price+'\n');
        sb.append('\t' +description+'\n');
        if(weight==0)
            sb.append('\t' +"Weight: N/A"+'\n');
        else sb.append('\t' +"Weight: "+weight+'g'+'\n');

        return sb.toString();
    }
}

public class Receipt {

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
        sb.append((char) cp);
        }
    return sb.toString();
    }

    public static String readJsonFromUrl(String url) throws IOException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            return readAll(rd);
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        ArrayList<Product> products=new ArrayList<>();
        Object object = new JSONParser().parse(readJsonFromUrl("https://interview-task-api.mca.dev/qr-scanner-codes/alpha-qr-gFpwhsQ8fkY1"));
        JSONArray skr = (JSONArray) object;
        for (Object o : skr) {
            JSONObject obj = (JSONObject) o;
            String name = (String) obj.get("name");
            boolean domestic = (boolean) obj.get("domestic");
            double price = (double) obj.get("price");
            String desc = (String) obj.get("description");
            if (obj.get("weight") == null) {
                products.add(new Product(name, domestic, price, desc));
            } else {
                long weight = (long) obj.get("weight");
                products.add(new Product(name, domestic, price, (int) weight, desc));
            }
        }
        ProductSort(products);
    }

    private static void ProductSort(ArrayList<Product> products) {
        ArrayList<Product> domesticProducts= new ArrayList<>();
        ArrayList<Product> importedProducts = new ArrayList<>();

        for(Product p : products){
            if(p.isDomestic())
                domesticProducts.add(p);
            else importedProducts.add(p);
        }

        domesticProducts.sort(Comparator.comparing(Product::getName));
        importedProducts.sort(Comparator.comparing(Product::getName));

        System.out.format("%s \n","Domestic");
        for(Product p : domesticProducts){
            System.out.printf("%s", p.toString());
        }
        System.out.format("%s \n","Imported");
        for(Product p : importedProducts){
            System.out.printf("%s", p.toString());
        }
        System.out.println("Domestic cost: $"+ sum(domesticProducts) + "\nImported cost: $"+ sum(importedProducts));
        System.out.println("Domestic count: "+ domesticProducts.size() + "\nImported count: "+importedProducts.size());
    }

    private static double sum(ArrayList<Product> products) {
        double sum=0;
        for(Product p: products){
            sum+=p.getPrice();
        }
        return sum;
    }
}
