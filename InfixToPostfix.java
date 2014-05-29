/* ASethi17
 * 5/6/2014
 * BJP Chapter 14 Programming Project 03
 *
 * Takes an infix expression and converts it into its corresponding postfix
 * expression. For example, "(9 + (8 * 7 - (6 / 5 ^ 4) * 3) * 2)" converts to
 * "9 8 7 * 6 5 4 ^ / 3 * - 2 * +"
 */

import java.util.*;

public class InfixToPostfix {
    public static void main (String [] args) {
        // Create sample infix expressions as test cases
        ArrayList<String> infix = new ArrayList<String>();

        // Should return postfix
        infix.add("(9 + (8 * 7 - (6 / 5 ^ 4) * 3) * 2)");
        infix.add("(2+(3*8)-5)");
        infix.add("(3*8)");
        infix.add("(4+8)*(6-5)/((3-2)*(2+2))");
        infix.add("(3*8()+ 4)");
        infix.add("(2.3*5.76) - 3");

        // Should return "Illegal expression"
        infix.add("(3*8) + 7)"); // Parentheses mismatch
        infix.add("(wut*8)+ 7"); // Alphabetical characters
        infix.add("(z*8)+ 4)"); // Alphabetical characters & parens mismatch
        infix.add("(22.3 @#@ 4.3) - 3"); // Other special characters

        // Convert infix test cases and print postfix expressions
        for (String expr : infix) {
            System.out.println("Original infix expression: " + expr);
            System.out.println("Postfix expression: " + createPostFix(expr));
            System.out.println();
        }
    }

    // Accepts an infix expression, invokes recursive postFixConverter() method
    // and returns the infix expression's equivalent postfix.
    //
    // pre : String infix expression. The infix may not necessarily be valid.
    // post: Valid postfix expression, or "Illegal expression" if an invalid
    //       invalid infix expression is passed.  
    public static String createPostFix(String infix) {
        // Surround with parens to make sure postFixConverter is invoked
        infix = "(" + infix + ")";

        StringSplitter data = new StringSplitter(infix);
        data.next(); // Skips first opening parenthesis

        // Map of operators and their precedence. Higher integers mean higher
        // precedence.
        Map<String, Integer> opMap = new HashMap<String, Integer>();
        opMap.put("+", 1);
        opMap.put("-", 1);
        opMap.put("*", 2);
        opMap.put("/", 2);
        opMap.put("^", 3);
        if (isInvalidExpression(infix)) {
            return "Illegal expression";
        } else {
            return postFixConverter(infix, data, opMap);
        }
    }

    // Accepts a String infix expression and recursively generates postfix
    // expression.
    //
    // post: equivalent postfix expression returned as String, or "Illegal
    //       expression" for invalid input
    public static String postFixConverter(String infix, 
            StringSplitter data, Map<String, Integer> opMap) {

        Stack<String> symbols = new Stack<String>(); // Stack of operators
        String postfix = "";
        String next = "";

        symbols.push("("); // Add opening parens
        while (data.hasNext() && !next.equals(")")) {
            next = data.next();
            if (isNumber(next)) { // Append to postfix string
                postfix += next + " ";
            } else if (opMap.containsKey(next)) { // Next token is an operator
                /* Check precedence. If the new operator has a lower precendence
                 * than the operator on top of the stack, pop the operator in
                 * the stack and append to postfix to ensure correct ordering
                 */
                if (!symbols.peek().equals("(") && 
                        opMap.get(next) <= opMap.get(symbols.peek())) {
                   postfix += symbols.pop() + " "; 
                }
                symbols.push(next);
            } else if (next.equals("(")) { // Create "sub-postfix" expr. first
                postfix += postFixConverter(infix, data, opMap) + " ";
            } 
        }
        while (!symbols.isEmpty() && !symbols.peek().equals("(")) {
            postfix += symbols.pop() + " ";
        }
        return postfix;

    }

    // Accepts an infix expression and returns true if it's valid, false if not.
    // The recursive call will only begin if this evaluates to true.
    public static boolean isInvalidExpression(String infix) {
        int openParens = 0;
        int closeParens = 0;
        boolean error = false;
        String next = "";
        StringSplitter parser = new StringSplitter(infix);

        while (parser.hasNext()) {
            next = parser.next();
            // Regex checks for anything that isn't a number, an operator, or
            // whitespace.
            if (next.matches("[^0-9\\+\\.\\-\\(\\)\\*/\\^\\%\\s]+")) {
                error = true;
            } else if (next.equals("(")) {
                openParens++;
            } else if (next.equals(")")) {
                closeParens++;
            }
        }
        if (openParens != closeParens) { // Check for parentheses mismatches
            error = true;
        }
        return error;
    }

    // Checks to see if a token is a valid number.
    // post: true if the next token in the infix is a valid number, false if not
    public static boolean isNumber(String next) {
        boolean isNumber = true;
        try { // Valid number if it can be parsed as Double, operator if not
            Double.parseDouble(next);
        } catch (NumberFormatException e) {
            isNumber = false; 
        }
        return isNumber;
    }
}
