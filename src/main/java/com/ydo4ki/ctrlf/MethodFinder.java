package com.ydo4ki.ctrlf;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleInfo;
import java.util.Optional;

/**
 * Used to find methods using method sequences
 *
 * @author Sulphuris
 * @since 12.10.2024 21:08
 */
public class MethodFinder<M extends MethodSequence<?>> {
	protected final M sequence;
	
	protected MethodHandle found = null;
	protected Object foundOwner;
	protected Throwable lastThrowable = null;
	
	MethodFinder(M sequence) {
		this.sequence = sequence;
	}
	
	
	/**
	 * @return the owner object of the found method, if found
	 */
	public Optional<Object> getFoundOwner() {
		return Optional.ofNullable(foundOwner);
	}
	
	/**
	 * @return the method handle of the found method, if found
	 */
	public Optional<MethodHandle> getFound() {
		return Optional.ofNullable(found);
	}
	
	
	public static class MethodFinderGeneric extends MethodFinder<MethodSequence.MethodSequenceGeneric> {
		protected String foundName;
		
		MethodFinderGeneric(MethodSequence.MethodSequenceGeneric sequence) {
			super(sequence);
		}
		
		/**
		 * If the method was not found, tries to find the method in the given owner class with the given name.
		 * If the method is not found, sets the last throwable to the exception that occurred.
		 *
		 * @param owner the owner class to search in
		 * @param name  the name of the method to search for
		 * @return this method finder
		 */
		public MethodFinderGeneric orElse(String owner, String name) {
			if (name.contains(".")) throw new IllegalArgumentException("Illegal method name: " + name);
			if (found == null) try {
				Class<?> refc = Class.forName(owner);
				found = sequence.searchMode.findMethodHandle(sequence, refc, name);
				foundOwner = refc;
				foundName = name;
			} catch (Exception e) {
				lastThrowable = e;
			}
			return this;
		}
		
		/**
		 * If the method was not found, tries to set the found method to the given one.
		 *
		 * @param mh the method handle to set as the found method
		 * @return the found method handle, or an empty result if the method type does not match
		 * @throws IllegalArgumentException if the method type of the given method handle does not match the expected method type
		 */
		public FoundMethod fallback(MethodHandle mh) {
			if (found == null) {
				if (!sequence.methodType.equals(mh.type()))
					throw new IllegalArgumentException("MethodTypes mismatch (" + mh.type() + ", expected: " + sequence.methodType + ")");
				MethodHandleInfo info = sequence.lookup.revealDirect(mh);
				found = mh;
				foundOwner = info.getDeclaringClass();
				foundName = info.getName();
			}
			return FoundMethod.found(foundOwner, foundName, found);
		}
		
		/**
		 * If the method was not found, tries to find the method in the given owner class with the given name.
		 * If the method is not found, returns a not found result with the last throwable.
		 *
		 * @param owner the owner class to search in
		 * @param name  the name of the method to search for
		 * @return the found method if found, or an empty result if the method was not found
		 */
		public FoundMethod.Named fallback(String owner, String name) {
			orElse(owner, name);
			if (found == null) return FoundMethod.notfound(foundOwner, foundName, lastThrowable);
			return FoundMethod.found(foundOwner, foundName, found);
		}
		
		/**
		 * Gets the name of the found method, or empty if the method was not found
		 *
		 * @return the name of the found method, or empty if the method was not found
		 */
		public Optional<String> getFoundName() {
			return Optional.ofNullable(foundName);
		}
	}
	
	public static class MethodFinderGenericNamed extends MethodFinderGeneric {
		MethodFinderGenericNamed(MethodSequence.MethodSequenceGenericNamed sequence) {
			super(sequence);
		}
		
		private String methodName() {
			return ((MethodSequence.MethodSequenceGenericNamed) sequence).methodName;
		}
		
		
		/**
		 * If the method was not found, tries to find the method in the given owner class with the name of the method as specified in the method sequence.
		 *
		 * @param owner the owner class to search in
		 * @return the found method if found, or an empty result if the method was not found
		 */
		public MethodFinderGenericNamed orElse(String owner) {
			return orElse(owner, methodName());
		}
		
