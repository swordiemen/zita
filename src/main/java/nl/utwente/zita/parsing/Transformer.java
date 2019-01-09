package nl.utwente.zita.parsing;

import nl.utwente.zita.ast.ASTNode;
import nl.utwente.zita.constants.Constants;
import nl.utwente.zita.util.Tuple;
import nl.utwente.zita.util.Util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

/**
 * @author Sjonnie
 * Created on 9/18/2018.
 */
public class Transformer {

    private List<String> classes = new ArrayList<>();

    private List<String> getClasses() {
        return classes;
    }

    /**
     * Tranforms a tuple (content, classification) into an arff format that weka can use.
     * @param fileContents a list of (content, classification) tuples.
     */
    public void transformToARFF(List<Tuple<ASTNode, String>> fileContents, boolean train) {
        for (Tuple<ASTNode, String> tuple : fileContents) {
            String classification = tuple.getRight();
            if (!"?".equalsIgnoreCase(classification) && !getClasses().contains(classification)) {
                getClasses().add(classification);
            }
        }
        StringBuilder arffBuilder = new StringBuilder();
        arffBuilder.append("@relation ast\r\n\r\n");
        // TreeMap to ensure that the order of attributes remains the same across different data points
        TreeMap<String, String> attributes = new TreeMap<>();
        for (Map.Entry<String, Object> attributeEntry : fileContents.get(0).getLeft().getAttributes().entrySet()) {
            String attribute = attributeEntry.getKey();
            String attrType = Util.getArffTypeFromJavaType(attributeEntry.getValue());
            attributes.put(attribute, attrType);
        }
        attributes.forEach((attr, type) -> arffBuilder.append(String.format("@attribute %s %s\r\n", attr, type)));
        arffBuilder.append("@attribute ClassArff {");
        for (String clazz : getClasses()) {
            arffBuilder.append(clazz).append(",");
        }
        int size = arffBuilder.length();
        arffBuilder.replace(size - 1, size, "");
        arffBuilder.append("}\r\n\r\n@data\r\n");
        for (Tuple<ASTNode, String> tuple : fileContents) {
            ASTNode node = tuple.getLeft();
            if (node.getAttributes().size() > 0) {
                String classification = tuple.getRight();
                if (!classification.equals("correct") || new Random().nextInt(100) == 1) {
                    for (String attr : attributes.keySet()) {
                        String surroundings = attributes.get(attr).equals("string") ? "'" : "";
                        arffBuilder.append(String.format("%s%s%s, ", surroundings, node.getAttribute(attr), surroundings));
                    }
                    arffBuilder.append(classification).append("\r\n");
                }
            }
        }
        File file = new File(String.format("%s/data.arff", train ? Constants.ARFF_TRAIN_DIR : Constants.ARFF_TEST_DIR));
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(arffBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Completed arff file (" + file.getName() + ")");
    }
}
