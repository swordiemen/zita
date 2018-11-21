package nl.utwente.zita.parsing;

import nl.utwente.zita.ast.ASTNode;
import nl.utwente.zita.constants.Constants;
import nl.utwente.zita.data.Data;
import nl.utwente.zita.data.DataPoint;
import nl.utwente.zita.util.Tuple;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Sjonnie
 * Created on 9/18/2018.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        File trainDir = new File(Constants.JAVA_TRAIN_DIR);
        File testDir = new File(Constants.JAVA_TEST_DIR);
        File codrDir = new File(Constants.CODR_FILES);
        List<File> codrFiles = new ArrayList<>();
        for (File file : codrDir.listFiles()) {
            codrFiles.addAll(Arrays.asList(file.listFiles()[0].listFiles()[0].listFiles()));
        }

        File warnings = new File(Constants.CODR_FILES + "/../warnings.csv");
        Data trainingData = new Data(codrFiles, warnings);
        trainingData.generateDataPoints();
        Data testData = new Data(codrFiles);
        testData.generateDataPoints();


        File trainFile = new File(String.format("%s/data.arff",Constants.ARFF_TRAIN_DIR));
        File testFile = new File(String.format("%s/data.arff",Constants.ARFF_TEST_DIR));

        StringToWordVector filter = new StringToWordVector();
        Classifier naiveBayes = new NaiveBayes();


        //training data
        Instances train = new Instances(new BufferedReader(new FileReader(trainFile)));
        int lastIndex = train.numAttributes() - 1;
        train.setClassIndex(lastIndex);
        filter.setInputFormat(train);
        train = Filter.useFilter(train, filter);

        //testing data
        Instances test = new Instances(new BufferedReader(new FileReader(testFile)));
        test.setClassIndex(lastIndex);
        Instances test2 = Filter.useFilter(test, filter);

        naiveBayes.buildClassifier(train);

        for(int i=0; i < test2.numInstances(); i++) {
            double index = naiveBayes.classifyInstance(test2.instance(i));
            String className = train.classAttribute().value((int) index);
            if (!className.equals("correct") && !test.instance(i).toString(0).contains("class")) {
                DataPoint dataPoint = trainingData.getDataPointByContent(test.instance(i));
                System.out.println(className);
                System.out.println(test.instance(i));
                if (dataPoint != null) {
                    System.out.printf("Datapoint: [%s] Line %d ~ %d%n", dataPoint.getFileName(),
                            dataPoint.getStartLineNumber(), dataPoint.getEndLineNumber());
                }
                System.out.println("-------------------");
            }
        }
    }
}
