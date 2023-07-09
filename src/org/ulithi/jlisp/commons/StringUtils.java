package org.ulithi.jlisp.commons;

import java.util.ArrayList;
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
	 * Function: join
	 *
	 * This function is used to implodde a generic list together
	 * with the specified "glue" string
	 *
	 * @author Joseph T. Anderson <jtanderson@ratiocaeli.com>
	 * @since 2012-11-01
	 * @version 2012-11-01
	 *
	 * @param s 	Any List whose elements support the toString method
	 * @param glue	A string with which to implode the list element strings
	 *
	 * @return 		The string of list elements stringified and joined
	 */
	public static String join(final List<String> s, final String glue){
		String newString = "";
		for ( int i = 0; i < s.size() - 1; i++ ){
			newString = newString + s.get(i).toString() + glue;
		}
		return newString + s.get(s.size() - 1).toString();
	}

	/**
	 * Function: vectorize
	 *
	 * @param s A string
	 * @return A vector where each element is a single character of the string
	 */
	public static List<String> vectorize(String s){
		List<String> v = new ArrayList<>();
		int i;
		for ( i = 0; i < s.length() - 1; i ++ ){
			v.add(s.substring(i,i+1));
		}
		if ( i > 0 ){
			v.add(s.substring(i));
		}
		return v;
	}

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
		//return startIndex + tokens.subList(startIndex, tokens.size() - 1).indexOf(target);
	}
}
