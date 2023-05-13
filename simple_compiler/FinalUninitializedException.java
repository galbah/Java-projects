package oop.ex6.main;

public class FinalUninitializedException extends Type1Exception {
    final static String MSG = "declare a final variable but didnt initialized it";

    @Override
    public String getMessage() {
        return MSG;
    }
}
