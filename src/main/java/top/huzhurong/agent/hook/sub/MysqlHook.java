package top.huzhurong.agent.hook.sub;

import top.huzhurong.agent.hook.BaseHook;
import top.huzhurong.agent.inter.sql.ResultSet;
import top.huzhurong.agent.inter.sql.RowData;
import top.huzhurong.agent.log.AgentLog;

import java.lang.reflect.Method;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/10/14
 */
public class MysqlHook extends BaseHook {

    private MysqlHook() {
        class_name.add("com.mysql.jdbc.PreparedStatement".replace(".", "/"));
        method_name.put("executeInternal", null);
    }

    public static final MysqlHook Instance = new MysqlHook();

    @Override
    public void into(Object curObject, Object[] args) {

    }

    @Override
    public void out(Object ret, Object cur, long execTime, Object[] args) {
        try {
            if (ret instanceof ResultSet) {
                ResultSet resultSet = (ResultSet) ret;
                RowData asmRowData = resultSet.getASMRowData();
                long size;
                if (asmRowData != null) {
                    size = asmRowData.size();
                } else {
                    size = resultSet.getUpdateCount();
                }
                String sql = cur.toString().substring(47).trim().replace("\t", "")
                        .replace("\n", "").replaceAll("\\s+", " ");
                String mess = "【sql:" + sql + "】,【rt:" + execTime + "(ms)】,【扫描行数:" + size + "】";
                if (log.equals("yes")) {
                    AgentLog.info(mess);
                } else {
                    System.out.println(mess);
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
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
        return new String(bytes).replace("\r", " ").replace("\n", " ").replace("\\s+", " ").trim();
    }
}
