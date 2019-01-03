package top.huzhurong.agent.hook.sub;

import com.mysql.jdbc.ResultSetImpl;
import top.huzhurong.agent.hook.BaseHook;
import top.huzhurong.agent.inter.sql.ResultSet;
import top.huzhurong.agent.inter.sql.RowData;
import top.huzhurong.agent.log.AgentLog;

/**
 * @author chenshun00@gmail.com
 * @since 2018/10/14
 */
public class MysqlHook extends BaseHook {

    public MysqlHook() {
        class_name.add("com.mysql.jdbc.PreparedStatement");
        method_name.put("executeInternal", null);
    }

    public static final MysqlHook Instance = new MysqlHook();

    @Override
    public void into(Object curObject, Object[] args) {
        ClassLoader classLoader = this.getClass().getClassLoader();
    }

    @Override
    public void out(Object ret, Object cur, long execTime, Object[] args) {
        try {
            if (ret instanceof ResultSetImpl) {
                System.out.println("美滋滋");
            }
            if (ret instanceof ResultSet) {
                ResultSet resultSet = (ResultSet) ret;
                RowData asmRowData = resultSet.getASMRowData();
                long size;
                if (asmRowData != null) {
                    size = asmRowData.size();
                } else {
                    size = resultSet.getUpdateCount();
                }
                String sql = cur.toString().substring(47).trim().replaceAll(":", "").replaceAll("\t", "")
                        .replaceAll("\n", "").replaceAll("\\s+", " ");
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
}
