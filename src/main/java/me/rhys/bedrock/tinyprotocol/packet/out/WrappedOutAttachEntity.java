/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package me.rhys.bedrock.tinyprotocol.packet.out;

import lombok.Getter;
import me.rhys.bedrock.tinyprotocol.api.NMSObject;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.reflection.FieldAccessor;
import org.bukkit.entity.Player;

@Getter
public class WrappedOutAttachEntity extends NMSObject {
    private static final String packet = Server.ATTACH;
    private static final FieldAccessor<Integer> fieldA = fetchField(packet, int.class, 0);
    private static final FieldAccessor<Integer> fieldB = fetchField(packet, int.class, 1);
    private static final FieldAccessor<Integer> fieldC = fetchField(packet, int.class, 2);

    private int a, b, c;


    public WrappedOutAttachEntity(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void updateObject() {

    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        a = fetch(fieldA);
        b = fetch(fieldB);
        c = fetch(fieldC);
    }
}
