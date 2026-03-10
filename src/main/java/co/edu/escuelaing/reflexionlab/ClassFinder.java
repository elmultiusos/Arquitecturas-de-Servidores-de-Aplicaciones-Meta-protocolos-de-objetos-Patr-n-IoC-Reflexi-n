package co.edu.escuelaing.reflexionlab;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClassFinder {

    public static List<Class<?>> find(String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');
        String basePath = "target/classes/" + path;
        File dir = new File(basePath);
        if (!dir.exists()) {
            return classes;
        }
        for (File file : dir.listFiles()) {
            if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                try {
                    classes.add(Class.forName(className));
                } catch (ClassNotFoundException e) {
                    // Ignorar
                }
            }
        }
        return classes;
    }
}
