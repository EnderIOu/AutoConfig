package info.loenwind.autoconfig.factory;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraftforge.fluids.Fluid;

import io.netty.buffer.ByteBuf;

public interface IValueFactory extends IFactory {

    IValue<Integer> make(String keyname, int defaultValue, String text);

    IValue<int[]> make(String keyname, int[] defaultValue, String text);

    IValue<Double> make(String keyname, double defaultValue, String text);

    IValue<Float> make(String keyname, float defaultValue, String text);

    IValue<String> make(String keyname, String defaultValue, String text);

    IValue<List<String>> make(String keyname, List<String> defaultValue, String text);

    IValue<String> make(String keyname, String defaultValue, String[] limit, String text);

    IValue<Boolean> make(String keyname, Boolean defaultValue, String text);

    <E extends Enum<E>> IValue<E> make(String keyname, E defaultValue, String text);

    /**
     * Please note that fluids won't work in or before preinit!
     */
    IValue<Fluid> makeFluid(String keyname, String defaultValue, String text);

    void endServerOverride();

    void addSyncValue(AbstractValue<?> value);

    @Nullable
    Map<String, Object> getServerConfig();

    boolean needsSyncing();

    void save(final ByteBuf buf);

    int read(final ByteBuf buf);
}
