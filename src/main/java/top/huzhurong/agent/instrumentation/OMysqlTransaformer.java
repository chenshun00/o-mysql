package top.huzhurong.agent.instrumentation;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import top.huzhurong.agent.hook.MysqlHookVisitor;
import top.huzhurong.agent.inter.ResultSet;
import top.huzhurong.agent.inter.RowData;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/9/29
 */
public class OMysqlTransaformer implements ClassFileTransformer {

    private Instrumentation instrumentation;

    public OMysqlTransaformer(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className.equals("com.mysql.jdbc.PreparedStatement".replace(".", "/"))) {
            ClassReader classReader = new ClassReader(classfileBuffer);
            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
            classReader.accept(new MysqlHookVisitor(Opcodes.ASM5, classWriter, null), ClassReader.EXPAND_FRAMES);
            writeToFile(classWriter, className);
            classfileBuffer = classWriter.toByteArray();
        }

        if (className.equals("com.mysql.jdbc.ResultSetImpl".replace(".", "/"))) {
            ClassReader classReader = new ClassReader(classfileBuffer);
            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
            classReader.accept(new MysqlHookVisitor(Opcodes.ASM5, classWriter, ResultSet.class), ClassReader.EXPAND_FRAMES);
            writeToFile(classWriter, className);
            classfileBuffer = classWriter.toByteArray();
        }

        if (className.equals("com/mysql/jdbc/RowDataStatic")) {
            ClassReader classReader = new ClassReader(classfileBuffer);
            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
            classReader.accept(new MysqlHookVisitor(Opcodes.ASM5, classWriter, RowData.class), ClassReader.EXPAND_FRAMES);
            writeToFile(classWriter, className);
            classfileBuffer = classWriter.toByteArray();
        }

        return classfileBuffer;
    }


    private void writeToFile(ClassWriter classWriter, String name) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(name.replaceAll("/", ".") + ".class");
            fileOutputStream.write(classWriter.toByteArray());
            fileOutputStream.close();
        } catch (IOException ignore) {

        }
    }
}
