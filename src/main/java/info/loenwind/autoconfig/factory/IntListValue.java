package info.loenwind.autoconfig.factory;

import java.util.Arrays;

import javax.annotation.Nullable;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.config.Property;

class IntListValue extends AbstractValue<int[]> {

    protected IntListValue(IValueFactory owner, String section, String keyname, int[] defaultValue, String text) {
        super(owner, section, keyname, defaultValue, text);
    }

    @Override
    protected @Nullable int[] makeValue() {
        int min = minValue != null ? minValue.intValue() : Integer.MIN_VALUE,
                max = maxValue != null ? maxValue.intValue() : Integer.MAX_VALUE;
        Property prop = owner.getConfig().get(section, keyname, defaultValue);
        prop.setLanguageKey(keyname);
        prop.setComment(
                getText() + " [range: " + min + " ~ " + max + ", default: " + Arrays.toString(defaultValue) + "]");
        prop.setMinValue(min);
        prop.setMaxValue(max);
        prop.setRequiresMcRestart(isStartup);
        final int[] intList = prop.getIntList();
        for (int i = 0; i < intList.length; i++) {
            intList[i] = MathHelper.clamp(intList[i], min, max);
        }
        return intList;
    }

    @Override
    protected IByteBufAdapter<int[]> getDataType() {
        return ByteBufAdapters.INTEGERARRAY;
    }
}
