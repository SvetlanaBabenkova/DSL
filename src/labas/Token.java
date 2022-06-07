package labas;
import java.util.Objects;

public class Token {
    private final String type;
    private final String value;
    public Token(String type, String value) {
        this.type = type;
        this.value = value;
    }
    public String getType() { return type; }
    public String getValue() { return value; }
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Token token = (Token) object;
        return Objects.equals(type, token.type) && Objects.equals(value, token.value);
    }
}
