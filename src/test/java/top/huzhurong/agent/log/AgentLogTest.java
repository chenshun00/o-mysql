package top.huzhurong.agent.log;

import org.junit.Test;


/**
 * @author luobo.cs@raycloud.com
 * @since 2018/9/29
 */
public class AgentLogTest {

    @Test
    public void info() {
        AgentLog.info("hello {}","chenshun");
    }

    @Test
    public void info1() {
        AgentLog.info("hello world", new Throwable("xxxx"));
    }

    @Test
    public void warn() {
        AgentLog.warn("xxxx");
    }

    @Test
    public void warn1() {
        AgentLog.warn("xxxx", new Throwable("wwwwww"));

    }
}