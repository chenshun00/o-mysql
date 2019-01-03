package top.huzhurong.agent.hook.sub.memcache;

import top.huzhurong.agent.hook.BaseHook;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/1
 */
public class MemcacheHook extends BaseHook {
    public static final MemcacheHook TEST_HOOK = new MemcacheHook();

    private MemcacheHook() {
        method_name.put("connect", null);
        class_name.add("java/net/HttpConnectSocketImpl");
    }
}
