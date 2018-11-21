package nl.utwente.zita.data;

import nl.utwente.zita.ast.ASTNode;
import nl.utwente.zita.parsing.Parser;
import nl.utwente.zita.parsing.Transformer;
import nl.utwente.zita.util.Tuple;
import weka.core.Instance;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        Map<ASTNode, Map<String, String>> astToText = Parser.createStrings(asts, train);
        for (ASTNode ast : astToText.keySet()) {
            for (Map.Entry<String, String> classifications : astToText.get(ast).entrySet()) {
                String content = classifications.getKey();
                String classification = classifications.getValue();
                getDataPoints().add(new DataPoint(ast, content, classification));
            }
        }
        // get all classifications of contents -> class
        List<Tuple<String, String>> contentClasses = astToText.values().stream()
                .flatMap(fileContents -> fileContents.entrySet().stream())
                .map(content -> new Tuple<>(content.getKey(), content.getValue()))
                .collect(Collectors.toList());
        getTransformer().transformToARFF(contentClasses, train);

    }

    public DataPoint getDataPointByContent(Instance instance) {
        for (DataPoint dataPoint : getDataPoints()) {
            String instanceContent = instance.toString(0);
            instanceContent = instanceContent.substring(1, instanceContent.length() - 1);
            if (dataPoint.getContent().equals(instanceContent)) {
                return dataPoint;
            }
        }
        return null;
    }
}
