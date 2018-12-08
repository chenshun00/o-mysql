package top.huzhurong.agent.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/7
 */
public class ClassPrint extends ClassVisitor {

    public ClassPrint(int i, ClassVisitor classVisitor) {
        super(i, classVisitor);
    }

    @Override
    public void visit(int i, int i1, String s, String s1, String s2, String[] strings) {
        super.visit(i, i1, s, s1, s2, strings);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String s, boolean b) {
        return super.visitAnnotation(s, b);
    }

    @Override
    public FieldVisitor visitField(int i, String s, String s1, String s2, Object o) {
        return super.visitField(i, s, s1, s2, o);
    }


    @Override
    public MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings) {
        return super.visitMethod(i, s, s1, s2, strings);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
