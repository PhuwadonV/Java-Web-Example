package backend;

import java.io.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Resource {
    private static final ClassLoader CLASS_LOADER = Resource.class.getClassLoader();

    public static InputStream GetAsStream(String name) {
        return CLASS_LOADER.getResourceAsStream(name);
    }

    public static Stream<String> Lines(String name) {
        return new BufferedReader(new InputStreamReader(GetAsStream(name))).lines();
    }
}
