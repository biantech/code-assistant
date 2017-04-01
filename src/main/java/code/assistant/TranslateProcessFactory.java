package code.assistant;

import code.assistant.processor.FormatProcessor;
import code.assistant.util.SolverUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslateProcessFactory {
	private static Pattern processCompile = Pattern.compile("Process.<([^>]+)>");
	private static Pattern methodCompile = Pattern.compile("List<([^>]+)> ([^(]+)\\(\\)");
	//private static Pattern oneLineCompile =Pattern.compile("List<([^>]+)> ([^(]+)\\(\\)");
	public static void main(String args[]){
		//String fileName = "C:\\CtripSource\\hotelsearchservice\\hotel-product-search-business\\src\\main\\java\\com\\ctrip\\hotel\\search\\biz\\factory\\searchtypes\\SpecialOfferSearch.java";
		String fileName = "D:\\SourceCode\\hotelsearchservice\\hotel-product-search-business\\src\\main\\java\\com\\ctrip\\hotel\\search\\biz\\WirelessSearchLazy.java";

		testProcessor(fileName);
	}

	public static void testProcessor(String fileName){
		try{
			String saveFileName = fileName.substring(fileName.lastIndexOf("\\"),fileName.length());
			File file = new File(fileName);
			List<String> sourceList=FileUtils.readLines(file, "utf-8");
			List<String> dest = new ArrayList<String>();
			List<String> methodInternal=new ArrayList<String>();
			List<String> methodDeclare=new ArrayList<>();
			List<String> afterPropertiesSet = new ArrayList<>();
			List<String> listFieldDest = new ArrayList<>();
			afterPropertiesSet.add("@Override  public void afterPropertiesSet() throws Exception { ");
			String listField="";
			for (int i = 0; i < sourceList.size(); i++) {
				String s1 = sourceList.get(i);
				Matcher matcher1=processCompile.matcher(s1);
				Matcher matcher2 = methodCompile.matcher(s1);

				if (matcher1.find()){
					String processClasssInstance=matcher1.group(1);
					processClasssInstance=StringUtils.uncapitalize(processClasssInstance);
					if(s1.trim().startsWith("//")) {
						methodInternal.add("//" + listField + ".add(" + processClasssInstance + ");");
					}else{
						methodInternal.add(listField+".add("+processClasssInstance+");");
					}
				}else if(matcher2.find()){
					if(CollectionUtils.isNotEmpty(methodInternal)) {
						methodDeclare.addAll(methodInternal);
						methodDeclare.add("}");
						methodInternal.clear();
					}
					int count=matcher2.groupCount();
					String methodLine = s1;
					String st1 = matcher2.group(1);
					String st2=matcher2.group(2);
					listField = "list"+ StringUtils.capitalize(st2);
					listFieldDest.add("private List<"+st1+"> "+listField+"=new ArrayList<>();");
					methodLine=methodLine+" return " + listField + "; \n } \n";
					methodDeclare.add(methodLine);
					methodInternal.add("private void init"+StringUtils.capitalize(st2)+"List(){");
					afterPropertiesSet.add("init"+StringUtils.capitalize(st2)+"List();");
				}else{
					if(!s1.trim().equalsIgnoreCase("}")) {
						methodInternal.add(s1);
						//dest.add(s1);
					}
				}
			}
			if(CollectionUtils.isNotEmpty(methodInternal)){
				methodDeclare.addAll(methodInternal);
				methodDeclare.add("}");
			}
			afterPropertiesSet.add("}");
			methodDeclare.addAll(afterPropertiesSet);
			File destFile= new File("d://"+saveFileName);
			FileUtils.writeLines(destFile,listFieldDest);
			FileUtils.writeLines(destFile,methodDeclare,true);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}


	public void test() throws Exception{
			String path =
					"D:\\zzq\\code\\HotelSearchService_backup\\hotelsearchservice_by_yuan\\hotel-product-search-business\\src\\main\\java\\com\\ctrip\\hotel\\search\\biz\\AreaRoomTypeListFilter.java";
			File basePath = new File("D:\\zzq\\code\\HotelSearchService_backup\\hotelsearchservice_by_yuan");
			List<String> srcPathes = FormatProcessor.getSrcPath(basePath);
			String[] srcPathArray = srcPathes.toArray(new String[srcPathes.size()]);
			SolverUtil solverUtil = new SolverUtil(srcPathArray);
			//        String path = "D:\\zzq\\code\\codebuff-master\\javaparser\\src\\test\\resources\\Test2.java";
			//        String path = "D:\\zzq\\code\\HotelSearchService_backup\\hotelsearchservice_by_yuan";
			//格式化代码目录
			FormatProcessor.preProcess(path);
			//                parsePath(path, true);
			//格式化单个文件
			FormatProcessor.parseFile(path, solverUtil, false);

	}


}
