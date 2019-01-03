package top.huzhurong.agent.inter.sql;

import top.huzhurong.agent.annotation.InjectInterface;

/**
 * @author chenshun00@gmail.com
 * @since 2018/10/1
 */
@InjectInterface("com/mysql/jdbc/ResultSetImpl")
public interface ResultSet {

    RowData getASMRowData();

    long getUpdateCount();

}
