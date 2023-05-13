package oop.ex6.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class VarTables {
    private static VarTables tables = null;
    private   final LinkedList<HashMap<String, Variable>> vars = new LinkedList<>();
    private   final HashMap<String, ArrayList<Variable>> methods = new HashMap<>();
    private VarTables(){

    }
    public static VarTables getInstance(){
        if( tables == null){
            tables = new VarTables();
        }
        return tables;
    }
    public LinkedList<HashMap<String, Variable>> getVars(){return vars;}
    public HashMap<String, ArrayList<Variable>>  getMethods(){return methods;}
}
