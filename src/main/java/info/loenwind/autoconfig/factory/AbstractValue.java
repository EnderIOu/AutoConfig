package info.loenwind.autoconfig.factory;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import info.loenwind.autoconfig.util.Lang;
import info.loenwind.autoconfig.util.Log;
import info.loenwind.autoconfig.util.NullHelper;
import io.netty.buffer.ByteBuf;

public abstract class AbstractValue<T> implements IValue<T> {

    protected final IValueFactory owner;
    protected int valueGeneration = 0;
    protected final String section, keyname;
    private final String text;
    protected final T defaultValue;
    protected @Nullable T value = null;
    protected @Nullable Double minValue, maxValue;
    private boolean isSynced = false;
    protected boolean isStartup = false;

    protected AbstractValue(IValueFactory owner, String section, String keyname, T defaultValue, String text) {
        this.owner = owner;
        this.section = section;
        this.keyname = keyname;
        this.text = text;
        this.defaultValue = defaultValue;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public T get() {
        if (value == null || valueGeneration != owner.getGeneration()) {
            final Map<String, Object> serverConfig = owner.getServerConfig();
            if (serverConfig != null && serverConfig.containsKey(keyname)) {
                try {
                    value = (T) serverConfig.get(keyname);
                } catch (java.lang.ClassCastException e) {
                    // I'm quite sure this will not happen here but when the caller gets the value.
                    // I'm not sure how to catch it at all, but it actually should not be possible to happen
                    // as the server config is generated in code, and that code should be the same on server and client.
                    Log.error("Server config value ", keyname, " is invalid");
                    value = null;
                }
            } else {
                value = makeValue();
                if (!owner.isInInit() && owner.getConfig().hasChanged()) {
                    owner.getConfig().save();
                }
            }
            valueGeneration = owner.getGeneration();
        }
        return NullHelper.first(value, defaultValue);
    }

    protected abstract @Nullable T makeValue();

    @Override
    @Nonnull
    public IValue<T> setMin(double min) {
        this.minValue = min;
        return this;
    }

    @Override
    @Nonnull
    public IValue<T> setMax(double max) {
        this.maxValue = max;
        return this;
    }

    @Override
    @Nonnull
    public IValue<T> sync() {
        if (!isSynced) {
            isSynced = true;
            owner.addSyncValue(this);
        }
        return this;
    };

    @Override
    @Nonnull
    public IValue<T> startup() {
        isStartup = true;
        return sync();
    }

    @SideOnly(Side.CLIENT)
    public void onServerSync(Map<String, Object> serverConfig) {
        if (isStartup && serverConfig.containsKey(keyname)) {
            @SuppressWarnings("unchecked")
            T serverValue = (T) serverConfig.get(keyname);
            T clientValue = get();
            if (!clientValue.equals(serverValue)) {
                Log.error(Lang.NETWORK_BAD_CONFIG.get(section, keyname, serverValue));
                Minecraft.getMinecraft().player.connection.getNetworkManager()
                        .closeChannel(Lang.NETWORK_BAD_CONFIG.toChat(section, keyname, serverValue));
            }
        }
    }

    public IValue<T> preload() {
        owner.addValue(this);
        return this;
    };

    public void save(final ByteBuf buf) {
        ByteBufAdapters.STRING127.saveValue(buf, keyname);
        IByteBufAdapter<T> dataType = getDataType();
        buf.writeByte(ByteBufAdapterRegistry.getID(dataType));
        dataType.saveValue(buf, get());
    }

    protected abstract IByteBufAdapter<T> getDataType();

    protected String getText() {
        return text + (isStartup ? FactoryManager.SERVER_SYNC : (isSynced ? FactoryManager.SERVER_OVERRIDE : ""));
    }
}
