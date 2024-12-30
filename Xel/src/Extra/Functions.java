package Extra;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Functions {
    static int charFrequency(char[] chars, char target){
        int frequency = 0;
        for(char ch : chars)
            if(target == ch) frequency++;
        return frequency;
    }
    static List<String> expressionTokenizer(String expression) {
        // Replace various types of Unicode dashes/minus signs with a standard ASCII hyphen
        expression = expression.replaceAll("[−–—]", "-"); // covers U+2212, U+2013, U+2014

        List<String> tokens = new ArrayList<>();

        // Updated regex to include:
        //   1) floating-point numbers (\d+\.\d+)
        //   2) integers (\d+)
        //   3) variables (start with letter or underscore, continue with letters, digits, underscores)
        //   4) arithmetic operators and parentheses ([()+\-*/%^])
        String tokenPatterns = "\\d+\\.\\d+|\\d+|[a-zA-Z_][a-zA-Z0-9_]*|[()+\\-*/%^]";
        Pattern pattern = Pattern.compile(tokenPatterns);

        // Instead of removing *all* whitespace, just do trim() if you want
        expression = expression.trim();

        Matcher matcher = pattern.matcher(expression);
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        return tokens;
    }

    public static List<String> assignmentTokenizer(String expression) {
        // Replace possible Unicode dashes with ASCII hyphen
        expression = expression.replaceAll("[−–—]", "-");

        // Regex pattern covering:
        //   1) Assignment operators: +=, -=, *=, /=, %=, or single '='
        //   2) Floating-point numbers:   \d+\.\d+
        //   3) Integers:                \d+
        //   4) Variables:               [a-zA-Z_][a-zA-Z0-9_]*
        //   5) Arithmetic and parens:   [+\-*/%^()]
        String tokenPatterns =
                "\\+=|-=|\\*=|/=|%=|=" +         // assignment operators
                        "|\\d+\\.\\d+" +                // floating-point numbers
                        "|\\d+" +                       // integers
                        "|[a-zA-Z_][a-zA-Z0-9_]*" +     // variables
                        "|[+\\-*/%^()]"                 // arithmetic ops and parentheses
                ;

        Pattern pattern = Pattern.compile(tokenPatterns);
        Matcher matcher = pattern.matcher(expression);

        List<String> tokens = new ArrayList<>();
        while (matcher.find()) {
            // Add the matched token to the list
            tokens.add(matcher.group());
        }

        return tokens;
    }

    public static List<String> tokenizeCurlyBraces(String[] lines) {
        List<String> tokens = new ArrayList<>();

        for (String line : lines) {
            // We split on (or around) '{' or '}'
            // Using a regex that looks "ahead" and "behind"
            // so we keep the braces as separate tokens.
            // The pattern `(?=[{}])|(?<=[{}])` means:
            //   - Split the string whenever we're about to see '{' or '}'
            //   - Or split the string right after we've seen '{' or '}'
            String[] parts = line.split("(?=[{}])|(?<=[{}])");

            for (String p : parts) {
                String trimmed = p.trim();
                if (!trimmed.isEmpty()) {
                    tokens.add(trimmed);
                }
            }
        }

        return tokens;
    }

    public static List<String> tokenizeParentheses(String[] lines) {
        List<String> tokens = new ArrayList<>();

        for (String line : lines) {
            // Similarly, we split on `(` or `)`
            // The regex is `(?=[()])|(?<=[()])`
            // which looks ahead and behind for '(' or ')'.
            String[] parts = line.split("(?=[()])|(?<=[()])");

            for (String p : parts) {
                String trimmed = p.trim();
                if (!trimmed.isEmpty()) {
                    tokens.add(trimmed);
                }
            }
        }

        return tokens;
    }

    public static String[] commaRemover(String input) {
        if (input == null || input.isEmpty()) {
            // Return an empty array if the input is null or empty
            return new String[0];
        }

        // Split on commas; "\\s*" can optionally be used around the comma
        // to also trim whitespace, e.g. input.split("\\s*,\\s*")
        String[] tokens = input.split(",");

        // (Optional) Trim each token in case there's leading/trailing whitespace
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].trim();
        }

        return tokens;
    }
}
