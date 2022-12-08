package me.xmrvizzy.skyblocker.config;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;

import java.net.Proxy.Type;
import java.util.ArrayList;
import java.util.List;

@Config(name = "skyblocker")
public class SkyblockerConfig implements ConfigData {

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.TransitiveObject
    public General general = new General();

    @ConfigEntry.Category("locations")
    @ConfigEntry.Gui.TransitiveObject
    public Locations locations = new Locations();

    @ConfigEntry.Category("messages")
    @ConfigEntry.Gui.TransitiveObject
    public Messages messages = new Messages();

    @ConfigEntry.Category("waypoint")
    @ConfigEntry.Gui.TransitiveObject
    public Waypoint waypoint = new Waypoint();

    @ConfigEntry.Category("hitbox")
    @ConfigEntry.Gui.TransitiveObject
    public Hitbox hitbox = new Hitbox();

    @ConfigEntry.Category("items")
    @ConfigEntry.Gui.TransitiveObject
    public Items items = new Items();
    
    @ConfigEntry.Category("ui")
    @ConfigEntry.Gui.TransitiveObject
    public UI ui = new UI();

    @ConfigEntry.Category("solvers")
    @ConfigEntry.Gui.TransitiveObject
    public Solvers solvers = new Solvers();
    
    @ConfigEntry.Category("network")
    @ConfigEntry.Gui.TransitiveObject
    public Network network = new Network();

    @ConfigEntry.Category("debug")
    @ConfigEntry.Gui.TransitiveObject
    public Debug debug = new Debug();


    public static class General {
        public String apiKey;
        public boolean cooldownDisplay = true;
        public boolean readableBazaarGraphs = false;

        @ConfigEntry.Category("tabList")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
        public TabList tabList = new TabList();

        @ConfigEntry.Category("sidebar")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
        public Sidebar sidebar = new Sidebar();

        @ConfigEntry.Category("bars")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
        public Bars bars = new Bars();

        @ConfigEntry.Gui.Excluded
        public List<Integer> lockedSlots = new ArrayList<>();
    }

    public static class TabList {
        @ConfigEntry.Gui.PrefixText
        public float tabSize = 1.0F;
        public boolean hideStatus = true;
    }

    public static class Sidebar {
        public boolean spookyCandy = true;
        public boolean lastCommission = true;
    }

    public static class Bars {
        public boolean enableBars = true;
    }

    public static class Hitbox {
        public boolean hitboxForAllHypixel = false;
        public boolean oldPlayerHitbox = false;
        public boolean oldSneakingEyeHeight = true;
        public boolean oldFarmlandHitbox = true;
    }

    public static class Items {
        public boolean priceInfoTooltip = true;
        public boolean potionOverlay = true;
        public boolean potionOverlayLevels = false;
        public boolean potionOverlayXPBoostTypes = true;
        @ConfigEntry.Gui.Tooltip
        public boolean particleHiderHotbar = false;
        public boolean hideParticleFrozenScythe = true;
        public boolean hideParticleDreadlord = true;
        public float potionOverlayScale = 1f;

        @ConfigEntry.Category("cornerMarks")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
        public CornerMarks cornerMarks = new CornerMarks();


    }

    public static class CornerMarks {
        public enum MarkPosition{
            UPPER_RIGHT,
            UPPER_LEFT,
            LOWER_LEFT
        }
        public MarkPosition markPosition = MarkPosition.UPPER_RIGHT;
        public float scale = 0.5f;
        public boolean potionLevels = true;
        public boolean petLevels = true;
        public boolean enchBookLevels = true;
        public boolean wishingCompassUses = true;
        public boolean jerryPotionIntelligence = true;
        public boolean defuserTraps = true;
        public boolean runeLevels = true;
        public boolean trainingWeightsStrength = true;
        public boolean prehistoricEggSteps = true;
    }

    public static class Locations {
        @ConfigEntry.Category("dungeons")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
        public Dungeons dungeons = new Dungeons();

        @ConfigEntry.Category("dwarvenmines")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
        public DwarvenMines dwarvenMines = new DwarvenMines();

        @ConfigEntry.Category("events")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
        public Events events = new Events();
    }

    public static class Waypoint {
        public Float lineWidth = 3.0f;
        public Float outlineWidth = 3.0f;
        public Double labelSize = 0.2d;
        public boolean autoWaypoints = true;
        public boolean autoClean = true;
        public boolean autoCleanFeedback = true;
        public boolean crystalHollowsWarning = true;
    }

    public static class UI {
        public boolean collectionLevels = true;
        public boolean skillLevels = true;
        public boolean hotmPerkLevels = true;
        public boolean sackItemCount = true;
    }

    public static class Dungeons {
        public boolean enableMap = true;
        public boolean solveThreeWeirdos = true;
        public boolean sidebarRanking = true;
        public enum MapPosition{
            UPPER_RIGHT,
            UPPER_LEFT,
            LOWER_LEFT,
            LOWER_RIGHT
        }
        public MapPosition mapPosition=MapPosition.UPPER_LEFT;
        public float mapX=2f;
        public float mapY=2f;
        public float mapScale=1f;
    }

    public static class DwarvenMines {
        public boolean enableDrillFuel = true;
        public boolean solveFetchur = true;
        public boolean solvePuzzler = true;
        public boolean wishingCompassLocator = true;
        public boolean metalDetectorLocator = true;
        @ConfigEntry.Gui.Tooltip
        public boolean metalDetectorLocatorFast = false;
    }
    public static class Events {
        public boolean ancestorSpadeLocator = true;
    }
    public static class Solvers {
        public boolean networkRelaySolver = true;
        public boolean expTableSolver = true;
    }
    public static class Messages {
        public boolean hideAbility = false;
        public boolean autoOpenMenu = false;
        public boolean hideUnbreakable = false;
        public boolean hideHeal = false;
        public boolean hideAOTE = false;
        public boolean hideImplosion = false;
        public boolean hideMoltenWave = false;
        public boolean chatCoords = true;
    }
    public static class Network {
        @ConfigEntry.Gui.PrefixText
        public Type proxyType = Type.DIRECT;
        public String proxyAddress = "";
        public int proxyPort = 8080;
    }
    public static class Debug {
        @ConfigEntry.Gui.PrefixText
        public boolean forceSkyblock = false;
        public boolean forceDungeons = false;
        public String forceArea = "CrystalHollows";
        public String forceServerId = "mini000A";
        public boolean showInternalNameOnRightClick = false;
        public boolean debugPointingLocator = false;
        public boolean debugDistancedLocator = false;
        public boolean debugExpTableSolver = false;
        public boolean printSounds = false;
        public boolean printParticles = false;
    }

    public static void init() {
        AutoConfig.register(SkyblockerConfig.class, GsonConfigSerializer::new);
    }

    public static SkyblockerConfig get() {
        return AutoConfig.getConfigHolder(SkyblockerConfig.class).getConfig();
    }
}
