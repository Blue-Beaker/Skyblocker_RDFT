package me.xmrvizzy.skyblocker.config;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;

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

    @ConfigEntry.Category("debug")
    @ConfigEntry.Gui.TransitiveObject
    public Debug debug = new Debug();

    public static class General {
        public String apiKey;
        public boolean readableBazaarGraphs = false;

        @ConfigEntry.Category("bars")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
        public Bars bars = new Bars();
        
        @ConfigEntry.Category("hitbox")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
        public Hitbox hitbox = new Hitbox();

        @ConfigEntry.Gui.Excluded
        public List<Integer> lockedSlots = new ArrayList<>();
    }

    public static class Bars {
        public boolean enableBars = true;
    }

    public static class Hitbox {
        public boolean oldFarmlandHitbox = true;
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

    public static class Dungeons {
        public boolean enableMap = true;
        public boolean solveThreeWeirdos = true;
    }

    public static class DwarvenMines {
        public boolean enableDrillFuel = true;
        public boolean solveFetchur = true;
        public boolean solvePuzzler = true;
        public boolean wishingCompassLocator = true;
        public boolean metalDetectorLocator = true;
    }
    public static class Events {
        public boolean ancestorSpadeLocator = true;
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
    public static class Debug {
        public boolean showInternalNameOnRightClick = false;
        public boolean debugPointingLocator = false;
        public boolean debugDistancedLocator = false;
    }

    public static void init() {
        AutoConfig.register(SkyblockerConfig.class, GsonConfigSerializer::new);
    }

    public static SkyblockerConfig get() {
        return AutoConfig.getConfigHolder(SkyblockerConfig.class).getConfig();
    }
}
