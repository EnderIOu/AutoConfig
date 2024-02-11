package info.loenwind.autoconfig.factory;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;

public interface IByteBufAdapter<T> {

    void saveValue(final ByteBuf buf, T value);

    @Nullable
    T readValue(final ByteBuf buf);

    String getName();
}
