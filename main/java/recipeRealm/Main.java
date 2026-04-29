package recipeRealm;

import recipeRealm.test.RecipeRealmTestSuite;
import recipeRealm.ui.SwingGame;

public class Main {

    public static void main(String[] args) {
        boolean useTerminal = false;
        boolean useTest = false;
        for (String arg : args) {
            if (arg.equals("--terminal")) useTerminal = true;
            else if(arg.equals("--test")) useTest = true;
        }

        if (useTerminal) {
            new TerminalGame().run();
        } else if(useTest){
            new RecipeRealmTestSuite().main(args);
        }
        else {
            new SwingGame().show();
        }
    }
}