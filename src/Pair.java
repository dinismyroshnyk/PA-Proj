
public class Pair<key, value> {
    private final key key;
    private final value value;

    public Pair(key key, value value) {
        this.key = key;
        this.value = value;
    }

    public key getKey() {
        return key;
    }

    public value getValue() {
        return value;
    }
}
