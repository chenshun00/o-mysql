package top.huzhurong.agent.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注入的接口
 *
 * @author chenshun00@gmail.com
 * @since 2018/10/13
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface InjectInterface {

    /**
     * 被注入的接口 @InjectInterface({"接口1","接口2","接口3"})
     */
    String[] value();

}
