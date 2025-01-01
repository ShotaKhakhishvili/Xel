package Extra;

import java.util.*;

public interface Functions {
    // Define known multi-character operators (extend as needed)
    Set<String> MULTI_CHAR_OPERATORS = new HashSet<>(Arrays.asList(
            "&&", "||", "++", "--", "+=", "-=", "*=", "/=", "%=", "==", "!=", ">=", "<="
    ));

    // Define known single-character operators/punctuation (extend as needed)
    Set<Character> SINGLE_CHAR_TOKENS = new HashSet<>(Arrays.asList(
            '+', '-', '*', '/', '%', '=', '<', '>', '!', '&', '|', '^', '~',
            '(', ')', '{', '}', '[', ']', ';', ',', ':', '.', '?'
    ));

    private static boolean isAlphaNumericOrUnderscore(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    // Identify if the character can start an identifier (letter or underscore)
    private static boolean isIdentifierStart(char c) {
        return Character.isLetter(c) || c == '_';
    }

    // Identify if the character can be in the body of an identifier (letter, digit, or underscore)
    private static boolean isIdentifierBody(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    // Check if a character might start a numeric literal (digit or '.')
    private static boolean isNumberStart(char c) {
        return Character.isDigit(c) || c == '.';
    }
    // Check if c is alphanumeric, underscore, or dot
    private static boolean isAlnumUnderscoreDot(char c) {
        return Character.isLetterOrDigit(c) || c == '_' || c == '.';
    }

    public static List<String> generalTokenizer(String sourceCode) {
        List<String> tokens = new ArrayList<>();
        int i = 0;
        int length = sourceCode.length();

        while (i < length) {
            char ch = sourceCode.charAt(i);

            // 1. Skip whitespace
            if (Character.isWhitespace(ch)) {
                i++;
                continue;
            }

            // 2. Check for multi-character operator
            if (i + 1 < length) {
                String twoChars = "" + ch + sourceCode.charAt(i + 1);
                if (MULTI_CHAR_OPERATORS.contains(twoChars)) {
                    tokens.add(twoChars);
                    i += 2;
                    continue;
                }
            }

            // 3. Check single-character operator
            if (SINGLE_CHAR_TOKENS.contains(ch)) {
                tokens.add(String.valueOf(ch));
                i++;
                continue;
            }

            // 4. If it's a string start, consume until its finished
            if(ch == '"'){
                int start = i++;
                while(i < length && sourceCode.charAt(i) != '"'){
                    i++;
                }
                if(i < sourceCode.length())i++;
                tokens.add(sourceCode.substring(start,i));
                continue;
            }

            // 5. If it's alnum/underscore/dot, consume entire run
            if (isAlnumUnderscoreDot(ch)) {
                int start = i;
                while (i < length && isAlnumUnderscoreDot(sourceCode.charAt(i))) {
                    i++;
                }
                tokens.add(sourceCode.substring(start, i));
                continue;
            }

            // 6. Otherwise, it's illegal
            tokens.add("ILLEGAL(" + ch + ")");
            i++;
        }

        return tokens;
    }

    static List<List<String>> groupInstructions(List<String> tokens) {
        List<List<String>> instructions = new ArrayList<>();
        List<String> current = new ArrayList<>();

        for (String token : tokens) {
            if (token.equals(";")) {
                // End the current instruction (discard ";")
                if (!current.isEmpty()) {
                    instructions.add(new ArrayList<>(current));
                    current.clear();
                }
            } else if (token.equals("{") || token.equals("}")) {
                // If there's a current instruction, finalize it
                if (!current.isEmpty()) {
                    instructions.add(new ArrayList<>(current));
                    current.clear();
                }
                // Then push the single-token instruction for "{" or "}"
                instructions.add(Arrays.asList(token));
            } else {
                // Normal token, add it to current
                current.add(token);
            }
        }

        // If there's anything left in 'current' at the end, push it
        if (!current.isEmpty()) {
            instructions.add(current);
        }
        return instructions;
    }

    static String[][] declarationSeperator(String[] tokens){
        List<List<String>> list = new ArrayList<>();
        list.add(new ArrayList<>());

        for(String token : tokens){
            if(token.equals(",")){
                if(!list.get(list.size() - 1).isEmpty())
                    list.add(new ArrayList<>());
            }
            else
                list.get(list.size()-1).add(token);
        }
        if(list.get(list.size()-1).isEmpty())
            list.remove(list.size()-1);
        return list.stream().map(l -> l.toArray(new String[0])).toArray(String[][]::new);
    }
}
