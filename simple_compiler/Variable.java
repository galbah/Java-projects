package oop.ex6.main;

/**
 * Class that represents a variable in the code.
 */
public class Variable {

    private final String name;
    private final String type;
    private final boolean isFinal;
    private  boolean isInitialized;
    private boolean isInitializedGlobal = false;

    public Variable(String name, String type, boolean isFinal, boolean isInitialized){
        this.name = name;
        this.type = type;
        this.isFinal = isFinal;
        this.isInitialized = isInitialized;
    }

    /**
     * @return true if c\variable was declared as final, false otherwise.
     */
    public boolean isFinal(){
        return isFinal;
    }

    /**
     * @return the name that was given to variable when was initialized.
     */
    public String getName(){return name;}

    /**
     * @return the type that variable was initialized in to.
     */
    public String getType(){return type;}

    /**
     * @return true if variable was initialized, false otherwise.
     */
    public boolean getIsInitialized(){return isInitialized;}

    /**
     * @param bool sets the isInitialized variable to given boolean.
     */
    public void setIsInitialized(boolean bool){isInitialized = bool;}

    /**
     * @return true if variable was initialized in global scope, false otherwise.
     */
    public boolean getIsInitializedGlobal(){return isInitializedGlobal;}

    /**
     * @param bool sets the isInitializedGlobal variable to given boolean.
     */
    public void setIsInitializedGlobal(boolean bool){
        isInitializedGlobal = bool;}





}
