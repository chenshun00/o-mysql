package top.huzhurong.agent.instrumentation;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import top.huzhurong.agent.annotation.InjectInterface;
import top.huzhurong.agent.asm.AgentHookVisitor;
import top.huzhurong.agent.asm.TraceClassWriter;
import top.huzhurong.agent.hook.BaseHook;
import top.huzhurong.agent.hook.sub.MysqlHook;
import top.huzhurong.agent.hook.sub.http.SunHttpClientHook;
import top.huzhurong.agent.inter.sql.ResultSet;
import top.huzhurong.agent.inter.sql.RowData;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/9/29
 */
public class OMysqlTransaformer implements ClassFileTransformer {

    /**
     * className --> class
     */
    private static Map<String, Class<?>> injectInterface = new HashMap<>();
    private static Set<Class> annotations = new HashSet<>();

    private static Map<String, BaseHook> inject_hooks = new HashMap<>();
    private static Set<BaseHook> hooks = new HashSet<>();

    private static AtomicBoolean atomicBoolean = new AtomicBoolean(false);
    private static boolean init = false;

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (className.startsWith("java")) {
            return classfileBuffer;
        }
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader.getClass().getName().contains("WebappClassLoader")) {
            checkAgain(contextClassLoader);
        }
        //hook 注入
        if (inject_hooks.containsKey(className)) {
            //java类解析器
            ClassReader classReader = new ClassReader(classfileBuffer);
            //ClassWriter 以二进制形式生成编译后的类
            ClassWriter classWriter = new TraceClassWriter(classReader, ClassWriter.COMPUTE_MAXS, loader);
            AgentHookVisitor agentHookVisitor = new AgentHookVisitor(Opcodes.ASM5, classWriter, injectInterface.get(className), inject_hooks.get(className));
            classReader.accept(agentHookVisitor, ClassReader.EXPAND_FRAMES);
            writeToFile(classWriter, className);
            classfileBuffer = classWriter.toByteArray();
        }

        //接口注入
        if (injectInterface.containsKey(className)) {
            ClassReader classReader = new ClassReader(classfileBuffer);
            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
            classReader.accept(new AgentHookVisitor(Opcodes.ASM5, classWriter, injectInterface.get(className), null), ClassReader.EXPAND_FRAMES);
            writeToFile(classWriter, className);
            classfileBuffer = classWriter.toByteArray();
        }

        return classfileBuffer;
    }

    private void checkAgain(ClassLoader contextClassLoader) {
        if (init) {
            return;
        }
        init = true;
        System.out.println("开始初始化\t" + contextClassLoader);
        try {
            Class<?> aClass = Class.forName(MysqlHook.class.getName(), true, contextClassLoader);
            MysqlHook mysqlHook = (MysqlHook) aClass.newInstance();
            System.out.println("加入mysqlHook 成功");
            hooks.add(mysqlHook);


            annotations.add(Class.forName(ResultSet.class.getName(), true, contextClassLoader));
            annotations.add(Class.forName(RowData.class.getName(), true, contextClassLoader));
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            System.exit(1);
        }
        for (BaseHook hook : hooks) {
            for (String name : hook.getClassName()) {
                inject_hooks.put(name.replaceAll("\\.", "/"), hook);
            }
        }

        for (Class annotation : annotations) {
            if (annotation.isAnnotationPresent(InjectInterface.class)) {
                InjectInterface ii = (InjectInterface) annotation.getAnnotation(InjectInterface.class);
                String[] value = ii.value();
                for (String target : value) {
                    injectInterface.put(target.replaceAll("\\.", "/"), annotation);
                }
            }
        }
    }

    /**
     * @param classWriter 写入文件的二进制类
     * @param name        名字
     */
    private void writeToFile(ClassWriter classWriter, String name) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(name.replaceAll("/", ".") + ".class");
            fileOutputStream.write(classWriter.toByteArray());
            fileOutputStream.close();
        } catch (IOException ignore) {

        }
    }

    public void init() {
        System.out.println("init some thing");
    }
}
