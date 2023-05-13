package oop.ex6.main;

public class ChangingFinalException extends Type1Exception {
    final static String MSG = "Tried to change a final variable.";

    @Override
    public String getMessage() {
        return MSG;
    }
}