		/**
		 * If the method was not found, tries to find the method in the given owner class with the given name.
		 *
		 * @param owner the owner class to search in
		 * @param name  the name of the method to search for
		 * @return this
		 */
		@Override
		public MethodFinderGenericNamed orElse(String owner, String name) {
			super.orElse(owner, name);
			return this;
		}
		
		/**
		 * If the method was not found, tries to find the method in the given owner class with the name of the method as specified in the method sequence.
		 * If the method is not found, returns a not found result with the last throwable.
		 *
		 * @param owner the owner class to search in
		 * @return the found method if found, or an empty result if the method was not found
		 */
		public FoundMethod fallback(String owner) {
			return fallback(owner, methodName());
		}
	}
	
	public static class MethodFinderGenericSpecifiedOwner extends MethodFinderGeneric {
		MethodFinderGenericSpecifiedOwner(MethodSequence.MethodSequenceGenericSpecifiedOwner sequence) {
			super(sequence);
		}
		
		private String owner() {
			return ((MethodSequence.MethodSequenceGenericSpecifiedOwner) sequence).owner;
		}
		
		
		/**
		 * If the method was not found, tries to find the method in the given class with the given name.
		 *
		 * @param name the name of the method to search for
		 * @return this
		 */
		public MethodFinderGenericSpecifiedOwner orElse(String name) {
			return orElse(owner(), name);
		}
		
		/**
		 * If the method was not found, tries to find the method in the given class with the given name.
		 *
		 * @param owner the owner class to search in
		 * @param name  the name of the method to search for
		 * @return this
		 */
		@Override
		public MethodFinderGenericSpecifiedOwner orElse(String owner, String name) {
			super.orElse(owner, name);
			return this;
		}
		
		/**
		 * If the method is not found, tries to find the method in the given owner class with the given name.
		 * If the method is not found, returns a not found result with the last throwable.
		 *
		 * @param name the name of the method to search for
		 * @return the found method if found, or an empty result if the method was not found
		 */
		public FoundMethod fallback(String name) {
			return fallback(owner(), name);
		}
	}
	
	
	public static class MethodFinderBind extends MethodFinder<MethodSequence.MethodSequenceBind> {
		protected String foundName;
		
		MethodFinderBind(MethodSequence.MethodSequenceBind sequence) {
			super(sequence);
		}
		
		/**
		 * If the method was not found, tries to find a bind method in the given object with the given name.
		 *
		 * @param receiver the object to search in
		 * @param name     the name of the method to search for
		 * @return this
		 */
		public MethodFinderBind orElse(Object receiver, String name) {
			if (name.contains(".")) throw new IllegalArgumentException("Illegal method name: " + name);
			if (found == null) try {
				found = sequence.searchMode.findBindMethodHandle(sequence, receiver, name);
				foundOwner = receiver;
				foundName = name;
			} catch (Exception e) {
				lastThrowable = e;
			}
			return this;
		}
		
		/**
		 * If the method is not found, tries to find a bind method in the given object with the given name.
		 * If the method is not found, returns a not found result with the last throwable.
		 *
		 * @param receiver the object to search in
		 * @param name     the name of the method to search for
		 * @return the found method if found, or an empty result if the method was not found
		 */
		public FoundMethod.Named fallback(Object receiver, String name) {
			orElse(receiver, name);
			if (found == null) return FoundMethod.notfound(foundOwner, foundName, lastThrowable);
			return FoundMethod.found(foundOwner, foundName, found);
		}
		
		/**
		 * Gets the name of the found method, or empty if the method was not found
		 *
		 * @return the name of the found method, or empty if the method was not found
		 */
		public Optional<String> getFoundName() {
			return Optional.ofNullable(foundName);
		}
	}
	
	public static class MethodFinderBindNamed extends MethodFinderBind {
		MethodFinderBindNamed(MethodSequence.MethodSequenceBindNamed sequence) {
			super(sequence);
		}
		
		private String methodName() {
			return ((MethodSequence.MethodSequenceBindNamed) sequence).methodName;
		}
		
		
		/**
		 * If the method is not found, tries to find a bind method in the given object with the default name.
		 * If the method is not found, returns a not found result with the last throwable.
		 *
		 * @param receiver the object to search in
		 * @return the found method if found, or an empty result if the method was not found
		 */
		public MethodFinderBindNamed orElse(Object receiver) {
			return orElse(receiver, methodName());
		}
		
