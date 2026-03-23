import java.util.concurrent.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
class InventoryManager {
    private final ConcurrentHashMap<String, AtomicInteger> stock = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Queue<Integer>> waitlist = new ConcurrentHashMap<>();

    public void addProduct(String productId, int quantity) {
        stock.put(productId, new AtomicInteger(quantity));
        waitlist.put(productId, new ConcurrentLinkedQueue<>());
    }

    public int checkStock(String productId) {
        return stock.getOrDefault(productId, new AtomicInteger(0)).get();
    }

    public String purchaseItem(String productId, int userId) {
        AtomicInteger currentStock = stock.get(productId);
        if (currentStock == null) return "Product not found";

        while (true) {
            int available = currentStock.get();
            if (available <= 0) {
                waitlist.get(productId).add(userId);
                return "Added to waiting list. Position: " + waitlist.get(productId).size();
            }
            if (currentStock.compareAndSet(available, available - 1)) {
                return "Success! Remaining stock: " + (available - 1);
            }
        }
    }
}