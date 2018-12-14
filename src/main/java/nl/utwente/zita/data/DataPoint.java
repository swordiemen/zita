package nl.utwente.zita.data;

import nl.utwente.zita.ast.ASTNode;

/**
 * @author Sjonnie
 * Created on 11/20/2018.
 */
public class DataPoint {

    private ASTNode tree;
    private String content;
    private String classification;

    public DataPoint(ASTNode tree, String content, String classification) {
        this.tree = tree;
        this.content = content.replaceAll("[\\s|']", "");
        this.classification = classification;
    }

    public ASTNode getTree() {
        return tree;
    }

    public String getContent() {
        return content.replace("'", "");
    }

    public String getClassification() {
        return classification;
    }

    public int getStartLineNumber() {
        return getTree().getStartLineNumber();
    }

    public int getEndLineNumber() {
        return getTree().getEndLineNumber();
    }

    @Override
    public String toString() {
        return "DataPoint{" +
                "content='" + content + '\'' +
                ", classification='" + classification + '\'' +
                ", start = " + getStartLineNumber() +'\'' +
                ", end = " + getEndLineNumber() +'\'' +
                '}';
    }

    public String getFileName() {
        return getTree().getFileName();
    }
}
