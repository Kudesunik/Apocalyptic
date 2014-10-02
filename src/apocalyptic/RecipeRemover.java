package apocalyptic;

public class RecipeRemover {

    private static boolean find;
    private static int[] RemovedRecipes = new int[100];

    public static boolean findRecipe(int rec) {
        find = false;
        for (int iter = 0; iter < 100; iter++) {
            if (find != true) {
                if (RemovedRecipes[iter] == rec) {
                    find = true;
                }
            }
        }
        return find;
    }

    public static void removeRecipe(String name) {
        int id = name.hashCode();
        boolean added = false;
        for (int iter = 0; iter < 100; iter++) {
            if (RemovedRecipes[iter] == 0 && added == false) {
                RemovedRecipes[iter] = id;
                Apocalyptic.apLogger.info("Recipe of " + name + " removed");
                added = true;
            }
        }
    }
}
