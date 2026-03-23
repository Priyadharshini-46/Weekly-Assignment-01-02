import java.util.*;
import java.util.concurrent.*;

class Analytics {

    private final ConcurrentHashMap<String, Integer> pageViews = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<String>> uniqueVisitors = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> trafficSource = new ConcurrentHashMap<>();

    public void processEvent(String url, String userId, String source) {
        pageViews.merge(url, 1, Integer::sum);

        uniqueVisitors.computeIfAbsent(url, k -> ConcurrentHashMap.newKeySet()).add(userId);

        trafficSource.merge(source, 1, Integer::sum);
    }

    public List<String> topPages(int k) {
        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>(Map.Entry.comparingByValue());

        for (Map.Entry<String, Integer> entry : pageViews.entrySet()) {
            pq.offer(entry);
            if (pq.size() > k) pq.poll();
        }

        List<String> result = new ArrayList<>();
        while (!pq.isEmpty()) result.add(pq.poll().getKey());
        Collections.reverse(result);
        return result;
    }
}