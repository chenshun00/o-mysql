package top.huzhurong.agent.data;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/11/29
 */
public class HttpUtilTest {

    @Test
    public void doGet() throws IOException {
        String s = HttpUtil.doGet(null, null);
        Assert.assertNotNull(s);
    }

    @Test
    public void doGet1() throws IOException {
        String s = HttpUtil.doGet(null, null, null, null);
        Assert.assertNotNull(s);
    }

    @Test
    public void doPost() throws IOException {
        String s = HttpUtil.doPost(null, null, 10000, 10000);
        Assert.assertNotNull(s);
    }

    @Test
    public void doPost1() {
    }
}