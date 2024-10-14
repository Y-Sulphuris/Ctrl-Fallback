package com.ydo4ki.ctrlf;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * Class which represents a sequence of operations for finding a method
 * @param <S> search mode
 */
public class MethodSequence<S extends SearchMode> {
	final MethodHandles.Lookup lookup;
	final MethodType methodType;
	final S searchMode;

	/* package-private */
	MethodSequence(MethodHandles.Lookup lookup, MethodType methodType, S searchMode) {
		this.lookup = lookup;
		this.methodType = methodType;
		this.searchMode = searchMode;
	}



	/**
	 * Shortcut for {@link #of(MethodHandles.Lookup, MethodType, SearchMode.GenericSearch)}
	 * with {@link MethodHandles#publicLookup()} as the lookup
	 * @param methodType method type
	 * @param searchMode search mode
	 * @return a new instance of {@link MethodSequenceGenericDefault}
	 */
	public static MethodSequenceGenericDefault of(MethodType methodType, SearchMode.GenericSearch searchMode) {
		return of(MethodHandles.publicLookup(), methodType, searchMode);
	}

	/**
	 * Creates MethodSequence object that searches
	 * methods using {@link MethodHandles.Lookup#findStatic(Class, String, MethodType)} (for ({@link SearchMode#findStatic}) or
	 * {@link MethodHandles.Lookup#findVirtual(Class, String, MethodType)} (for {@link SearchMode#findVirtual})
	 * @param lookup lookup
	 * @param methodType method type
	 * @param searchMode search mode
	 * @return a new instance of {@link MethodSequenceGenericDefault}
	 */
	public static MethodSequenceGenericDefault of(MethodHandles.Lookup lookup, MethodType methodType, SearchMode.GenericSearch searchMode) {
		return new MethodSequenceGenericDefault(lookup, methodType, searchMode);
	}




	/**
	 * Shortcut for {@link #of(MethodHandles.Lookup, MethodType, SearchMode.FindSpecial)}
	 * with {@link MethodHandles#publicLookup()} as the lookup
	 * @param methodType method type
	 * @param searchMode search mode
	 * @return a new instance of {@link MethodSequenceSpecialDefault}
	 */
	public static MethodSequenceSpecialDefault of(MethodType methodType, SearchMode.FindSpecial searchMode) {
		return of(MethodHandles.publicLookup(), methodType, searchMode);
	}
	/**
	 * Creates MethodSequence object that searches
	 * methods using {@link MethodHandles.Lookup#findSpecial(Class, String, MethodType, Class)} (for {@link SearchMode#findSpecial})
	 * @param lookup lookup
	 * @param methodType method type
	 * @param searchMode search mode
	 * @return a new instance of {@link MethodSequenceSpecialDefault}
	 */
	public static MethodSequenceSpecialDefault of(MethodHandles.Lookup lookup, MethodType methodType, SearchMode.FindSpecial searchMode) {
		return new MethodSequenceSpecialDefault(lookup, methodType, searchMode);
	}
	
	/**
	 * Shortcut for {@link #of(MethodHandles.Lookup, Class, SearchMode.FieldAccessor)}
	 * with {@link MethodHandles#publicLookup()} as the lookup
	 * @param fieldType field type
	 * @param searchMode search mode
	 * @return a new instance of {@link MethodSequenceSpecialDefault}
	 */
	public static MethodSequenceField of(Class<?> fieldType, SearchMode.FieldAccessor searchMode) {
		return of(MethodHandles.publicLookup(), fieldType, searchMode);
	}

	
	/**
	 * Creates MethodSequence object that searches
	 * fields using {@link MethodHandles.Lookup#findGetter(Class, String, Class)} or
	 * {@link MethodHandles.Lookup#findSetter(Class, String, Class)} or its static equivalent depending on the search mode
	 * @param lookup lookup
	 * @param fieldType field type
	 * @param searchMode search mode
	 * @return a new instance of {@link MethodSequenceField}
	 */
	public static MethodSequenceField of(MethodHandles.Lookup lookup, Class<?> fieldType, SearchMode.FieldAccessor searchMode) {
		return new MethodSequenceField(lookup, fieldType, searchMode);
	}



