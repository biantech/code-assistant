package code.assistant.visitor;

import code.assistant.util.SolverUtil;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.symbolsolver.model.typesystem.Type;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static com.github.javaparser.ast.Modifier.PUBLIC;

/**
 * Created by zq_zhou on 2017/3/17.
 */
public class MyModifierVisitor extends ModifierVisitor {
    private List<NodeList<?>> nodes;
    private SolverUtil solverUtil;
    private CompilationUnit compilationUnit;

    public MyModifierVisitor(CompilationUnit cu, SolverUtil solverUtil) {
        this.nodes = cu.getNodeLists();
        this.compilationUnit = cu;
        this.solverUtil = solverUtil;
    }

    @Override public Visitable visit(final FieldDeclaration n, Object arg) {
        Visitable visitable = super.visit(n, arg);
        EnumSet<Modifier> modifiers = n.getModifiers();
        boolean hasFinal = modifiers.contains(Modifier.FINAL);
        boolean hasStatic = modifiers.contains(Modifier.STATIC);
        if (hasStatic) {
            return visitable;
        }
        EnumSet<Modifier> newModifiers = n.getModifiers().clone();
        newModifiers.clear();
        NodeList<VariableDeclarator> vds = n.getVariables();
        //格式化大小写和去掉is
        for (Iterator<VariableDeclarator> iterator = vds.iterator(); iterator.hasNext(); ) {
            VariableDeclarator variableDeclarator = iterator.next();
            String nameAsString = variableDeclarator.getNameAsString();
            if (nameAsString.toLowerCase().startsWith("is")) {
                variableDeclarator.setName(StringUtils.uncapitalize(nameAsString.substring(2)));
            } else {
                variableDeclarator.setName(StringUtils.uncapitalize(nameAsString));
            }
        }
        //add get set
        for (Iterator<Modifier> iterator = modifiers.iterator(); iterator.hasNext(); ) {
            Modifier modifier = iterator.next();
            if (modifier.equals(Modifier.PUBLIC) && !hasFinal) {
                newModifiers.add(Modifier.PRIVATE);
                createGetter(n);
                if (!hasStatic)
                    createSetter(n);
            } else {
                newModifiers.add(modifier);
            }
        }
        n.setModifiers(newModifiers);

        return visitable;
    }

    public MethodDeclaration createSetter(FieldDeclaration f) {
        if (f.getVariables().size() != 1)
            throw new IllegalStateException("You can use this only when the field declares only 1 variable name");
        Optional<ClassOrInterfaceDeclaration> parentClass = f.getAncestorOfType(ClassOrInterfaceDeclaration.class);
        Optional<EnumDeclaration> parentEnum = f.getAncestorOfType(EnumDeclaration.class);
        if (!(parentClass.isPresent() || parentEnum.isPresent()) || (parentClass.isPresent() && parentClass.get()
            .isInterface()))
            throw new IllegalStateException("You can use this only when the field is attached to a class or an enum");
        VariableDeclarator variable = f.getVariable(0);
        String fieldName = variable.getNameAsString();
        String fieldNameUpper = fieldName.toUpperCase().substring(0, 1) + fieldName.substring(1, fieldName.length());
        String setName = "set" + fieldNameUpper;
        if (parentClass.isPresent() && CollectionUtils.isNotEmpty(parentClass.get().getMethodsByName(setName))) {
            return null;
        }
        final MethodDeclaration setter;
        setter = parentClass.map(clazz -> clazz.addMethod(setName, PUBLIC))
            .orElseGet(() -> parentEnum.get().addMethod(setName, PUBLIC));
        setter.setType(new VoidType());
        setter.getParameters().add(new Parameter(variable.getType(), fieldName));
        BlockStmt blockStmt2 = new BlockStmt();
        setter.setBody(blockStmt2);
        blockStmt2.addStatement(
            new AssignExpr(new NameExpr("this." + fieldName), new NameExpr(fieldName), AssignExpr.Operator.ASSIGN));
        return setter;
    }

