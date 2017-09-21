package code.assistant;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CorrectRequestXml {
    private String utf8="utf-8";
    private static Pattern processCompile = Pattern.compile("<City>\\d*</City>");
    private static Pattern processCompile2 = Pattern.compile("<DotX>\\d*.\\d*</DotX>");
    private static Pattern getProcessCompile3=Pattern.compile("<DotY>\\d*.\\d*</DotY>");

    public static void main(String arags[]) throws IOException {
        String fileName="D:\\work\\Hotel-SearchCache\\JavaMigration\\TestRequest\\Response-Java-03.xml";
        CorrectRequestXml correctRequestXml = new CorrectRequestXml();
        correctRequestXml.processDir("C:\\temp2\\renamed");
    }


    public void processDir(String dirName) throws IOException {
        String pattern="City>\\d*";
        File dir = new File(dirName);
        File[] files = dir.listFiles();
        for(File tempFile:files){
            if(tempFile.isFile()){
                System.out.println("process file "+ tempFile.getName());
                String fileContext=FileUtils.readFileToString(tempFile,"utf-8");
                Matcher matcher1 = processCompile.matcher(fileContext);
                boolean find=matcher1.find();
                if(find){
                    fileContext=matcher1.replaceAll("<City>2</City>");
                }
                Matcher matcher2 = processCompile2.matcher(fileContext);
                find = matcher2.find();
                if(find){
                    fileContext=matcher2.replaceAll("<DotX>31.213494</DotX>");
                }
                matcher2=getProcessCompile3.matcher(fileContext);
                find=matcher2.find();
                if(find){
                    fileContext=matcher2.replaceAll("<DotY>121.432298</DotY>");
                }
                FileUtils.write(tempFile,fileContext,"utf-8");
            }
        }
    }
}
