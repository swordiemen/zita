package nl.utwente.zita.parsing;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Parser {
    public static void main(String[] args) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream("/home/tim/uni/master/thesis/test-code/community/sketch310/" +
                "application.linux64/source/sketch310.java");
        CompilationUnit cu = JavaParser.parse(fis);
        printChildNodeLines(cu);

//        System.out.println(cu);
    }

    public static void printChildNodeLines(Node node) {
        if (node.getChildNodes().size() > 0) {
            for (Node n : node.getChildNodes()) {
                printChildNodeLines(n);
            }
        } else {
            System.out.println(node + "(line " + node.getParentNodeForChildren().getParentNodeForChildren().getBegin().get().line + ")");
        }
    }
}
