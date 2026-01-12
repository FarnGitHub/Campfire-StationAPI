package farn.campfire.recipe;

import com.google.gson.Gson;
import farn.campfire.CampFireStationAPI;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class CampfireJsonRecipeManager {
    public static final File folderWithRecipeJson;

    static {
        try {
            folderWithRecipeJson = Files.createDirectories(Paths.get(FabricLoader.getInstance().getConfigDir().toString(), "campfire_recipe")).toFile();
            writeDefaultRecipe("cooked_porkchop",new ItemData("minecraft:porkchop", -1),new ItemData("minecraft:cooked_porkchop", 0));
            writeDefaultRecipe("cooked_fish",new ItemData("minecraft:cod", -1),new ItemData("minecraft:cooked_cod", 0));
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public static void writeDefaultRecipe(String jsonName, ItemData input, ItemData output) {
        File jsonFile = new File(folderWithRecipeJson, jsonName + ".json");
        if(!jsonFile.exists()) {
            RecipeData recipe = new RecipeData(input, output);
            try(FileWriter writer = new FileWriter(jsonFile)) {
                (new Gson()).toJson(recipe, writer);
                addRecipeFromRecord(recipe.input, recipe.output);
            } catch (Exception e) {
                CampFireStationAPI.LOGGER.error(e.toString());
            }
        }
    }

    public static void readJson(File jsonFile) {
        Gson json = new Gson();
        try(FileReader reader = new FileReader(jsonFile)) {
            RecipeData recipe = json.fromJson(reader, RecipeData.class);
            addRecipeFromRecord(recipe.input, recipe.output);
        } catch (Exception e) {
            CampFireStationAPI.LOGGER.error(e.toString());
        }
    }

    public static void addRecipeFromRecord(ItemData input, ItemData output) {
        ItemStack newInput =
                new ItemStack(
                        identifierToItemId(input.namespace), 1, input.damage);
        ItemStack newOutput =
                new ItemStack(
                        identifierToItemId(output.namespace), 1, output.damage);

        if(input.damage < 0)
            CampFireRecipeManager.addRecipe(newInput.itemId, newOutput);
        else
            CampFireRecipeManager.addRecipe(newInput, newOutput);

    }

    public static class ItemData {
        public String namespace;
        public int damage;

        public ItemData(String namespace, int damage) {
            this.namespace = namespace;
            this.damage = damage;
        }

    }
    public static class RecipeData {
        public ItemData input;
        public ItemData output;

        public RecipeData(ItemData input, ItemData output) {
            this.input = input;
            this.output = output;
        }

    }

    public static int identifierToItemId(String n) {
        Optional<Item> item = ItemRegistry.INSTANCE.getOrEmpty(Identifier.of(n));
        return item.map(itemBase -> itemBase.id).orElse(1);
    }
}
