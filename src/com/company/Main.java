package com.company;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main
{
    private static HashMap<String, Pattern> lexem = new HashMap<>();
    static
    {
        lexem.put("VAR", Pattern.compile("^[a-z][a-z0-9]{0,}$"));
        lexem.put("STRING", Pattern.compile("\".*\""));
        lexem.put("DIGIT", Pattern.compile("^0|([1-9][0-9]*)$"));
        lexem.put("ASSIGN_OP", Pattern.compile("^=$"));
        lexem.put("OP", Pattern.compile("^\\+|-|/|%|\\*$"));
        lexem.put("PROBEL", Pattern.compile("^( )$"));
        lexem.put("EXCLAMATION", Pattern.compile("^(!)$"));
        lexem.put("LEFT_BRACKET", Pattern.compile("^\\($"));
        lexem.put("RIGHT_BRACKET", Pattern.compile("^\\)$"));
        lexem.put("LESS_POINT", Pattern.compile("^(<)$"));
        lexem.put("MORE_POINT", Pattern.compile("^(>)$"));
        lexem.put("LEFT_BRACE", Pattern.compile("^\\{$"));
        lexem.put("RIGHT_BRACE", Pattern.compile("^(})$"));
    }

    public static void main(String[] args)
    {
        String srcExample = "primer=810+-18 \"хелло ворлд\" {}()<>";
        List<Token> tokens = new LinkedList<>();
        makeTokens(srcExample, tokens); // строка и список токенов
        printTokens(tokens);
    }

    public static void makeTokens(String srcExamp, List<Token> tokens)
    {
        String lineZ1 = ""; //инициалтзируем пустые строки
        String lineZ2 = "";
        for (int i = 0 ; i < srcExamp.length(); i++) {
            lineZ1 += srcExamp.substring(i, i + 1);    //делаем подстроку по символу методом substring
            if (i < srcExamp.length() - 1)
            {
                lineZ2 = lineZ1 + srcExamp.substring(i + 1, i + 2); //подстрока, которая возвращает на символ вперед
            }
            for (String lexName : lexem.keySet())
            {
                Matcher mat1 = lexem.get(lexName).matcher(lineZ1);
                Matcher mat2 = lexem.get(lexName).matcher(lineZ2);
                if (mat1.matches())
                {
                    if (!mat2.matches() || i == srcExamp.length()-1) {        //правило вывода
                        tokens.add(new Token(lexName, lineZ1));
                        lineZ1 = "";
                    }
                    break;
                }
            }
        }
    }

  public static void printTokens(List<Token> tokens)
    {
        for(Token token: tokens)
        {
            System.out.println(token);
        }
    }

}