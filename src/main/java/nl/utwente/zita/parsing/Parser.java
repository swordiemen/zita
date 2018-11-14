package nl.utwente.zita.parsing;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import nl.utwente.zita.ast.ASTNode;
import nl.utwente.zita.ast.Comment;
import nl.utwente.zita.ast.javaast.JASTNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Parser {
    private static String START_JAVA_CODE = "public class Processing {\r\n";
    private static String END_JAVA_CODE = "\r\n}";

    /**
     * Creates a list of ASTs corresponding the the list of Files.
     *
     * @param files the list of files with java code
     * @return a list of ASTs corresponding to the given files
     */
    public static List<ASTNode> parseFiles(List<File> files) {
        List<ASTNode> contents = new ArrayList<>();
        for (File file : files) {
            String code = readFile(file);
            if (file.getName().endsWith(".pde")) {
                code = START_JAVA_CODE + code + END_JAVA_CODE;
            }
            CompilationUnit cu;
            try {
                cu = JavaParser.parse(code);
            } catch (Exception e) {
                System.err.println(String.format("Error when parsing file %s, skipping.)", file.getAbsolutePath()));
                continue;
            }
            JASTNode node = JASTNode.createFrom(cu);
            String fileName = "Tutorial2018" + file.getAbsolutePath().split("Tutorial2018")[1];
            node.setFileName(fileName);
            contents.add(node);
        }
        return contents;
    }

    public static String readFile(File file) {
        StringBuilder code = new StringBuilder();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                code.append(scanner.nextLine()).append("\r\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return code.toString();
    }

    /**
     * Creates a list of EXASTs corresponding to the list of files and one CSV file containing all comments.
     *
     * @param files        the list of files with java code
     * @param commentsFile the CSV for comments corresponding the the files. Expected format:
     *                     ID, File, Priority, Line, Description, Rule set, Rule
     * @return a list of EXASTs corresponding to the given files
     */
    public static List<ASTNode> parseFiles(List<File> files, File commentsFile) {
        List<ASTNode> asts = parseFiles(files);
        List<Comment> comments = parseCommentsFile(commentsFile);
        for (ASTNode ast : asts) {
            // not very efficient
            List<Comment> astSpecificComments = comments.stream()
                    .filter(c -> c.getFile().equals(ast.getFileName()))
                    .collect(Collectors.toList());
            int i = 0;
            for (ASTNode node : ast.getAll()) {
                for (Comment comment : astSpecificComments) {
                    if (comment.getLineNumbers().contains(node.getStartLineNumber() - 1) &&
                            node.getParent() != null && node.getParent().getStartLineNumber() != node.getStartLineNumber()) {
                        System.out.println("Set comment \r\n" + comment.getMessage() + " \r\nto line \r\n" + node.getContent().split("\r\n")[0]
                                + "(type = " + ((JASTNode) node).getNodeType() + ", begin=" + node.getStartLineNumber()
                                + ", end=" + node.getEndLineNumber() + ")");
                        System.out.println("------------------------<>");
                        i++;
                        node.setComment(comment);
                    }
                }
            }
            if (ast.getFileName().contains("248930")) {
                System.out.println("i = " + i);
                System.out.println(ast.getFileName() + " ---------------------------------------");
            }
        }
        return asts;
    }

    private static List<Comment> parseCommentsFile(File commentsFile) {
        List<Comment> comments = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(commentsFile);
            String line;
            scanner.nextLine(); // to skip the CSV column definitions
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                // ID[0], File[1], Priority[2], Line[3], Description[4], Rule set[5], Rule[6]
                String[] commaSplit = line.split(";");
                Comment comment = new Comment(
                        Integer.parseInt(commaSplit[3]),
                        commaSplit[4],
                        commaSplit[1].replace("/", "\\"),
                        commaSplit[5],
                        commaSplit[6]
                );
                comments.add(comment);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return comments;
    }

    public static void printChildNodeLines(Node node) {
        String code = node.toString();
        System.out.println(code);
    }

    private static List<String> createStrings(List<ASTNode> asts, boolean withComment) {
        List<String> contents = new ArrayList<>();
        for (ASTNode ast : asts) {
            for (ASTNode node : ast.getAll()) {
                // only take everything under the class definition
                if (withComment == (node.getComment() != null)) {
                    String content = node.getContent().replace("'", ""); // TODO improve
                    if (!content.contains("public") && !content.contains("private")) {
                        contents.add(content);
                    } else if (node.getChildren().size() > 1) {
                        for (int j = 0; j < node.getChildren().size(); j++) {
                            contents.add(node.getChildren().get(j).getContent().replace("'", "")); // TODO improve
                        }
                    }
                }
            }
        }
        return contents;
    }

    public static List<String> createCorrectStrings(List<ASTNode> asts) {
        return createStrings(asts, false);
    }

    public static List<String> createIncorrectStrings(List<ASTNode> asts) {
        return createStrings(asts, true);
    }
}
