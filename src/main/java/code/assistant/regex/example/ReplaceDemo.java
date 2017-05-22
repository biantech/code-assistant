package code.assistant.regex.example;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ReplaceDemo {
 
    private static String REGEX = "dog";
    private static String INPUT = "The dog says meow. All dogs say meow.";
    private static String REPLACE = "cat";
 
    public static void main(String[] args) {
        Pattern p = Pattern.compile(REGEX);
        Matcher m = p.matcher(INPUT); // 获得匹配器对象
        INPUT = m.replaceAll(REPLACE);
        System.out.println(INPUT);
    }
}
