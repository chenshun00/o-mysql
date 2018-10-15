package top.huzhurong.agent.inter.sql;

import top.huzhurong.agent.annotation.InjectInterface;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/10/1
 */
@InjectInterface("com/mysql/jdbc/ResultSetImpl")
public interface ResultSet {

    RowData getASMRowData();

    long getUpdateCount();

}
