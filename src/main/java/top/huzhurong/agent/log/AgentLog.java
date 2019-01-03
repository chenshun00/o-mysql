package top.huzhurong.agent.log;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * jul 日志，支持占位符语法
 *
 * @author chenshun00@gmail.com
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

    private static String changeMode(String msg) {
        Matcher matcher = PATTERN.matcher(msg);
        if (!matcher.matches()) {
            return msg;
        }
        StringBuilder stringBuilder = new StringBuilder();
        String[] split = msg.split("\\{}");
        for (int i = 0; i < split.length; i++) {
            stringBuilder.append(split[i]).append("{").append(i).append("}");
        }
        return stringBuilder.toString();
    }

    private static Pattern PATTERN = Pattern.compile("\\{}");
}
