package oop.ex6.main;

import java.util.*;
import java.util.regex.Matcher;

/**
 * this class is in charge of all method related compilations.
 */
public class MethodCompiler {
    private final VarCompiler varCompile = new VarCompiler();

    private final ConditionCompiler conditionCompile = new ConditionCompiler();
    private final LinkedList<HashMap<String, Variable>> vars = VarTables.getInstance().getVars();
    private   final HashMap<String, ArrayList<Variable>> methods = VarTables.getInstance().getMethods();



    /**
     * compiles the line of method declaration
     * @param name the name of the method
     * @param params the parameters it gets.
     * @throws Type1Exception te exception type of illegal declaration
     */
     public void compileMethodDec(String name, String params) throws Type1Exception {
        ArrayList<Variable> methodSet = new ArrayList<>();
        String[] arrSplit = params.split(",");
        Matcher match;
        if(methods.containsKey(name)){
            throw new DoubleDeclareMethodException();
        }
        HashSet<String> vars = new HashSet<>();
        for (String param : arrSplit) {
            match = Regex.ARG.matcher(param);
            if (match.matches()) {
                if(vars.contains(match.group(3))){
                    throw new DoubleDeclareMethodException();
                }
                vars.add(match.group(3));
                methodSet.add(new Variable(match.group(3), match.group(2), !(match.group(1) == null), true ));
            }
            else if(!(Regex.EMPTY_LINE.matcher(param).matches() && arrSplit.length == 1)){
                throw new IllegalLineException();
            }
        }
        methods.put(name, methodSet);
    }

    /**
     * compiles the inner lines of the functions.
     * @param inputFile the code file to compile.
     * @return true if func is legal, false otherwise.
     * @throws Type1Exception the exception that might be thrown if code is illegal.
     */
    public boolean compileFunc(Scanner inputFile) throws Type1Exception {
        String line;
        Matcher matcher;
        boolean returnEnd = false;
        while (inputFile.hasNextLine()) {
            line = inputFile.nextLine();
            if(ifWhileHelper(line, inputFile)){
                returnEnd =false;
                continue;
            }
            matcher = Regex.VARS.matcher(line);
            if (matcher.matches()) {
                returnEnd = false;
                boolean isFinal = matcher.group(1) != null;
                varCompile.compileVars(matcher.group(3), matcher.group(2), isFinal);
                continue;
            }
            if (Regex.VAR.matcher(line).matches()) {
                returnEnd = false;
                varCompile.compileStatements(line);
                continue;
            }
            if (canContinue(line)){
                continue;
            }
            matcher = Regex.METHOD_CALL.matcher(line);
            if(matcher.matches()){
                returnEnd = false;
                checkCall(matcher.group(1),matcher.group(2));
                continue;
            }
            if(Regex.END_BLOCK.matcher(line).matches()){
                return returnEnd;
            }
            if(Regex.RETURN.matcher(line).matches()){
                returnEnd = true;
                continue;
            }
            throw new IllegalLineException();
        }
        return returnEnd;
    }

    /**
     * compiles an if or while statement.
     * @param line the line of if / while statement.
     * @param inputFile the stream of the code - for recursive call.
     * @return true if was a match to if/while regex, false otherwise.
     * @throws Type1Exception exception if statement is not legal.
     */
    public  boolean ifWhileHelper(String line, Scanner inputFile) throws Type1Exception {
        Matcher matcher = Regex.IF_WHILE.matcher(line);
        if (matcher.matches()) {
            if(conditionCompile.checkCondition(matcher.group(1))){
//                CompileEngine.scope++;
                vars.addFirst(new HashMap<>());
                compileFunc(inputFile);
                vars.removeFirst();
//                CompileEngine.scope--;
                return true;
            }
            else{
                throw new IllegalConditionException();
            }
        }
        return false;
    }

    /**
     * checks if line are from type that can be skipped.
     * @param line the line
     * @return true if can skip, false otherwise.
     */
    private  boolean canContinue(String line){
        Matcher matcher;
        matcher = Regex.EMPTY_LINE.matcher(line);
        if (matcher.matches()) {
            return true;
        }
        matcher = Regex.COMMENT.matcher(line);
        return matcher.matches();
    }

    /**
     * checks if the call to a function is legal.
     * @param name the name of the function.
     * @param param the param list the is given to it.
     * @throws Type1Exception exception that might be thrown if not legal.
     */
    private void checkCall(String name, String param) throws Type1Exception {
        String [] params = param.split(",");
        int counter = 0;
        String varType = null;
        if(methods.containsKey(name)){
            if(params.length != methods.get(name).size()){
                if(!(Regex.EMPTY_LINE.matcher(param).matches() &&
                        methods.get(name).size() == 0)){
                    throw  new WrongCallException();
                }
            }
                for( Variable var : methods.get(name)){
                if(varCompile.classifyVar(var.getType(), params[counter])){
                    counter++;
                    continue;
                }
                for(HashMap<String,Variable> vars : vars){
                    if(vars.containsKey(params[counter])){
                        varType = vars.get(params[counter]).getType();
                        if(!vars.get(params[counter]).getIsInitialized()){
                            throw new InitializedVarException();
                        }
                        break;
                    }
                }
                if(Objects.equals(var.getType(), varType) || (var.getType().equals("boolean") &&
                        (varType.equals("int") || varType.equals("double")))||
                        (var.getType().equals("double") &&
                        varType.equals("int"))){
                    counter++;
                    continue;
                }
                throw new WrongCallException();
            }
        }
        else{
            throw new IlegelFunctionNameException();
        }
    }
}
