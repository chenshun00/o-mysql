package top.huzhurong.agent.log;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/9/29
 */
class LoggerUtils {
    private static Lock LOCK = new ReentrantLock();

    static void disableOtherHandlers(Logger logger, Handler handler) {
        if (logger == null) {
            return;
        }
        LOCK.lock();
        try {
            Handler[] handlers = logger.getHandlers();
            if (handlers == null) {
                return;
            }
            if (handlers.length == 1 && handlers[0].equals(handler)) {
                return;
            }

            logger.setUseParentHandlers(false);
            for (Handler h : handlers) {
                logger.removeHandler(h);
            }
            logger.addHandler(handler);
        } finally {
            LOCK.unlock();
        }
    }
}
