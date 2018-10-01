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
        if (name.equals("sqlQueryDirect")) {
            return new MethodAdviceAdapter(Opcodes.ASM5, mv, access, name, descriptor);
        }
        return mv;
    }

    @Override
    public void visitEnd() {

        {
            FieldVisitor fv = super.visitField(ACC_PRIVATE, "interMethod", "Lcom/mysql/jdbc/ResultSetInternalMethods;", null, null);
            fv.visitEnd();
        }
        {
            MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "setSetMethod", "(Lcom/mysql/jdbc/ResultSetInternalMethods;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, "com/mysql/jdbc/MysqlIO", "interMethod", "Lcom/mysql/jdbc/ResultSetInternalMethods;");
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        {
            MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "getRowData", "()Ljava/lang/Integer;", null, null);
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 0);

            mv.visitInsn(ACONST_NULL);
            mv.visitFieldInsn(PUTFIELD, "com/mysql/jdbc/MysqlIO", "interMethod", "Lcom/mysql/jdbc/ResultSetInternalMethods;");

            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mv.visitInsn(ARETURN);

            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        super.visitEnd();
    }
}
