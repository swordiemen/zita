package nl.utwente.zita.ast;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents a node inside an AST (Abstract Syntax Tree).
 *
 * @author Tim Blok
 * Created on 10/15/2018.
 */
public interface ASTNode {

    /**
     * Gets the file name of the file corresponding to this AST.
     * @return the file name of the file corresponding to this AST
     */
    String getFileName();

    /**
     * Gets the value of an attribute, given its name.
     * @return the corresponding value of the attribute, or null if there is no such attribute
     */
    Object getAttribute(String key);

    /**
     * Gets a list of attributes (features) and their values.
     */
    Map<String, Object> getAttributes();

    /**
     * Gets the parent node of this node.
     * @return the parent node
     */
    ASTNode getParent();

    /**
     * Generates attributes (features) of this AST's (sub)graph.
     */
    void generateAttributes();

    /**
     * Sets the parent node of this node.
     * @param astNode the parent node
     */
    void setParent(ASTNode astNode);

    /**
     * Gets a list of children nodes of this node.
     * @return the list of children
     */
    List<ASTNode> getChildren();

    /**
     * Adds a child to this node.
     */
    void addChild(ASTNode astNode);

    /**
     * Gets the first line number that this node corresponds to.
     * @return the line number
     */
    int getStartLineNumber();

    /**
     * Sets the line number of this node.
     */
    void setStartLineNumber(int lineNumber);

    /**
     * Gets the last line number that this node corresponds to.
     * @return the line number
     */
    int getEndLineNumber();

    /**
     * Sets the last line number that this node corresponds to.
     * @param lineNumber the line number
     */
    void setEndLineNumber(int lineNumber);

    /**
     * Gets the comment of this node, or null if there is no such comment.
     * @return the comment
     */
    Comment getComment();

    /**
     * Add a comment to this node.
     */
    void setComment(Comment comment);

    /**
     * Gets the content of this node. Will also include the content of its children.
     * @return the content of this node
     */
    String getContent();

    /**
     * Sets the content of this node.
     * @param content the new content of the node
     */
    void setContent(String content);

    Set<ASTNode> getAll();
    Set<ASTNode> getAll(Set<ASTNode> set);

    String getNodeType();

    ASTNode getNodeWithNodeTypeBF(String nodeType);
}
