package oop.ex6.main;

public class DoubleDeclareMethodException extends Type1Exception {
    final static String MSG = "Tried to declare a method with the same name twice.";

    @Override
    public String getMessage() {
        return MSG;
    }
}