	/**
	 * Shortcut for {@link #of(MethodHandles.Lookup, MethodType, SearchMode.Bind)}
	 * with {@link MethodHandles#publicLookup()} as the lookup
	 * @param methodType method type
	 * @param searchMode search mode
	 * @return a new instance of {@link MethodSequenceBind}
	 */
	public static MethodSequenceBind of(MethodType methodType, SearchMode.Bind searchMode) {
		return of(MethodHandles.publicLookup(), methodType, searchMode);
	}
	/**
	 * Creates MethodSequence object that searches
	 * methods using {@link MethodHandles.Lookup#bind(Object, String, MethodType)} (for {@link SearchMode#bind})
	 * @param lookup lookup
	 * @param methodType method type
	 * @param searchMode search mode
	 * @return a new instance of {@link MethodSequenceBind}
	 */
	public static MethodSequenceBindDefault of(MethodHandles.Lookup lookup, MethodType methodType, SearchMode.Bind searchMode) {
		return new MethodSequenceBindDefault(lookup, methodType, searchMode);
	}



	/**
	 * Shortcut for {@link #of(MethodHandles.Lookup, MethodType, SearchMode.FindConstructor)}
	 * with {@link MethodHandles#publicLookup()} as the lookup
	 * @param methodType method type
	 * @param searchMode search mode
	 * @return a new instance of {@link MethodSequenceConstructor}
	 */
	public static MethodSequenceConstructor of(MethodType methodType, SearchMode.FindConstructor searchMode) {
		return of(MethodHandles.publicLookup(), methodType, searchMode);
	}

	/**
	 * Creates MethodSequence object that searches
	 * constructors using {@link MethodHandles.Lookup#findConstructor(Class, MethodType)} (for {@link SearchMode#findConstructor})
	 * @param lookup lookup
	 * @param methodType method type
	 * @param searchMode search mode
	 * @return a new instance of {@link MethodSequenceConstructor}
	 */
	public static MethodSequenceConstructor of(MethodHandles.Lookup lookup, MethodType methodType, SearchMode.FindConstructor searchMode) {
		return new MethodSequenceConstructor(lookup, methodType, searchMode);
	}



	public static abstract class MethodSequenceGeneric extends MethodSequence<SearchMode.GenericSearch> {

		MethodSequenceGeneric(MethodHandles.Lookup lookup, MethodType methodType, SearchMode.GenericSearch searchMode) {
			super(lookup, methodType, searchMode);
		}
		
		
		/**
		 * Start method searching and try to find first element
		 * @param owner method owner (class name)
		 * @param name method name
		 * @return MethodFinder for this sequence
		 */
		public MethodFinder.MethodFinderGeneric find(String owner, String name) {
			MethodFinder.MethodFinderGeneric finder = new MethodFinder.MethodFinderGeneric(this);
			finder.orElse(owner, name);
			return finder;
		}
	}

	public static class MethodSequenceGenericDefault extends MethodSequenceGeneric {

		MethodSequenceGenericDefault(MethodHandles.Lookup lookup, MethodType methodType, SearchMode.GenericSearch searchMode) {
			super(lookup, methodType, searchMode);
		}

		/**
		 * Binds default method name to the method sequence
		 * @param name method name
		 * @return a new instance of {@link MethodSequenceGenericNamed}
		 */
		public MethodSequenceGenericNamed withName(String name) {
			return new MethodSequenceGenericNamed(lookup, methodType, searchMode, name);
		}

		/**
		 * Binds default owner class name to the method sequence
		 * @param owner method owner (class name)
		 * @return a new instance of {@link MethodSequenceGenericSpecifiedOwner}
		 */
		public MethodSequenceGenericSpecifiedOwner withOwner(String owner) {
			return new MethodSequenceGenericSpecifiedOwner(lookup, methodType, searchMode, owner);
		}
	}

	public static class MethodSequenceGenericNamed extends MethodSequenceGeneric {
		final String methodName;

