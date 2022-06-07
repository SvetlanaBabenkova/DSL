package labas;
import java.util.*;

public class MyParser {

    public Map<String, String> ourVariable = new HashMap<>();
    public LinkedList<String> ourListers = new LinkedList<>();
    private final Map<Integer, LinkedList<Token>> tokensOfTabs = new TreeMap<>();
    int key; String resultChecking = "-1";
    double digitWhile=-1000.0;
    public void estimation (Map<Integer, LinkedList<Token>> tokensMap) throws Exception {
        try {
            for (key=1; key<=tokensMap.size(); key++) {
                if (!tokensMap.get(key).isEmpty()) {
                    switch (Objects.requireNonNull(tokensMap.get(key).peekFirst()).getType()) {
                        case "LIST" -> {
                            if (tokensMap.get(key).size()==4) {
                                if (Objects.equals(tokensMap.get(key).get(1).getValue(), "("))
                                    tokensMap.get(key).remove(1);
                                else error(key);
                                if (Objects.equals(tokensMap.get(key).get(2).getValue(), ")"))
                                    tokensMap.get(key).remove(2);
                                else error(key);
                                switch (tokensMap.get(key).peekFirst().getValue()){
                                    case "L.A" -> addList(tokensMap.get(key).get(1));
                                    case "L.P" -> printList(tokensMap.get(key).get(1));
                                    case "L.R" -> removeList(tokensMap.get(key).get(1));
                                    default -> error(key);
                                }
                                tokensMap.get(key).clear();
                            }
                            else error(key);
                        }
                        case "VAR" -> {
                            if (tokensMap.get(key).size()==1)
                                System.out.println(tokensMap.get(key).peekFirst().getValue()+": "+checkVariable(tokensMap.get(key)).pollFirst().getValue());
                            else if (Objects.equals(tokensMap.get(key).get(1).getValue(), ":")&&(tokensMap.get(key).size()>2)) {
                                tokensMap.get(key).remove(1);
                                ourVariable.put(tokensMap.get(key).pollFirst().getValue(), calculation(checkVariable(tokensMap.get(key))));
                                tokensMap.get(key).clear();
                            } else error(key);
                        }
                        case "DIGIT" -> {
                            if (tokensMap.get(key).size()==1)
                                System.out.print(tokensMap.get(key).pollFirst());
                            else error(key);
                        }
                        case "CONDITION" -> {
                            if (Objects.equals(tokensMap.get(key + 1).get(0).getValue(), "{") &&
                                    (Objects.equals(tokensMap.get(key).get(1).getValue(), "("))) {
                                tokensMap.get(key).removeFirst();
                                resultChecking = calculation(checkVariable(tokensMap.get(key)));
                            } else error(key);
                        }
                        case "CYCLE" -> {
                            if (Objects.equals(tokensMap.get(key + 1).get(0).getValue(), "{") &&
                                    (Objects.equals(tokensMap.get(key).get(1).getValue(), "("))) {
                                for (Token token : tokensMap.get(key)) if (Objects.equals(token.getType(), "DIGIT")) {
                                    digitWhile = Double.parseDouble(token.getValue());
                                }
                                if (digitWhile==-1000.0) error(key);
                                else resultChecking = "1!";
                            } else error(key);
                        }
                        case "LEFT_BRACE" -> {
                            if (Objects.equals(resultChecking, "1")||Objects.equals(resultChecking, "1!")) {
                                int i=1; tokensMap.get(key).removeFirst();
                                if (Objects.equals(resultChecking, "1!")) {
                                    String nameVar = tokensMap.get(key).peekFirst().getValue();
                                    double var = Double.parseDouble(checkVariable(tokensMap.get(key)).peekFirst().getValue());
                                    double numberWhile = Double.parseDouble(tokensMap.get(key).peekLast().getValue());
                                    while (digitWhile > var) {var = var+numberWhile; System.out.println(nameVar+": " +var);}
                                    while (!tokensMap.get(key).contains(new Token("RIGHT_BRACE", "}"))) {
                                        tokensMap.get(key).clear();
                                        key++;
                                    }
                                    tokensMap.get(key).clear();
                                }
                                else {
                                    while (!tokensMap.get(key).contains(new Token("RIGHT_BRACE", "}"))) {
                                        tokensOfTabs.put(i, tokensMap.get(key));
                                        i++; key++;
                                    }
                                    tokensMap.get(key).removeLast();
                                    tokensOfTabs.put(i, tokensMap.get(key));
                                    estimation(tokensOfTabs);
                                }
                                resultChecking = "-1";
                            } else if (Objects.equals(resultChecking, "0")||Objects.equals(resultChecking, "0!")){
                                int z=0;
                                while (!tokensMap.get(key).contains(new Token("RIGHT_BRACE", "}"))) {
                                    tokensMap.get(key).clear();
                                    z++; key++;
                                }
                                tokensMap.get(key).clear();
                            } else error(key);
                        }
                        default -> error(key);
                    }
                }
            }
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
            System.exit(0);
        }
    }
    private void addList(Token token) {
        if (Objects.equals(token.getValue(), "all"))
            error(key);
        else ourListers.addLast(token.getValue());
    }
    private void removeList(Token token) {
        if (ourListers.contains(token.getValue())) {
            while (ourListers.contains(token.getValue())) ourListers.remove(token.getValue());
        } else if (Objects.equals(token.getValue(), "all")) {
            ourListers.clear();
        } else error(key);
    }
    private void printList(Token token) {
        if (ourListers.contains(token.getValue())) {
            System.out.println("Only first index: "+ourListers.indexOf(token.getValue()));
        } else if (Objects.equals(token.getValue(), "all")) {
            int index = 0;
            for (String str : ourListers) {
                index++;
                System.out.println("["+index+"]: "+ str);
            }
            if (index==0) System.out.println("List is empty");
        } else error(key);
    }
    private void error(int k) {
        System.out.print("Error: "+ k); System.exit(0);
    }
    private String calculation(LinkedList<Token> tokens) {
        while (tokens.contains(new Token("LEFT_BRACKET", "("))) {
            brackets(tokens);}
        while (tokens.contains(new Token("HOP", "*"))||tokens.contains(new Token("HOP", "/")))
            highOperation(tokens);
        while (tokens.contains(new Token("LOP", "+"))||tokens.contains(new Token("LOP", "-")))
            lowOperation(tokens);
        while (tokens.contains(new Token("MORE_POINT", ">"))||tokens.contains(new Token("LESS_POINT", "<"))
                ||tokens.contains(new Token("EQUALS", "=")))
            compare(tokens);
        while (tokens.contains(new Token("DOPCOMPARE", "||"))||tokens.contains(new Token("DOPCOMPARE", "&&")))
            dopCompare(tokens);
        if (tokens.size()!=1) {
            error(key);
            return "";
        } else return tokens.pollFirst().getValue();
    }
    private void  brackets(LinkedList<Token> tokens) {
        int l = tokens.indexOf(new Token("LEFT_BRACKET", "("));
        int r = tokens.indexOf(new Token("RIGHT_BRACKET", ")"));
        int i = 0; LinkedList<Token> tokensBracket=sublist(tokens, l+1, r-1);
        while (tokensBracket.contains(new Token("LEFT_BRACKET", "("))) {
            i++; l = tokensBracket.indexOf(new Token("LEFT_BRACKET", "("))+i;
            tokensBracket=sublist(tokens, l+1, r-1);
        }
        tokens.set(l, new Token ("DIGIT", calculation(tokensBracket)));
        tokens.subList(l+1, r+1).clear();
    }
    private LinkedList<Token> sublist(LinkedList<Token> tokens, int start, int end) {
        LinkedList<Token> tokensHelper = new LinkedList<>();
        for (int n=start; n<=end; n++) tokensHelper.addLast(tokens.get(n));
        return tokensHelper;
    }
    private void highOperation(LinkedList<Token> tokens) {
        while (tokens.contains(new Token("HOP", "*"))) {
            checkLaR(tokens, new Token("HOP", "*"));
        }
        while (tokens.contains(new Token("HOP", "/"))) {
            checkLaR(tokens, new Token("HOP", "/"));
        }
    }
    private void lowOperation(LinkedList<Token> tokens) {
        while (tokens.contains(new Token("LOP", "+"))) {
            checkLaR(tokens, new Token("LOP", "+"));
        }
        while (tokens.contains(new Token("LOP", "-"))) {
            checkLaR(tokens, new Token("LOP", "-"));
        }
    }
    private void compare(LinkedList<Token> tokens) {
        if (tokens.contains(new Token("MORE_POINT", ">"))) {
            checkLaR(tokens, new Token("MORE_POINT", ">"));
        }
        if (tokens.contains(new Token("LESS_POINT", "<"))) {
            checkLaR(tokens, new Token("LESS_POINT", "<"));
        }
        if (tokens.contains(new Token("EQUALS", "="))) {
            checkLaR(tokens, new Token("EQUALS", "="));
        }
    }
    private void dopCompare(LinkedList<Token> tokens) {
        while (tokens.contains(new Token("DOPCOMPARE", "||"))) {
            checkLaR(tokens, new Token("DOPCOMPARE", "||"));
        }
        while (tokens.contains(new Token("DOPCOMPARE", "&&"))) {
            checkLaR(tokens, new Token("DOPCOMPARE", "&&"));
        }
    }
    private void checkLaR(LinkedList<Token> tokens, Token token) {
        int n = tokens.indexOf(token);
        if (Objects.equals(tokens.get(n - 1).getType(), "DIGIT")&&Objects.equals(tokens.get(n + 1).getType(), "DIGIT")) {
            tokens.set(n-1, new Token("DIGIT", yourAccount(tokens.get(n).getValue(),
                    Double.parseDouble(tokens.get(n - 1).getValue()), Double.parseDouble(tokens.get(n + 1).getValue()))));
            tokens.remove(n); tokens.remove(n);
        } else error(key);
    }
    public LinkedList<Token> checkVariable(LinkedList<Token> tokens) {
        for (int i=0; i<tokens.size(); i++) {
            if (Objects.equals(tokens.get(i).getType(), "VAR"))
                if (!ourVariable.containsKey(tokens.get(i).getValue())) error(key);
                else tokens.set(i, new Token("DIGIT", ourVariable.get(tokens.get(i).getValue())));
        }
        return tokens;
    }
    private String yourAccount(String operation, double n1, double n2) {
        switch (operation) {
            case "+" -> {return n1+n2+"";}
            case "-" -> {return n1-n2+"";}
            case "*" -> {return n1*n2+"";}
            case "/" -> {return n1/n2+"";}
            case ">" -> {if (n1>n2) return "1";
            else return "0";}
            case "<" -> {if (n1<n2) return "1";
            else return "0";}
            case "=" -> {if (n1==n2) return "1";
            else return "0";}
            case "||" -> {if ((n1==0)&&(n2==0)) return "0";
            else return "1";}
            case "&&" -> {if ((n1==1)&&(n2==1)) return "1";
            else return "0";}
            default -> {return "fghj";}
        }
    }
}