		/**
		 * If the method is not found, tries to find a bind method in the given object with the given name.
		 * If the method is not found, returns a not found result with the last throwable.
		 *
		 * @param receiver the object to search in
		 * @param name     the name of the method to search for
		 * @return the found method if found, or an empty result if the method was not found
		 */
		@Override
		public MethodFinderBindNamed orElse(Object receiver, String name) {
			super.orElse(receiver, name);
			return this;
		}
		
		/**
		 * If the method was not found, tries to find the method in the given owner class with the default name.
		 * If the method is not found, returns a not found result with the last throwable.
		 *
		 * @param owner the owner class to search in
		 * @return the found method if found, or an empty result if the method was not found
		 */
		public FoundMethod fallback(String owner) {
			return fallback(owner, methodName());
		}
	}
	
	
	public static class MethodFinderSpecial extends MethodFinder<MethodSequence.MethodSequenceSpecial> {
		protected String foundName;
		
		MethodFinderSpecial(MethodSequence.MethodSequenceSpecial sequence) {
			super(sequence);
		}
		
		/**
		 * If the method was not found, tries to find a special method with the given name in the given owner class with the given special caller.
		 * If the method is not found, sets the last throwable to the exception that occurred.
		 *
		 * @param owner         the owner class to search in
		 * @param name          the name of the method to search for
		 * @param specialCaller the special caller class name
		 * @return this method finder
		 */
		public MethodFinderSpecial orElse(String owner, String name, String specialCaller) {
			if (name.contains(".")) throw new IllegalArgumentException("Illegal method name: " + name);
			if (found == null) try {
				Class<?> refc = Class.forName(owner);
				Class<?> caller = Class.forName(specialCaller);
				found = sequence.searchMode.findMethodHandle(sequence, refc, name, caller);
				foundOwner = refc;
				foundName = name;
			} catch (Exception e) {
				lastThrowable = e;
			}
			return this;
		}
		
		
		/**
		 * If the method was not found, tries to find a special method with the given name in the given owner class with the given special caller.
		 * If the method is not found, returns a not found result with the last throwable.
		 *
		 * @param owner         the owner class to search in
		 * @param name          the name of the method to search for
		 * @param specialCaller the special caller class name
		 * @return the found method if found, or a not found result with the last throwable if the method was not found
		 */
		public FoundMethod.Named fallback(String owner, String name, String specialCaller) {
			orElse(owner, name, specialCaller);
			
			if (found == null) return FoundMethod.notfound(foundOwner, foundName, lastThrowable);
			return FoundMethod.found(foundOwner, foundName, found);
		}
		
		/**
		 * Gets the name of the found special method, or empty if the method was not found
		 *
		 * @return the name of the found special method, or empty if the method was not found
		 */
		public Optional<String> getFoundName() {
			return Optional.ofNullable(foundName);
		}
	}
	
	public static class MethodFinderSpecialNamed extends MethodFinderSpecial {
		MethodFinderSpecialNamed(MethodSequence.MethodSequenceSpecialNamed sequence) {
			super(sequence);
		}
		
		private String methodName() {
			return ((MethodSequence.MethodSequenceSpecialNamed) sequence).methodName;
		}
		
		/**
		 * If the method was not found, tries to find a special method with the default name in the given owner class with the given special caller.
		 * If the method is not found, sets the last throwable to the exception that occurred.
		 *
		 * @param owner         the owner class to search in
		 * @param specialCaller the special caller class name
		 * @return this method finder
		 */
		public MethodFinderSpecialNamed orElse(String owner, String specialCaller) {
			return orElse(owner, methodName(), specialCaller);
		}
		
		/**
		 * If the method was not found, tries to find a special method with the given name in the given owner class with the given special caller.
		 * If the method is not found, sets the last throwable to the exception that occurred.
		 *
		 * @param owner         the owner class to search in
		 * @param name          the name of the special method to search for
		 * @param specialCaller the special caller class name
		 * @return this method finder
		 */
		@Override
		public MethodFinderSpecialNamed orElse(String owner, String name, String specialCaller) {
			super.orElse(owner, name, specialCaller);
			return this;
		}
		
