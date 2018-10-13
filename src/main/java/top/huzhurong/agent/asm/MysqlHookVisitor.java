package top.huzhurong.agent.asm;

import org.objectweb.asm.*;

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
            //注入接口
            Set<String> set = new TreeSet<>(Arrays.asList(interfaces));
            set.add(interfaceName.getName().replace(".", "/"));
            String[] strings = set.toArray(new String[0]);
            super.visit(version, access, name, signature, superName, strings);
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
        if (name.equals("executeInternal") && access == (Opcodes.ACC_PROTECTED)) {
            return new MethodAdviceAdapter(Opcodes.ASM5, mv, access, name, descriptor);
        } else {
            return mv;
        }
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        if (name.equals("rowData")) {
            MethodVisitor mmv = cv.visitMethod(Opcodes.ACC_PUBLIC, "getASMRowData", "()Ltop/huzhurong/agent/inter/sql/RowData;", null, null);
            mmv.visitCode();
            mmv.visitVarInsn(Opcodes.ALOAD, 0);
            mmv.visitFieldInsn(Opcodes.GETFIELD, "com/mysql/jdbc/ResultSetImpl", name, "Lcom/mysql/jdbc/RowData;");
            mmv.visitInsn(Opcodes.ARETURN);
            mmv.visitMaxs(2, 1);
            mmv.visitEnd();
        }
        return super.visitField(access, name, descriptor, signature, value);
    }

    //内部类
    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
