package info.loenwind.autoconfig.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraftforge.fml.common.network.ByteBufUtils;

import info.loenwind.autoconfig.util.NullHelper;
import io.netty.buffer.ByteBuf;

public final class ByteBufAdapterRegistry {

    private static final Map<String, Integer> NAME2ID = new HashMap<>();
    private static List<IByteBufAdapter<?>> ID2ADAPTER = new ArrayList<>();

    public static void register(IByteBufAdapter<?> adapter) {
        NAME2ID.put(adapter.getName(), ID2ADAPTER.size());
        ID2ADAPTER.add(adapter);
    }

    public static int getID(IByteBufAdapter<?> adapter) {
        return NAME2ID.get(adapter.getName());
    }

    public static IByteBufAdapter<?> get(int id) {
        return NullHelper.notnull(ID2ADAPTER.get(id), "Bad mapping " + id);
    }

    public static void saveMapping(ByteBuf buf) {
        synchronized (NAME2ID) {
            buf.writeByte(ID2ADAPTER.size());
            for (IByteBufAdapter<?> adapter : ID2ADAPTER) {
                ByteBufUtils.writeUTF8String(buf, adapter.getName());
            }
        }
    }

    // side-only: client
    public static void loadMapping(ByteBuf buf) {
        synchronized (NAME2ID) {
            int count = buf.readByte();
            if (count != ID2ADAPTER.size()) {
                throw new RuntimeException("Server and client ByteBufAdapter lists are out of sync. Server has " +
                        count + ", client has " + ID2ADAPTER.size());
            }
            List<IByteBufAdapter<?>> synced = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                String name = ByteBufUtils.readUTF8String(buf);
                Integer oldID = NAME2ID.get(name);
                if (oldID == null) {
                    throw new RuntimeException("Server and client ByteBufAdapter lists are out of sync. Server has " +
                            name + ", client doesn't know that one");
                }
                IByteBufAdapter<?> adapter = ID2ADAPTER.get(oldID);
                synced.add(adapter);
                NAME2ID.put(name, i);
            }
            ID2ADAPTER = synced;
        }
    }
}
