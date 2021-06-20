package me.rhys.bedrock.tinyprotocol.packet.in;

import lombok.Getter;
import me.rhys.bedrock.tinyprotocol.api.NMSObject;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.reflection.FieldAccessor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class WrappedInWindowClickPacket extends NMSObject {
    private static final String packet = Client.WINDOW_CLICK;

    // Fields
    private static final FieldAccessor<Integer> fieldId = fetchField(packet, int.class, 0);
    private static final FieldAccessor<Integer> fieldSlot = fetchField(packet, int.class, 1);
    private static final FieldAccessor<Integer> fieldButton = fetchField(packet, int.class, 2);
    private static final FieldAccessor<Short> fieldAction = fetchField(packet, short.class, 0);
    private static final FieldAccessor<Object> fieldItemStack = fetchField(packet, Type.ITEMSTACK, 0);

    // Decoded data
    private int id;
    private short slot;
    private byte button;
    private short counter;
    private ClickType action;
    private ItemStack item;
    private byte mode;

    public WrappedInWindowClickPacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void updateObject() {

    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        id = fetch(fieldId);
        slot = fetch(fieldSlot).shortValue();
        byte button = fetch(fieldButton).byteValue();
        counter = fetch(fieldAction);
        item = toBukkitStack(fetch(fieldItemStack));

        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)) {
            FieldAccessor<Integer> fieldShift = fetchField(packet, int.class, 3);
            mode = fetch(fieldShift).byteValue();
        } else {
            FieldAccessor<Enum> fieldShift = fetchField(packet, Enum.class, 0);
            mode = (byte) fetch(fieldShift).ordinal();
        }

        if (slot == -1) {
            action = button == 0 ? ClickType.WINDOW_BORDER_LEFT : ClickType.WINDOW_BORDER_RIGHT;
        } else if (mode == 0) {
            if (button == 0) {
                action = ClickType.LEFT;
            } else if (button == 1) {
                action = ClickType.RIGHT;
            }
        } else if (mode == 1) {
            if (button == 0) {
                action = ClickType.SHIFT_LEFT;
            } else if (button == 1) {
                action = ClickType.SHIFT_RIGHT;
            }
        } else if (mode == 2) {
            if (button >= 0 && button < 9) {
                action = ClickType.NUMBER_KEY;
            }
        } else if (mode == 3) {
            if (button == 2) {
                action = ClickType.MIDDLE;
            } else {
                action = ClickType.UNKNOWN;
            }
        } else if (mode == 4) {
            if (slot >= 0) {
                if (button == 0) {
                    action = ClickType.DROP;
                } else if (button == 1) {
                    action = ClickType.CONTROL_DROP;
                }
            } else {
                // Sane default (because this happens when they are holding nothing. Don't ask why.)
                action = ClickType.LEFT;
                if (button == 1) {
                    action = ClickType.RIGHT;
                }
            }
        } else if (mode == 5) {
            action = ClickType.DRAG;
        } else if (mode == 6) {
            action = ClickType.DOUBLE_CLICK;
        }
    }

    public enum ClickType {
        LEFT,
        SHIFT_LEFT,
        RIGHT,
        SHIFT_RIGHT,
        WINDOW_BORDER_LEFT,
        WINDOW_BORDER_RIGHT,
        MIDDLE,
        NUMBER_KEY,
        DOUBLE_CLICK,
        DROP,
        CONTROL_DROP,
        CREATIVE,
        DRAG,
        UNKNOWN;

        public boolean isKeyboardClick() {
            return this == NUMBER_KEY || this == DROP || this == CONTROL_DROP;
        }

        public boolean isCreativeAction() {
            return this == MIDDLE || this == CREATIVE;
        }

        public boolean isRightClick() {
            return this == RIGHT || this == SHIFT_RIGHT;
        }

        public boolean isLeftClick() {
            return this == LEFT || this == SHIFT_LEFT || this == DOUBLE_CLICK || this == CREATIVE;
        }

        public boolean isShiftClick() {
            return this == SHIFT_LEFT || this == SHIFT_RIGHT || this == CONTROL_DROP;
        }
    }
}
