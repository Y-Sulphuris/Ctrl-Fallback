package com.ydo4ki.ctrlf;

import java.lang.invoke.MethodHandle;
import java.util.Optional;

/**
 * Class that represents a method found with {@link MethodFinder}
 * @author Sulphuris
 * @since 12.10.2024 21:01
 */
public class FoundMethod {
	private final Object foundOwner;
	private final MethodHandle found;
	private final Throwable err;



	static FoundMethod.Named found(Object foundOwner, String foundName, MethodHandle found) {
		return new FoundMethod.Named(foundOwner, foundName, found, null);
	}
	static FoundMethod.Named notfound(Object foundOwner, String foundName, Throwable err) {
		return new FoundMethod.Named(foundOwner, foundName, null, err);
	}
	static FoundMethod found(Object foundOwner, MethodHandle found) {
		return new FoundMethod(foundOwner, found, null);
	}
	static FoundMethod notfound(Object foundOwner, Throwable err) {
		return new FoundMethod(foundOwner, null, err);
	}

	FoundMethod(Object foundOwner, MethodHandle found, Throwable err) {
		this.foundOwner = foundOwner;
		this.found = found;
		this.err = err;
	}


	/**
	 * Gets the owner of the found method
	 * @return the owner of the found method, or empty if the method was not found
	 */
	public Optional<Object> getFoundOwner() {
		return Optional.ofNullable(foundOwner);
	}

	/**
	 * Gets the found method, or empty if the method was not found
	 * @return the found method, or empty if the method was not found
	 */
	public Optional<MethodHandle> getFound() {
		return Optional.ofNullable(found);
	}

	/**
	 * Gets the exception that was thrown when looking up the method, or empty if the method was found
	 * @return the exception that was thrown when looking up the method, or empty if the method was found
	 */
	public Optional<Throwable> getErr() {
		return Optional.ofNullable(err);
	}

	/**
	 * Returns the found method, or throws the exception that was thrown when
	 * looking up the method if the method was not found.
	 *
	 * @return the found method, or throws the exception that was thrown when
	 *         looking up the method if the method was not found
	 * @throws RuntimeException if the method was not found, and the exception
	 *         that was thrown when looking up the method is a
	 *         {@link RuntimeException}
	 * @throws Error if the method was not found, and the exception that was
	 *         thrown when looking up the method is an {@link Error}
	 * @throws RuntimeException if the method was not found, and the exception
	 *         that was thrown when looking up the method is a
	 *         {@link Throwable} that is not a {@link RuntimeException} or an
	 *         {@link Error}
	 */
	public MethodHandle methodHandle() {
		if (found == null) {
			if (err instanceof RuntimeException) throw (RuntimeException) err;
			if (err instanceof Error) throw (Error) err;
			throw new RuntimeException(err);
		}
		return found;
	}

	public static final class Named extends FoundMethod {
		private final String name;

		Named(Object foundOwner, String foundName, MethodHandle found, Throwable err) {
			super(foundOwner, found, err);
			this.name = foundName;
		}

		/**
		 * Gets the name of the found method, or empty if the method was not found
		 * @return the name of the found method, or empty if the method was not found
		 */
		public Optional<String> getFoundName() {
			return Optional.ofNullable(name);
		}
	}
}
