package top.huzhurong.agent.instrumentation;

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
        return new byte[0];
    }
}
