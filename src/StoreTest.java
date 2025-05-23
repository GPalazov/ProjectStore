import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StoreTest {
    private Store store;
    private Cashier cashier;
    private Item foodItem;
    private Item nonFoodItem;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {

        Field counterField = Receipt.class.getDeclaredField("receiptCounter");
        counterField.setAccessible(true);
        counterField.set(null, 0);


        File counterFile = new File("receipt_counter.ser");
        if (counterFile.exists()) {
            counterFile.delete();
        }

        store = new Store();
        cashier = new Cashier("Cashier001", "Мартин Петров", 1500.0);
        foodItem = new Item("Item001", "Хляб", 2.0, Category.FOOD, LocalDate.now().plusDays(3), 10);
        nonFoodItem = new Item("Item002", "Сапун", 1.5, Category.NON_FOOD, LocalDate.now().plusDays(100), 20);
        store.addCashier(cashier);
        store.addItem(foodItem);
        store.addItem(nonFoodItem);
    }

    @Test
    void testCalculateSellingPriceWithDiscount() {
        double expectedPrice = 2.0 * (1 + 20.0 / 100) * (1 - 50.0 / 100);
        assertEquals(1.2, store.calculateSellingPrice(foodItem), 0.01, "Цената с отстъпка не е коректна");
    }

    @Test
    void testCalculateSellingPriceWithoutDiscount() {
        double expectedPrice = 1.5 * (1 + 30.0 / 100);
        assertEquals(1.95, store.calculateSellingPrice(nonFoodItem), 0.01, "Цената без отстъпка не е коректна");
    }

    @Test
    void testInsufficientQuantityException() {
        Map<Item, Integer> itemsToBuy = new HashMap<>();
        itemsToBuy.put(foodItem, 15);

        InsufficientQuantityException exception = assertThrows(InsufficientQuantityException.class, () -> {
            store.sellItems(cashier, itemsToBuy);
        });

        assertTrue(exception.getMessage().contains("Хляб") && exception.getMessage().contains("Заявено: 15, Налично: 10"));
    }

    @Test
    void testReceiptCounter() throws IOException, ClassNotFoundException, InsufficientQuantityException {
        Map<Item, Integer> itemsToBuy = new HashMap<>();
        itemsToBuy.put(foodItem, 2);
        store.sellItems(cashier, itemsToBuy);

        int initialCount = store.getReceiptCount();
        assertEquals(1, initialCount, "Броят на касовите бележки не е коректен");

        store.sellItems(cashier, itemsToBuy);
        assertEquals(2, store.getReceiptCount(), "Броячът не се увеличава коректно");
    }

    @Test
    void testReceiptSerializationAndDeserialization() throws IOException, ClassNotFoundException, InsufficientQuantityException {
        Map<Item, Integer> itemsToBuy = new HashMap<>();
        itemsToBuy.put(foodItem, 2);
        Receipt receipt = store.sellItems(cashier, itemsToBuy);

        String filename = tempDir.resolve("receipt_1.ser").toString();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(receipt);
        }

        Receipt loadedReceipt = Receipt.loadFromFile(filename);
        assertEquals(receipt.getReceiptNumber(), loadedReceipt.getReceiptNumber(), "Номерът на касовата бележка не съвпада");
        assertEquals(receipt.getTotalAmount(), loadedReceipt.getTotalAmount(), 0.01, "Общата сума не съвпада");
    }

    @Test
    void testCalculateProfit() throws InsufficientQuantityException {
        Map<Item, Integer> itemsToBuy = new HashMap<>();
        itemsToBuy.put(foodItem, 2);
        itemsToBuy.put(nonFoodItem, 1);
        store.sellItems(cashier, itemsToBuy);

        double expectedExpenses = 1500.0 + (2.0 * 8) + (1.5 * 19);
        double expectedProfit = 4.35 - (1500.0 + (2.0 * 8) + (1.5 * 19));

        assertEquals(expectedProfit, store.calculateProfit(), 0.01, "Печалбата не е коректна");
    }
}