		/**
		 * If the method was not found, tries to find a special method with the default name in the given owner class with the given special caller.
		 * If the method is not found, returns a not found result with the last throwable.
		 *
		 * @param owner         the owner class to search in
		 * @param specialCaller the special caller class name
		 * @return the found method if found, or a not found result with the last throwable if the method was not found
		 */
		public FoundMethod fallback(String owner, String specialCaller) {
			return fallback(owner, methodName(), specialCaller);
		}
	}
	
	public static class MethodFinderSpecialSpecifiedOwner extends MethodFinderSpecial {
		MethodFinderSpecialSpecifiedOwner(MethodSequence.MethodSequenceSpecialSpecifiedOwner sequence) {
			super(sequence);
		}
		
		private String owner() {
			return ((MethodSequence.MethodSequenceSpecialSpecifiedOwner) sequence).owner;
		}
		
		
		/**
		 * If the method was not found, tries to find a special method with the given name in the default owner class with the given special caller.
		 * If the method is not found, sets the last throwable to the exception that occurred.
		 *
		 * @param name          the name of the special method to search for
		 * @param specialCaller the special caller class name
		 * @return this method finder
		 */
		public MethodFinderSpecialSpecifiedOwner orElse(String name, String specialCaller) {
			return orElse(owner(), name, specialCaller);
		}
		
		/**
		 * If the method was not found, tries to find a special method with the given name in the given owner class with the given special caller.
		 * If the method is not found, sets the last throwable to the exception that occurred.
		 *
		 * @param owner         the owner class to search in
		 * @param name          the name of the special method to search for
		 * @param specialCaller the special caller class name
		 * @return this method finder
		 */
		@Override
		public MethodFinderSpecialSpecifiedOwner orElse(String owner, String name, String specialCaller) {
			super.orElse(owner, name, specialCaller);
			return this;
		}
		
		/**
		 * If the method was not found, tries to find a special method with the given name in the default owner class with the given special caller.
		 * If the method is not found, returns a not found result with the last throwable.
		 *
		 * @param name          the name of the special method to search for
		 * @param specialCaller the special caller class name
		 * @return the found method if found, or a not found result with the last throwable if the method was not found
		 */
		public FoundMethod fallback(String name, String specialCaller) {
			return fallback(owner(), name, specialCaller);
		}
	}
	
	public static class MethodFinderSpecialSpecifiedCaller extends MethodFinderSpecial {
		MethodFinderSpecialSpecifiedCaller(MethodSequence.MethodSequenceSpecialSpecifiedCaller sequence) {
			super(sequence);
		}
		

		private String caller() {
			return ((MethodSequence.MethodSequenceSpecialSpecifiedCaller) sequence).specialCaller;
		}
		
		
		/**
		 * If the method was not found, tries to find a special method with the given name in the given owner class with the default special caller.
		 * If the method is not found, sets the last throwable to the exception that occurred.
		 *
		 * @param owner the owner class to search in
		 * @param name the name of the special method to search for
		 * @return this method finder
		 */
		public MethodFinderSpecialSpecifiedCaller orElse(String owner, String name) {
			return orElse(owner, name, caller());
		}
		
		/**
		 * If the method was not found, tries to find a special method with the given name in the given owner class with the given special caller.
		 * If the method is not found, sets the last throwable to the exception that occurred.
		 *
		 * @param owner         the owner class to search in
		 * @param name          the name of the special method to search for
		 * @param specialCaller the special caller class name
		 * @return this method finder
		 */
		@Override
		public MethodFinderSpecialSpecifiedCaller orElse(String owner, String name, String specialCaller) {
			super.orElse(owner, name, specialCaller);
			return this;
		}
		
		/**
		 * If the method was not found, tries to find a special method with the given name in the given owner class with the default special caller.
		 * If the method is not found, returns a not found result with the last throwable.
		 *
		 * @param owner the owner class to search in
		 * @param name the name of the special method to search for
		 * @return the found method if found, or a not found result with the last throwable if the method was not found
		 */
		public FoundMethod fallback(String owner, String name) {
			return fallback(owner, name, caller());
		}
	}
	
	
	public static class MethodFinderField extends MethodFinder<MethodSequence.MethodSequenceField> {
		protected String foundName;
		
