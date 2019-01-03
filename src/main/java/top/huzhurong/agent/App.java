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
 * @author chenshun00@gmail.com
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
        if (instrumentation == null) {
            System.out.println("o-mysql agent init ");
            //从class获取jar包名称
            if (App.class.getClassLoader() instanceof URLClassLoader) {
                ClassLoader classLoader = App.class.getClassLoader();
                URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
                URL[] urls = urlClassLoader.getURLs();
                for (URL url : urls) {
                    String protocol = url.getProtocol();
                    String pkgPath = url.getPath();
                    if (protocol.equals("jar")) {
                        JarURLConnection urlConnection = (JarURLConnection) url.openConnection();
                        JarFile jarFile = urlConnection.getJarFile();
                        if (jarFile.getEntry(App.class.getName().replaceAll("\\.", "/") + ".class") != null) {
                            System.out.println("agent appendToBootstrapClassLoaderSearch success");
                            //inst.appendToBootstrapClassLoaderSearch(jarFile);
                            inst.appendToSystemClassLoaderSearch(jarFile);
                            break;
                        }
                    }
                    if (protocol.equals("file")) {
                        File file = new File(pkgPath);
                        if (!file.isDirectory() && file.exists()) {
                            JarFile jarFile = new JarFile(file);
                            if (jarFile.getEntry(App.class.getName().replaceAll("\\.", "/") + ".class") != null) {
                                System.out.println("agent appendToBootstrapClassLoaderSearch success");
                                //inst.appendToBootstrapClassLoaderSearch(jarFile);
                                inst.appendToSystemClassLoaderSearch(jarFile);
                                break;
                            }
                        }
                    }
                }
            }
        }
        Timer.init();
        instrumentation = inst;
        System.out.println("classLoader\t"+App.class.getClassLoader());
        System.out.println("thread\t"+Thread.currentThread().getContextClassLoader());
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
        System.out.println(System.getProperty("java.class.path"));
    }
}
