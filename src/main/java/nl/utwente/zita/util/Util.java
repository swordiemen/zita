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

    public static int countChars(char c, String str) {
        int count = c;
        for (char c1 : str.toCharArray()) {
            if (c1 == c) {
                count++;
            }
        }
        return count;
    }
}
