package top.huzhurong.agent.hook;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/10/13
 */
public abstract class BaseHook {

    /**
     * 进入方法
     *
     * @param curObject 调用该方法的对象，如果方法是静态的那么为null
     * @param args      参数，如果没有参数，那么为null
     */
    public void into(Object curObject, Object[] args) {
        if (args == null || args.length == 0) {
            return;
        }
    }


    /**
     * 出去方法
     *
     * @param curObject 调用该方法的对象，如果方法是静态的那么为null
     * @param retObject 返回值，如果没有返回值，那么是null
     * @param args      参数，如果没有参数，那么为null
     */
    public void out(Object curObject, Object retObject, Object[] args) {

    }


    /**
     * 执行方法出现异常
     *
     * @param curObject 调用该方法的对象，如果方法是静态的那么为null
     * @param ex        执行方法出现的异常(该异常需要重新抛出，上层应用可能会获取该异常并且进行处理)
     * @param args      参数，如果没有参数，那么为null
     */
    public void error(Object curObject, Throwable ex, Object[] args) {

    }

}
