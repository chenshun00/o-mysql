package top.huzhurong.agent.hook;

import org.objectweb.asm.Type;
import top.huzhurong.agent.inter.sql.ResultSet;
import top.huzhurong.agent.inter.sql.RowData;
import top.huzhurong.agent.log.AgentLog;

import java.lang.reflect.Method;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/9/29
 */
public class ClassHook {

    private static final String log;

    static {
        log = System.getProperty("mysql.log", "no");
    }

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
        threadLocal.set(System.currentTimeMillis());
    }


    /**
     * @param object        返回值
     * @param currentObject this/null
     * @param args          方法参数
     */
    public static void endMethod(Object object, Object currentObject, Object[] args) {
        Long aLong = threadLocal.get();
        if (aLong != null) {
            threadLocal.remove();
            long l = System.currentTimeMillis();
            long rt = l - aLong;
            try {
                if (object instanceof ResultSet) {
                    ResultSet resultSet = (ResultSet) object;
                    RowData asmRowData = resultSet.getASMRowData();
                    if (asmRowData != null) {
                        String sql = currentObject.toString().substring(47).trim().replace("\t", "")
                                .replace("\n", "").replaceAll("\\s+", " ");
                        String mess = "【sql:" + sql + "】,【rt:" + rt + "(ms)】,【扫描行数:" + asmRowData.size() + "】";
                        if (log.equals("yes")) {
                            AgentLog.info(mess);
                        } else {
                            System.out.println(mess);
                        }
                    }
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    //从字节数组中计算sql语句
    public static String getSql(Object comMysqlJdbcBuffer) throws Throwable {
        StringBuilder buffer = new StringBuilder();
        Method getByteBuffer = comMysqlJdbcBuffer.getClass().getDeclaredMethod("getByteBuffer");
        byte[] getByteBuffers = (byte[]) getByteBuffer.invoke(comMysqlJdbcBuffer);
        for (int i = 0; i < 4; ++i) {
            String hexVal = Integer.toHexString(getByteBuffers[i] & 255);
            if (hexVal.length() == 1) {
                hexVal = "0" + hexVal;
            }
            buffer.insert(0, hexVal);
        }
        Integer integer = Integer.decode(buffer.insert(0, "0x").toString());
        if (integer > 2048) {
            integer = 2048;
        }
        byte[] bytes = new byte[integer];
        System.arraycopy(getByteBuffers, 5, bytes, 0, integer - 1);
        return new String(bytes).replace("\r", " ").replace("\n", " ").replace("\t", " ").trim();
    }


    public static void errorMethod(Throwable ex, Object cur, Object[] args) {

    }
}
