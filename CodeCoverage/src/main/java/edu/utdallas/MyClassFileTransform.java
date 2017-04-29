package edu.utdallas;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import edu.utdallas.util.Helper;

class MyClassFileTransform implements ClassFileTransformer {

	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		if(className.contains("org/ahocorasick") || className.contains("org/apache/commons") || className.contains("org/whispersystems/bithub/tests") || className.contains("ru/yandex/qatools") || className.contains("io/gsonfire")|| className.contains("net/sourceforge/argparse4j")){
			ClassReader cr = new ClassReader(classfileBuffer);
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
			ClassTransformVisitor ca = new ClassTransformVisitor(cw);
			cr.accept(ca, 0);
			return cw.toByteArray();
		}
		return classfileBuffer;
	}
}
