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

    static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void enterMethod(Object currentObject, Object[] args) {
        if (args.length > 0 && args[0] != null && args[0].toString().length() > 48) {
            System.out.println(args[0].toString().substring(47).replace("\t", "")
                    .replace("\n", "").replaceAll("\\s+", " "));
            threadLocal.set(System.currentTimeMillis());
        }
    }


    public static void endMethod() {
        Long aLong = threadLocal.get();
        if (aLong != null) {
            threadLocal.remove();
            long l = System.currentTimeMillis();
            long rt = l - aLong;
            System.out.println("rt:" + rt + "(ms)");
        }
    }

    public static void errorMethod() {

    }
}
