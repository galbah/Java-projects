package oop.ex6.main;

public class IllegalLineException extends Type1Exception {
    final static String MSG = "Illegal line of code";

    @Override
    public String getMessage() {
        return MSG;
    }
}
