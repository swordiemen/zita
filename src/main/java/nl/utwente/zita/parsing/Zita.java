package nl.utwente.zita.parsing;

import nl.utwente.zita.constants.Constants;
import nl.utwente.zita.data.Data;
import nl.utwente.zita.data.DataPoint;
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
public class Zita {

    private Instances train;
    private Data trainingData;
    private static final File TRAIN_FILE = new File(String.format("%s/data.arff",Constants.ARFF_TRAIN_DIR));
    private static final File TEST_FILE = new File(String.format("%s/data.arff",Constants.ARFF_TEST_DIR));

    public Zita() {
    }

    public void addTrainingFile(File file, File comments) {
        trainingData.addDataPoint(file, comments);
    }

    private void init() throws Exception {
        File trainDir = new File(Constants.JAVA_TRAIN_DIR);
        File testDir = new File(Constants.JAVA_TEST_DIR);
        File codrDir = new File(Constants.CODR_FILES);
//        File codrDir = new File("/home/tim/uni/master/thesis/zita/resources/singletest/single");
        List<File> codrFiles = new ArrayList<>();
        for (File file : codrDir.listFiles()) {
            codrFiles.addAll(Arrays.asList(file.listFiles()[0].listFiles()[0].listFiles()));
//            codrFiles.add(file);
        }

        File warnings = new File(codrDir + "/../warnings.csv");
        Data trainingData = new Data(codrFiles, warnings);
        this.trainingData = trainingData;
        trainingData.generateDataPoints();
        Data testData = new Data(codrFiles);
        testData.generateDataPoints();



        StringToWordVector filter = new StringToWordVector();
        Classifier naiveBayes = new NaiveBayes();


        //training data
        Instances train = new Instances(new BufferedReader(new FileReader(TRAIN_FILE)));
        int lastIndex = train.numAttributes() - 1;
        train.setClassIndex(lastIndex);
        filter.setInputFormat(train);
        train = Filter.useFilter(train, filter);

        //testing data
        Instances test = new Instances(new BufferedReader(new FileReader(TEST_FILE)));
        test.setClassIndex(lastIndex);
        Instances test2 = Filter.useFilter(test, filter);

        naiveBayes.buildClassifier(train);

        for(int i = 0; i < test2.numInstances(); i++) {
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

    public static void main(String[] args) throws Exception {
        Zita zita = new Zita();
        zita.init();
    }
}
