package top.huzhurong.agent.hook.sub;

import top.huzhurong.agent.hook.BaseHook;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/10/18
 */
public class TestHook extends BaseHook {

    public static final TestHook TEST_HOOK = new TestHook();

    private TestHook() {
        method_name.put("connect", null);
        class_name.add("java/net/HttpConnectSocketImpl");
    }

    @Override
    public void into(Object curObject, Object[] args) {
        System.out.println("进入");
    }


}
