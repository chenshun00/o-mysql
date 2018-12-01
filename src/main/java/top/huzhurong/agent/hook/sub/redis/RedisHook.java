package top.huzhurong.agent.hook.sub.redis;

import top.huzhurong.agent.hook.BaseHook;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/1
 */
public class RedisHook extends BaseHook {

    public static final RedisHook TEST_HOOK = new RedisHook();

    private RedisHook() {
        method_name.put("connect", null);
        class_name.add("java/net/HttpConnectSocketImpl");
    }

}
