package top.huzhurong.agent.data;

import java.util.TimerTask;

/**
 * @author chenshun00@gmail.com
 * @since 2018/11/29
 */
public class Timer {
    public static void init() {
        System.out.println("==================timer init==================");
        java.util.Timer timer = new java.util.Timer("agent-timer", true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //定时输出数据
            }
        }, 10 * 1000, 60 * 1000);
    }
}
