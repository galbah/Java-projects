package oop.ex6.main;

public class IlegelFunctionNameException extends Type1Exception {
    final static String MSG = "Tried to call a function that isn't in the program.";

    @Override
    public String getMessage() {
        return MSG;
    }
}
