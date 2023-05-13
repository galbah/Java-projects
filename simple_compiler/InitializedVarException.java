package oop.ex6.main;

public class InitializedVarException extends Type1Exception {
    final static String MSG = "Tried to initialized a variable with uninitialized variable";

    @Override
    public String getMessage() {
        return MSG;
    }
}
