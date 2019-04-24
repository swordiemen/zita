package nl.utwente.zita.ast.javaast;

import com.github.javaparser.ast.Node;
import nl.utwente.zita.ast.ASTNode;
import nl.utwente.zita.ast.Comment;
import nl.utwente.zita.data.Attribute;
import nl.utwente.zita.parsing.Transformer;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A node of a Java program's AST.
 *
 * @author Tim Blok
 * Created on 10/15/2018.
 */
public class JASTNode implements ASTNode {
    private static final String METHOD_CALL_PATTERN = "MethodCallExpr";

    private final static Map<String, Integer> functionStringIds = new HashMap<>();
    private static int functionId = 1;
    private final static Map<String, Integer> nodeTypeStringIds = new HashMap<>();
    private static int nodeTypeId = 1;
    private final static Map<String, Integer> varStringIds = new HashMap<>();
    private static int varId = 1;
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

    @Override
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
        for (ASTNode node : getChildren().stream().filter(n -> !(((JASTNode) n).isUselessNodeType())).collect(Collectors.toList())) {
            set.addAll(node.getAll(set));
        }
        return set;
    }

    private boolean isUselessNodeType() {
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
                "type = " + nodeType +
                ", startLineNumber=" + startLineNumber +
                ", endLineNumber=" + endLineNumber +
                ", content='" + content + '\'' +
                ", comment=" + comment +
                '}';
    }

    @Override
    public void generateAttributes() {
        setAttribute(Attribute.NODE_COUNT.getArffKey(), getNodeCount());
        setAttribute(Attribute.DEPTH.getArffKey(), getDepth());
        setAttribute(Attribute.USED_FUNCTION.getArffKey(), getUsedFunction());
        setAttribute(Attribute.USED_VARIABLE.getArffKey(), getUsedVariable());
        setAttribute(Attribute.NODE_TYPE.getArffKey(), getNodeTypeForAttr());
        setAttribute(Attribute.CONTAINING_BLOCK.getArffKey(), getContainingBlock());
        setAttribute(Attribute.CONTAINING_FUNCTION.getArffKey(), getContainingFunction());
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

    private String getNodeTypeForAttr() {
        Transformer.addNominalAttribute("node_type", getNodeType());
        return getNodeType();
    }

    private String getUsedFunction() {
        ASTNode node = getNodeWithNodeTypeBF(METHOD_CALL_PATTERN);
        String method = "None";
        if (node != null) {
            // Replace the variable part of the function call (>foo.<method()) and the parameter call
            // since we're only interested in the method name
//            method = getContent().split("[(]")[0].replaceAll("[(),]", "").replaceFirst("(.)*\\.", "");
            method = node.getNodeWithNodeTypeBF("SimpleName").getContent();
        }
        Transformer.addNominalAttribute("used_function", method);
        Transformer.addFunctionCall(method);
        return method;
    }

    private String getUsedVariable() {
        return "UnImpl";
    }

    private int getNodeTypeAsInt() {
        String nodeType = getNodeType();
        if (!nodeTypeStringIds.containsKey(nodeType)) {
            nodeTypeStringIds.put(nodeType, getNewNodeTypeId());
        }
        return nodeTypeStringIds.get(nodeType);
    }

    private String getContainingBlock() {
        JASTNode node = this;
        String nodeType = node.getNodeType();
        while (node != null && !isBlockNodeType(nodeType)) {
            node = (JASTNode) node.getParent();
            if (node == null) {
                nodeType = "None";
            } else {
                nodeType = node.getNodeType();
            }
        }
        Transformer.addNominalAttribute("containing_block", nodeType);
        return nodeType;
    }

    private String getContainingFunction() {
        JASTNode node = this;
        String nodeType = node.getNodeType();
        while (node != null && !isFunctionNode(nodeType)) {
            node = (JASTNode) node.getParent();
            if (node != null) {
                nodeType = node.getNodeType();
            }
        }
        String methodName = "None";
        if (node != null) {
            node = (JASTNode) node.getNodeWithNodeTypeBF("SimpleName"); // First occurrence of simple name is the method's name
            methodName = node.getContent();
        }
        Transformer.addNominalAttribute("containing_function", methodName);
        Transformer.addFunctionCall(methodName);
        return methodName;
    }

    private int getNewFunctionId() {
        return functionId++;
    }

    private int getNewVarId() {
        return varId++;
    }

    private int getNewNodeTypeId() {
        return nodeTypeId++;
    }

    private int getNodeCount() {
        return getAll().size();
    }

    public ASTNode getNodeWithNodeTypeBF(String nodeType) {
        if (getNodeType().equals(nodeType)) {
            return this;
        }
        ASTNode node = null;
        for (ASTNode n : getChildren()) {
            if (nodeType.equals(n.getNodeType())) {
                return n;
            }
        }
        for (ASTNode n : getChildren()) {
            node = n.getNodeWithNodeTypeBF(nodeType);
            if (node != null) {
                return node;
            }
        }
        return node;
    }

    private static boolean isBlockNodeType(String nodeType) {
        switch (nodeType) {
            case "IfStmt":
            case "ForEachStmt":
            case "ForStmt":
            case "LocalClassDeclarationStmt":
            case "SwitchStmt":
            case "WhileStmt":
                return true;
        }
        return false;
    }

    private static boolean isFunctionNode(String nodeType) {
        return "MethodDeclaration".equals(nodeType);
    }
}
