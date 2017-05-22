package code.assistant.batchreplace;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jqbian on 2017-04-10.
 *
 * @author jqbian
 */
public class SearchLazyFactoryReplace {
    private static Pattern processCompile = Pattern.compile("new (\\w+)()");
    public static void main(String args[]) throws IOException {
        //String fileName = "C:\\CtripSource\\hotelsearchservice\\hotel-product-search-business\\src\\main\\java\\com\\ctrip\\hotel\\search\\biz\\factory\\searchtypes\\SpecialOfferSearch.java";
        String fileName = "C:\\CtripSource\\hotelsearchservice\\hotel-product-search-business\\src\\main\\java\\com\\ctrip\\hotel\\search\\biz\\factory\\fw\\SearchLazyFactory.java";
        SearchLazyFactoryReplace test = new SearchLazyFactoryReplace();
        File file = new File(fileName);
        test.process(file);
    }

    public void process(File file) throws IOException {
        //List<String> sourceList= FileUtils.readLines(file, "utf-8");
        String source=FileUtils.readFileToString(file, "utf-8");
        //for (int i = 0; i < sourceList.size(); i++) {
        //    String s1 = sourceList.get(i);
        Matcher matcher1 = processCompile.matcher(source);
        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer instanceBuffer = new StringBuffer();
        ArrayList<String> insta = new ArrayList<>();
        while (matcher1.find()){
             String findS=matcher1.group(1);
            int count=matcher1.groupCount();
             matcher1.appendReplacement(stringBuffer, StringUtils.uncapitalize(findS));
            instanceBuffer.append("@Resource "+findS+" " + StringUtils.uncapitalize(findS)+";\n");
        }
        File file2 = new File("c:/testTxt.txt");
        FileUtils.write(file2,stringBuffer,"utf-8");
        FileUtils.write(file2,instanceBuffer,"utf-8",true);

    }
}

