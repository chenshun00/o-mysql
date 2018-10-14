package top.huzhurong.agent.hook;

import org.objectweb.asm.Type;
import top.huzhurong.agent.data.ThreadData;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/9/29
 */
public abstract class HookUtils {

    public final static String CLASS_NAME = HookUtils.class.getName().replace(".", "/");

    public static String ENTER_METHOD_NAME = "enterMethod";
    public static String END_METHOD_NAME = "endMethod";
    public static String ERROR_METHOD_NAME = "errorMethod";


    public static String ENTER_METHOD_DESC;
    public static String END_METHOD_DESC;
    public static String ERROR_METHOD_DESC;

    private final static int THREAD_SIZE = 65535;

    private final static ThreadData[] THREAD_DATA = new ThreadData[THREAD_SIZE];

    private final static AtomicLong count = new AtomicLong(0);
    /**
     * 对应的hook
     */
    private static Map<String, BaseHook> hooks = new StrictMap<>();

    static {
        for (Method method : HookUtils.class.getDeclaredMethods()) {
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

    public synchronized static long hookKey(BaseHook baseHook) {
        long key = count.incrementAndGet();
        hooks.put(String.valueOf(key), baseHook);
        return key;
    }

    public static void enterMethod(long key, Object currentObject, Object[] args) {
        Thread thread = Thread.currentThread();
        int id = (int) thread.getId();
        if (id >= THREAD_SIZE) {
            return;
        }
        if (THREAD_DATA[id] == null) {
            ThreadData threadData = new ThreadData();
            threadData.setCurTime(System.currentTimeMillis());
            THREAD_DATA[id] = threadData;
        }

        hooks.get(String.valueOf(key)).into(currentObject, args);
    }


    /**
     * @param object        this/null
     * @param ret 返回值
     * @param args          方法参数
     */
    public static void endMethod(Object ret, long key, Object object, Object[] args) {
        Thread thread = Thread.currentThread();
        int id = (int) thread.getId();
        if (id >= THREAD_SIZE) {
            return;
        }
        ThreadData thrData = THREAD_DATA[(int) thread.getId()];
        if (thrData == null) {
            // 没有执行start,直接执行end/可能是异步停止导致的
            return;
        }
        long curTime = System.currentTimeMillis();
        long rt = curTime - thrData.getCurTime();

        hooks.get(String.valueOf(key)).out(ret, object, rt, args);
    }

    public static void errorMethod(Throwable ex, long key, Object cur, Object[] args) {
        Thread thread = Thread.currentThread();
        int id = (int) thread.getId();
        if (id < THREAD_SIZE) {
            return;
        }
        ThreadData thrData = THREAD_DATA[(int) thread.getId()];
        if (thrData == null) {
            // 没有执行start,直接执行end/可能是异步停止导致的
            return;
        }
        hooks.get(String.valueOf(key)).error(cur, ex, args);
    }
}
