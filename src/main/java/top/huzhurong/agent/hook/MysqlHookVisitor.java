package top.huzhurong.agent.hook;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/9/29
 */
public class MysqlHookVisitor extends ClassVisitor {

    private Class<?> interfaceName;

    public MysqlHookVisitor(int api, ClassWriter classWriter, Class<?> interfaceName) {
        super(api, classWriter);
        this.interfaceName = interfaceName;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if (interfaceName == null) {
            super.visit(version, access, name, signature, superName, interfaces);
        } else {
            Set<String> set = new TreeSet<>(Arrays.asList(interfaces));
            set.add(interfaceName.getName().replace(".", "/"));
            String[] strings = set.toArray(new String[0]);
            super.visit(version, access, name, signature, superName, strings);
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
        if (name.equals("sqlQueryDirect")) {
            return new MethodAdviceAdapter(Opcodes.ASM5, mv, access, name, descriptor);
        }
        return mv;
    }
}
