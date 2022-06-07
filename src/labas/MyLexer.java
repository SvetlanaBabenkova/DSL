package labas;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyLexer {
    private static final SortedMap<String, Pattern> lexem = new TreeMap<>();
    private static final MyParser parser = new MyParser();
    static {
        lexem.put("CONDITION", Pattern.compile("^\\?$"));
        lexem.put("LIST", Pattern.compile("^L\\.(A|R|P)"));
        lexem.put("VAR", Pattern.compile("^([a-z][a-z0-9]*)$"));
        lexem.put("DIGIT", Pattern.compile("^(-?)(0|([1-9][0-9]*))(\\.[0-9]+)?"));
        lexem.put("ASSIGN_OP", Pattern.compile("^:$"));
        lexem.put("HOP", Pattern.compile("^/|\\*$"));
        lexem.put("LOP", Pattern.compile("^\\+|-$"));
        lexem.put("PROBEL", Pattern.compile("^( )$"));
        lexem.put("CYCLE", Pattern.compile("^!$"));
        lexem.put("LEFT_BRACKET", Pattern.compile("^\\($"));
        lexem.put("RIGHT_BRACKET", Pattern.compile("^\\)$"));
        lexem.put("LESS_POINT", Pattern.compile("^(<)$"));
        lexem.put("MORE_POINT", Pattern.compile("^(>)$"));
        lexem.put("EQUALS", Pattern.compile("^(=)$"));
        lexem.put("LEFT_BRACE", Pattern.compile("^\\{$"));
        lexem.put("RIGHT_BRACE", Pattern.compile("^(})$"));
        lexem.put("DOPCOMPARE", Pattern.compile("^&&|\\|\\|$"));
    }
    public static Map<Integer, LinkedList<Token>> tokensMap = new TreeMap<>();
    public static void main(String[] args) throws Exception {
        String srcExample = "L.A(6) L.A(+) L.A(7) L.P(7) L.P(all) L.R(7) L.P(all) ";
        LinkedList<Token> tokens = new LinkedList<>();
        makeTokens(srcExample, tokens);
        distributionTokens(tokens);
        //for (Token token : tokens) System.out.println(token.getType() + token.getValue());
        parser.estimation(tokensMap);
    }
    public static void makeTokens(String srcExample, List<Token> tokens) {
        String lineZ1 = "";
        String lineZ2 = "";
        for (int i = 0 ; i < srcExample.length(); i++) {
            lineZ1 += srcExample.substring(i, i + 1);
            if (i < srcExample.length() - 1) {
                lineZ2 = lineZ1 + srcExample.charAt(i + 1);
            }
            for (String lexName : lexem.keySet()) {
                Matcher mat1 = lexem.get(lexName).matcher(lineZ1);
                Matcher mat2 = lexem.get(lexName).matcher(lineZ2);
                if (mat1.matches()) {
                    if (!mat2.matches() || i == srcExample.length()-1) {
                        tokens.add(new Token(lexName, lineZ1));
                        lineZ1 = "";
                    }
                    break;
                }
            }
        }
    }
    public static void distributionTokens(LinkedList<Token> tokens) {
        int i=0; int j=0;
        for(int z=0; z<tokens.size(); z++) {
            if (Objects.equals(tokens.get(z).getType(), "PROBEL")) {
                i++;
                tokensMap.put(i, sublist(tokens, j, z-1));
                j=z+1;
            }
        }
    }
    public static LinkedList<Token> sublist(LinkedList<Token> tokens, int start, int end) {
        LinkedList<Token> tokensHelper = new LinkedList<>();
        for (int n=start; n<=end; n++) tokensHelper.addLast(tokens.get(n));
        return tokensHelper;
    }

}
