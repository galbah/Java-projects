package oop.ex6.main;

public class WrongCallException extends Type1Exception {
    final static String MSG = "The argument the function receive and and the variable it gets types " +
            "doesnt match.";

    @Override
    public String getMessage() {
        return MSG;
    }
}
