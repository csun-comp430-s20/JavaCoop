package code;
public class FunctionToken implements Token {
    public boolean equals(final Object other) {
        return other instanceof FunctionToken;
    }
}