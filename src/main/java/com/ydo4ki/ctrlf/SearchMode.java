package com.ydo4ki.ctrlf;

import java.lang.invoke.*;

/**
 * Used to specify method of {@code MethodHandles.Lookup} used to find methods
 * @author Sulphuris
 * @since 12.10.2024 21:06
 */
public abstract class SearchMode {
	
	
	/**
	 * Search mode that searches for static methods (linked to {@link MethodHandles.Lookup#findStatic(Class, String, MethodType)})
	 */
	public static final GenericSearch findStatic = new FindStatic();
	/**
	 * Search mode that searches for instance methods (linked to {@link MethodHandles.Lookup#findVirtual(Class, String, MethodType)})
	 */
	public static final GenericSearch findVirtual = new FindVirtual();
	/**
	 * Search mode that binds method to specified owner instance (linked to {@link MethodHandles.Lookup#bind(Object, String, MethodType)})
	 */
	public static final Bind bind = new Bind();
	/**
	 * Search mode that searches for instance field getter (linked to {@link MethodHandles.Lookup#findGetter(Class, String, Class)})
	 */
	public static final FieldAccessor findGetter = new FindGetter();
	/**
	 * Search mode that searches for instance field setter (linked to {@link MethodHandles.Lookup#findSetter(Class, String, Class)})
	 */
	public static final FieldAccessor findSetter = new FindSetter();
	/**
	 * Search mode that searches for static field getter (linked to {@link MethodHandles.Lookup#findStaticGetter(Class, String, Class)})
	 */
	public static final FieldAccessor findStaticGetter = new FindStaticGetter();
	/**
	 * Search mode that searches for static field setter (linked to {@link MethodHandles.Lookup#findStaticSetter(Class, String, Class)})
	 */
	public static final FieldAccessor findStaticSetter = new FindStaticSetter();
	/**
	 * Search mode that searches for constructors (linked to {@link MethodHandles.Lookup#findConstructor(Class, MethodType)})
	 */
	public static final FindConstructor findConstructor = new FindConstructor();
	/**
	 * Search mode that searches for special call of virtual methods (linked to {@link MethodHandles.Lookup#findSpecial(Class, String, MethodType, Class)})
	 */
	public static final FindSpecial findSpecial = new FindSpecial();
	
	SearchMode() {
	
	}
	
	public static abstract class GenericSearch extends SearchMode {
		/**
		 * Finds a method with the given name in the given class
		 * @param sequence sequence to search in
		 * @param refc the class to search in
		 * @param name method name
		 * @return a method handle that invokes the given static/instance method
		 * @throws NoSuchMethodException if there is no such method
		 */
		abstract MethodHandle findMethodHandle(MethodSequence<?> sequence, Class<?> refc, String name)
				throws NoSuchMethodException, IllegalAccessException;
	}

	static final class FindStatic extends GenericSearch {
	
		/**
		 * Finds a method with the given name in the given class
		 * @param sequence sequence to search in
		 * @param refc the class to search in
		 * @param name method name
		 * @return a method handle that invokes the given static method
		 * @throws NoSuchMethodException if there is no such method
		 */
		@Override
		public MethodHandle findMethodHandle(MethodSequence<?> sequence, Class<?> refc, String name)
				throws NoSuchMethodException, IllegalAccessException {
			return sequence.lookup.findStatic(refc, name, sequence.methodType);
		}

		FindStatic() {}
	}
	static final class FindVirtual extends GenericSearch {
		
		/**
		 * Finds a method with the given name in the given class
		 * @param sequence sequence to search in
		 * @param refc the class to search in
		 * @param name method name
		 * @return a method handle that invokes the given method
		 * @throws NoSuchMethodException if there is no such method
		 */
		@Override
		public MethodHandle findMethodHandle(MethodSequence<?> sequence, Class<?> refc, String name)
				throws NoSuchMethodException, IllegalAccessException {
			return sequence.lookup.findVirtual(refc, name, sequence.methodType);
		}

		FindVirtual() {}
	}
	public static final class Bind extends SearchMode {
		
		/**
		 * Finds a method with the given name in the given receiver
		 * @param sequence sequence to search in
		 * @param receiver the object to search in
		 * @param name method name
		 * @return a method handle that invokes the given method
		 * @throws NoSuchMethodException if there is no such method
		 * @throws IllegalAccessException if the method is not accessible
		 */
		public MethodHandle findBindMethodHandle(MethodSequence<?> sequence, Object receiver, String name)
				throws NoSuchMethodException, IllegalAccessException {
			return sequence.lookup.bind(receiver, name, sequence.methodType);
		}

		Bind() {}
	}

	public static abstract class FieldAccessor extends SearchMode {
		
