package me.rhys.anticheat.util.box;


import lombok.Getter;
import me.rhys.anticheat.tinyprotocol.api.ProtocolVersion;
import me.rhys.anticheat.util.box.boxes.BlockBox1_7_R4;
import me.rhys.anticheat.util.box.boxes.BlockBox1_8_R1;
import me.rhys.anticheat.util.box.boxes.BlockBox1_8_R2;
import me.rhys.anticheat.util.box.boxes.BlockBox1_8_R3;

@Getter
public class BlockBoxManager {
    private BlockBox blockBox;

    public BlockBoxManager() {
        String version = ProtocolVersion.getGameVersion().getServerVersion().replaceAll("v", "");

        if (version.equalsIgnoreCase("1_7_R4")) {
            blockBox = new BlockBox1_7_R4();
        } else if (version.equalsIgnoreCase("1_8_R1")) {
            blockBox = new BlockBox1_8_R1();
        } else if (version.equalsIgnoreCase("1_8_R2")) {
            blockBox = new BlockBox1_8_R2();
        } else if (version.equalsIgnoreCase("1_8_R3")) {
            blockBox = new BlockBox1_8_R3();
        }
    }
}
