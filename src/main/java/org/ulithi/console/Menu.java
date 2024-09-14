package org.ulithi.console;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A menu system for organizing code in the JavaConsole.
 *
 * @author David MacDermot
 *
 * 02-07-2012
 *
 */
public class Menu
{
    private final Console console;

    private final List<MenuItem> items = new ArrayList<MenuItem>();

    /**
     * Class Constructor,
     *
     * @param console an instance of the JavaConsole UI
     */
    public Menu (final Console console) {
        this.console = console;
    }

    /**
     * A menu item object.
     */
    private static class MenuItem {
        private final MenuCallback _mc;
        private final String _text;

        /**
         * Class Constructor
         * @param text The text to display
         * @param mc an MenuCallback object
         */
        public MenuItem (String text, MenuCallback mc) {
            _mc = mc;
            _text = text;
        }

        /**
         * @return the MenuCallback object
         */
        public MenuCallback get_mc () {
            return _mc;
        }

        /**
         * @return the display text
         */
        public String get_text () {
            return _text;
        }
    }

    /**
     * @param text The text to display
     * @param mc an MenuCallback object
     * @return boolean true if successful.
     */
    public boolean add(String text, MenuCallback mc) {
        return items.add(new MenuItem(text, mc));
    }

    /**
     * Display the list of menu item choices
     */
    public void show() {
        int choosen = 0;
        Scanner in = new Scanner(System.in);

        for (int i = 0; i < items.size(); ++i) {
            MenuItem mi = items.get(i);
            System.out.printf(" [%d] %s \n", i + 1, mi.get_text());
        }

        System.out.println(); // add a line

        try {
            choosen = in.nextInt();
        } catch (Exception e1) { /* Ignore non numeric and mixed */ }

        console.clear();

        if (choosen > items.size() || choosen < 1) {
            System.out.println("Invalid option.\nPress enter to continue...");
            in.nextLine();
            in.nextLine();
        } else {
            MenuItem mi = items.get(choosen - 1);
            MenuCallback mc = mi.get_mc();
            mc.invoke();
        }
    }
}
