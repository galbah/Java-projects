package oop.ex6.main;

public class DoubleDeclareVarException extends Type1Exception {
    final static String MSG = "Tried to declare a variable with the same name twice.";

    @Override
    public String getMessage() {
        return MSG;
    }
}
