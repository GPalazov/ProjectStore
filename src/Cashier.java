import java.io.Serializable;

public class Cashier implements Serializable {
    private String id;
    private String name;
    private double monthlySalary;

    public Cashier(String id, String name, double monthlySalary) {
        this.id = id;
        this.name = name;
        this.monthlySalary = monthlySalary;
    }

    public String getName() { return name; }
    public double getMonthlySalary() { return monthlySalary; }
}