package info.loenwind.autoconfig;

import java.io.File;

import javax.annotation.Nullable;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import info.loenwind.autoconfig.factory.ByteBufAdapters;
import info.loenwind.autoconfig.factory.IRootFactory;
import info.loenwind.autoconfig.util.Log;
import info.loenwind.autoconfig.util.NullHelper;

public class ConfigHandler {

    protected final File configDirectory;
    protected final Configuration config;

    protected final IRootFactory factory;

    public ConfigHandler(FMLPreInitializationEvent event, IRootFactory factory) {
        this(event, factory, null);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public ConfigHandler(FMLPreInitializationEvent event, IRootFactory factory, @Nullable String folder) {
        this.factory = factory;
        ByteBufAdapters.NONE.getClass(); // trigger system init
        MinecraftForge.EVENT_BUS.register(this);
        if (folder != null) {
            configDirectory = new File(event.getModConfigurationDirectory(), folder);
        } else {
            configDirectory = NullHelper.notnullF(event.getModConfigurationDirectory(),
                    "FMLPreInitializationEvent has no config folder");
        }
        if (!configDirectory.exists()) {
            configDirectory.mkdir();
        }

        File configFile = new File(configDirectory, factory.getModid() + ".cfg");
        config = new Configuration(configFile);
        syncConfig();
    }

    public void syncConfig() {
        try {
            factory.setConfig(config);
        } catch (Exception e) {
            Log.error(factory.getModid() + " has a problem loading its configuration:");
            e.printStackTrace();
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    @SubscribeEvent
    public void onConfigChanged(OnConfigChangedEvent event) {
        if (event.getModID().equals(factory.getModid())) {
            Log.info("Updating config...");
            syncConfig();
        }
    }
}
