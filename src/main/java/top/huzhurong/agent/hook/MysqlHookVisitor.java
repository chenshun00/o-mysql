package top.huzhurong.agent.hook;

import org.objectweb.asm.*;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import static org.objectweb.asm.Opcodes.*;

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
        if (name.equals("executeInternal") && access == (Opcodes.ACC_PROTECTED)) {
            return new MethodAdviceAdapter(Opcodes.ASM5, mv, access, name, descriptor);
        }
        return mv;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        if (name.equals("rowData")) {
            MethodVisitor mmv = cv.visitMethod(Opcodes.ACC_PUBLIC, "getASMRowData", "()Ltop/huzhurong/agent/inter/RowData;", null, null);

            mmv.visitCode();
            mmv.visitVarInsn(Opcodes.ALOAD, 0);
            mmv.visitFieldInsn(Opcodes.GETFIELD, "com/mysql/jdbc/ResultSetImpl", name, "Lcom/mysql/jdbc/RowData;");
            mmv.visitInsn(Opcodes.ARETURN);
            mmv.visitMaxs(2, 1);
            mmv.visitEnd();
        }
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public void visitEnd() {
//        {
//            MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "size", "()I", null, new String[]{"java/sql/SQLException"});
//            mv.visitCode();
//            mv.visitVarInsn(ALOAD, 0);
//            mv.visitFieldInsn(GETFIELD, "com/mysql/jdbc/PreparedStatement", "results", "Lcom/mysql/jdbc/ResultSetInternalMethods;");
//            mv.visitTypeInsn(CHECKCAST, "com/mysql/jdbc/JDBC4ResultSet");
//            mv.visitFieldInsn(GETFIELD, "com/mysql/jdbc/JDBC4ResultSet", "rowData", "Lcom/mysql/jdbc/RowData;");
//            mv.visitMethodInsn(INVOKEINTERFACE, "com/mysql/jdbc/RowData", "size", "()I", true);
//            mv.visitInsn(IRETURN);
//            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/sql/SQLException"});
//            mv.visitVarInsn(ASTORE, 1);
//            mv.visitInsn(ICONST_0);
//            mv.visitInsn(IRETURN);
//            mv.visitMaxs(1, 2);
//            mv.visitEnd();
//        }
        super.visitEnd();
    }
}
