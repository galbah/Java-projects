package oop.ex6.main;

public class IllegalConditionException extends Type1Exception {
    final static String MSG = "condition is not legal";

    @Override
    public String getMessage() {
        return MSG;
    }
}
