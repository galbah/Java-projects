package oop.ex6.main;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.regex.Matcher;

/**
 * this class is in charge of any variable related compilations.
 */
public class VarCompiler {
    private final LinkedList<HashMap<String, Variable>> vars = VarTables.getInstance().getVars();


    /**
     * compiles a line of var declaration.
     * @param names names of the var
     * @param varType type of vars
     * @param isFinal true if vrs were declared as final, false otherwise.
     * @throws Type1Exception the exception might be raised if line is illegal
     */
     public void compileVars(String names, String varType, Boolean isFinal) throws Type1Exception {
        names = names + " ";
        String[] arrSplit = names.split(",");
        Matcher match;
        boolean flag = false;
        for (String var : arrSplit) {
            match = Regex.VAR.matcher(var);
            if (match.matches()) {
                if (!(match.group(2) == null)) {
                    flag = true;
                    if (!classifyVar(varType, match.group(2))) {
                        throw new IllegalLineException();
                    }
                } else if (isFinal) {
                    throw new FinalUninitializedException();
                }
                if (vars.getFirst().containsKey(match.group(1))) {
                    throw new DoubleDeclareVarException();
                } else {
                        vars.getFirst().put(match.group(1), new Variable(match.group(1), varType, isFinal, flag));
                    flag = false;
                }
            } else {
                throw new IllegalLineException();
            }
        }
    }

    /**
     * compile statements of vars
     * @param statements the statements to compile
     * @throws Type1Exception the exception might be raised if statement is illegal.
     */
    public void compileStatements(String statements) throws Type1Exception {
        String[] arr = statements.split(";");
        Matcher match;
        if (arr.length == 2) {
            match = Regex.EMPTY_LINE.matcher(arr[1]);
            if (!match.matches()) {
                throw new IllegalLineException();
            }
        }
        String[] arrSplit = arr[0].split(",");
        for (String var : arrSplit) {
            match = Regex.VAR.matcher(var);
            if (match.matches()) {
                if (match.group(2) == null) {
                    throw new IllegalInitializedException();
                }
                for (HashMap<String, Variable> var1 : vars) {
                    if (var1.containsKey(match.group(1))) {
                        if (var1.get(match.group(1)).isFinal()) {
                            throw new ChangingFinalException();
                        }
                        if (classifyVar(var1.get(match.group(1)).getType(), match.group(2))) {
                            if(vars.size() != 1){
                                var1.get(match.group(1)).setIsInitializedGlobal(true);
                            }
                            else {
                                var1.get(match.group(1)).setIsInitialized(true);
                            }
                            break;
                        }
                        throw new IllegalInitializedException();
                    }

                }
            }
        }
    }

    /**
     * checks if the given var is from the given type.
     * works whether var is raw or identifier.
     * @param type the type to check
     * @param toSearch the name of var to search for.
     * @return true if the var and type matches, false otherwise.
     * @throws Type1Exception the exception type that may rise from illegal line.
     */
    public boolean classifyVar(String type, String toSearch) throws Type1Exception {
        Matcher match = null;
        switch (type) {
            case "String":
                match = Regex.STRING.matcher(toSearch);
                break;
            case "char":
                match = Regex.CHAR.matcher(toSearch);
                break;
            case "int":
                match = Regex.INT.matcher(toSearch);
                break;
            case "double":
                match = Regex.DOUBLE.matcher(toSearch);
                break;
            case "boolean":
                match = Regex.BOOLEAN.matcher(toSearch);
                break;
        }
        toSearch = toSearch.strip();
        if (!match.matches()) {
            String varType;
            for (HashMap<String, Variable> vars1 : vars) {
                if (vars1.containsKey(toSearch)) {
                    if(!vars1.get(toSearch).getIsInitialized() &&
                            !vars1.get(toSearch).getIsInitializedGlobal()){
                        throw new InitializedVarException();
                    }
                    varType = vars1.get(toSearch).getType();
                    if (Objects.equals(type, varType) || (type.equals("boolean") &&
                            (varType.equals("int") || varType.equals("double"))) ||
                            (type.equals("double") && varType.equals("int"))) {
                        return true;
                    }
                    break;
                }
            }
        }
        return match.matches();
    }
}
