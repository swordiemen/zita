package nl.utwente.zita.parsing;

import nl.utwente.zita.ast.ASTNode;
import nl.utwente.zita.constants.Constants;
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


        List<File> trainFiles = codrFiles;
        List<File> testFiles = codrFiles;
//        List<File> trainFiles = new ArrayList<>(Arrays.asList(trainDir.listFiles()));
//        List<File> testFiles = new ArrayList<>(Arrays.asList(testDir.listFiles()));

        File warnings = new File(Constants.CODR_FILES + "/../warnings.csv");
        List<ASTNode> trainingASTs = Parser.parseFiles(trainFiles, warnings);
        List<ASTNode> testingASTs = Parser.parseFiles(testFiles);

        List<String> trainingContentsCorrect = Parser.createCorrectStrings(trainingASTs);
        List<String> trainingContentsIncorrect = Parser.createIncorrectStrings(trainingASTs);
        List<String> testingContents = Parser.createCorrectStrings(testingASTs);
        Transformer transformer = new Transformer();
        List<Tuple<String, String>> trainingData= new ArrayList<>();
        List<Tuple<String, String>> testData = new ArrayList<>();
        for (String content : trainingContentsCorrect) {
            trainingData.add(new Tuple<>(content, "correct"));
        }
        for (String content : trainingContentsIncorrect) {
//            System.out.println(content);
//            System.out.println("----------------");
            trainingData.add(new Tuple<>(content, "incorrect"));
        }
//        for (int i = 0; i < trainFiles.size(); i++) {
//            File file = trainFiles.get(i);
//            String content = trainingContents.get(i);
//            // TODO: "Correct" part and "incorrect" part
//            boolean incorrect = file.getName().startsWith("C");
//            trainingData.add(new Tuple<>(content, incorrect ? "incorrect" : "correct"));
//        }
        for (int i = 0; i < testingContents.size(); i++) {
            String content = testingContents.get(i);
            testData.add(new Tuple<>(content, "?"));
        }
        transformer.transformToARFF(trainingData, true);
        transformer.transformToARFF(testData, false);
//
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
            if (className.equals("incorrect") && !test.instance(i).toString(0).contains("class")) {
//                System.out.println(index);
                System.out.println(className);
                System.out.println(test.instance(i));
                System.out.println("-------------------");
            }
        }
    }
}
