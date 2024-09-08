package org.ulithi.jlisp.commons;

import java.util.List;

/**
 * Utilities for working with strings and lists of strings.
 */
public class StringUtils {

	/**
	 * Static methods only: do not construct.
	 */
	private StringUtils() { }

	/**
	 * Returns the zero-based index in {@code tokens} of the first occurrence of the given target
	 * string occurring at or after {@code startIndex}. The returned value is relative to the
	 * entire {@code tokens} list, not the sublist starting at {@code startIndex}.
	 * @param tokens A non-null list of {@code String}s.
	 * @param startIndex The zero-based index into {@code tokens} where the search should begin.
	 * @param target The token to search for.
	 * @return The index of {@code target} in the {@code tokens} list, or -1 if not found.
	 */
	public static int indexOf(final List<String> tokens, final int startIndex, final String target) {
		final int subListIndex = tokens.subList(startIndex, tokens.size() - 1).indexOf(target);

		return (subListIndex < 0 ? subListIndex : startIndex + subListIndex);
	}
}
