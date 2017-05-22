package code.assistant;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jqbian on 2017-05-15.
 *
 * @author jqbian
 */
public class FetchSpecialData {
    private static Pattern processCompile = Pattern.compile("(roomTypeId..\\d+..startDate)");
    private static Pattern processCompileNum=Pattern.compile("\\d+");
    public static void main(String args[]) {
        String fileName = "e:\\new1.txt";
				testProcessor(fileName);
			  //testUseString(fileName);
    }

    private static void testUseString2(String fileName){
			try {
				File file = new File(fileName);
				List<String> sourceList = FileUtils.readLines(file, "utf-8");
				List<String> dest = new ArrayList<String>();
				for (int i = 0; i < sourceList.size(); i++) {
					String s1 = sourceList.get(i);
					String tempS1[]=s1.split("roomTypeId..\\d+");
					for(int index=0;index<tempS1.length;index++){
						System.out.println(tempS1[index]);
					}
				}
				}catch(Exception ex){

				}
		}

    private static void testUseString(String fileName){
		try {
			File file = new File(fileName);
			List<String> sourceList = FileUtils.readLines(file, "utf-8");
			List<String> dest = new ArrayList<String>();
			for (int i = 0; i < sourceList.size(); i++) {
				String s1 = sourceList.get(i);
				String tempS1[]=s1.split("roomTypeId\":");
				for(int index=0;index<tempS1.length;index++){
					String s2 = tempS1[index];
					String[] tempS2=s2.split(",\"startDate");
					if(tempS2.length>1){
						String dest2=tempS2[0];
						dest.add(dest2);
					}
				}
			}
			File destFile=new File("e:\\test.txt");
			String tempStr=dest.toString();
			tempStr="select hotel,room,basicroomtypeid from roomtype where room in ("+tempStr.substring(1,tempStr.length()-1) + ") and hotel<=0";
			System.out.println(tempStr);
			FileUtils.write(destFile,tempStr,"utf-8");
		}catch(Exception ex){
			 ex.printStackTrace();
		 }
    }

    public static void testProcessor(String fileName){
		try{
			String saveFileName = fileName.substring(fileName.lastIndexOf("\\"),fileName.length());
			File file = new File(fileName);
			List<String> sourceList=FileUtils.readLines(file, "utf-8");
			List<String> dest = new ArrayList<String>();
			List<String> listFieldDest = new ArrayList<>();
			String listField="";
			for (int i = 0; i < sourceList.size(); i++) {
				String s1 = sourceList.get(i);
				Matcher matcher1 = processCompile.matcher(s1);
				while(matcher1.find()) {
					String matcher1Str=matcher1.group();
					//System.out.println(matcher1.group());
					//int count = matcher1.groupCount();
					Matcher numMather=processCompileNum.matcher(matcher1Str);
					if(numMather.find()){
						System.out.println(numMather.group());
					}
//					for(int index=1;index<matcher1.groupCount();index++) {
//						String processClasssInstance = matcher1.group(index);
//						dest.add(processClasssInstance);
//					}
//				  //processClasssInstance = StringUtils.uncapitalize(processClasssInstance);
			  }
			}
		  File destFile=new File("e:\\test.txt");
			FileUtils.writeLines(destFile,listFieldDest);
			//FileUtils.writeLines(destFile,methodDeclare,true);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

}
