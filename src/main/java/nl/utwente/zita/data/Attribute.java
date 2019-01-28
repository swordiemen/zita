package nl.utwente.zita.data;

/**
 * @author Sjonnie
 * Created on 1/15/2019.
 */
public enum Attribute {

    CONTENT("content"),
    NODE_COUNT("nodes"),
    DEPTH("depth"),
    USED_FUNCTION("used_function"),
    USED_VARIABLE("used_variable"),
    NODE_TYPE("node_type"),
    CONTAINING_BLOCK("containing_block"),
    CONTAINING_FUNCTION("containing_function");

    private final String arffKey;

    Attribute(String arffKey) {
        this.arffKey = arffKey;
    }

    public static boolean isFunctionAttribute(String attr) {
        return attr.equals(USED_FUNCTION.getArffKey()) || attr.equals(CONTAINING_FUNCTION.getArffKey());
    }

    public String getArffKey() {
        return arffKey;
    }
}
