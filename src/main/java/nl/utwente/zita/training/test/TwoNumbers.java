package nl.utwente.zita.training.test;

/**
 * @author Sjonnie
 * Created on 9/18/2018.
 */
public class TwoNumbers {
    private int x;
    private int y;

    public TwoNumbers(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void printXThroughY() {
        boolean isPassed = true;
        int x = getX();
        int y = getY();
        if (x > y) {
            isPassed = true;
        }
        while (isPassed) {
            System.out.println(getY() - getX());
        }
    }
}
