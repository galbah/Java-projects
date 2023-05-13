package oop.ex6.main;

public  class Type2Exception extends Exception{
        final static String MSG = "I/O error.";

        @Override
        public String getMessage() {
            return MSG;
        }
}
