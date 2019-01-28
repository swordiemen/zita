package nl.utwente.zita.parsing;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import nl.utwente.zita.ast.ASTNode;
import nl.utwente.zita.ast.Comment;
import nl.utwente.zita.ast.javaast.JASTNode;
import nl.utwente.zita.constants.Constants;
import nl.utwente.zita.util.Tuple;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
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
        int errorCount = 0;
        for (File file : files) {
            String code = readFile(file);
            if (file.getName().endsWith(".pde")) {
                code = START_JAVA_CODE + code + END_JAVA_CODE;
                // 2017: ignore pde files
//                continue;
            }
            CompilationUnit cu;
            try {
                cu = JavaParser.parse(code);
            } catch (Exception e) {
                System.err.println(String.format("Error when parsing file %s, skipping.)", file.getAbsolutePath()));
                errorCount++;
                continue;
            }
            JASTNode node = JASTNode.createFrom(cu);

            // 2018
            String fileName = "Tutorial2018" + file.getAbsolutePath().split("Tutorial2018")[1];
            // 2017
//            String fileName = file.getAbsolutePath().split(Constants.CSEDU_SUBFOLDER)[1];

            node.setFileName(fileName);
            contents.add(node);
        }
        System.err.println("Encountered " + errorCount + " unparsabale files.");
        for (ASTNode node : contents) {
            for (ASTNode n : node.getAll()) {
                n.generateAttributes();
            }
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
        if (commentsFile == null) {
            return parseFiles(files);
        }
        List<ASTNode> asts = parseFiles(files);
        List<Comment> comments = parseCommentsFile(commentsFile);
        for (ASTNode ast : asts) {
            // not very efficient
            List<Comment> astSpecificComments = comments.stream()
                    .filter(c -> c.getFile().equals(ast.getFileName()))
                    .collect(Collectors.toList());
            for (ASTNode node : ast.getAll()) {
                for (Comment comment : astSpecificComments) {
                    if (comment.getLineNumbers().contains(node.getStartLineNumber() - 1) &&
                            node.getParent() != null && node.getParent().getStartLineNumber() != node.getStartLineNumber()) {
                        node.setComment(comment);
                    }
                }
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
                // 2018:
                // ID[0], File[1], Priority[2], Line[3], Description[4], Rule set[5], Rule[6]
                String[] commaSplit = line.split(";");
                Comment comment = new Comment(
                        Integer.parseInt(commaSplit[3]),
                        commaSplit[4],
                        commaSplit[1].replace("/", File.separator),
                        commaSplit[5],
                        commaSplit[6]
                );

                // 2017:
                // ID[0], Package[1], File[2], Priority[3], Line[4], Description[5], Rule set[6], Rule[7]
//                String[] commaSplit = line.split(";");
//                Comment comment = new Comment(
//                        Integer.parseInt(commaSplit[3]),
//                        commaSplit[5],
//                        commaSplit[2].replace("/", File.separator).split(Constants.CSEDU_SUBFOLDER)[1],
//                        commaSplit[6],
//                        commaSplit[7]
//                );

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


    /**
     * Creates a list of (String, String) tuples from a list of asts. The tuple represents the text (left), and
     * the classification (right).
     *
     * @param asts the list of asts of which the tuple should be created.
     * @return a list of tuples of format (Text, correct/incorrect)
     */
    public static Map<ASTNode, Map<ASTNode, String>> createStrings(List<ASTNode> asts, boolean isTrainingData) {
        Map<ASTNode, Map<ASTNode, String>> contents = new HashMap<>();
        for (ASTNode ast : asts) {
            contents.put(ast, new HashMap<>());
            Map<ASTNode, String> contentForFile = contents.get(ast);
            for (ASTNode node : ast.getAll()) {
                // only take everything under the class definition
                boolean isCorrect = node.getComment() == null; // no comment => correct code (assumption)
                String classification =
                        isTrainingData ?
                                isCorrect ? "correct"
                                        : node.getComment().getRule()
                                : "?"; // testing data has a "?" as classification
                String content = node.getContent().replace("'", ""); // TODO improve
                if (!content.contains("public") && !content.contains("private") && !content.contains("//")) {
                    contentForFile.put(node, classification);
                } else if (node.getChildren().size() > 1) {
                    for (int j = 0; j < node.getChildren().size(); j++) {
                        contentForFile.put(
                                node.getChildren().get(j),
                                classification
                        );
                        // TODO improve
                    }
                }
            }
        }
        return contents;
    }
}
