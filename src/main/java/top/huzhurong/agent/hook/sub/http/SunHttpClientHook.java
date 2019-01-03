package top.huzhurong.agent.hook.sub.http;

import top.huzhurong.agent.hook.BaseHook;

/**
 * @author chenshun00@gmail.com
 * @since 2018/11/28
 */
public class SunHttpClientHook extends BaseHook {
    public static final SunHttpClientHook Instance = new SunHttpClientHook();

    private SunHttpClientHook() {
        class_name.add("sun.net.www.protocol.http.HttpURLConnection".replaceAll("\\.", "/"));
        class_name.add("sun.net.www.protocol.https.AbstractDelegateHttpsURLConnection".replaceAll("\\.", "/"));
        method_name.put("plainConnect", "()V");
    }


    @Override
    public void out(Object ret, Object cur, long execTime, Object[] args) {
        super.out(ret, cur, execTime, args);
        System.out.println("执行耗时:" + execTime + "ms");
    }
}
