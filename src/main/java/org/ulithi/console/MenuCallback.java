package org.ulithi.console;

import java.util.EventListener;

/**
 * A menu callback interface for invoking methods.
 */
public interface MenuCallback extends EventListener {
    void invoke ();
}
