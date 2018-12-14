package nl.utwente.zita.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sjonnie
 * Created on 10/15/2018.
 */
public class Comment {
    private List<Integer> lineNumbers;
    private String message;
    private CommentClass type = CommentClass.GENERAL; // default value
    private String file;
    private String ruleSet;
    private String rule;
    // accepted/rejected/unk

    public Comment(int lineNumber, String message, String file, String ruleSet, String rule) {
        lineNumbers = new ArrayList<>();
        lineNumbers.add(lineNumber);
        this.message = message;
        this.file = file;
        this.ruleSet = ruleSet;
        this.rule = rule;
    }

    public Comment() {
        lineNumbers = new ArrayList<>();
    }

    public List<Integer> getLineNumbers() {
        return lineNumbers;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CommentClass getType() {
        return type;
    }

    public void setType(CommentClass type) {
        this.type = type;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getRuleSet() {
        return ruleSet;
    }

    public void setRuleSet(String ruleSet) {
        this.ruleSet = ruleSet;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "lineNumbers=" + lineNumbers +
                ", message='" + message + '\'' +
                ", type=" + type +
                '}';
    }
}
