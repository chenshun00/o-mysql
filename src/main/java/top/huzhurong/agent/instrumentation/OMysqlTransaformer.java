package top.huzhurong.agent.instrumentation;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import top.huzhurong.agent.annotation.InjectInterface;
import top.huzhurong.agent.asm.AgentHookVisitor;
import top.huzhurong.agent.asm.TraceClassWriter;
import top.huzhurong.agent.hook.BaseHook;
import top.huzhurong.agent.hook.sub.MysqlHook;
import top.huzhurong.agent.hook.sub.TestHook;
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

    static {
        hooks.add(MysqlHook.Instance);

        for (BaseHook hook : hooks) {
            for (String name : hook.getClassName()) {
                inject_hooks.put(name.replace("\\.", "/"), hook);
            }
        }


        annotations.add(ResultSet.class);
        annotations.add(RowData.class);

        for (Class annotation : annotations) {
            if (annotation.isAnnotationPresent(InjectInterface.class)) {
                InjectInterface ii = (InjectInterface) annotation.getAnnotation(InjectInterface.class);
                String[] value = ii.value();
                for (String target : value) {
                    injectInterface.put(target.replace("\\.", "/"), annotation);
                }
            }
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        //hook 注入
        if (inject_hooks.containsKey(className)) {
            ClassReader classReader = new ClassReader(classfileBuffer);
            ClassWriter classWriter = new TraceClassWriter(classReader, ClassWriter.COMPUTE_MAXS, loader);
            classReader.accept(new AgentHookVisitor(Opcodes.ASM5, classWriter, injectInterface.get(className), inject_hooks.get(className)),
                    ClassReader.EXPAND_FRAMES);
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

}
