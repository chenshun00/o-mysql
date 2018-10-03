package top.huzhurong.agent.hook;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/9/29
 */
public class MethodAdviceAdapter extends AdviceAdapter {

    private Label start = new Label();// 方法方法字节码开始位置
    private Label end = new Label();// 方法方法字节码结束位置

    /**
     * Constructs a new {@link AdviceAdapter}.
     *
     * @param api           the ASM API version implemented by this visitor. Must be one of {@link
     *                      Opcodes#ASM4}, {@link Opcodes#ASM5}, {@link Opcodes#ASM6} or {@link Opcodes#ASM7}.
     * @param methodVisitor the method visitor to which this adapter delegates calls.
     * @param access        the method's access flags (see {@link Opcodes}).
     * @param name          the method's name.
     * @param descriptor    the method's descriptor (see {@link Type Type}).
     */
    protected MethodAdviceAdapter(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(api, methodVisitor, access, name, descriptor);
    }

    @Override
    public void visitCode() {
        mv.visitCode();
        mv.visitLabel(start);
        insertParameter();
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, ClassHook.CLASS_NAME, ClassHook.ENTER_METHOD_NAME, ClassHook.ENTER_METHOD_DESC, false);
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
    }

    @Override
    public void onMethodExit(int opcode) {
        if (opcode == ATHROW) {
            return;
        }
        if (opcode == RETURN) {
            visitInsn(1);
        } else if (opcode == ACONST_NULL) {
            //返回的是对象
            dup();
        } else {
            if ((opcode == LRETURN) || (opcode == DRETURN)) {
                dup2();
            } else {
                dup();
            }
            box(Type.getReturnType(this.methodDesc));
        }
        insertParameter();
        mv.visitMethodInsn(INVOKESTATIC, ClassHook.CLASS_NAME,
                ClassHook.END_METHOD_NAME, ClassHook.END_METHOD_DESC, false);
    }

    @Override
    public void visitEnd() {
        mv.visitLabel(end);
        mv.visitTryCatchBlock(start, end, end, null);
        mv.visitInsn(DUP);
        insertParameter();
        mv.visitMethodInsn(INVOKESTATIC, ClassHook.CLASS_NAME,
                ClassHook.END_METHOD_NAME, ClassHook.END_METHOD_DESC, false);
        //未捕获的异常会进入这里
        mv.visitInsn(ATHROW); // 重新把异常抛出
        mv.visitEnd();
    }

    private void insertParameter() {
        //目标类是static
        if ((this.methodAccess & 0x8) == 0) {
            loadThis();
        } else {
            //null
            mv.visitInsn(Opcodes.ACONST_NULL);
        }
        int size = Type.getArgumentTypes(this.methodDesc).length;
        if (size > 0) {
            loadArgArray();
        } else {
            mv.visitInsn(Opcodes.ACONST_NULL);
        }
    }

}
