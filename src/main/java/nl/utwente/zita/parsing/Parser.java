package nl.utwente.zita.parsing;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    public static List<String> parseFiles(List<File> files) {
        List<String> contents = new ArrayList<>();
        for (File file : files) {
            try (FileInputStream fis = new FileInputStream(file)) {
                CompilationUnit cu = JavaParser.parse(fis);
                for (Node node : cu.getChildNodes()) {
                    // only take everything under the class definition
                    if (node.toString().contains("class ")) {
                        for (int i = 1; i < node.getChildNodes().size(); i++) {
                            Node funcNode = node.getChildNodes().get(i);
                            String content = funcNode.toString();
                            if (!content.contains("public") && !content.contains("private")) {
                                contents.add(content);
                            } else if (funcNode.getChildNodes().size() > 1) {
                                for (int j = 0; j < funcNode.getChildNodes().size(); j++) {
                                    contents.add(funcNode.getChildNodes().get(j).toString());
                                }
                            }
                        }
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return contents;
    }

    public static void printChildNodeLines(Node node) {
        String code = node.toString();
        System.out.println(code);
    }
}
