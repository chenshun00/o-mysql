package top.huzhurong.agent.hook;

import org.objectweb.asm.Type;

import java.lang.reflect.Method;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/9/29
 */
public class ClassHook {

    public final static String CLASS_NAME = ClassHook.class.getName().replace(".", "/");

    static String ENTER_METHOD_NAME = "enterMethod";
    static String END_METHOD_NAME = "endMethod";
    static String ERROR_METHOD_NAME = "errorMethod";


    static String ENTER_METHOD_DESC;
    static String END_METHOD_DESC;
    static String ERROR_METHOD_DESC;

    static {
        for (Method method : ClassHook.class.getDeclaredMethods()) {
            if (method.getName().equals(ENTER_METHOD_NAME)) {
                ENTER_METHOD_DESC = Type.getMethodDescriptor(method);
            }
            if (method.getName().equals(END_METHOD_NAME)) {
                END_METHOD_DESC = Type.getMethodDescriptor(method);
            }
            if (method.getName().equals(ERROR_METHOD_NAME)) {
                ERROR_METHOD_DESC = Type.getMethodDescriptor(method);
            }
        }
    }

    public static void enterMethod(Object currentObject, Object[] args) {
        if (args.length > 0 && args[0] != null && args[0].toString().length() > 48) {
            System.out.println(args[0].toString().substring(47).replace("\t", " ")
                    .replace("\n", " ").replaceAll("\\s{2,5}", " "));
        }
    }


    public static void endMethod() {

    }

    public static void errorMethod() {

    }
}
