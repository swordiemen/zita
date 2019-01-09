package nl.utwente.zita.util;

import com.github.javaparser.resolution.UnsolvedSymbolException;

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

    public static String getArffTypeFromJavaType(Object o) {
        if (o instanceof String) {
            return "string";
        } else if (o instanceof Integer || o instanceof Double) {
            return "numeric";
        } else {
            throw new UnsupportedOperationException("Unsupported class type " + o.getClass());
        }
    }
}
