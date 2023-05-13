package oop.ex6.main;

public class IllegalInitializedException extends Type1Exception {
    final static String MSG = "Tried to initialized a variable with the wrong type.";

    @Override
    public String getMessage() {
        return MSG;
    }
}
