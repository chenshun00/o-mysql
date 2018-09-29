package top.huzhurong.agent.log;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * jul 日志，支持占位符语法
 *
 * @author luobo.cs@raycloud.com
 * @since 2018/9/29
 */
public class AgentLog extends AbstractLog {
    private static final Logger log = Logger.getLogger("mysql-agent");
    private static final String FILE_NAME = "agent.log";
    private static Handler logHandler;

    static {
        logHandler = makeLogger(FILE_NAME, log);
    }

    /**
     * 重置log目录
     */
    public static void resetLogBaseDir(String baseDir) {
        setLogBaseDir(baseDir);
        logHandler = makeLogger(FILE_NAME, log);
    }

    public static void info(String detail, Throwable e) {
        LoggerUtils.disableOtherHandlers(log, logHandler);
        log.log(Level.INFO, detail, e);
    }

    public static void info(String detail, Object... objects) {
        LoggerUtils.disableOtherHandlers(log, logHandler);
        log.log(Level.INFO, detail, objects);
    }

    public static void warn(String detail, Object... objects) {
        LoggerUtils.disableOtherHandlers(log, logHandler);
        log.log(Level.WARNING, detail);
    }

    public static void warn(String detail, Throwable e) {
        LoggerUtils.disableOtherHandlers(log, logHandler);
        log.log(Level.WARNING, detail, e);
    }

}
