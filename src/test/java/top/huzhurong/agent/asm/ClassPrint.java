package top.huzhurong.agent.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * 在分析一个已经存在的类时，惟一必需的组件是 ClassReader 组件，使用ClassReader读取二进制类信息，然后visit，在读取信息的时候进行操作即可
 * <p>
 * 类信息
 * byte[] b1 = ...;
 * //读取未处理之前的类信息
 * ClassReader cr = new ClassReader(b1);
 * //用于生成类，应该是一点一点的将字节数组写入到一个总的数组中
 * ClassWriter cw = new ClassWriter(0);
 * <p>
 * // cv 将所有事件转发给 将为处理完之后形成的二进制数组转发给cw
 * ClassVisitor cv = new ClassVisitor(ASM4, cw) { };
 * //开始处理
 * cr.accept(cv, 0);
 * //最后的数组
 * byte[] b2 = cw.toByteArray(); // b2 与 b1 表示同一个类
 *
 * @author chenshun00@gmail.com
 * @since 2018/12/7
 */
public class ClassPrint extends ClassVisitor {

    public ClassPrint(int api) {
        super(api);
    }

    public ClassPrint(int i, ClassVisitor classVisitor) {
        super(i, classVisitor);
    }

    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        System.out.println(name + " extends " + superName + " {");
    }

    @Override
    public AnnotationVisitor visitAnnotation(String s, boolean b) {
        return super.visitAnnotation(s, b);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc,
                                   String signature, Object value) {
        System.out.println(" " + desc + " " + name);
        return super.visitField(access, name, desc, signature, value);
    }


    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        System.out.println(" " + name + desc);
        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        System.out.println("}");
        super.visitEnd();
    }
}
