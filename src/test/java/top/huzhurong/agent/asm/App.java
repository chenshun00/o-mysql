package top.huzhurong.agent.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import java.io.IOException;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/11
 */
public class App {
    public static void main(String[] args) throws IOException {
        //创建一个ClassVisitor 用来处理字节码，主要就是事件的处理，因为asm的事件读取机制
        //每读取一个元素都会出发一个事件，ClassPrint继承了ClassVisitor用来处理这个读取机制，
        // 并且对这些字节码做一些修改就是ClassVisitor对使命了
        ClassPrint cp = new ClassPrint(Opcodes.ASM5);
        //类读取器，这里主要就是读取类的信息(全类名，InputStream，byte[])都是可以的，ClassReader会在accept之后顺序读取
        //类信息然后进行，每读取一个数据之后就会触发 ClassVisitor 中实现的事件
        ClassReader cr = new ClassReader("java.lang.Runnable");
        /*
         * Makes the given visitor visit the Java class of this {@link ClassReader}
         * . This class is the one specified in the constructor (see
         * {@link #ClassReader(byte[]) ClassReader}).
         *
         * @param classVisitor
         *            the visitor that must visit this class.
         * @param flags
         *            option flags that can be used to modify the default behavior
         *            of this class. See {@link #SKIP_DEBUG}, {@link #EXPAND_FRAMES}
         *            , {@link #SKIP_FRAMES}, {@link #SKIP_CODE}.
         */
        cr.accept(cp, 0);
    }
}
