package info.loenwind.autoconfig.factory;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import info.loenwind.autoconfig.util.NullHelper;
import io.netty.buffer.ByteBuf;

public class PacketConfigSync implements IMessage {

    protected @Nullable IValueFactory factory;
    protected @Nullable String modid, section;
    protected @Nullable ByteBuf bufferCopy;

    public PacketConfigSync() {
        this.factory = null;
    }

    PacketConfigSync(IValueFactory factory) {
        this.factory = factory;
    }

    @SuppressWarnings("null")
    @Override
    public void toBytes(@Nullable ByteBuf buf) {
        NullHelper.notnull(factory, "factory was null");
        NullHelper.notnull(buf, "buffer was null");
        ByteBufUtils.writeUTF8String(buf, factory.getModid());
        ByteBufUtils.writeUTF8String(buf, factory.getSection());
        ByteBufAdapterRegistry.saveMapping(buf);
        factory.save(buf);
    }

    @SuppressWarnings("null")
    @Override
    public void fromBytes(@Nullable ByteBuf buf) {
        NullHelper.notnull(buf, "buffer was null");
        modid = ByteBufUtils.readUTF8String(buf);
        section = ByteBufUtils.readUTF8String(buf);
        ByteBufAdapterRegistry.loadMapping(buf);
        bufferCopy = buf.copy();
    }

    public static class Handler implements IMessageHandler<PacketConfigSync, IMessage> {

        @SuppressWarnings("null")
        @Override
        public IMessage onMessage(@Nullable PacketConfigSync message, @Nullable MessageContext ctx) {
            NullHelper.notnull(message, "message was null");
            if (!Minecraft.getMinecraft().isIntegratedServerRunning()) {
                NullHelper.notnull(message.modid, "modid was null");
                NullHelper.notnull(message.section, "section was null");
                NullHelper.notnull(message.bufferCopy, "buffer was null");
                FactoryManager.read(message.modid, message.section, message.bufferCopy);
            }
            if (message.bufferCopy != null) {
                message.bufferCopy.release();
            }
            return null;
        }
    }
}
