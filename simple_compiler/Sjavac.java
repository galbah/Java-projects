package oop.ex6.main;

import java.io.File;

public class Sjavac {

    private static final String COMPILATION_SUCCESSFUL = "0";
    private static final String CODE_ILLEGAL = "1";
    private static final String IO_ERROR_CODE = "2";

    /**
     * main function - receives a code file, compiles it and prints :
     * 0 - if code is legal, 1 - if code is not legal, 2 - if file path is illegal.
     * @param args input - args[0] should be file path.
     */
    public static void main(String[] args) {

        try {
            if(args.length != 1){
                throw new Type2Exception();
            }
            File file = new File(args[0]);
            new CompileEngine(file);
            System.out.println(COMPILATION_SUCCESSFUL);
        }
        catch (Type1Exception e){
            System.err.println(e.getMessage());
            System.out.println(CODE_ILLEGAL);
        }
        catch (Type2Exception e){
            System.err.println(e.getMessage());
            System.out.println(IO_ERROR_CODE);
        }

    }

}