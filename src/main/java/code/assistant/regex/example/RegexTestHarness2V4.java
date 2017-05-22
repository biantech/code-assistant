package code.assistant.regex.example;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexTestHarness2V4 {

    public static void main(String[] args) throws IOException {
        Pattern pattern = null;
        Matcher matcher = null;
        BufferedReader br  = new BufferedReader(
                new InputStreamReader(new BufferedInputStream(System.in))
            );
        while (true) {
            try {
                System.out.print("\nEnter your regex: ");
                pattern = Pattern.compile(br.readLine());
                System.out.print("Enter input string to search: ");
                matcher = pattern.matcher(br.readLine());
            } catch (PatternSyntaxException pse) {                
                System.out.println("There is a problem with the regular expression!");
                System.out.println("The pattern in question is: " + pse.getPattern());
                System.out.println("The description is: " + pse.getDescription());
                System.out.println("The message is: " + pse.getMessage());
                System.out.println("The index is: " + pse.getIndex());
                System.exit(0);
            }
            boolean found = false;
            while (matcher.find()) {
                System.out.println("I found the text \"" + matcher.group() + 
                        "\" starting at index " + matcher.start() + 
                        " and ending at index " + matcher.end() + 
                        ".");
                found = true;
            }
            if (!found) {
                System.out.println("No match found.");
            }
        }
    }
}
