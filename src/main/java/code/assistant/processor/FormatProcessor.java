package code.assistant.processor;

import code.assistant.util.SolverUtil;
import code.assistant.visitor.MyModifierVisitor;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.SourceRoot;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.io.FileUtils.listFiles;

/**
 * Created by jqbian on 2017-03-30.
 *
 * @author jqbian
 */
public class FormatProcessor {
    private static final Pattern FIELD_PATTERN = Pattern.compile("(<[^>]+>)\\.");

    public static List<String> getSrcPath(File basePath) {
        String[] subFiles = basePath.list(DirectoryFileFilter.INSTANCE);
        List<String> srcPathes = Lists.newArrayList();
        Lists.newArrayList(subFiles).stream().forEach(subFile -> {
            File srcDir = new File(
                    basePath.getAbsolutePath() + File.separatorChar + subFile + File.separatorChar + "src/main/java");
            if (srcDir.exists() && srcDir.isDirectory()) {
                srcPathes.add(srcDir.getAbsolutePath());
            }
        });
        return srcPathes;
    }

    public static void preProcess(String filePath) throws IOException {
        File f = new File(filePath);
        if (f.isDirectory()) {
            Collection<File> files = listFiles(f, new String[] {"java"}, true);
            for (Iterator<File> iterator = files.iterator(); iterator.hasNext(); ) {
                File file = iterator.next();
                preProcess(file);
            }
        } else {
            preProcess(f);
        }
    }

    public static void preProcess(File file) throws IOException {
        String code = FileUtils.readFileToString(file, Charset.forName("UTF-8"));
        String md5 = Hashing.md5().hashUnencodedChars(code).toString();
        Matcher matcher = FIELD_PATTERN.matcher(code);
        StringBuilder sb = new StringBuilder();
        int index = 0;
        while (matcher.find()) {
            String generic = matcher.group(1);
            sb.append(code.substring(index, matcher.start(1)) + "." + generic);
            index = matcher.end(1) + 1;
        }
        sb.append(code.substring(index));
        String result = sb.toString().replaceAll("yield ", "");
        if (!md5.equals(Hashing.md5().hashUnencodedChars(result).toString())) {
            FileUtils.write(file, result, Charset.forName("UTF-8"));
        }
    }

    private static void parsePath(String path, SolverUtil solverUtil, boolean save) throws IOException {
        SourceRoot src = new SourceRoot(Paths.get(path, StringUtils.EMPTY));
        Set<Map.Entry<Path, ParseResult<CompilationUnit>>> entries = src.tryToParse().entrySet();
        for (Iterator<Map.Entry<Path, ParseResult<CompilationUnit>>> iterator = entries.iterator(); iterator
                .hasNext(); ) {
            Map.Entry<Path, ParseResult<CompilationUnit>> entry = iterator.next();
            CompilationUnit cu = entry.getValue().getResult().orElse(null);
            if (cu == null) {
                System.out.println("error:" + entry.getKey());
                continue;
            }
            new MyModifierVisitor(cu, solverUtil).visit(cu, new Object());
        }
        if (save)
            src.saveAll();
    }

    public static void parseFile(String file, SolverUtil solverUtil, boolean save)
            throws IOException, IllegalAccessException {
        FileInputStream in = new FileInputStream(file);
        try {
            CompilationUnit cu;
            cu = JavaParser.parse(in);
            new MyModifierVisitor(cu, solverUtil).visit(cu, null);
            String source = cu.toString();
            System.out.println(source);
            if (save)
                FileUtils.write(new File(file), source, Charset.forName("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            in.close();
        }
    }

    public static String parseCode(String orignalCode, SolverUtil solverUtil) {
        CompilationUnit cu;
        cu = JavaParser.parse(orignalCode);
        new MyModifierVisitor(cu, solverUtil).visit(cu, null);
        String source = cu.toString();
        return source;
    }
}
