package farn.campfire;

import farn.campfire.block.CampFireBlock;
import farn.campfire.block_entity.CampFireBlockEntity;
import farn.campfire.block_entity.CampFireBlockEntityRenderer;
import farn.campfire.packet.PacketUpdateCampfireItem;
import farn.campfire.recipe.CampfireJsonRecipeManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.event.block.entity.BlockEntityRendererRegisterEvent;
import net.modificationstation.stationapi.api.client.event.texture.TextureRegisterEvent;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.client.texture.atlas.ExpandableAtlas;
import net.modificationstation.stationapi.api.event.block.entity.BlockEntityRegisterEvent;
import net.modificationstation.stationapi.api.event.init.InitFinishedEvent;
import net.modificationstation.stationapi.api.event.network.packet.PacketRegisterEvent;
import net.modificationstation.stationapi.api.event.recipe.RecipeRegisterEvent;
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.recipe.CraftingRegistry;
import net.modificationstation.stationapi.api.registry.PacketTypeRegistry;
import net.modificationstation.stationapi.api.registry.Registry;
import net.modificationstation.stationapi.api.util.Namespace;
import net.modificationstation.stationapi.api.util.Null;
import org.apache.logging.log4j.Logger;

import java.io.File;

@SuppressWarnings("unused")
public class CampFireStationAPI {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE = Null.get();

    @Entrypoint.Logger
    public static Logger LOGGER = Null.get();

    public static int campfire_fire = 0;
    public static int campfire_log = 0;
    public static int campfire_log_lit = 0;
    public static Block campfire_block;

    @Environment(EnvType.CLIENT)
    @EventListener
    public void registerTextures(TextureRegisterEvent event) {
        ExpandableAtlas terrainAtlas = Atlases.getTerrain();
        campfire_fire = terrainAtlas.addTexture(NAMESPACE.id("block/campfire_fire")).index;
        campfire_log = terrainAtlas.addTexture(NAMESPACE.id("block/campfire_log")).index;
        campfire_log_lit = terrainAtlas.addTexture(NAMESPACE.id("block/campfire_log_lit")).index;
    }

    @EventListener
    public void readJsonRecipe(InitFinishedEvent event) {
        File[] theJsons = CampfireJsonRecipeManager.folderWithRecipeJson.listFiles();
        if(theJsons != null) {
            for(File file : theJsons) {
                if(file != null && file.getName().endsWith(".json"))
                    CampfireJsonRecipeManager.readJson(file);
            }
        }
    }

    @EventListener
    public void registerBlocks(BlockRegistryEvent event) {
        campfire_block = new CampFireBlock(NAMESPACE.id("campfire"), Material.WOOD).setTranslationKey(NAMESPACE, "camp_fire").setSoundGroup(Block.WOOD_SOUND_GROUP).setHardness(0.5F);
    }

    @EventListener
    public void registerRecipe(RecipeRegisterEvent event) {
        RecipeRegisterEvent.Vanilla type = RecipeRegisterEvent.Vanilla.fromType(event.recipeId);

        if(type == RecipeRegisterEvent.Vanilla.CRAFTING_SHAPED) {
            CraftingRegistry.addShapedRecipe(new ItemStack(campfire_block),
                    " s ",
                            "scs",
                            "lll",
                    's', Item.STICK,
                    'c', new ItemStack(Item.COAL, 1, 1),
                    'l', Block.LOG);
            CraftingRegistry.addShapedRecipe(new ItemStack(campfire_block),
                    " s ",
                            "scs",
                            "lll",
                    's', Item.STICK,
                    'c', new ItemStack(Item.COAL, 1, 0),
                    'l', Block.LOG);

        }

    }

    @EventListener
    public void registerBlockEntity(BlockEntityRegisterEvent event) {
        event.register(CampFireBlockEntity.class, NAMESPACE.id("Campfire_entity").toString());
    }

    @EventListener
    public void registerTileEntityRender(BlockEntityRendererRegisterEvent e) {
        e.renderers.put(CampFireBlockEntity.class, new CampFireBlockEntityRenderer());
    }

    @EventListener
    public void registerPacket(PacketRegisterEvent event) {
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("Campfire_client"), PacketUpdateCampfireItem.TYPE);
    }
}