		MethodSequenceGenericNamed(MethodHandles.Lookup lookup, MethodType methodType, SearchMode.GenericSearch searchMode, String methodName) {
			super(lookup, methodType, searchMode);
			this.methodName = methodName;
		}
		/**
		 * Finds a method in the given class with the default name
		 * @param owner owner class name
		 * @return a finder that searches for the given method in the given class
		 */
		public MethodFinder.MethodFinderGenericNamed find(String owner) {
			return find(owner, methodName);
		}
		/**
		 * Finds a method in the given class with the given name
		 * @param owner owner class name
		 * @param name method name
		 * @return a finder that searches for the given method in the given class
		 */
		public MethodFinder.MethodFinderGenericNamed find(String owner, String name) {
			MethodFinder.MethodFinderGenericNamed finder = new MethodFinder.MethodFinderGenericNamed(this);
			finder.orElse(owner, name);
			return finder;
		}
	}

	public static class MethodSequenceGenericSpecifiedOwner extends MethodSequenceGeneric {
		final String owner;

		MethodSequenceGenericSpecifiedOwner(MethodHandles.Lookup lookup, MethodType methodType, SearchMode.GenericSearch searchMode, String owner) {
			super(lookup, methodType, searchMode);
			this.owner = owner;
		}
		/**
		 * Finds a method with the given name in default owner class
		 * @param name method name
		 * @return a finder that searches for the given method in the owner class
		 */
		public MethodFinder.MethodFinderGenericSpecifiedOwner find(String name) {
			return find(owner, name);
		}
		
		/**
		 * Finds a method with the given name in the given class
		 * @param owner owner class name
		 * @param name method name
		 * @return a finder that searches for the given method in the given class
		 */
		public MethodFinder.MethodFinderGenericSpecifiedOwner find(String owner, String name) {
			MethodFinder.MethodFinderGenericSpecifiedOwner finder = new MethodFinder.MethodFinderGenericSpecifiedOwner(this);
			finder.orElse(owner, name);
			return finder;
		}
	}



	public static abstract class MethodSequenceBind extends MethodSequence<SearchMode.Bind> {

		MethodSequenceBind(MethodHandles.Lookup lookup, MethodType methodType, SearchMode.Bind searchMode) {
			super(lookup, methodType, searchMode);
		}

		/**
		 * Finds a method with the given name in the given owner class
		 * @param owner owner class name
		 * @param name method name
		 * @return a finder that searches for the given method in the given class
		 */
		public MethodFinder.MethodFinderBind find(String owner, String name) {
			MethodFinder.MethodFinderBind finder = new MethodFinder.MethodFinderBind(this);
			finder.orElse(owner, name);
			return finder;
		}
	}

	public static class MethodSequenceBindDefault extends MethodSequenceBind {

		MethodSequenceBindDefault(MethodHandles.Lookup lookup, MethodType methodType, SearchMode.Bind searchMode) {
			super(lookup, methodType, searchMode);
		}
		/**
		 * Creates a new instance of {@link MethodSequenceBindNamed} that finds a method with the given name
		 * @param name method name
		 * @return a new instance of {@link MethodSequenceBindNamed}
		 */
		public MethodSequenceBindNamed withName(String name) {
			return new MethodSequenceBindNamed(lookup, methodType, searchMode, name);
		}
	}

	public static class MethodSequenceBindNamed extends MethodSequenceBind {
		final String methodName;

		MethodSequenceBindNamed(MethodHandles.Lookup lookup, MethodType methodType, SearchMode.Bind searchMode, String methodName) {
			super(lookup, methodType, searchMode);
			this.methodName = methodName;
		}
		/**
		 * Finds a method with the given name in the given class
		 * @param receiver the object to search in
		 * @return a finder that searches for the given method in the given class
		 */
		public MethodFinder.MethodFinderBindNamed find(Object receiver) {
			return find(receiver, methodName);
		}
		
		
		/**
		 * Finds a method with the given name in the given object
		 * @param receiver the object to search in
		 * @param name method name
		 * @return a finder that searches for the given method in the given object
		 */
		public MethodFinder.MethodFinderBindNamed find(Object receiver, String name) {
			MethodFinder.MethodFinderBindNamed finder = new MethodFinder.MethodFinderBindNamed(this);
			finder.orElse(receiver, name);
			return finder;
		}
	}

	public static abstract class MethodSequenceSpecial extends MethodSequence<SearchMode.FindSpecial> {

