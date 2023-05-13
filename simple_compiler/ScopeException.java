package oop.ex6.main;

public class ScopeException extends Type1Exception {

    final static String MSG = "closing barracks arent matching opening barracks";

    @Override
    public String getMessage() {
        return MSG;
    }
}
