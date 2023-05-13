package oop.ex6.main;

import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * main function of compiler, starts the process of compiling the file.
 */
public class CompileEngine {
    private final VarTables tables  = VarTables.getInstance();

//    public static final HashMap<String, ArrayList<Variable>> methods = new HashMap<>();
    private Matcher matcher;
//    public static final LinkedList<HashMap<String, Variable>> vars = new LinkedList<>();
    public  int scope = 0;
    private final MethodCompiler methodCompile = new MethodCompiler();
    private final VarCompiler varCompile = new VarCompiler();


    /**
     * constructor - starts the process of compiling file immediately when called.
     * @param filename the file to compile
     * @throws Type2Exception the exception to throw when file path is not legal.
     * @throws Type1Exception the exception type of all code exceptions that may rise
     *                        from code file.
     */
    public CompileEngine(File filename) throws Type2Exception, Type1Exception {
        tables.getVars().clear();
        tables.getMethods().clear();
        Scanner inputFile;
        try {
            inputFile = new Scanner(filename);
        } catch (FileNotFoundException e) {
            throw new Type2Exception();
        }
        fillDeclarations(inputFile);
        try {
            inputFile = new Scanner(filename);
        } catch (FileNotFoundException e) {
            throw new Type2Exception();
        }
        compile(inputFile);
    }

    /**
     * first run over the code - iterates over method declarations and global variable.
     * saves the names and types of vars and also the variables that the methods should
     * receive when called.
     * @param inputFile the code file that is compiled.
     */
    private void fillDeclarations(Scanner inputFile) throws Type1Exception {
        tables.getVars().add(new HashMap<>());
        String line, names, varType;
        while (inputFile.hasNextLine()) {
            line = inputFile.nextLine();
            matcher = Regex.VARS.matcher(line);
            if (matcher.matches()) {
                boolean isFinal = matcher.group(1) != null;
                varType = matcher.group(2);
                names = matcher.group(3);
                varCompile.compileVars(names, varType, isFinal);
                continue;
            }
            matcher = Regex.VAR.matcher(line);
            if (matcher.matches()) {
                varCompile.compileStatements(line);
                continue;
            }
            matcher = Regex.METHOD_DEC.matcher(line);
            if (matcher.matches()) {
                names = matcher.group(2);
                methodCompile.compileMethodDec(names, matcher.group(3));
                scope++;
                reachEndOfFunction(inputFile);
                if (!inputFile.hasNextLine() && scope != 0) {
                    throw new ScopeException();
                }
                continue;
            }
            matcher = Regex.EMPTY_LINE.matcher(line);
            if (matcher.matches()) {
                continue;
            }
            matcher = Regex.COMMENT.matcher(line);
            if (matcher.matches()) {
                continue;
            }
            throw new IllegalLineException();
        }
    }

    /**
     * is called when is on a method declaration. this functions reads next line
     * until reaches the end of the method.
     * @param inputFile the file to read lines from.
     */
    private void reachEndOfFunction(Scanner inputFile){
        String line;
        while (scope != 0 && inputFile.hasNextLine()) {
            line = inputFile.nextLine();
            matcher = Regex.END_BLOCK.matcher(line);
            if (matcher.matches()) {
                scope--;
                continue;
            }
            matcher = Regex.IF_WHILE.matcher(line);
            if (matcher.matches()) {
                scope++;
            }
        }
    }


    /**
     * the second run through the code. dives into methods and validated every line in it.
     * @param inputFile the code file to compile.
     * @throws Type1Exception the exception type that may rise from illegal code lines.
     */
    void compile(Scanner inputFile) throws Type1Exception {
        String line;
        while (inputFile.hasNextLine()) {
            line = inputFile.nextLine();
            matcher = Regex.METHOD_DEC.matcher(line);
            if (matcher.matches()) {
                HashMap<String, Variable> locals = new HashMap<>();
                for (Variable var : tables.getMethods().get(matcher.group(2))) {
                    locals.put(var.getName(), var);
                }
                tables.getVars().addFirst(locals);
                scope ++;
                if(!methodCompile.compileFunc(inputFile)){
                    throw new FunctionReturnException();
                }
                scope--;
                tables.getVars().removeFirst();
                for( Variable var : tables.getVars().getLast().values()){
                    var.setIsInitializedGlobal(false);
                }
            }
        }
    }
}