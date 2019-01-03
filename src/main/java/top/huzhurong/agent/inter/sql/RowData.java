package top.huzhurong.agent.inter.sql;

import top.huzhurong.agent.annotation.InjectInterface;

import java.sql.SQLException;

/**
 * @author chenshun00@gmail.com
 * @since 2018/10/1
 */
@InjectInterface("com/mysql/jdbc/RowDataStatic")
public interface RowData {

    int size() throws SQLException;

}
