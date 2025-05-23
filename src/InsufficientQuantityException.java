public class InsufficientQuantityException extends Exception {
    public InsufficientQuantityException(String itemName, int requested, int available) {
        super("Недостатъчно количество за " + itemName + ". Заявено: " + requested + ", Налично: " + available);
    }
}