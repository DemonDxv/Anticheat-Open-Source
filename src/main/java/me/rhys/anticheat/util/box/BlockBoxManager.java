package me.rhys.anticheat.util.box;


import lombok.Getter;
import me.rhys.anticheat.tinyprotocol.api.ProtocolVersion;
import me.rhys.anticheat.util.box.boxes.*;

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
        } else if (version.equalsIgnoreCase("1_9_R1")) {
            blockBox = new BlockBox1_9_R1();
        } else if (version.equalsIgnoreCase("1_9_R2")) {
            blockBox = new BlockBox1_9_R2();
        } else if (version.equalsIgnoreCase("1_10_R1")) {
            blockBox = new BlockBox1_10_R1();
        } else if (version.equalsIgnoreCase("1_11_R1")) {
            blockBox = new BlockBox1_11_R1();
        } else if (version.equalsIgnoreCase("1_12_R1")) {
            blockBox = new BlockBox1_12_R1();
        } else if (version.equalsIgnoreCase("1_13_R1")) {
            blockBox = new BlockBox1_13_R1();
        } else if (version.equalsIgnoreCase("1_14_R1")) {
            blockBox = new BlockBox1_14_R1();
        } else if (version.equalsIgnoreCase("1_15_R1")) {
            blockBox = new BlockBox1_15_R1();
        } else if (version.equalsIgnoreCase("1_16_R1")) {
            blockBox = new BlockBox1_16_R1();
        } else if (version.equalsIgnoreCase("1_16_R2")) {
            blockBox = new BlockBox1_16_R2();
        } else if (version.equalsIgnoreCase("1_16_R3")) {
            blockBox = new BlockBox1_16_R3();
        }
    }
}
