package nl.utwente.zita.ast.javaast;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import nl.utwente.zita.ast.ASTNode;
import nl.utwente.zita.ast.Comment;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A node of a Java program's AST.
 *
 * @author Tim Blok
 * Created on 10/15/2018.
 */
public class JASTNode implements ASTNode {

    private final static Map<String, Integer> stringIds = new HashMap<>();
    private static int id = 1;
    private Comment comment;
    private String content;
    private int startLineNumber;
    private int endLineNumber;
    private ASTNode parent;
    private List<ASTNode> children;
    private String nodeType;
    private String fileName;
    private Map<String, Object> attributes;

    public JASTNode() {
        children = new ArrayList<>();
        attributes = new HashMap<>();
    }

    public static JASTNode createFrom(Node node) {
        JASTNode astNode = new JASTNode();
        astNode.setContent(node.toString());
        astNode.setStartLineNumber(node.getBegin().get().line);
        astNode.setEndLineNumber(node.getEnd().get().line);
        astNode.setNodeType(node.getClass().getSimpleName());
        for (Node child : node.getChildNodes()) {
            JASTNode childNode = createFrom(child);
            childNode.setParent(astNode);
            astNode.addChild(childNode);
        }
//        if (node instanceof CompilationUnit) {
//            System.out.println();
//            astNode.printTree();
//        }
        return astNode;
    }

    public void printTree() {
        System.out.print(getNodeType());
        for (ASTNode nAAA : getChildren()) {
            JASTNode n = (JASTNode) nAAA;
            System.out.print("[");
            for (ASTNode n2 : n.getChildren()) {
                ((JASTNode) n2).printTree();
            }
            System.out.print("]");
        }
    }

    @Override
    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public String getContent() {
        return content.replaceAll("[\r\n \t']", "");
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int getStartLineNumber() {
        return startLineNumber;
    }

    public void setStartLineNumber(int lineNumber) {
        this.startLineNumber = lineNumber;
    }

    @Override
    public List<ASTNode> getChildren() {
        return children;
    }

    public void addChild(ASTNode child) {
        getChildren().add(child);
    }

    public int getEndLineNumber() {
        return endLineNumber;
    }

    public void setEndLineNumber(int endLineNumber) {
        this.endLineNumber = endLineNumber;
    }

    @Override
    public ASTNode getParent() {
        return parent;
    }

    @Override
    public void setParent(ASTNode parent) {
        this.parent = parent;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    @Override
    public String getFileName() {
        if (fileName == null && getParent() != null) {
            return getParent().getFileName();
        }
        return fileName;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Object getAttribute(String key) {
        return getAttributes().get(key);
    }

    public void setAttribute(String key, Object value) {
        getAttributes().put(key, value);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public Set<ASTNode> getAll() {
        Set<ASTNode> nodes = new HashSet<>();
        nodes.add(this);
        for (ASTNode node : getChildren()) {
            nodes.addAll(node.getAll(nodes));
        }
        return nodes;
    }

    @Override
    public Set<ASTNode> getAll(Set<ASTNode> set) {
        set.add(this);
        for (ASTNode node : getChildren().stream().filter(n -> !(((JASTNode) n).isUsefulNodeType())).collect(Collectors.toList())) {
            set.addAll(node.getAll(set));
        }
        return set;
    }

    private boolean isUsefulNodeType() {
        switch (getNodeType()) {
            case "NameExpr":
            case "SimpleName":
            case "EnclosedExpr":
            case "IntegerLiteralExpr":
            case "PrimitiveType":
                return true;
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return "JASTNode{" +
                "startLineNumber=" + startLineNumber +
                ", endLineNumber=" + endLineNumber +
                ", content='" + content + '\'' +
                ", comment=" + comment +
                '}';
    }

    @Override
    public void generateAttributes() {
        setAttribute("nodes", getNodeCount());
        setAttribute("containingFunction", getContainingFunction());
        setAttribute("depth", getDepth());
//        setAttribute("content", getContent());
    }

    private int getDepth() {
        int depth = 0;
        ASTNode node = getParent();
        while (node != null) {
            node = node.getParent();
            depth++;
        }
        return depth;
    }

    private int getContainingFunction() {
        return 3;
    }

    private int getNewId() {
        return id++;
    }

    private int getNodeCount() {
        return getAll().size();
    }
}
