package apocalyptic;

import ic2.api.item.Items;
import ic2.api.recipe.Recipes;

import java.util.logging.Logger;

import thermalexpansion.api.crafting.CraftingManagers;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import apocalyptic.blocks.*;
import apocalyptic.blocks.gas.GasFlowing;
import apocalyptic.blocks.gas.GasStationary;
import apocalyptic.bukkit.PermissionNode;
import apocalyptic.commands.AdminCommand;
import apocalyptic.commands.InfoCommand;
import apocalyptic.commands.RegenerationCommand;
import apocalyptic.commands.TeleportationCommand;
import apocalyptic.crossmod.CrossmodIndustrialCraft2;
import apocalyptic.entitylife.MainLifeThread;
import apocalyptic.gui.GuiHandler;
import apocalyptic.items.IngotSomnium;
import apocalyptic.items.ItemAntiRad;
import apocalyptic.items.ItemFilter;
import apocalyptic.items.ItemStimulator;
import apocalyptic.items.ItemTeleportationCore;
import apocalyptic.potions.PotionRegenFeel;
import apocalyptic.potions.PotionRegenRad;
import apocalyptic.tile.TileEntityDetector;
import apocalyptic.tile.TileEntityFilter;
import apocalyptic.tile.TileEntityPortalStarterHeaven;
import apocalyptic.tile.TileEntityPortalStarterLvl2;
import apocalyptic.tile.TileEntityPortalStarterLvl3;
import apocalyptic.world.biomes.BiomeGenGrassDesert;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.Mod.ServerStopping;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.block.BlockStone;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.potion.Potion;
import net.minecraft.world.biome.BiomeGenBase;

/**
 * Apocalyptic mod -- adds special blocks, items and stuff for apocalyptic
 * server
 *
 * @author Kunik
 *
 */
@Mod(modid = "Apocalyptic", name = "Apocalyptic", dependencies = "after:IC2", version = "1.0.0")
@NetworkMod(channels = {"Apocalyptic-rad", "Apocalyptic-bv"}, versionBounds = "1.0.0", clientSideRequired = true, serverSideRequired = true, packetHandler = PacketHandler.class)
public class Apocalyptic {

    public static Configuration configuration;
    public static final Logger apLogger = Logger.getLogger("Apocalyptic");
    public CrossmodIndustrialCraft2 cross;
    public static MinecraftServer server = null;
    public static boolean isServer;
    public static final String LOG_PREFIX = "[Apocalyptic]";
    @SidedProxy(clientSide = "apocalyptic.client.ClientProxy", serverSide = "apocalyptic.CommonProxy")
    public static CommonProxy apocalyptic_proxy;
    public static MainLifeThread mlt;
    @Instance("Apocalyptic")
    public static Apocalyptic instance;
    public static int gasModel;
    private GuiHandler guiHandler = new GuiHandler();
    public static int tePerChunk;
    public static double silverfishChance;
    public static String[] transferLimit;
    public static String[] transferTagRemove;
    public static String databaseURL;
    public static String databaseUsername;
    public static String databasePassword;
    //Add blocks
    public static Block detectorBlock;
    public static Block filterBlock;
    public static final Block FogStill = new GasStationary(119, Material.gas).setUnlocalizedName("Fog");
    public static final Block FogMoving = new GasFlowing(FogStill.blockID - 1, Material.gas).setUnlocalizedName("Fog");
    public static final Block Surface = new Surface(120).setUnlocalizedName("Surface");
    public static final Block HardSurface = new HardSurface(122).setUnlocalizedName("HardSurface");
    public static final Block BakedSurface = new BakedSurface(3100).setUnlocalizedName("BakedSurface");
    public static final Block FilteredAir = new FilteredAir(3101).setUnlocalizedName("FilteredAir");
    public static final Block PortalStarterLvl2 = new PortalStarterLvl2(3105).setUnlocalizedName("PortalStarterLvl2");
    public static final Block PortalFrameLvl2 = new PortalFrameLvl2(3106).setUnlocalizedName("PortalFrameLvl2");
    public static final Block PortalStarterLvl3 = new PortalStarterLvl3(3107).setUnlocalizedName("PortalStarterLvl3");
    public static final Block PortalFrameLvl3 = new PortalFrameLvl3(3108).setUnlocalizedName("PortalFrameLvl3");
    public static final Block PortalStarterHeaven = new PortalStarterHeaven(116).setUnlocalizedName("PortalStarterHeaven");
    public static final Block PortalFrameHeaven = new PortalFrameHeaven(117).setUnlocalizedName("PortalFrameHeaven");
    public static final Block PortalLvl2 = new PortalLvl2(3111).setUnlocalizedName("PortalLvl2");
    public static final Block PortalLvl3 = new PortalLvl3(3112).setUnlocalizedName("PortalLvl3");
    public static final Block PortalHeaven = new PortalHeaven(105).setUnlocalizedName("PortalHeaven");
    public static final Block SomniumOre = new SonmiumOre(3114).setUnlocalizedName("Somnium");
    public static final Block Plate = new Plates(3115).setUnlocalizedName("Plate");
    public static final Block ForceField = new ForceField(3116, Material.rock).setUnlocalizedName("ForceField");
    public static final Block Lamp = new Lamp(3117, Material.glass).setUnlocalizedName("Lamp");
    public static final Block SpawnPortalFrame = new SpawnPortalFrames(3118).setUnlocalizedName("SpawnPortalFrame");
    public static final Block SpawnPortal = new SpawnPortal(3119).setUnlocalizedName("SpawnPortal");
    //Add biomes
    public static final BiomeGenBase grassDesert = (new BiomeGenGrassDesert(23)).setColor(16421912).setBiomeName("GrassDesert").setDisableRain().setTemperatureRainfall(0.8F, 0.0F).setMinMaxHeight(0.1F, 0.2F);
    
