package oop.ex6.main;

public class FunctionReturnException extends Type1Exception {
    final static String MSG = "Function didnt have return in the end.";

    @Override
    public String getMessage() {
        return MSG;
    }
}
