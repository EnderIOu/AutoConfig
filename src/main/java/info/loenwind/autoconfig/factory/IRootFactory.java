package info.loenwind.autoconfig.factory;

import net.minecraftforge.common.config.Configuration;

public interface IRootFactory extends IFactory {

    void setConfig(Configuration config);
}
