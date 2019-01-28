package nl.utwente.zita.constants;

/**
 * @author Sjonnie
 * Created on 9/18/2018.
 */
public class Constants {
    public static final String DIR = System.getProperty("user.dir");
    public static final String MAIN_DIR = String.format("%s/src/main/java/nl/utwente/zita", DIR);
    public static final String RESOURCES_DIR = String.format("%s/resources", DIR);
    public static final String CODR_FILES = String.format("%s/codr/Tutorial2018", RESOURCES_DIR);
    public static final String CSEDU_FILES = String.format("%s/ccisAll/finals", RESOURCES_DIR);
    public static final String CSEDU_SUBFOLDER = "finals";
    public static final String JAVA_TRAIN_DIR = String.format("%s/training/train", MAIN_DIR);
    public static final String JAVA_TEST_DIR = String.format("%s/training/test", MAIN_DIR);
    public static final String ARFF_TRAIN_DIR = String.format("%s/trainingArffs/train", RESOURCES_DIR);
    public static final String ARFF_TEST_DIR = String.format("%s/trainingArffs/test", RESOURCES_DIR);

}
