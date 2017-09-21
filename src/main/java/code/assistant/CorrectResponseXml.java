package code.assistant;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jqbian on 2017-05-22.
 * @author jqbian
 */
public class CorrectResponseXml {
    private static Pattern processCompile = Pattern.compile("(<[a-zA-Z]+></[a-zA-Z]+>)");
    public static void main(String arags[]){
        String fileName="D:\\work\\Hotel-SearchCache\\JavaMigration\\TestRequest\\Response-Java-03.xml";
        processFile(fileName);
    }
    public static void processFile(String fileName){
        try{
            String saveFileName = fileName.substring(fileName.lastIndexOf("\\"),fileName.length());
            File file = new File(fileName);
            List<String> sourceList= FileUtils.readLines(file, "utf-8");

            List<String> destList=new ArrayList<>();
            List<String> listFieldDest = new ArrayList<>();
            for (int i = 0; i < sourceList.size(); i++) {
                String s1 = sourceList.get(i);
                Matcher matcher1 = processCompile.matcher(s1);
                //destList.add(s1);
                if(matcher1.find()) {
                    String matcher1Str=matcher1.group();
                    int indexOf=matcher1Str.indexOf(">");
                    String substr=matcher1Str.substring(0,indexOf);
                    substr=substr+"/>";
                    StringBuffer stringBuffer=new StringBuffer();
                    matcher1.appendReplacement(stringBuffer,substr);
                    destList.add(stringBuffer.toString());
                }else{
                    if(s1.indexOf(".0<")>0){
                        s1=s1.replace(".0<","<");
                    }else if(s1.indexOf("+08:00<")>0){
                        s1=s1.replace("+08:00<","<");
                    }
                    destList.add(s1);
                }
            }
            //File destFile=new File("d:\\test.xml");
            FileUtils.writeLines(file,destList);
            //FileUtils.writeLines(destFile,methodDeclare,true);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

}
