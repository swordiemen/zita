package nl.utwente.zita.parsing;

import nl.utwente.zita.constants.Constants;
import nl.utwente.zita.util.Tuple;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public void transformToARFF(List<Tuple<String, String>> fileContents, boolean train) {
        for (Tuple<String, String> tuple : fileContents) {
            String classification = tuple.getRight();
            if (!"?".equalsIgnoreCase(classification) && !getClasses().contains(classification)) {
                getClasses().add(classification);
            }
        }
        StringBuilder arffBuilder = new StringBuilder();
        arffBuilder.append("@relation test\r\n\r\n@attribute text string\r\n@attribute Class {");
        for (String clas : getClasses()) {
            arffBuilder.append(clas).append(",");
        }
        int size = arffBuilder.length();
        arffBuilder.replace(size - 1, size, "");
        arffBuilder.append("}\r\n\r\n@data\r\n");
        for (Tuple<String, String> tuple : fileContents) {
            String content = tuple.getLeft().trim().replace("\r\n", "").replace("\t", "");
            String classification = tuple.getRight();
            arffBuilder.append("'").append(content).append("',").append(classification).append("\r\n");
        }
        File file = new File(String.format("%s/data.arff", train ? Constants.ARFF_TRAIN_DIR : Constants.ARFF_TEST_DIR));
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(arffBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