		MethodSequenceSpecial(MethodHandles.Lookup lookup, MethodType methodType, SearchMode.FindSpecial searchMode) {
			super(lookup, methodType, searchMode);
		}

		/**
		 * Finds a method with the given name in the given owner class, using specialCaller as special caller class name
		 * @param owner owner class name
		 * @param name method name
		 * @param specialCaller special caller class name
		 * @return a finder that searches for the given method in the given class
		 */
		public MethodFinder.MethodFinderSpecial find(String owner, String name, String specialCaller) {
			MethodFinder.MethodFinderSpecial finder = new MethodFinder.MethodFinderSpecial(this);
			finder.orElse(owner, name, specialCaller);
			return finder;
		}
	}

	public static class MethodSequenceSpecialDefault extends MethodSequenceSpecial {

		MethodSequenceSpecialDefault(MethodHandles.Lookup lookup, MethodType methodType, SearchMode.FindSpecial searchMode) {
			super(lookup, methodType, searchMode);
		}


		/**
		 * Binds the default method name to the method sequence
		 * @param name method name
		 * @return a new instance of {@link MethodSequenceSpecialNamed} with bound name
		 */
		public MethodSequenceSpecialNamed withName(String name) {
			return new MethodSequenceSpecialNamed(lookup, methodType, searchMode, name);
		}

		/**
		 * Binds the default owner class name to the method sequence
		 * @param owner owner class name
		 * @return a new instance of {@link MethodSequenceSpecialSpecifiedOwner} with bound owner
		 */
		public MethodSequenceSpecialSpecifiedOwner withOwner(String owner) {
			return new MethodSequenceSpecialSpecifiedOwner(lookup, methodType, searchMode, owner);
		}

		/**
		 * Binds the given owner class name as special caller to the method sequence
		 * @param owner special caller class name
		 * @return a new instance of {@link MethodSequenceSpecialSpecifiedCaller} with bound owner
		 */
		public MethodSequenceSpecialSpecifiedCaller withCaller(String owner) {
			return new MethodSequenceSpecialSpecifiedCaller(lookup, methodType, searchMode, owner);
		}
	}

	public static class MethodSequenceSpecialNamed extends MethodSequenceSpecial {
		final String methodName;

		MethodSequenceSpecialNamed(MethodHandles.Lookup lookup, MethodType methodType, SearchMode.FindSpecial searchMode, String methodName) {
			super(lookup, methodType, searchMode);
			this.methodName = methodName;
		}
		/**
		 * Finds a special method with the given name in the given owner class and special caller
		 * @param owner owner class name
		 * @param specialCaller special caller class name
		 * @return a finder that searches for the given method in the given class
		 */
		public MethodFinder.MethodFinderSpecialNamed find(String owner, String specialCaller) {
			return find(owner, methodName, specialCaller);
		}
		/**
		 * Finds a special method with the given name in the given owner class and special caller
		 * @param owner owner class name
		 * @param name method name
		 * @param specialCaller special caller class name
		 * @return a finder that searches for the given method in the given class
		 */
		public MethodFinder.MethodFinderSpecialNamed find(String owner, String name, String specialCaller) {
			MethodFinder.MethodFinderSpecialNamed finder = new MethodFinder.MethodFinderSpecialNamed(this);
			finder.orElse(owner, name, specialCaller);
			return finder;
		}
	}

	public static class MethodSequenceSpecialSpecifiedOwner extends MethodSequenceSpecial {
		final String owner;

		MethodSequenceSpecialSpecifiedOwner(MethodHandles.Lookup lookup, MethodType methodType, SearchMode.FindSpecial searchMode, String owner) {
			super(lookup, methodType, searchMode);
			this.owner = owner;
		}
		/**
		 * Finds a special method with the given name in the given owner class and special caller
		 * @param name method name
		 * @param specialCaller special caller class name
		 * @return a finder that searches for the given method in the given class
		 */
		public MethodFinder.MethodFinderSpecialSpecifiedOwner find(String name, String specialCaller) {
			return find(owner, name, specialCaller);
		}
		/**
		 * Finds a special method with the given name in the given owner class and special caller
		 * @param owner owner class name
		 * @param name method name
		 * @param specialCaller special caller class name
		 * @return a finder that searches for the given method in the given class
		 */
		public MethodFinder.MethodFinderSpecialSpecifiedOwner find(String owner, String name, String specialCaller) {
			MethodFinder.MethodFinderSpecialSpecifiedOwner finder = new MethodFinder.MethodFinderSpecialSpecifiedOwner(this);
			finder.orElse(owner, name, specialCaller);
			return finder;
		}
	}

