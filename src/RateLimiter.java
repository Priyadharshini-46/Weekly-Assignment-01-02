import java.util.concurrent.*;

class RateLimiter {

    class TokenBucket {
        int tokens;
        long lastRefill;
        final int capacity;
        final int refillRate; // tokens per second

        TokenBucket(int capacity, int refillRate) {
            this.capacity = capacity;
            this.refillRate = refillRate;
            this.tokens = capacity;
            this.lastRefill = System.currentTimeMillis();
        }

        synchronized boolean allowRequest() {
            refill();

            if (tokens > 0) {
                tokens--;
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long seconds = (now - lastRefill) / 1000;

            if (seconds > 0) {
                int newTokens = (int) (seconds * refillRate);
                tokens = Math.min(capacity, tokens + newTokens);
                lastRefill = now;
            }
        }
    }

    private final ConcurrentHashMap<String, TokenBucket> clients = new ConcurrentHashMap<>();

    public boolean checkRateLimit(String clientId) {
        clients.putIfAbsent(clientId, new TokenBucket(1000, 1));
        return clients.get(clientId).allowRequest();
    }
}