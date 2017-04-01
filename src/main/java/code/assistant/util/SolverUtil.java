package code.assistant.util;

import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.google.common.collect.Lists;

import java.io.File;

/**
 * Created by zq_zhou on 2017/3/21.
 */
public class SolverUtil {
    private CombinedTypeSolver combinedTypeSolver;
    private JavaParserFacade javaParserFacade;

    public SolverUtil(String... path) {
        init(path);
    }

    public void init(String[] path) {
        this.combinedTypeSolver = new CombinedTypeSolver();
        initJarTypeSolver("java.class.path");
        initJarTypeSolver("sun.boot.class.path");
        combinedTypeSolver.add(new ReflectionTypeSolver());
        for (int i = 0; i < path.length; i++) {
            combinedTypeSolver.add(new JavaParserTypeSolver(new File(path[i])));
        }
        javaParserFacade = JavaParserFacade.get(combinedTypeSolver);
    }

    private void initJarTypeSolver(String property) {
        Lists.newArrayList(System.getProperties().get(property).toString().split(";")).forEach(s -> {
            try {
                if (s.endsWith("jar") && new File(s).exists()) {
                    combinedTypeSolver.add(new JarTypeSolver(s));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public JavaParserFacade getJavaParserFacade() {
        return javaParserFacade;
    }

    public CombinedTypeSolver getCombinedTypeSolver() {
        return combinedTypeSolver;
    }

    //    public Type getType(Node node) {
    //        Type type = JavaParserFacade.get(combinedTypeSolver).getType(node);
    //        return type;
    //    }
}
