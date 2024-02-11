package info.loenwind.autoconfig.factory;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

import info.loenwind.autoconfig.util.Log;
import info.loenwind.autoconfig.util.NullHelper;
import io.netty.buffer.ByteBuf;

public class FactoryManager {

    private static boolean registered = false;

    private static void create() {
        if (!registered) {
            MinecraftForge.EVENT_BUS.register(FactoryManager.class);
            Network.create();
            registered = true;
        }
    }

    public static final String SERVER_OVERRIDE = " (synced from server)";
    public static final String SERVER_SYNC = " (must be kept in sync with server)";
    private static final Map<String, IValueFactory> factories = new HashMap<>();

    private static int overrides = 0;

    public static boolean hasOverrides() {
        return overrides > 0;
    }

    static void read(String mod, String section, final ByteBuf buf) {
        final IValueFactory factory = factories.get(mod + "." + section);
        if (factory != null) {
            Log.debug("Read " + factory.read(buf) + " config values from server packet for " + mod + " (" + section +
                    ")");
            overrides++;
        } else {
            Log.warn("Ignoring configuration for unknown mod or section " + mod + " (" + section +
                    ") sent by the server");
        }
    }

    static void registerFactory(IValueFactory factory) {
        synchronized (factories) {
            create();
            factories.put(factory.getModid() + "." + factory.getSection(), factory);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggon(final PlayerLoggedInEvent evt) {
        for (IValueFactory factory : factories.values()) {
            if (factory.needsSyncing()) {
                Network.sendTo(new PacketConfigSync(factory),
                        (EntityPlayerMP) NullHelper.notnullF(evt.player, "PlayerLoggedInEvent without player"));
                Log.debug("Sent config to player " + evt.player.getDisplayNameString() + " for " + factory.getModid() +
                        " (" + factory.getSection() + ")");
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(final ClientDisconnectionFromServerEvent event) {
        for (IValueFactory factory : factories.values()) {
            factory.endServerOverride();
            Log.debug("Removed server config override for " + factory.getModid() + " (" + factory.getSection() + ")");
        }
        overrides = 0;
    }
}
