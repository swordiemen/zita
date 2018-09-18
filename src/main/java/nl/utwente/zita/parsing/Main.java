package nl.utwente.zita.parsing;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import nl.utwente.zita.constants.Constants;
import nl.utwente.zita.util.Tuple;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.bayes.NaiveBayesMultinomialText;
import weka.classifiers.rules.DecisionTable;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sjonnie
 * Created on 9/18/2018.
 */
public class Main {

    public static void main(String[] args) throws Exception {
//        File trainDir = new File(Constants.JAVA_TRAIN_DIR);
//        File testDir = new File(Constants.JAVA_TEST_DIR);
//        List<File> trainFiles = new ArrayList<>(Arrays.asList(trainDir.listFiles()));
//        List<File> testFiles = new ArrayList<>(Arrays.asList(testDir.listFiles()));
//        List<String> trainingContents = Parser.parseFiles(trainFiles);
//        List<String> testingContents = Parser.parseFiles(testFiles);
//        Transformer transformer = new Transformer();
//        List<Tuple<String, String>> trainingData= new ArrayList<>();
//        List<Tuple<String, String>> testData = new ArrayList<>();
//        for (int i = 0; i < trainFiles.size(); i++) {
//            File file = trainFiles.get(i);
//            String content = trainingContents.get(i);
//            boolean incorrect = file.getName().startsWith("C");
//            trainingData.add(new Tuple<>(content, incorrect ? "incorrect" : "correct"));
//        }
//        for (int i = 0; i < testingContents.size(); i++) {
//            String content = testingContents.get(i);
//            testData.add(new Tuple<>(content, "?"));
//        }
//        transformer.transformToARFF(trainingData, true);
//        transformer.transformToARFF(testData, false);

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
            System.out.println(test.instance(i));
            double index = naiveBayes.classifyInstance(test2.instance(i));
            System.out.println(index);
            String className = train.classAttribute().value((int) index);
            System.out.println(className);
        }
    }
}
