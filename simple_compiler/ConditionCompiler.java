package oop.ex6.main;

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.regex.Matcher;

/**
 * this class validated condition statements
 */
public class ConditionCompiler {
    private final VarCompiler varCompile = new VarCompiler();
    /**
     * receives a condition as a string and check if its valid.
     * @param cond the condition as a string.
     * @return true if valid, false otherwise.
     * @throws Type1Exception the exception might throw if code is not legal.
     */
    public  boolean checkCondition(String cond) throws Type1Exception {
        Matcher matcher1 = Regex.CONDITION_START.matcher(cond);
        int prev_end = 0;
        while (matcher1.find()){
            if (prev_end  != matcher1.start()){
                return false;
            }
            prev_end = matcher1.end();
            if(!validateVar(matcher1.group(1))){
                return false;
            }
        }
        matcher1 = Regex.CONDITION_END.matcher(cond.substring(prev_end));
        if (matcher1.matches()){
            return validateVar(matcher1.group(1));
        }
        return false;
    }

    /**
     * checks if var can be a boolean
     * @param varName the name of the var to check
     * @return true if var can br boolean, false otherwise.
     * @throws Type1Exception the exception might throw if line of code is not legal.
     */
    private boolean validateVar(String varName) throws Type1Exception {
        Variable var = getVar(varName);
        if (var == null ){

            return varCompile.classifyVar("boolean", varName);
        }
        else return (var.getType().equals("boolean") || var.getType().equals("int")
                || var.getType().equals("double")) && (var.getIsInitializedGlobal() ||
                var.getIsInitialized());
    }

    /**
     * searches the variable name in the vars hashMap.
     * @param var the variable name to be searched.
     * @return if var exists - the matching variable object, else null
     */
    public Variable getVar(String var) throws Type1Exception {
        for(HashMap<String, Variable> varMap : VarTables.getInstance().getVars()){
            if (varMap.containsKey(var)){
                return varMap.get(var);
            }
        }
        return null;
    }
}

