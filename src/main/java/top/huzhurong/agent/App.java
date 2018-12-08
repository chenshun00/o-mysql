package top.huzhurong.agent;

import top.huzhurong.agent.data.Timer;
import top.huzhurong.agent.instrumentation.OMysqlTransaformer;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/9/29
 */
public class App {
    private static Instrumentation instrumentation;

    public static void premain(String args, Instrumentation inst) throws Exception {
        initialize(args, inst);
    }

    public static void agentmain(String args, Instrumentation inst) throws Exception {
        initialize(args, inst);
    }

    public static void initialize(String args, Instrumentation inst) throws IOException {
        Timer.init();
        instrumentation = inst;
        OMysqlTransaformer oMysqlTransaformer = new OMysqlTransaformer();
        try {
            instrumentation.addTransformer(oMysqlTransaformer, true);
        } catch (Exception e) {
            System.err.println("open-jdk不支持对jdk自带的包做修改.这个错误可以忽略!");
            instrumentation.addTransformer(oMysqlTransaformer);
        } finally {
            oMysqlTransaformer.init();
        }
    }

    public static void main(String[] args) {
        ;
    }
}