		/**
		 * Finds a field accessor in the given class
		 * @param sequence the sequence object to work with
		 * @param refc the class to search in
		 * @param name the name of the field
		 * @param type the type of the field
		 * @return a method handle to the getter of the given field
		 * @throws NoSuchFieldException if no such field exists
		 * @throws IllegalAccessException if the field is not accessible
		 */
		public abstract MethodHandle findField(MethodSequence<?> sequence, Class<?> refc, String name, Class<?> type)
				throws NoSuchFieldException, IllegalAccessException;

		FieldAccessor() {}

		public abstract MethodType methodType(Class<?> fieldType);
	}

	static final class FindGetter extends FieldAccessor {
		
		/**
		 * Finds a getter method for the given field in the given class
		 * @param sequence the method sequence
		 * @param refc the owner class
		 * @param name the field name
		 * @param type the field type
		 * @return the getter method
		 * @throws NoSuchFieldException if no such field exists
		 * @throws IllegalAccessException if the getter is not accessible
		 */
		@Override
		public MethodHandle findField(MethodSequence<?> sequence, Class<?> refc, String name, Class<?> type)
				throws NoSuchFieldException, IllegalAccessException {
			return sequence.lookup.findGetter(refc, name, type);
		}

		public MethodType methodType(Class<?> fieldType) {
			return MethodType.methodType(fieldType);
		}
	}

	static final class FindStaticGetter extends FieldAccessor {
		
		/**
		 * Finds a static getter method for the given field in the given class
		 * @param sequence the method sequence
		 * @param refc the owner class
		 * @param name the field name
		 * @param type the field type
		 * @return the setter method
		 * @throws NoSuchFieldException if no such field exists
		 * @throws IllegalAccessException if the setter is not accessible
		 */
		@Override
		public MethodHandle findField(MethodSequence<?> sequence, Class<?> refc, String name, Class<?> type)
				throws NoSuchFieldException, IllegalAccessException {
			return sequence.lookup.findStaticGetter(refc, name, type);
		}

		public MethodType methodType(Class<?> fieldType) {
			return MethodType.methodType(fieldType);
		}
	}

	static final class FindSetter extends FieldAccessor {
		
		/**
		 * Finds a setter method for the given field in the given class
		 * @param sequence the method sequence
		 * @param refc the owner class
		 * @param name the field name
		 * @param type the field type
		 * @return the setter method
		 * @throws NoSuchFieldException if no such field exists
		 * @throws IllegalAccessException if the setter is not accessible
		 */
		@Override
		public MethodHandle findField(MethodSequence<?> sequence, Class<?> refc, String name, Class<?> type)
				throws NoSuchFieldException, IllegalAccessException {
			return sequence.lookup.findSetter(refc, name, type);
		}

		public MethodType methodType(Class<?> fieldType) {
			return MethodType.methodType(void.class, fieldType);
		}
	}

	static final class FindStaticSetter extends FieldAccessor {
		
		/**
		 * Finds a method handle for the given static setter in the given class
		 * @param sequence the method sequence
		 * @param refc the owner class
		 * @param name the field name
		 * @param type the field type
		 * @return the method handle
		 * @throws NoSuchFieldException if no such field exists
		 * @throws IllegalAccessException if the setter is not accessible
		 */
		@Override
		public MethodHandle findField(MethodSequence<?> sequence, Class<?> refc, String name, Class<?> type)
				throws NoSuchFieldException, IllegalAccessException {
			return sequence.lookup.findStaticSetter(refc, name, type);
		}

		public MethodType methodType(Class<?> fieldType) {
			return MethodType.methodType(void.class, fieldType);
		}
	}

	public static final class FindConstructor extends SearchMode {
		
		/**
		 * Finds a method handle for the given constructor in the given class
		 * @param sequence the method sequence
		 * @param refc the owner class
		 * @return a MethodHandle for the given constructor
		 * @throws NoSuchMethodException if the constructor is not found
		 * @throws IllegalAccessException if the constructor is not accessible
		 */
		public MethodHandle findConstructor(MethodSequence<?> sequence, Class<?> refc)
				throws NoSuchMethodException, IllegalAccessException {
			return sequence.lookup.findConstructor(refc, sequence.methodType);
		}

		FindConstructor() {}
	}


	public static final class FindSpecial extends SearchMode {
		/**
		 * Finds a method handle for the given name in the given class with special caller.
		 * @param sequence the method sequence
		 * @param refc the owner class
		 * @param name the method name
		 * @param specialCaller the special caller class name
		 * @return a MethodHandle for the given method
		 * @throws NoSuchMethodException if the method is not found
		 * @throws IllegalAccessException if the method is not accessible
		 */
		public MethodHandle findMethodHandle(MethodSequence<?> sequence, Class<?> refc, String name, Class<?> specialCaller)
				throws NoSuchMethodException, IllegalAccessException {
			return sequence.lookup.findSpecial(refc, name, sequence.methodType, specialCaller);
		}

		FindSpecial() {}
	}
}
