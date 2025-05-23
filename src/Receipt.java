import java.io.*;
import java.time.LocalDateTime;
import java.util.Map;

public class Receipt implements Serializable {
    private static final String COUNTER_FILE = "receipt_counter.ser";
    private static int receiptCounter = loadReceiptCounter();
    private int receiptNumber;
    private Cashier cashier;
    private LocalDateTime dateTime;
    private Map<Item, Integer> items;
    private double totalAmount;

    public Receipt(Cashier cashier, Map<Item, Integer> items, double totalAmount) {
        this.receiptNumber = ++receiptCounter;
        this.cashier = cashier;
        this.dateTime = LocalDateTime.now();
        this.items = items;
        this.totalAmount = totalAmount;
        saveReceiptCounter();
    }

    public int getReceiptNumber() { return receiptNumber; }
    public double getTotalAmount() { return totalAmount; }
    public static int getTotalReceipts() { return receiptCounter; }

    public String generateReceiptText(Store store) {
        StringBuilder sb = new StringBuilder();
        sb.append("Касова бележка №").append(receiptNumber).append("\n");
        sb.append("Дата и час: ").append(dateTime).append("\n");
        sb.append("Касиер: ").append(cashier.getName()).append("\n");
        sb.append("Стоки:\n");
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            Item item = entry.getKey();
            int quantity = entry.getValue();
            double price = store.calculateSellingPrice(item);
            sb.append(item.getName()).append(" x").append(quantity)
                    .append(" @ ").append(String.format("%.2f", price))
                    .append(" = ").append(String.format("%.2f", price * quantity)).append("\n");
        }
        sb.append("Общо: ").append(String.format("%.2f", totalAmount)).append("\n");
        return sb.toString();
    }

    public void saveToFile(Store store) {
        String filename = "receipt_" + receiptNumber + ".txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println(generateReceiptText(store));
        } catch (IOException e) {
            System.err.println("Грешка при запис на касова бележка: " + e.getMessage());
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("receipt_" + receiptNumber + ".ser"))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println("Грешка при сериализация: " + e.getMessage());
        }
    }

    public static Receipt loadFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (Receipt) ois.readObject();
        }
    }

    private static int loadReceiptCounter() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(COUNTER_FILE))) {
            return (int) ois.readObject();
        } catch (FileNotFoundException e) {
            return 0;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Грешка при зареждане на брояча: " + e.getMessage());
            return 0;
        }
    }

    private void saveReceiptCounter() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(COUNTER_FILE))) {
            oos.writeObject(receiptCounter);
        } catch (IOException e) {
            System.err.println("Грешка при запазване на брояча: " + e.getMessage());
        }
    }
}