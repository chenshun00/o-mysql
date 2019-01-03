package top.huzhurong.agent.asm;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import top.huzhurong.agent.hook.BaseHook;
import top.huzhurong.agent.hook.HookUtils;

/**
 * 调用方法的原则是先将参数入栈，然后在调用方法的时候一个一个的将参数从操作数栈中弹出，同时在方法的底部，即return的前一个指令，必然是返回值
 * 1、方法参数入栈，例如dup(),加入其他参数，ldc都可以
 * 2、调用方法 invoke，根据方法的描述符去栈顶拿参数
 *
 * @author chenshun00@gmail.com
 * @since 2018/9/29
 */
public class MethodAdviceAdapter extends AdviceAdapter {

    private long key;
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
    MethodAdviceAdapter(int api, MethodVisitor methodVisitor, int access, String name, String descriptor, BaseHook baseHook) {
        super(api, methodVisitor, access, name, descriptor);
        key = HookUtils.hookKey(baseHook);
    }

    @Override
    public void visitCode() {
        mv.visitCode();
        mv.visitLabel(start);
        insertParameter();
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, HookUtils.CLASS_NAME, HookUtils.ENTER_METHOD_NAME, HookUtils.ENTER_METHOD_DESC, false);
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
    }

    /**
     * opcode 代表的指令是方法返回的值，例如 RETURN 是void的返回值
     * dup 复制栈顶数值，并压入栈顶,在方法的最后，栈顶元素是返回值了
     *
     * @param opcode one of {@link Opcodes#RETURN}, {@link Opcodes#IRETURN}, {@link Opcodes#FRETURN},
     *               {@link Opcodes#ARETURN}, {@link Opcodes#LRETURN}, {@link Opcodes#DRETURN} or {@link
     */
    @Override
    public void onMethodExit(int opcode) {
        if (opcode == ATHROW) {
            return;
        }
        if (opcode == RETURN) {
            visitInsn(ACONST_NULL);
        } else if (opcode == ARETURN) {
            dup();
        } else {
            //long double 类型是2个嘈 slot
            if ((opcode == LRETURN) || (opcode == DRETURN)) {
                dup2();
            } else {
                dup();
            }
            box(Type.getReturnType(this.methodDesc));
        }
        insertParameter();
        mv.visitMethodInsn(INVOKESTATIC, HookUtils.CLASS_NAME,
                HookUtils.END_METHOD_NAME, HookUtils.END_METHOD_DESC, false);
    }

    /**
     * 最后包装一下异常，有可能开发人员会自己处理这个异常，重新将异常抛出
     */
    @Override
    public void visitEnd() {
        mv.visitLabel(end);
        mv.visitTryCatchBlock(start, end, end, null);
        mv.visitInsn(DUP);
        insertParameter();
        mv.visitMethodInsn(INVOKESTATIC, HookUtils.CLASS_NAME,
                HookUtils.ERROR_METHOD_NAME, HookUtils.ERROR_METHOD_DESC, false);
        //未捕获的异常会进入这里
        mv.visitInsn(ATHROW); // 重新把异常抛出
        mv.visitEnd();
    }

    private void insertParameter() {
        mv.visitLdcInsn(key);
        //注入非static方法 参考Modifier#isStatic
        if ((this.methodAccess & 0x00000008) == 0) {
            loadThis();
        } else {
            mv.visitInsn(Opcodes.ACONST_NULL);
        }
        int size = Type.getArgumentTypes(this.methodDesc).length;
        if (size > 0) {
            loadArgArray();
        } else {
            mv.visitInsn(Opcodes.ACONST_NULL);
        }
    }

    public void visitMaxs(int maxStack, int maxLocals) {
        if (maxStack == 0) {
            super.visitMaxs(11, maxLocals + 1);
        } else {
            super.visitMaxs(maxStack + 10, maxLocals + 1);
        }
    }
}
