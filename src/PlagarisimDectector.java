import java.util.*;

class PlagiarismDetector {

    private final Map<String, Set<String>> index = new HashMap<>();
    private final int N = 5;

    public void addDocument(String docId, String text) {
        List<String> words = Arrays.asList(text.split("\\s+"));

        for (int i = 0; i <= words.size() - N; i++) {
            String ngram = String.join(" ", words.subList(i, i + N));
            index.computeIfAbsent(ngram, k -> new HashSet<>()).add(docId);
        }
    }

    public double compare(String docA, String text) {
        List<String> words = Arrays.asList(text.split("\\s+"));
        int match = 0, total = 0;

        for (int i = 0; i <= words.size() - N; i++) {
            total++;
            String ngram = String.join(" ", words.subList(i, i + N));

            if (index.containsKey(ngram) && index.get(ngram).contains(docA)) {
                match++;
            }
        }

        return (total == 0) ? 0 : (match * 100.0 / total);
    }
}