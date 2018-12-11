package top.huzhurong.agent.asm.write;

import org.objectweb.asm.ClassWriter;

import java.io.FileOutputStream;
import java.io.IOException;

import static org.objectweb.asm.Opcodes.*;

/**
 * 为生成一个类，惟一必需的组件是 ClassWriter 组件
 * 这里是直接让ClassWriter生成,感受下asm的力量，不过一般来说
 * 都是外部传递类的字节码信息进来byte[]，我们自定义一个visitor处理业务，
 * 定义一个ClassWriter处理visitor业务留下来的字节码并且返回出去给jvm用
 *
 * @author chenshun00@gmail.com
 * @since 2018/12/11
 */
public class Writer {
    public static void main(String[] args) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classWriter.visit(V1_5, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE,
                "top.huzhurong.agent.asm.write/Comparable".replaceAll("\\.", "./"), null, "java/lang/Object",
                null);
        classWriter.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "LESS", "I",
                null, -1).visitEnd();
        classWriter.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "EQUAL", "I",
                null, 0).visitEnd();
        classWriter.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "GREATER", "I",
                null, 1).visitEnd();
        classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "compareTo",
                "(Ljava/lang/Object;)I", null, null).visitEnd();
        classWriter.visitEnd();
        //生成的类信息
        byte[] classInfo = classWriter.toByteArray();

        try {
            FileOutputStream fileOutputStream = new FileOutputStream("Comparable.class");
            fileOutputStream.write(classInfo);
            fileOutputStream.close();
        } catch (IOException ignore) {

        }
    }
}
