import utils.RegexUtils;
import utils.BardDataFactory;

import java.text.BreakIterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.AbstractMap.SimpleEntry;
import static java.util.stream.Collectors.toMap;
import static utils.RegexUtils.makeRegex;

/**
 * This example shows how the Java regular expression methods can be
 * used in conjunction with Java sequential streams to search the
 * complete works of Shakespeare ({@code bardWorks}) for particular
 * words and phrases.
 */
public class ex45 {
    /**
     * The complete works of William Shakespeare.
     */
    private static final String sSHAKESPEARE_DATA_FILE =
        "completeWorksOfShakespeare.txt";

    /**
     * Main entry point into the program.
     */
    static public void main(String[] argv) {
        var test = new ex45();

        // Test the makeRegex() method.
        test.testRegexList();

        // Create a List of String objects containing the complete
        // works of Shakespeare.
        List<String> bardWorks = BardDataFactory
            .getInput(sSHAKESPEARE_DATA_FILE,
                      // Split input into "works".
                      "@");

        // Search the works of Shakespeare for a certain word/phrase ("lord").
        test.processBardWorks(Objects.requireNonNull(bardWorks),
                         "lord");
    }

    /**
     * Test the makeRegex() method.
     */
    private void testRegexList() {
        // The quote to search for matches.
        String quote = """
                       The quick brown fox jumps 
                       \nover the lazy dog.""";

        // The words to match.
        var wordsToMatch = List
            .of("Cat", "Dog", "Mouse", "sox");

        // Convert 'wordsToMatch' to a regular expression.
        var regexString =
            makeRegex(wordsToMatch);

        // Check whether 'quote' is matched by 'regexString'.
        var result = quote
            // Match across newlines.
            .toLowerCase().matches("(?s)" + regexString)
            ? "matches" : "does not match";

        System.out.println("The quote \""
                           + quote
                           + "\" "
                           + result
                           + " the regex string \""
                           +  regexString
                           + "\"\n");
    }

    /**
     * Show how the Java regular expression methods can be used to
     * search the complete works of Shakespeare ({@code bardWorks} for
     * {@code word}.
     */
    private void processBardWorks(List<String> bardWorks,
                                  String word) {
        // Create a List of Shakespeare works containing 'word'.
        var bardWorksMatchingWord = bardWorks
            // Convert List to a Stream.
            .stream()

            // Only keep works containing 'word'.
            .filter(work ->
                    // Return true if 'word' appears in 'work'.
                    findMatch(work, word))

            // Convert List to a Stream.
            .toList();  

        var pattern = Stream
            // The regular expression to compile, which matches the
            // phrase "'word' followed by either 'true' or 'false'".
            .of("\\b"
                + word
                + "\\b"
                + makeRegex(List.of("true", "false")))

            // Compile the regular expression to perform
            // case-insensitive matches.
            .map(regex -> Pattern
                 .compile(regex, Pattern.CASE_INSENSITIVE))

            // Return the first match.
            .findFirst().orElse(null);

        // Show the portions of the works of Shakespeare that match
        // the pattern.
        showRegexMatches(bardWorksMatchingWord, pattern);
    }

    /**
     * Return true if the {@code work} contains the {@code
     * searchWord}.
     *
     * @param work The text to search
     * @param searchWord The word to search for
     * @return true if the {@code work} contains the {@code
     *         searchWord}, else false
     */
    private boolean findMatch(String work,
                              String searchWord) {
        BreakIterator iterator = BreakIterator
            // Get the word iterator for the US locale.
            .getWordInstance(Locale.US);

        // Associate iterator with the 'work'.
        iterator.setText(work);

        // Create a Stream of word boundaries and collect them in a
        // List.
        List<Integer> boundaries = Stream
            // Iterate over the boundaries of 'work', beginning
            // with the first boundary.
            .iterate(iterator.first(),
                     // Stop iterating when the iterator returns
                     // BreakIterator.DONE.
                     boundary -> boundary != BreakIterator.DONE,
                     // Move the iterator to the next boundary.
                     boundary -> iterator.next())

            // Convert the Stream of boundaries into a List.
            .toList();

        // Create a Stream of words using the collected boundaries and
        // return true if any of the words are 'searchWord'.
        return IntStream
            // Iterate over the boundaries of 'work'.
            .range(0, boundaries.size() - 1)

            // Convert the boundaries into a Stream of words.
            .mapToObj(i -> work
                      .substring(boundaries.get(i),
                                 boundaries.get(i + 1)))

            // Return true if any of the words equal 'searchWord'.
            .anyMatch(word -> word
                      .toLowerCase()
                      .equals(searchWord));
    }

    /**
     * Show the portions of the works of Shakespeare that match the
     * {@link Pattern}.
     *
     * @param bardWorksMatchingWord The Shakespeare works matching
     *                              a search word       
     * @param pattern The compiled regular expression to search for
     */
    private void showRegexMatches(List<String> bardWorksMatchingWord,
                                  Pattern pattern) {
        // Collect all match results across all works into a Map.
        var allMatchResults = bardWorksMatchingWord
            // Convert List to a Stream.
            .stream()

            // Map each work to a SimpleEntry contain the title of the
            // work and the List of matches for that work.
            .mapMulti(mapWork(pattern))

            // Convert the Stream of SimpleEntry objects to a Map
            // where the 'key' is the title of the work, and the
            // 'value' is the List of matches for each work.
            .collect(toMap(SimpleEntry::getKey,
                           SimpleEntry::getValue));

        // Print the total number of matches.
        System.out.println("Number of works that match is "
                           + allMatchResults.size()
                           + " out of a total of "
                           + bardWorksMatchingWord.size()
                           + " works and the matches for each work are:");

        allMatchResults
            // Iterate through the Map.
            .forEach((key, value) -> {
                    // Print out the title of the work.
                    System.out.println(key);

                    value
                        // Iterate through the List of matches and
                        // print them out.
                        .forEach(matchResult -> System.out
                                 .println("\"" 
                                          + matchResult.group()
                                          + "\" ["
                                          + matchResult.start()
                                          + "]"));
                });
    }

    /**
     * This factory method returns a {@link BiConsumer} that maps each
     * work to a {@link SimpleEntry} containing the title of the work
     * and an associated {@link List} of {@code pattern} matches for
     * that work. Only works that match the {@code pattern} are
     * returned.
     *
     * @param pattern The compiled regular expression to search for
     * @return A {@link BiConsumer} that maps the title of each work
     *         with a {@link List} of non-empty matches for that work
     */
    private static BiConsumer<String,
                              Consumer<SimpleEntry<String,
                                       List<MatchResult>>>>
    mapWork(Pattern pattern) {
        return (String work,
                Consumer<SimpleEntry<String, List<MatchResult>>> consumer) -> {
            // Get a List of all matches for the work.
            var matchList = pattern
                // Associate the 'work' with the 'pattern'.
                .matcher(work)

                // Get a Stream of MatchResult objects.
                .results()

                // Convert the Stream into a List.
                .toList();

            // Filter out any work that has no matches.
            if (!matchList.isEmpty()) {
                // Get the title of the work.
                var title = RegexUtils.getFirstLine(work);

                // Create a SimpleEntry containing the title of the
                // work and the List of matches for that work.
                var entry = new SimpleEntry<String, List<MatchResult>>
                    (title, matchList);

                // Accept the entry into the consumer.
                consumer.accept(entry);
            }
        };
    }
}

