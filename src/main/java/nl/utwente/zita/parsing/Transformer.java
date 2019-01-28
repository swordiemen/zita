package nl.utwente.zita.parsing;

import nl.utwente.zita.ast.ASTNode;
import nl.utwente.zita.constants.Constants;
import nl.utwente.zita.data.Attribute;
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

    public static final int FUNCTION_USE_THRESHOLD = 1000;

    private List<String> classes = new ArrayList<>();

    private List<String> getClasses() {
        return classes;
    }

    private static Map<String, Set<String>> attributesToNominalValues = new TreeMap<>();

    private static Map<String, Integer> functionCount = new HashMap<>();

    /**
     * Tranforms a tuple (content, classification) into an arff format that weka can use.
     *
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

        // Numerical values
        TreeMap<String, String> attributes = new TreeMap<>();
        for (Map.Entry<String, Object> attributeEntry : fileContents.get(0).getLeft().getAttributes().entrySet()) {
            String attribute = attributeEntry.getKey();
            String attrType = Util.getArffTypeFromJavaType(attributeEntry.getValue());
            if (!attrType.equals("string")) {
                attributes.put(attribute, attrType);
            }
        }
        attributes.forEach((attr, type) -> arffBuilder.append(String.format("@attribute %s %s\r\n", attr, type)));

        // Nominal values
        for (Map.Entry<String, Set<String>> nominalValues : attributesToNominalValues.entrySet()) {
            String attribute = nominalValues.getKey();
            Set<String> values = nominalValues.getValue();
            if (Attribute.isFunctionAttribute(attribute)) {
                arffBuilder.append(String.format("@attribute %s {SelfDefined,", attribute));
                for (String val : values) {
                    if (functionCount.getOrDefault(val, 0) >= FUNCTION_USE_THRESHOLD) {
                        arffBuilder.append(val).append(",");
                    }
                }
            } else {
                arffBuilder.append(String.format("@attribute %s {", attribute));
                for (String val : values) {
                    arffBuilder.append(val).append(",");
                }
            }
            int size = arffBuilder.length();
            arffBuilder.replace(size - 1, size, "");
            arffBuilder.append("}\r\n");
        }

        // Classification
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
//                if (!classification.equals("correct") || new Random().nextInt(100) == 1) {
                for (int i = 0; i < (classification.equals("correct") ? 1 : 100); i++) {
                    for (String attr : attributes.keySet()) {
                        String surroundings = attributes.get(attr).equals("string") ? "'" : "";
                        arffBuilder.append(String.format("%s%s%s, ", surroundings, node.getAttribute(attr), surroundings));
                    }
                    for (String attr : attributesToNominalValues.keySet()) {
                        String val = (String) node.getAttribute(attr);
                        if (Attribute.isFunctionAttribute(attr) && functionCount.getOrDefault(val, 0) < FUNCTION_USE_THRESHOLD) {
                            arffBuilder.append("SelfDefined,");
                        } else {
                            arffBuilder.append(val).append(",");
                        }
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
        System.out.println("Completed arff file (" + file.getName() + "). Train = " + train);
    }

    public static void addNominalAttribute(String attribute, String nominalValue) {
        Map<String, Set<String>> map = attributesToNominalValues;
        Set<String> strings;
        if (!map.containsKey(attribute)) {
            strings = new HashSet<>();
            map.put(attribute, strings);
        } else {
            strings = map.get(attribute);
        }
        strings.add(nominalValue);
    }

    public static void addFunctionCall(String functionName) {
        functionCount.put(functionName, functionCount.getOrDefault(functionName, 0) + 1);
    }
}
