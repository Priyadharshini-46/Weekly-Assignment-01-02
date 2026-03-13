import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WeeklyAssignment {

    // username -> userId
    private final ConcurrentHashMap<String, String> userDatabase;

    // username -> attempt count
    private final ConcurrentHashMap<String, AtomicInteger> attemptFrequency;

    public WeeklyAssignment() {
        userDatabase = new ConcurrentHashMap<>();
        attemptFrequency = new ConcurrentHashMap<>();
    }

    // O(1) Availability Check
    public boolean checkAvailability(String username) {
        // Track frequency
        attemptFrequency
                .computeIfAbsent(username, k -> new AtomicInteger(0))
                .incrementAndGet();

        return !userDatabase.containsKey(username);
    }

    // Register user
    public boolean registerUser(String username, String userId) {
        if (checkAvailability(username)) {
            userDatabase.put(username, userId);
            return true;
        }
        return false;
    }

    // Suggest alternatives
    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();

        if (checkAvailability(username)) {
            suggestions.add(username);
            return suggestions;
        }

        // Strategy 1: Append numbers
        for (int i = 1; i <= 5; i++) {
            String suggestion = username + i;
            if (!userDatabase.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        // Strategy 2: Replace underscore with dot
        if (username.contains("_")) {
            String modified = username.replace("_", ".");
            if (!userDatabase.containsKey(modified)) {
                suggestions.add(modified);
            }
        }

        // Strategy 3: Add random number
        String randomSuggestion = username + new Random().nextInt(1000);
        if (!userDatabase.containsKey(randomSuggestion)) {
            suggestions.add(randomSuggestion);
        }

        return suggestions;
    }

    // Get most attempted username
    public String getMostAttempted() {
        String mostAttempted = null;
        int maxCount = 0;

        for (Map.Entry<String, AtomicInteger> entry : attemptFrequency.entrySet()) {
            int count = entry.getValue().get();
            if (count > maxCount) {
                maxCount = count;
                mostAttempted = entry.getKey();
            }
        }

        return mostAttempted;
    }

    // For testing
    public static void main(String[] args) {
       WeeklyAssignment service = new WeeklyAssignment();

        service.registerUser("john_doe", "U1001");

        System.out.println(service.checkAvailability("john_doe"));    // false
        System.out.println(service.checkAvailability("jane_smith"));  // true

        System.out.println(service.suggestAlternatives("john_doe"));
        System.out.println(service.getMostAttempted());
    }
}