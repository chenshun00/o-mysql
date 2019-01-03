package top.huzhurong.agent.log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * @author chenshun00@gmail.com
 * @since 2018/9/29
 */
class DateFileLogHandler extends Handler {

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd:HH");

    private volatile FileHandler handler;

    private final String pattern;
    private final int limit;
    private final int count;
    private final boolean append;

    private volatile boolean initialized = false;

    private volatile long startDate = System.currentTimeMillis();
    private volatile long endDate;

    private final Object monitor = new Object();

    DateFileLogHandler(String pattern, int limit, int count, boolean append) throws SecurityException {
        this.pattern = pattern;
        this.limit = limit;
        this.count = count;
        this.append = append;
        rotateDate();
        this.initialized = true;
    }

    @Override
    public void close() throws SecurityException {
        handler.close();
    }

    @Override
    public void flush() {
        handler.flush();
    }

    @Override
    public void publish(LogRecord record) {
        synchronized (monitor) {
            if (endDate < record.getMillis() || !logFileExits()) { rotateDate(); }
        }

        if (System.currentTimeMillis() - startDate > 25 * 60 * 60 * 1000) {
            String msg = record.getMessage();
            record.setMessage("missed file rolling at: " + new Date(endDate) + "\n" + msg);
        }
        handler.publish(record);
    }

    @Override
    public void setFormatter(Formatter newFormatter) {
        super.setFormatter(newFormatter);
        if (handler != null) { handler.setFormatter(newFormatter); }
    }

    private boolean logFileExits() {
        try {
            File logFile = new File(pattern);
            return logFile.exists();
        } catch (Throwable e) {

        }
        return false;
    }

    private void rotateDate() {
        this.startDate = System.currentTimeMillis();
        if (handler != null) { handler.close(); }
        String newPattern = pattern.replace("%d", format.format(new Date()));
        // Get current date.
        Calendar next = Calendar.getInstance();
        // Begin of next date.
        next.set(Calendar.HOUR_OF_DAY, 0);
        next.set(Calendar.MINUTE, 0);
        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);
        next.add(Calendar.DATE, 1);
        this.endDate = next.getTimeInMillis();

        try {
            this.handler = new FileHandler(newPattern, limit, count, append);
            if (initialized) {
                handler.setEncoding(this.getEncoding());
                handler.setErrorManager(this.getErrorManager());
                handler.setFilter(this.getFilter());
                handler.setFormatter(this.getFormatter());
                handler.setLevel(this.getLevel());
            }
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

}
