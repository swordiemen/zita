package nl.utwente.zita.data;

import nl.utwente.zita.ast.ASTNode;
import nl.utwente.zita.parsing.Parser;
import nl.utwente.zita.parsing.Transformer;
import nl.utwente.zita.util.Tuple;
import nl.utwente.zita.util.Util;
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
    private Map<ASTNode, Map<ASTNode, String>> astToTexts;

    public Data(List<File> files) {
        this.files = files;
        dataPoints = new ArrayList<>();
        astToTexts = new HashMap<>();
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
        getAstToTexts().putAll(astToText);
        List<Tuple<ASTNode, String>> contentClasses = getContentClassifications();
        getTransformer().transformToARFF(contentClasses, train);
    }

    public Map<ASTNode, Map<ASTNode, String>> getAstToTexts() {
        return astToTexts;
    }

    private List<Tuple<ASTNode, String>> getContentClassifications() {
        // get all classifications of contents -> class
        return getAstToTexts().values().stream()
                .flatMap(fileContents -> fileContents.entrySet().stream())
                .map(content -> new Tuple<>(content.getKey(), content.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Adds a new training data point to this Data object. Updates the training ARFF file.
     * @param file the training file containing the program
     * @param warningFile the warning file containing the comments about the program
     */
    public void addDataPoint(File file, File warningFile) {
        List<ASTNode> asts = Parser.parseFiles(Util.singletonList(file), warningFile);
        Map<ASTNode, Map<ASTNode, String>> astToText = Parser.createStrings(asts, true);
        for (ASTNode ast : astToText.keySet()) {
            for (Map.Entry<ASTNode, String> classifications : astToText.get(ast).entrySet()) {
                ASTNode node = classifications.getKey();
                String content = node.getContent().replaceAll("[\\s]", "").replaceAll("'", "");
                String classification = classifications.getValue();
                getDataPoints().add(new DataPoint(ast, content, classification));
            }
        }
        getAstToTexts().putAll(astToText);
        List<Tuple<ASTNode, String>> contentClasses = getContentClassifications();
        getTransformer().transformToARFF(contentClasses, true);
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


}
