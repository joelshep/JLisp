package org.ulithi.jlisp.commons;

import java.util.Collection;

/**
 * A few static methods to simplify working with {@link Collection} objects.
 */
public class CollectionUtils {

    /**
     * Indicates if the given {@link Collection} is null or empty.
     * @param collection A {@link Collection} object.
     * @return True if the {@link Collection} is null or empty, false otherwise.
     */
    public static <T> boolean isEmpty(final Collection<T> collection) {
        return (collection == null || collection.isEmpty());
    }
}
