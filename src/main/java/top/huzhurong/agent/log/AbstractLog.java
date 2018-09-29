package top.huzhurong.agent.log;


import java.io.File;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/9/29
 */
public class AbstractLog {
    public static final String LOG_CHARSET = "utf-8";
    private static final String DIR_NAME = "logs" + File.separator + "agent";
    private static String logBaseDir;

    static {
        setLogBaseDir(System.getProperty("user.home"));
    }

    public static String getLogBaseDir() {
        return logBaseDir;
    }

    protected static void setLogBaseDir(String baseDir) {
        if (!baseDir.endsWith(File.separator)) {
            baseDir += File.separator;
        }
        String path = baseDir + DIR_NAME + File.separator;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        logBaseDir = path;
    }

    protected static Handler makeLogger(String logName, Logger heliumRecordLog) {
        AgentFormatter formatter = new AgentFormatter();
        String fileName = AbstractLog.getLogBaseDir() + logName;
        Handler handler = null;
        try {
            handler = new DateFileLogHandler(fileName + ".%d", 1024 * 1024 * 200, 1, true);
            handler.setFormatter(formatter);
            handler.setEncoding(LOG_CHARSET);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LoggerUtils.disableOtherHandlers(heliumRecordLog, handler);
        heliumRecordLog.setLevel(Level.ALL);
        return handler;
    }
}
