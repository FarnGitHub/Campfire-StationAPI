package farn.campfire.recipe;

import com.google.gson.Gson;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.resource.DataManager;
import net.modificationstation.stationapi.api.resource.ResourceFinder;
import net.modificationstation.stationapi.api.util.Identifier;

import java.io.*;
import java.util.Optional;

public class CampfireJsonRecipeManager {

    private CampfireJsonRecipeManager() {
    }

    public static void readAll() {
        ResourceFinder finder = ResourceFinder.json("campfire_recipe");
        Gson json = new Gson();
        finder.findResources(DataManager.INSTANCE).forEach((id, resource) -> {
            try(BufferedReader reader = resource.getReader()) {
                RecipeData recipe = json.fromJson(reader, RecipeData.class);
                addRecipeFromRecord(recipe.input, recipe.output);
            } catch (Exception ignored) {
            }
        });
    }

    public static void addRecipeFromRecord(ItemData input, ItemData output) {
        if(input.item == null || output.item == null) return;
        if(output.meta < 0) output.meta = 0;

        int inputItemId = identifierToItemId(input.item);
        int outputItemId = identifierToItemId(output.item);

        if(inputItemId <= 0 || outputItemId <= 0) return;

        ItemStack newInput = new ItemStack(inputItemId, 1, input.meta);
        ItemStack newOutput = new ItemStack(outputItemId, 1, output.meta);

        if(input.meta < 0)
            CampFireRecipeManager.addRecipe(newInput.itemId, newOutput);
        else
            CampFireRecipeManager.addRecipe(newInput, newOutput);

    }

    public static class ItemData {
        public String item = null;
        public int meta = -1;
    }

    public static class RecipeData {
        public ItemData input;
        public ItemData output;
    }

    public static int identifierToItemId(String n) {
        Optional<Item> item = ItemRegistry.INSTANCE.getOrEmpty(Identifier.of(n));
        return item.map(itemBase -> itemBase.id).orElse(0);
    }
}
