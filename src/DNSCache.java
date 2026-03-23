import java.util.*;

class DNSCache {

    class Entry {
        String ip;
        long expiry;

        Entry(String ip, long ttlSeconds) {
            this.ip = ip;
            this.expiry = System.currentTimeMillis() + ttlSeconds * 1000;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiry;
        }
    }

    private final int capacity;
    private final Map<String, Entry> cache;

    private int hits = 0, misses = 0;

    public DNSCache(int capacity) {
        this.capacity = capacity;

        this.cache = new LinkedHashMap<>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, Entry> eldest) {
                return size() > capacity;
            }
        };
    }

    public synchronized String resolve(String domain) {
        if (cache.containsKey(domain)) {
            Entry entry = cache.get(domain);
            if (!entry.isExpired()) {
                hits++;
                return entry.ip;
            } else {
                cache.remove(domain);
            }
        }

        misses++;
        String ip = queryUpstreamDNS(domain);
        cache.put(domain, new Entry(ip, 300));
        return ip;
    }

    private String queryUpstreamDNS(String domain) {
        // Simulated DNS call
        return "192.168.1." + new Random().nextInt(255);
    }

    public void printStats() {
        int total = hits + misses;
        System.out.println("Hit Rate: " + (total == 0 ? 0 : (hits * 100.0 / total)) + "%");
    }
}