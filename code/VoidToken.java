package code;
public class VoidToken implements Token {
    public boolean equals(final Object other) {
        return other instanceof VoidToken;
    }
}