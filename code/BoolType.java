package code;

public class BoolType implements Type {
    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof BoolType;
    }

    @Override
    public String toString() {
        return "bool";
    }

    public String toDescriptorString() {
        return "Z";
    }
} // BoolType

