package farn.campfire.recipe;
import net.minecraft.item.ItemStack;
import java.util.HashMap;
import java.util.Map;

public class CampFireRecipeManager {
    private static final Map<Object, ItemStack> recipes = new HashMap<>();

    private CampFireRecipeManager() {
    }

    public static void addRecipe(int inputId, ItemStack output) {
        recipes.put(inputId, output);
    }

    public static void addRecipe(ItemStack inputId, ItemStack output) {
        recipes.put(inputId, output);
    }

    public static ItemStack getResultFor(ItemStack input) {
        for (Map.Entry<Object, ItemStack> entry : recipes.entrySet()) {
            if (entry.getKey() instanceof ItemStack item && input.isItemEqual(item))
                return entry.getValue();
        }
        return recipes.get(input.itemId);
    }
}
