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
    static List<String> tokenize(String expression) {
        // Replace various types of Unicode dashes/minus signs with a standard ASCII hyphen
        expression = expression
                .replaceAll("[−–—]", "-"); // This covers U+2212, U+2013, and U+2014
        List<String> tokens = new ArrayList<>();

        // Updated regex to include:
        //   1) floating-point numbers (\d+\.\d+)
        //   2) integers (\d+)
        //   3) variables (start with letter or underscore, continue with letters, digits, underscores)
        //   4) arithmetic operators and parentheses ([()+\-*/%^])
        String tokenPatterns = "\\d+\\.\\d+|\\d+|[a-zA-Z_][a-zA-Z0-9_]*|[()+\\-*/%^]";
        Pattern pattern = Pattern.compile(tokenPatterns);

        // Remove whitespace, then match
        Matcher matcher = pattern.matcher(expression.replaceAll("\\s+", ""));
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        return tokens;
    }

}
