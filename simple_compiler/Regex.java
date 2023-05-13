package oop.ex6.main;

import java.util.regex.Pattern;

/**
 * this class contains all regexes that are used in the compiler.
 */
public  abstract class Regex {
    private static final String START = "^[ \\t]*";
    private static final String END = "[ \\t]*$";
    private static final String VAR_DEC_BEGIN = "(?:(final)[ \\t]+)?(int|double|String|boolean|char)[ \\t]+";
    public static final Pattern CONDITION_START = Pattern.compile("[ \\t]*(?:([^\\s&|]+)[ \\t]*(?:\\|\\||&&))");
    public static final Pattern CONDITION_END = Pattern.compile(START+"([^&|\\s]+)"+END);
    public static final Pattern METHOD_DEC = Pattern.compile(START+"(void)[ \\t]+([a-z|A-Z]\\w*)[ \\t]*\\((.*)\\)[ \\t]*\\{"+END);
    public static final Pattern METHOD_CALL = Pattern.compile(START+"([a-z|A-z]\\w*)[ \\t]*\\((.*)\\)[ \\t]*;"+END);
    public static final Pattern EMPTY_LINE = Pattern.compile("^[ \\t]*$");
    public static final Pattern COMMENT = Pattern.compile("^//.*");
    public static final Pattern END_BLOCK = Pattern.compile(START+"}"+END);
    public static final Pattern VAR = Pattern.compile(START+"([A-Z|a-z]\\w*|_\\w+)[ \\t]*(?:=[ \\t]*(.*))?"+END);
    public static final Pattern ARG = Pattern.compile(START+VAR_DEC_BEGIN+"([A-z|a-z]\\w*)"+END);
    public static final Pattern VARS = Pattern.compile(START+VAR_DEC_BEGIN+"(.*)[ \\t]*;"+END);
    public static final Pattern STRING = Pattern.compile(START+"\".*\""+END);
    public static final Pattern INT = Pattern.compile(START+"[+|-]?\\d+"+END);
    public static final Pattern TRUE = Pattern.compile(START+"true"+END);
    public static final Pattern FALSE = Pattern.compile(START+"false"+END);
    public static final Pattern DOUBLE = Pattern.compile(START + "[+|-]?(?:\\d+|(?:\\d*\\.\\d+|\\d+\\.\\d*))" + END);
    public static final Pattern BOOLEAN = Pattern.compile("("+TRUE+"|"+FALSE+"|"+INT+"|"+DOUBLE+")");
    public static final Pattern CHAR = Pattern.compile(START+"'.'"+END);
    public static final Pattern RETURN = Pattern.compile(START+"return[ \\t]*;"+END);
    public static final Pattern IF_WHILE = Pattern.compile(START+"(?:if|while)[ \\t]*\\((.*)\\)[ \\t]*\\{"+END);


}
