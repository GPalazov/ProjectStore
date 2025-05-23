import java.time.LocalDate;
import java.util.*;

public class Store {
    private List<Item> items;
    private List<Cashier> cashiers;
    private List<Receipt> receipts;
    private double foodMarkupPercent = 20.0;
    private double nonFoodMarkupPercent = 30.0;
    private int expiryDiscountDays = 5;
    private double expiryDiscountPercent = 50.0;
    private double totalRevenue = 0.0;

    public Store() {
        this.items = new ArrayList<>();
        this.cashiers = new ArrayList<>();
        this.receipts = new ArrayList<>();
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void addCashier(Cashier cashier) {
        cashiers.add(cashier);
    }

    public double calculateSellingPrice(Item item) {
        if (item.getExpiryDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Стоката " + item.getName() + " е с изтекъл срок на годност!");
        }

        double markup = item.getCategory() == Category.FOOD ? foodMarkupPercent : nonFoodMarkupPercent;
        double price = item.getSupplyPrice() * (1 + markup / 100);

        if (item.getExpiryDate().isBefore(LocalDate.now().plusDays(expiryDiscountDays))) {
            price *= (1 - expiryDiscountPercent / 100);
        }
        return price;
    }

    public Receipt sellItems(Cashier cashier, Map<Item, Integer> requestedItems) throws InsufficientQuantityException {
        double totalAmount = 0.0;
        Map<Item, Integer> soldItems = new HashMap<>();

        for (Map.Entry<Item, Integer> entry : requestedItems.entrySet()) {
            Item item = entry.getKey();
            int requestedQuantity = entry.getValue();
            if (item.getQuantity() < requestedQuantity) {
                throw new InsufficientQuantityException(item.getName(), requestedQuantity, item.getQuantity());
            }
            double price = calculateSellingPrice(item);
            totalAmount += price * requestedQuantity;
            item.setQuantity(item.getQuantity() - requestedQuantity);
            soldItems.put(item, requestedQuantity);
        }

        Receipt receipt = new Receipt(cashier, soldItems, totalAmount);
        receipts.add(receipt);
        totalRevenue += totalAmount;
        receipt.saveToFile(this);
        System.out.println(receipt.generateReceiptText(this));
        return receipt;
    }

    public double calculateTotalExpenses() {
        double salaryExpenses = cashiers.stream().mapToDouble(Cashier::getMonthlySalary).sum();
        double supplyExpenses = items.stream().mapToDouble(item -> item.getSupplyPrice() * item.getQuantity()).sum();
        return salaryExpenses + supplyExpenses;
    }

    public double calculateProfit() {
        return totalRevenue - calculateTotalExpenses();
    }

    public int getReceiptCount() {
        return Receipt.getTotalReceipts();
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }
}