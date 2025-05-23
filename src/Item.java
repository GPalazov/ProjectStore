import java.io.Serializable;
import java.time.LocalDate;

public class Item implements Serializable {
    private String id;
    private String name;
    private double supplyPrice;
    private Category category;
    private LocalDate expiryDate;
    private int quantity;

    public Item(String id, String name, double supplyPrice, Category category, LocalDate expiryDate, int quantity) {
        this.id = id;
        this.name = name;
        this.supplyPrice = supplyPrice;
        this.category = category;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getSupplyPrice() { return supplyPrice; }
    public Category getCategory() { return category; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}