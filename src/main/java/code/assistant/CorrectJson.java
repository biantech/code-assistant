package code.assistant;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jqbian on 2017-04-07.
 *
 * @author jqbian
 */
public class CorrectJson {
    private static Pattern processCompile = Pattern.compile("(\\w+):");
    public static void  main(String []args) throws IOException {
        regex();
    }

    private static void regex() throws IOException {
        File file = new File("c:/test.json");
        String jsonTest= FileUtils.readFileToString(file,"utf-8");
        Matcher matcher1=processCompile.matcher(jsonTest);
        StringBuffer stringBuffer = new StringBuffer();
        while (matcher1.find()) {
            String sgroup1=matcher1.group(1);
            matcher1.appendReplacement(stringBuffer, "\"" + StringUtils.uncapitalize(sgroup1) + "\":");
        }
        matcher1.appendTail(stringBuffer);
        System.out.println(stringBuffer);
        File file2 = new File("c:/test2.json");
        FileUtils.write(file2,stringBuffer,"utf-8");
    }

    public static void testSplit() throws IOException {
        File file = new File("c:/test.json");
        String jsonTest=FileUtils.readFileToString(file,"utf-8");
        String s[]=jsonTest.split(":");
        for(int index=0;index<s.length;index++){
            String tempS=s[index];
            if(tempS.indexOf("")<0){

            }
        }
    }
}
