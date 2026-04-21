package recipeRealm;

import recipeRealm.ui.SwingGame;

/**
 * Entry point for Recipe Realm.
 *
 * Usage:
 *   java -cp out recipeRealm.Main            → Swing UI (default)
 *   java -cp out recipeRealm.Main --swing    → Swing UI
 *   java -cp out recipeRealm.Main --terminal → Terminal UI
 */
public class Main {

    public static void main(String[] args) {
        boolean useTerminal = false;
        for (String arg : args) {
            if (arg.equals("--terminal")) useTerminal = true;
        }

        if (useTerminal) {
            new TerminalGame().run();
        } else {
            new SwingGame().show();
        }
    }
}