    //Add items
    public static Item filterItem;
    public static Item teleportationCore;
    public static Item ingotSomnium;
    public static Item itemAntiRad;
    public static Item itemStimulator;
    
    //Add potions
    public static Potion regenRad;
    public static Potion regenFeel;

    @PreInit
    public void preInit(FMLPreInitializationEvent event) {
        apLogger.setParent(FMLLog.getLogger());

        apLogger.info("[Apocalyptic] Starting pre-initialization");
        apLogger.info("[Apocalyptic] Loading configuration");

        isServer = event.getSide().isServer();

        Configuration config = new Configuration(event.getSuggestedConfigurationFile());

        tePerChunk = config.get("general", "TileEntitiesPerChunk", 900).getInt(900);
        silverfishChance = config.get("general", "SilverfishSpawnChance", 0.001).getDouble(0.001);
        transferLimit = config.get("transfer", "limit", new String[]{"0:0:0", "1:X:1"}).getStringList();
        transferTagRemove = config.get("transfer", "tag-remove", new String[]{"0:0", "1:X"}).getStringList();
        //databaseURL = config.get("database", "URL", "jdbc:mysql://site.ru:3306/database").getString();
        //databaseUsername = config.get("database", "Username", "Username").getString();
        //databasePassword = config.get("database", "Password", "Password").getString();

        config.load();
        config.save();
        
        this.initCustomPotions(event);

        apLogger.info("[Apocalyptic] Pre-initialization completed");
    }
    
    /**
     * Custom potions initialization
     * @param event 
     */
    private void initCustomPotions(FMLPreInitializationEvent event) {
        Potion[] potionTypes = null;
        for (Field f : Potion.class.getDeclaredFields()) {
            f.setAccessible(true);
            try {
                if (f.getName().equals("potionTypes") || f.getName().equals("field_76425_a")) {
                    Field modfield = Field.class.getDeclaredField("modifiers");
                    modfield.setAccessible(true);
                    modfield.setInt(f, f.getModifiers() & ~Modifier.FINAL);

                    potionTypes = (Potion[]) f.get(null);
                    final Potion[] newPotionTypes = new Potion[256];
                    System.arraycopy(potionTypes, 0, newPotionTypes, 0, potionTypes.length);
                    f.set(null, newPotionTypes);
                }
            } catch (Exception ex) {
                apLogger.severe("Custom potion initialization error!");
                System.err.println(ex);
            }
        }
    }

