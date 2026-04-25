package recipeRealm;

import recipeRealm.ui.SwingGame;

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