	public static class MethodSequenceSpecialSpecifiedCaller extends MethodSequenceSpecial {
		final String specialCaller;

		MethodSequenceSpecialSpecifiedCaller(MethodHandles.Lookup lookup, MethodType methodType, SearchMode.FindSpecial searchMode, String caller) {
			super(lookup, methodType, searchMode);
			this.specialCaller = caller;
		}
		
		/**
		 * Finds a special method with the given name in the given owner class and special caller
		 * @param owner owner class name
		 * @param name method name
		 * @return a finder that searches for the given method in the given class
		 */
		public MethodFinder.MethodFinderSpecialSpecifiedCaller find(String owner, String name) {
			return find(owner, name, specialCaller);
		}
		
		/**
		 * Finds a special method with the given name in the given owner class and special caller
		 * @param owner owner class name
		 * @param name method name
		 * @param specialCaller special caller class name
		 * @return a finder that searches for the given method in the given class
		 */
		public MethodFinder.MethodFinderSpecialSpecifiedCaller find(String owner, String name, String specialCaller) {
			MethodFinder.MethodFinderSpecialSpecifiedCaller finder = new MethodFinder.MethodFinderSpecialSpecifiedCaller(this);
			finder.orElse(owner, name, specialCaller);
			return finder;
		}
	}


	public static class MethodSequenceField extends MethodSequence<SearchMode.FieldAccessor> {

		MethodSequenceField(MethodHandles.Lookup lookup, Class<?> fieldType, SearchMode.FieldAccessor searchMode) {
			super(lookup, searchMode.methodType(fieldType), searchMode);
		}

		/**
		 * Finds a field with the given name in the given owner class
		 * @param owner owner class name
		 * @param name field name
		 * @return a finder that searches for the given field in the given class
		 */
		public MethodFinder.MethodFinderField find(String owner, String name) {
			MethodFinder.MethodFinderField finder = new MethodFinder.MethodFinderField(this);
			finder.orElse(owner, name);
			return finder;
		}
	}

	public static class MethodSequenceNamedField extends MethodSequenceField {
		final String methodName;

		MethodSequenceNamedField(MethodHandles.Lookup lookup, Class<?> fieldType, SearchMode.FieldAccessor searchMode, String methodName) {
			super(lookup, fieldType, searchMode);
			this.methodName = methodName;
		}
		
		/**
		 * Finds a field with default name in the given owner class
		 * @param owner owner class name
		 * @return a finder that searches for the given field in the given class
		 */
		public MethodFinder.MethodFinderFieldNamed find(String owner) {
			return find(owner, methodName);
		}
		
		/**
		 * Finds a field with the given name in the given owner class
		 * @param owner owner class name
		 * @param name field name
		 * @return a finder that searches for the given field in the given class
		 */
		public MethodFinder.MethodFinderFieldNamed find(String owner, String name) {
			MethodFinder.MethodFinderFieldNamed finder = new MethodFinder.MethodFinderFieldNamed(this);
			finder.orElse(owner, name);
			return finder;
		}
	}


	public static class MethodSequenceConstructor extends MethodSequence<SearchMode.FindConstructor> {

		MethodSequenceConstructor(MethodHandles.Lookup lookup, MethodType methodType, SearchMode.FindConstructor searchMode) {
			super(lookup, methodType, searchMode);
		}

		/**
		 * Finds a constructor with the given name in the given owner class
		 * @param owner owner class name
		 * @return a finder that searches for the given constructor in the given class
		 */
		public MethodFinder.MethodFinderConstructor find(String owner) {
			MethodFinder.MethodFinderConstructor finder = new MethodFinder.MethodFinderConstructor(this);
			finder.orElse(owner);
			return finder;
		}
	}
}