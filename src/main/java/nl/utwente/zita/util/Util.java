package nl.utwente.zita.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sjonnie
 * Created on 12/6/2018.
 */
public class Util {
    public static <T> List<T> singletonList(T elem) {
        List<T> list = new ArrayList<>();
        list.add(elem);
        return list;
    }
}
