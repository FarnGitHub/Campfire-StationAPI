package farn.campfire;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CampFireRecipe {
    public static final Int2ObjectArrayMap<ItemStack> recipes = new Int2ObjectArrayMap<>();

    private CampFireRecipe() {
    }

    static {
        addRecipe(Item.RAW_PORKCHOP.id, new ItemStack(Item.COOKED_PORKCHOP));
        addRecipe(Item.RAW_FISH.id, new ItemStack(Item.COOKED_FISH));
    }

    public static void addRecipe(int inputId, ItemStack output) {
        recipes.put(inputId, output);
    }

    public static ItemStack getCookedItem(int inputId) {
        return recipes.get(inputId);
    }

    public static boolean canCook(int inputId) {
        return recipes.containsKey(inputId);
    }
}
