import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
// github link към проекта :   https://github.com/GPalazov/ProjectStore
public class Main {
    public static void main(String[] args) {
        try {
            Store store = new Store();
            Cashier cashier = new Cashier("Cashier001", "Мартин Петров", 1600.0);
            store.addCashier(cashier);

            Item bread = new Item("Item001", "Хляб", 2.5, Category.FOOD, LocalDate.now().plusDays(3), 15);
            Item soap = new Item("Item002", "Сапун", 2, Category.NON_FOOD, LocalDate.now().plusDays(100), 8);
            Item chainsaw = new Item("Item003", "Резачка",350, Category.NON_FOOD, LocalDate.now().plusDays(999), 5 );
            store.addItem(bread);
            store.addItem(soap);
            store.addItem(chainsaw);

            Map<Item, Integer> itemsToBuy = new HashMap<>();
            itemsToBuy.put(bread, 5);
            itemsToBuy.put(soap, 3);
            itemsToBuy.put(chainsaw,4);

            System.out.println("Извършване на продажба:");
            store.sellItems(cashier, itemsToBuy);

            System.out.println("\nСтатистика за магазина:");
            System.out.println("Общ брой касови бележки: " + store.getReceiptCount());
            System.out.println("Общ оборот: " + String.format("%.2f", store.getTotalRevenue()));
            System.out.println("Разходи: " + String.format("%.2f", store.calculateTotalExpenses()));
            System.out.println("Печалба: " + String.format("%.2f", store.calculateProfit()));

            System.out.println("\nЗареждане на последната касова бележка:");
            Receipt loadedReceipt = Receipt.loadFromFile("receipt_" + store.getReceiptCount() + ".ser");
            System.out.println("Касова бележка №" + loadedReceipt.getReceiptNumber() + ", Обща сума: " + String.format("%.2f", loadedReceipt.getTotalAmount()));

        } catch (InsufficientQuantityException e) {
            System.err.println("Грешка при продажба: " + e.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Грешка при работа с файл: " + e.getMessage());
        }
    }
}