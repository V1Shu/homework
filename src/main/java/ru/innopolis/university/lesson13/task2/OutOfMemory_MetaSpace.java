package ru.innopolis.university.lesson13.task2;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.innopolis.university.lesson13.task2.model.MyClassLoader;
import ru.innopolis.university.lesson13.task2.model.Worker;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author v.shulepov
 */
public class OutOfMemory_MetaSpace {
    private static final Logger LOGGER = LogManager.getLogger(OutOfMemory_MetaSpace.class);
    /**
     * array for code, readied from console
     */
    private static final ArrayList<String> CODE_ARRAY = new ArrayList<>();

    public static void main(String[] args){
        CODE_ARRAY.add("System.out.println(\"Test MetaSpace\");");
        Object obj = null;
        Class<?> someClass = null;

        for (int i = 1; i < 20; i++) {
            File file = enterCodeToSomeClassFile(i);

            compileSomeClass(file);
            someClass = loadSomeClass(i);
            try {
                obj = someClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.info("Can't create instance of SomeClass");
            }
        }
    }

    /**
     * load new SomeClass by loader
     * @return class SomeClass
     */
    private static Class<?> loadSomeClass(int number) {
        MyClassLoader classLoader = new MyClassLoader();

        Class<?> someClass = null;
        try {
            someClass = Class.forName("ru.innopolis.university.lesson13.model.SomeClass" + number, true, classLoader);
        } catch (ClassNotFoundException e) {
            LOGGER.info("SomeClass not found");
        }
        return someClass;
    }

    /**
     * compile file SomeClass.java to SomeClass.class
     * @param file file SomeClass.java
     */
    private static void compileSomeClass(File file) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, file.getPath());
    }

    /**
     * write code to file SomeClass.java
     * @return file with code
     */
    private static File enterCodeToSomeClassFile(int number) {
        File file = new File(getPathToSomeClass(number));
        file.getParentFile().mkdirs();
        try (OutputStreamWriter dataOutputStream = new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8)) {
            file.createNewFile();
            dataOutputStream.write("package ru.innopolis.university.lesson13.model;");
            dataOutputStream.write("import ru.innopolis.university.lesson13.model.Worker;");
            dataOutputStream.write("public class SomeClass" + number + " implements Worker {");
            dataOutputStream.write("public void doWork() {");
            for (String codeStr : CODE_ARRAY) {
                dataOutputStream.write(codeStr);
            }
            dataOutputStream.write("}");
            dataOutputStream.write("}");
        } catch (IOException e) {
            LOGGER.info("Can't write file SomeClass.java");
        }
        return file;
    }

    private static String getPathToSomeClass(int number) {
        return OutOfMemory_MetaSpace.class.getClassLoader().getResource("path").getPath().replace("path","") +
                "ru" + File.separator +
                "innopolis" + File.separator +
                "university" + File.separator +
                "lesson13" +File.separator +
                "model" + File.separator +
                "SomeClass" + number + ".java";
    }
}