		MethodFinderField(MethodSequence.MethodSequenceField sequence) {
			super(sequence);
		}
		
		/**
		 * If the method was not found, tries to find a field with the given name in the given owner class.
		 * If the field is not found, sets the last throwable to the exception that occurred.
		 *
		 * @param owner the owner class to search in
		 * @param name the name of the field to search for
		 * @return this method finder
		 */
		public MethodFinderField orElse(String owner, String name) {
			if (name.contains(".")) throw new IllegalArgumentException("Illegal method name: " + name);
			if (found == null) try {
				Class<?> refc = Class.forName(owner);
				found = sequence.searchMode.findField(sequence, refc, name, sequence.methodType.returnType());
				foundOwner = refc;
				foundName = name;
			} catch (Exception e) {
				lastThrowable = e;
			}
			return this;
		}
		
		/**
		 * If the method was not found, tries to find a field with the given name in the given owner class.
		 * If the field is not found, returns a not found result with the last throwable.
		 *
		 * @param owner the owner class to search in
		 * @param name the name of the field to search for
		 * @return the found method if found, or a not found result with the last throwable if the method was not found
		 */
		public FoundMethod.Named fallback(String owner, String name) {
			orElse(owner, name);
			if (found == null) return FoundMethod.notfound(foundOwner, foundName, lastThrowable);
			return FoundMethod.found(foundOwner, foundName, found);
		}
		
		/**
		 * Gets the name of the found field, or empty if the field was not found
		 *
		 * @return the name of the found field, or empty if the field was not found
		 */
		public Optional<String> getFoundName() {
			return Optional.ofNullable(foundName);
		}
	}
	
	public static class MethodFinderFieldNamed extends MethodFinderField {
		MethodFinderFieldNamed(MethodSequence.MethodSequenceNamedField sequence) {
			super(sequence);
		}
		
		private String methodName() {
			return ((MethodSequence.MethodSequenceNamedField) sequence).methodName;
		}
		
		
		/**
		 * If the field was not found, tries to find a field with the default name in the given owner class.
		 *
		 * @param owner the owner class to search in
		 * @return this
		 */
		public MethodFinderFieldNamed orElse(String owner) {
			return orElse(owner, methodName());
		}
		
		/**
		 * If the field was not found, tries to find a field with the given name in the given owner class.
		 * If the field is not found, sets the last throwable to the exception that occurred.
		 *
		 * @param owner the owner class to search in
		 * @param name the name of the field to search for
		 * @return this method finder
		 */
		@Override
		public MethodFinderFieldNamed orElse(String owner, String name) {
			super.orElse(owner, name);
			return this;
		}
		
		/**
		 * If the field was not found, tries to find a field with the default name in the given owner class.
		 * If the field is not found, returns a not found result with the last throwable.
		 *
		 * @param owner the owner class to search in
		 * @return the found method if found, or a not found result with the last throwable if the method was not found
		 */
		public FoundMethod fallback(String owner) {
			return fallback(owner, methodName());
		}
	}
	
	
	public static class MethodFinderConstructor extends MethodFinder<MethodSequence.MethodSequenceConstructor> {
		MethodFinderConstructor(MethodSequence.MethodSequenceConstructor sequence) {
			super(sequence);
		}
		
		/**
		 * If the constructor was not found, tries to find a constructor in the given owner class.
		 * If the constructor is not found, sets the last throwable to the exception that occurred.
		 *
		 * @param owner the owner class to search in
		 * @return this method finder
		 */
		public MethodFinderConstructor orElse(String owner) {
			if (found == null) try {
				Class<?> refc = Class.forName(owner);
				found = sequence.searchMode.findConstructor(sequence, refc);
				foundOwner = refc;
			} catch (Exception e) {
				lastThrowable = e;
			}
			return this;
		}
		
		/**
		 * If the constructor was not found, tries to find the constructor in the given owner class with the given name.
		 * If the constructor is not found, returns a not found result with the last throwable.
		 *
		 * @param owner the owner class to search in
		 * @return the found method if found, or an empty result if the method was not found
		 */
		public FoundMethod fallback(String owner) {
			orElse(owner);
			if (found == null) return FoundMethod.notfound(foundOwner, lastThrowable);
			return FoundMethod.found(foundOwner, found);
		}
	}
}
