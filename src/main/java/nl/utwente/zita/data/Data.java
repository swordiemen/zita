package nl.utwente.zita.data;

import nl.utwente.zita.ast.ASTNode;
import nl.utwente.zita.parsing.Parser;
import nl.utwente.zita.parsing.Transformer;
import nl.utwente.zita.util.Tuple;
import weka.core.Instance;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Sjonnie
 * Created on 11/20/2018.
 */
public class Data {
    private static Transformer transformer = new Transformer();
    private boolean trainingData;
    private List<DataPoint> dataPoints;

    private List<File> files;
    private File warningFile;

    public Data(List<File> files) {
        this.files = files;
        dataPoints = new ArrayList<>();
    }

    public Data(List<File> files, File warningFile) {
        this(files);
        this.warningFile = warningFile;
        trainingData = true;
    }

    public List<File> getFiles() {
        return files;
    }

    public List<DataPoint> getDataPoints() {
        return dataPoints;
    }

    public File getWarningFile() {
        return warningFile;
    }

    public boolean isTrainingData() {
        return trainingData;
    }

    public Transformer getTransformer() {
        return transformer;
    }

    public void generateDataPoints() {
        boolean train = isTrainingData();
        List<File> files = getFiles();
        List<ASTNode> asts = Parser.parseFiles(files, getWarningFile()); // if warning file is null, makes a normal ast
        Map<ASTNode, Map<ASTNode, String>> astToText = Parser.createStrings(asts, train);
        for (ASTNode ast : astToText.keySet()) {
            for (Map.Entry<ASTNode, String> classifications : astToText.get(ast).entrySet()) {
                ASTNode node = classifications.getKey();
                String content = node.getContent().replace("'", ""); // TODO improve;
                String classification = classifications.getValue();
                getDataPoints().add(new DataPoint(node, content, classification));
            }
        }
        // get all classifications of contents -> class
        List<Tuple<ASTNode, String>> contentClasses = astToText.values().stream()
                .flatMap(fileContents -> fileContents.entrySet().stream())
                .map(content -> new Tuple<>(content.getKey(), content.getValue()))
                .collect(Collectors.toList());
        getTransformer().transformToARFF(contentClasses, train);

    }

    public DataPoint getDataPointByContent(Instance instance) {
        String instanceContent = instance.toString(0).replaceAll("[\\s]", "");
        instanceContent = instanceContent.substring(1, instanceContent.length() - 1);
        for (DataPoint dataPoint : getDataPoints()) {
            if (dataPoint.getContent().equals(instanceContent)) {
                return dataPoint;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        Set<Integer> set = new HashSet<>();
        for (int j = 0; j < 10; j++) {
            set.clear();
            for (int i = 0; i < 1000000; i++) {
                set.add(new Random().nextInt());
            }
            long start = System.currentTimeMillis();
            int count = 0;
            for (int i : set) {
                if (i < 1000) {
                    count++;
                }
            }
            System.out.println("Time taken (Loop ) : " + (System.currentTimeMillis() - start) + "ms, count = " + count);
            start = System.currentTimeMillis();
            count = (int) set.stream().filter(i -> i < 1000).count();
            System.out.println("Time taken (Steam) : " + (System.currentTimeMillis() - start) + "ms, count = " + count);
        }


    }
}
