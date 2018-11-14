package nl.utwente.zita.training.test;

/**
 * @author Sjonnie
 * Created on 10/15/2018.
 */
public class ThesisExample {
    public int bar(int testNumber) {
        int base = 10;
        int result;
        if (testNumber > base) {
            result = base * testNumber;
        } else {
            result = base - testNumber;
        }
        return result;
    }
}
