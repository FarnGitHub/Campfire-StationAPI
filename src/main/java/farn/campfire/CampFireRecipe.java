package farn.campfire;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.impl.recipe.JsonSmelting;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CampFireRecipe {
    private static final Map<Object, ItemStack> recipes = new HashMap<>();
    private static final File cookingListFile = new File(Minecraft.getRunDirectory(), "/config/campfireJson/");

    private CampFireRecipe() {
    }

    static {
        addRecipe(Item.RAW_PORKCHOP.id, new ItemStack(Item.COOKED_PORKCHOP));
        addRecipe(Item.RAW_FISH.id, new ItemStack(Item.COOKED_FISH));
        /*createDefaultRecipe("cookedFish", String identifierInput, String identifierOutPut)
        File[] listOfFile = cookingListFile.listFiles();
        if(listOfFile != null) {
            for (File file : listOfFile) {
                if(file != null && file.toPath().endsWith(".json")) {
                    parseCooking(file);
                }
            }
        }*/
    }

    public static void createDefaultRecipe(String jsonName, String identifierInput, String identifierOutPut) {
        File file = new File(Minecraft.getRunDirectory(), "/config/campfireJson/" + jsonName + ".json");
        if(!file.exists()) {
            String uhWhat = "{  \n" +
                    "  \"ingredient\": {  \n" +
                    "    \"item\": \"minecraft:" + identifierInput + "\",\n" +
                    "    \"damage\": 0\n" +
                    "  },  \n" +
                    "  \"result\": {  \n" +
                    "    \"item\": \"minecraft:"  + identifierOutPut + "\"  \n" +
                    "    \"damage\": 0\n" +
                    "  }  \n" +
                    "}";
            try(FileWriter writer = new FileWriter(file)) {
                writer.write(uhWhat);
            } catch (Exception e) {
            }
        }
    }

    public static void addRecipe(int inputId, ItemStack output) {
        recipes.put(inputId, output);
    }

    public static void addRecipe(ItemStack inputId, ItemStack output) {
        recipes.put(inputId, output);
    }

    private static void parseCooking(File jsonFile) {
        JsonSmelting json;
        try {
            json = new Gson().fromJson(new BufferedReader(new FileReader(jsonFile)), JsonSmelting.class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        json.getIngredient().get().ifLeft(item -> addRecipe(item, json.getResult().getItemStack()));
    }

    public static ItemStack getResultFor(ItemStack input) {
        for (Map.Entry<Object, ItemStack> entry : recipes.entrySet()) {
            if (entry.getKey() instanceof ItemStack item && input.isItemEqual(item))
                return entry.getValue();
        }
        return recipes.get(input.itemId);
    }
}