    public MethodDeclaration createGetter(FieldDeclaration f) {
        if (f.getVariables().size() != 1)
            throw new IllegalStateException("You can use this only when the field declares only 1 variable name");
        Optional<ClassOrInterfaceDeclaration> parentClass = f.getAncestorOfType(ClassOrInterfaceDeclaration.class);
        Optional<EnumDeclaration> parentEnum = f.getAncestorOfType(EnumDeclaration.class);
        if (!(parentClass.isPresent() || parentEnum.isPresent()) || (parentClass.isPresent() && parentClass.get()
            .isInterface()))
            throw new IllegalStateException("You can use this only when the field is attached to a class or an enum");
        VariableDeclarator variable = f.getVariable(0);
        String fieldName = variable.getNameAsString();
        String fieldNameUpper = fieldName.toUpperCase().substring(0, 1) + fieldName.substring(1, fieldName.length());
        final MethodDeclaration getter;
        com.github.javaparser.ast.type.Type type = variable.getType();
        boolean isBoolean = false;
        if (type instanceof PrimitiveType && "boolean".equals(((PrimitiveType) type).getType().name().toLowerCase())) {
            isBoolean = true;
        }
        String getName = (isBoolean ? "is" : "get") + fieldNameUpper;
        if (parentClass.isPresent() && CollectionUtils.isNotEmpty(parentClass.get().getMethodsByName(getName))) {
            return null;
        }
        getter = parentClass.map(clazz -> clazz.addMethod(getName, PUBLIC))
            .orElseGet(() -> parentEnum.get().addMethod(getName, PUBLIC));
        getter.setType(type);
        BlockStmt blockStmt = new BlockStmt();
        getter.setBody(blockStmt);
        blockStmt.addStatement(new ReturnStmt(fieldName));
        return getter;
    }

    @Override public Visitable visit(MethodDeclaration n, Object arg) {
        super.visit(n, arg);
        n.setName(StringUtils.uncapitalize(n.getNameAsString()));
        return n;
    }

    @Override public Visitable visit(final FieldAccessExpr n, Object arg) {
        Optional<Expression> exp = n.getScope();
        Visitable visitable = super.visit(n, arg);
        if (exp.isPresent() && !(exp.get() instanceof ThisExpr)) {
            if ("argValue".equals(n.getNameAsString())) {
                return visitable;
            }
            Exception ex = null;
            try {
                Type type = solverUtil.getJavaParserFacade().getType(n);
                if (type.isReference()) {
                    if (type.asReferenceType().getTypeDeclaration().isEnum()) {
                        return visitable;
                    }
                }
            } catch (Exception e) {
                //                e.printStackTrace();
                ex = e;
            }
            //            if (ex == null) { // for debug
            //                System.out.println();
            //            }
            final MethodCallExpr getExpression = getMethodCallExpr(n);
            return getExpression;
        }
        return visitable;
    }

    private MethodCallExpr getMethodCallExpr(FieldAccessExpr n) {
        NodeList args = new NodeList(n);
        return new MethodCallExpr(n.getScope().get(), null,
            new SimpleName("get" + StringUtils.capitalize(n.getNameAsString())), args);
    }

    public Visitable visit(final AssignExpr n, Object arg) {
        Visitable visitable = super.visit(n, arg);
        if (!AssignExpr.Operator.ASSIGN.equals(n.getOperator())) {
            return visitable;
        }
        MethodCallExpr setExpression = null;
        if (n.getTarget() instanceof FieldAccessExpr) {
            setExpression = setMethodWitheField(n);
        }
        if (setExpression != null)
            return setExpression;
        return visitable;
    }

    public Visitable visit(final MethodCallExpr n, Object arg) {
        Visitable visitable = super.visit(n, arg);
        n.setName(StringUtils.uncapitalize(n.getNameAsString()));
        return n;
    }

    private MethodCallExpr setMethodWitheField(AssignExpr n) {
        FieldAccessExpr fae = (FieldAccessExpr) n.getTarget();
        NodeList args = new NodeList(n);
        args.add(n.getValue());
        if (!fae.getNameAsString().toLowerCase().startsWith("is")) {
            return new MethodCallExpr(fae.getScope().get(), null,
                new SimpleName("set" + StringUtils.capitalize(fae.getNameAsString())), args);
        }
        return null;
    }

    private MethodCallExpr getMethodWithNameExpr(AssignExpr n) {
        NameExpr fae = (NameExpr) n.getTarget();
        NodeList args = new NodeList(n);
        args.add(n.getValue());
        if (!fae.getNameAsString().toLowerCase().startsWith("is")) {
            return new MethodCallExpr(null, null, new SimpleName("set" + StringUtils.capitalize(fae.getNameAsString())),
                args);
        }
        return null;
    }

}