    @Init
    public void load(FMLInitializationEvent evt) {
        apLogger.info("[Apocalyptic] Initialization started");

        MinecraftForge.EVENT_BUS.register(this);

        apocalyptic_proxy.registerRenderers();

        apocalyptic_proxy.registerTextureFX();

        apocalyptic_proxy.initTickHandler(this);

        filterBlock = new Filter(3102).setUnlocalizedName("Filter");

        detectorBlock = new Detector(3103).setUnlocalizedName("Detector");

        filterItem = new ItemFilter(3104).setUnlocalizedName("CoalFilter");

        teleportationCore = new ItemTeleportationCore(3115).setUnlocalizedName("TeleportationCore");

        ingotSomnium = new IngotSomnium(3105).setUnlocalizedName("IngotSomnium");
        
        
        itemAntiRad = new ItemAntiRad(3106).setUnlocalizedName("AntiRad");
        
        itemStimulator = new ItemStimulator(3107).setUnlocalizedName("Stimulator");
        
        regenRad = (new PotionRegenRad(33, false, 0)).setPotionName("potion.regenRad").setIconIndex(7, 0);
        
        regenFeel = (new PotionRegenFeel(34, false, 0)).setPotionName("potion.regenFeel").setIconIndex(7, 0);

        //Register blocks
        GameRegistry.registerBlock(detectorBlock);
        GameRegistry.registerBlock(filterBlock);
        GameRegistry.registerBlock(FogMoving);
        GameRegistry.registerBlock(FogStill);
        GameRegistry.registerBlock(Surface);
        GameRegistry.registerBlock(HardSurface);
        GameRegistry.registerBlock(BakedSurface);
        GameRegistry.registerBlock(FilteredAir);
        GameRegistry.registerBlock(PortalStarterLvl2);
        GameRegistry.registerBlock(PortalFrameLvl2);
        GameRegistry.registerBlock(PortalStarterLvl3);
        GameRegistry.registerBlock(PortalFrameLvl3);
        GameRegistry.registerBlock(PortalStarterHeaven);
        GameRegistry.registerBlock(PortalFrameHeaven);
        GameRegistry.registerBlock(PortalLvl2);
        GameRegistry.registerBlock(PortalLvl3);
        GameRegistry.registerBlock(PortalHeaven);
        GameRegistry.registerBlock(ForceField);
        GameRegistry.registerBlock(Lamp);
        GameRegistry.registerBlock(Plate, MultiPlates.class);
        GameRegistry.registerBlock(SpawnPortal);
        GameRegistry.registerBlock(SpawnPortalFrame, MultiSpawnPortalFrames.class);
        GameRegistry.registerBlock(SomniumOre);

        //Register items
        GameRegistry.registerItem(filterItem, "CoalFilter");
        GameRegistry.registerItem(teleportationCore, "TeleportationCore");
        GameRegistry.registerItem(ingotSomnium, "IngotSomnium");
        GameRegistry.registerItem(itemStimulator, "Stimulator");
        GameRegistry.registerItem(itemAntiRad, "AntiRad");

        //Register biomes
        GameRegistry.addBiome(grassDesert);

        GameRegistry.registerTileEntity(TileEntityFilter.class, "Filter");
        GameRegistry.registerTileEntity(TileEntityDetector.class, "Detector");
        GameRegistry.registerTileEntity(TileEntityPortalStarterLvl2.class, "PortalStarterLvl2");
        GameRegistry.registerTileEntity(TileEntityPortalStarterLvl3.class, "PortalStarterLvl3");
        GameRegistry.registerTileEntity(TileEntityPortalStarterHeaven.class, "PortalStarterHeaven");

        //Add localized block names
        LanguageRegistry.addName(Surface, "\u0418\u0441\u0441\u0443\u0448\u0435\u043D\u043D\u0430\u044F \u0437\u0435\u043C\u043B\u044F");
        LanguageRegistry.addName(BakedSurface, "\u0420\u0430\u0441\u043F\u043B\u0430\u0432\u043B\u0435\u043D\u043D\u0430\u044F \u0437\u0435\u043C\u043B\u044F");
        LanguageRegistry.addName(HardSurface, "\u0422\u0432\u0435\u0440\u0434\u0430\u044F \u0438\u0441\u0441\u0443\u0448\u0435\u043D\u043D\u0430\u044F \u0437\u0435\u043C\u043B\u044F");
        LanguageRegistry.addName(FogMoving, "\u0413\u0430\u0437Moving");
        LanguageRegistry.addName(FogStill, "\u0413\u0430\u0437Still");
        LanguageRegistry.addName(FilteredAir, "\u0424\u0438\u043B\u044C\u0442\u0440\u043E\u0432\u0430\u043D\u044B\u0439 \u0432\u043E\u0437\u0434\u0443\u0445");
        LanguageRegistry.addName(PortalFrameLvl2, "\u041E\u0433\u0440\u0430\u043D\u0438\u0447\u0438\u0442\u0435\u043B\u044C \u043F\u043E\u043B\u044F 2-\u043E\u0433\u043E \u0443\u0440\u043E\u0432\u043D\u044F");
        LanguageRegistry.addName(PortalFrameLvl3, "\u041E\u0433\u0440\u0430\u043D\u0438\u0447\u0438\u0442\u0435\u043B\u044C \u043F\u043E\u043B\u044F 3-\u0435\u0433\u043E \u0443\u0440\u043E\u0432\u043D\u044F");
        LanguageRegistry.addName(PortalFrameHeaven, "\u041E\u0433\u0440\u0430\u043D\u0438\u0447\u0438\u0442\u0435\u043B\u044C \u043F\u043E\u043B\u044F \u0420\u0430\u044F");
        LanguageRegistry.addName(PortalLvl2, "\u041F\u043E\u0440\u0442\u0430\u043B 2-\u043E\u0433\u043E \u0443\u0440\u043E\u0432\u043D\u044F");
        LanguageRegistry.addName(PortalLvl3, "\u041F\u043E\u0440\u0442\u0430\u043B 3-\u0435\u0433\u043E \u0443\u0440\u043E\u0432\u043D\u044F");
        LanguageRegistry.addName(SomniumOre, "\u0421\u043E\u043C\u043D\u0438\u0443\u043C");
        LanguageRegistry.addName(ForceField, "\u0421\u0438\u043B\u043E\u0432\u043E\u0435 \u043F\u043E\u043B\u0435");
        LanguageRegistry.addName(Lamp, "\u041B\u0430\u043C\u043F\u0430");
        LanguageRegistry.addName(SpawnPortal, "\u041F\u043E\u0440\u0442\u0430\u043B \u0441\u043F\u0430\u0432\u043D\u0430");

        //Add localized multiblock names
        LanguageRegistry.addName(new ItemStack(Plate, 1, 0), "\u0427\u0451\u0440\u043D\u0430\u044F \u043F\u043B\u0438\u0442\u0430");
        LanguageRegistry.addName(new ItemStack(Plate, 1, 1), "\u0412\u0430\u0441\u0438\u043B\u044C\u043A\u043E\u0432\u0430\u044F \u043F\u043B\u0438\u0442\u0430");
        LanguageRegistry.addName(new ItemStack(Plate, 1, 2), "\u041D\u0430\u043F\u043E\u043B\u044C\u043D\u0430\u044F \u043F\u043B\u0438\u0442\u0430");
        LanguageRegistry.addName(new ItemStack(Plate, 1, 4), "\u041F\u0440\u0435\u0434\u0443\u043F\u0440\u0435\u0436\u0434\u0430\u044E\u0449\u0430\u044F \u043F\u043B\u0438\u0442\u0430");
        LanguageRegistry.addName(new ItemStack(Plate, 1, 3), "\u0411\u0435\u043B\u0430\u044F \u043F\u043B\u0438\u0442\u0430");
        LanguageRegistry.addName(new ItemStack(SpawnPortalFrame, 1, 0), "\u0426\u0435\u043D\u0442\u0440\u0430\u043B\u044C\u043D\u044B\u0439 \u043E\u0433\u0440\u0430\u043D\u0438\u0447\u0438\u0442\u0435\u043B\u044C \u043F\u043E\u043B\u044F \u0441\u043F\u0430\u0432\u043D\u0430");
        LanguageRegistry.addName(new ItemStack(SpawnPortalFrame, 1, 1), "\u043E\u0433\u0440\u0430\u043D\u0438\u0447\u0438\u0442\u0435\u043B\u044C \u043F\u043E\u043B\u044F \u0441\u043F\u0430\u0432\u043D\u0430");

        LanguageRegistry.instance().addStringLocalization("tile.Detector.name", "ru_RU", "\u0414\u0435\u0442\u0435\u043A\u0442\u043E\u0440");
        LanguageRegistry.instance().addStringLocalization("tile.Filter.name", "ru_RU", "\u0424\u0438\u043B\u044C\u0442\u0440");
        LanguageRegistry.instance().addStringLocalization("item.CoalFilter.name", "ru_RU", "\u0423\u0433\u043E\u043B\u044C\u043D\u044B\u0439 \u0444\u0438\u043B\u044C\u0442\u0440");
        LanguageRegistry.instance().addStringLocalization("item.TeleportationCore.name", "ru_RU", "\u0421\u0442\u0430\u0431\u0438\u043B\u0438\u0437\u0430\u0442\u043E\u0440 \u043F\u043E\u0442\u043E\u043A\u0430");
        LanguageRegistry.instance().addStringLocalization("tile.PortalStarterLvl2.name", "ru_RU", "\u041D\u0430\u043A\u043E\u043F\u0438\u0442\u0435\u043B\u044C \u044D\u043D\u0435\u0440\u0433\u0438\u0438 2-\u043E\u0433\u043E \u0443\u0440\u043E\u0432\u043D\u044F");
        LanguageRegistry.instance().addStringLocalization("tile.PortalStarterLvl3.name", "ru_RU", "\u041D\u0430\u043A\u043E\u043F\u0438\u0442\u0435\u043B\u044C \u044D\u043D\u0435\u0440\u0433\u0438\u0438 3-\u0435\u0433\u043E \u0443\u0440\u043E\u0432\u043D\u044F");
        LanguageRegistry.instance().addStringLocalization("tile.PortalStarterHeaven.name", "ru_RU", "\u041D\u0430\u043A\u043E\u043F\u0438\u0442\u0435\u043B\u044C \u044D\u043D\u0435\u0440\u0433\u0438\u0438 \u0420\u0430\u044F");
        LanguageRegistry.instance().addStringLocalization("item.IngotSomnium.name", "ru_RU", "\u0421\u043B\u0438\u0442\u043E\u043A \u0441\u043E\u043C\u043D\u0438\u0443\u043C\u0430");
        LanguageRegistry.instance().addStringLocalization("item.Stimulator.name", "ru_RU", "Стимулятор");
        LanguageRegistry.instance().addStringLocalization("item.AntiRad.name", "ru_RU", "АнтиРад");
        
        NetworkRegistry.instance().registerGuiHandler(this, guiHandler);

        MinecraftForge.EVENT_BUS.register(new EventPlayerHook());
        MinecraftForge.EVENT_BUS.register(new EventPlayerPlaceTE());
        MinecraftForge.EVENT_BUS.register(new EventPlayerDestroyBlock());
        MinecraftForge.EVENT_BUS.register(new EventPlayerPotionEffect());

        Recipes.advRecipes.addRecipe(new ItemStack(Apocalyptic.filterBlock, 1), new Object[]{
            "I I", " M ", "IEI",
            'I', Items.getItem("refinedIronIngot"),
            'M', Items.getItem("machine"),
            'E', Items.getItem("electronicCircuit")
        });

        Recipes.advRecipes.addRecipe(new ItemStack(Apocalyptic.filterItem, 1), new Object[]{
            " D ", " D ", " C ",
            'D', Items.getItem("hydratedCoalDust"),
            'C', Items.getItem("cell")
        });

        Recipes.advRecipes.addRecipe(new ItemStack(Apocalyptic.detectorBlock, 1), new Object[]{
            "G G", "GCG", "MBM",
            'G', Items.getItem("goldCableItem"),
            'C', Items.getItem("reBattery"),
            'M', Items.getItem("electronicCircuit"),
            'B', Items.getItem("machine")
        });

        Recipes.advRecipes.addRecipe(new ItemStack(Block.blockClay, 2), new Object[]{
            "DDD", "DCD", "DDD",
            'D', Items.getItem("clayDust"),
            'C', Items.getItem("waterCell")
        });

        Recipes.advRecipes.addRecipe(new ItemStack(teleportationCore, 1), new Object[]{
            "ABA", "CDC", "ABA",
            'A', Items.getItem("doubleInsulatedGoldCableItem"),
            'B', Items.getItem("electronicCircuit"),
            'C', Item.redstone,
            'D', ingotSomnium
        });

        Recipes.advRecipes.addRecipe(new ItemStack(PortalFrameHeaven, 1), new Object[]{
            "ABA", "CDC", "ABA",
            'A', new ItemStack(30147, 1, 0), //Iridium plate
            'B', new ItemStack(30089, 1, 0), //Improved heat sink
            'C', ingotSomnium,
            'D', PortalFrameLvl3
        });

        Recipes.advRecipes.addRecipe(new ItemStack(PortalStarterHeaven, 1), new Object[]{
            "ABA", "CDC", "ABA",
            'A', new ItemStack(30147, 1, 0), //Iridium plate
            'B', new ItemStack(30089, 1, 0), //Improved heat sink
            'C', ingotSomnium,
            'D', PortalStarterLvl3
        });

        Recipes.advRecipes.addRecipe(new ItemStack(PortalFrameLvl3, 1), new Object[]{
            "ABA", "CDC", "CEC",
            'A', new ItemStack(223, 1, 1), //Tesla coil
            'B', PortalFrameLvl2,
            'C', new ItemStack(30091, 1, 0), //Fast heat sink
            'D', Items.getItem("advancedCircuit"),
            'E', new ItemStack(30086, 1, 0)
        });

        Recipes.advRecipes.addRecipe(new ItemStack(PortalStarterLvl3, 1), new Object[]{
            "ABA", "CCC", "CDC",
            'A', new ItemStack(223, 1, 1), //Tesla coil
            'B', PortalStarterLvl2,
            'C', Items.getItem("mfsUnit"), //Fast heat sink
            'D', Items.getItem("hvTransformer")
        });

        Recipes.advRecipes.addRecipe(new ItemStack(PortalFrameLvl2, 1), new Object[]{
            "ABA", "CDC", "AEA",
            'A', new ItemStack(30092, 1, 0), //Reactor cooler
            'B', new ItemStack(223, 1, 1), //Tesla coil
            'C', Items.getItem("advancedCircuit"),
            'D', Items.getItem("advancedMachine"),
            'E', new ItemStack(30147, 1, 0) //Iridium plate
        });

        Recipes.advRecipes.addRecipe(new ItemStack(PortalStarterLvl2, 1), new Object[]{
            "ABA", "CDC", "AEA",
            'A', Items.getItem("mfsUnit"),
            'B', new ItemStack(223, 1, 1), //Tesla coil
            'C', Items.getItem("advancedCircuit"),
            'D', Items.getItem("advancedMachine"),
            'E', new ItemStack(30147, 1, 0) //Iridium plate
        });

        GameRegistry.addSmelting(Apocalyptic.Surface.blockID, new ItemStack(Apocalyptic.BakedSurface), 0F);
        GameRegistry.addSmelting(Apocalyptic.SomniumOre.blockID, new ItemStack(Apocalyptic.ingotSomnium), 0F);

        Recipes.macerator.addRecipe(new ItemStack(Apocalyptic.Surface, 1), new ItemStack(Block.sand, 2));
        Recipes.macerator.addRecipe(new ItemStack(Apocalyptic.BakedSurface, 1), new ItemStack(Items.getItem("clayDust").getItem(), 4));

        apLogger.info("[Apocalyptic] Initialization completed");
    }

