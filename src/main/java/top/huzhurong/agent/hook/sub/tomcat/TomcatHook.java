package top.huzhurong.agent.hook.sub.tomcat;

import top.huzhurong.agent.hook.BaseHook;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/1
 */
public class TomcatHook extends BaseHook {

    public static TomcatHook instance = new TomcatHook();

    private TomcatHook() {
        class_name.add("");
        method_name.put("", "");
    }

}
