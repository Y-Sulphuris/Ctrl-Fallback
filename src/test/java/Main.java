import com.ydo4ki.ctrlf.MethodSequence;
import com.ydo4ki.ctrlf.SearchMode;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

/**
 * @author Sulphuris
 * @since 12.10.2024 20:54
 */
public class Main {
	private static final MethodHandle caller = MethodSequence.of(MethodType.methodType(Class.class, int.class), SearchMode.findStatic)
			.find("sun.reflect.Reflection", "getCallerClass") // priority 1
			.orElse("com.ydo4ki.Callers", "getCallerClass") // priority 2 (if 1 not found)
			.fallback("com.ydo4ki.CallersSlow", "getCallerClass") // lowest priority
			.methodHandle(); // will throw exception if nothing found
	
	private static final MethodHandle callerDefaultName = MethodSequence.of(MethodType.methodType(Class.class, int.class), SearchMode.findStatic).withName("getCallerClass")
			.find("sun.reflect.Reflection")
			.orElse("com.ydo4ki.Callers")
			.fallback("com.ydo4ki.CallersSlow")
			.methodHandle();
	
	public static void main(String[] args) {
	
	}
}