    @ServerStarting
    public void serverStarting(FMLServerStartingEvent event) {

        event.registerServerCommand(new AdminCommand());

        event.registerServerCommand(new RegenerationCommand());

        event.registerServerCommand(new TeleportationCommand());

        event.registerServerCommand(new InfoCommand());

        BlockStone.silverfishChance = silverfishChance;
        apLogger.info("[Apocalyptic] Silverfish spawn chance setted");

        mlt = new MainLifeThread();
        apLogger.info("[Apocalyptic] Main life system loaded");

        PermissionNode.setPermissions();
        apLogger.info("[Apocalyptic] Apocalyptic permissions setted");
    }

    @ServerStopping
    public void serverStopping(FMLServerStoppingEvent event) {
        mlt.saveAllPlayers();
        apLogger.info("[Apocalyptic] Main life system unloaded with saving");
    }

    @PostInit
    public void modsLoaded(FMLPostInitializationEvent evt) {

        CraftingManager.getInstance().removeRestrictedRecipes();

        apLogger.info("Mod loading completed");

        CraftingManagers.smelterManager.addRecipe(512, Items.getItem("goldDust"), new ItemStack(Item.gunpowder, 1), new ItemStack(Item.blazePowder, 1), false);

        CraftingManagers.pulverizerManager.addRecipe(1024, new ItemStack(Block.sandStone, 1), new ItemStack(Block.sand, 1), true);
    }
}