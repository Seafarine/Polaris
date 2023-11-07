package net.shieldcommunity.spigot.config;

import net.elytrium.serializer.NameStyle;
import net.elytrium.serializer.SerializerConfig;
import net.elytrium.serializer.annotations.Transient;

import java.nio.file.Paths;

public class ShieldSpigotConfigImpl extends SafeYamlSerializable {

    @Transient
    private static final SerializerConfig SPIGOT_CONFIG = new SerializerConfig.Builder()
            .setCommentValueIndent(1)
            .setFieldNameStyle(NameStyle.MACRO_CASE)
            .setNodeNameStyle(NameStyle.KEBAB_CASE)
            .build();

    public static final ShieldSpigotConfigImpl IMP = new ShieldSpigotConfigImpl();

    public ShieldSpigotConfigImpl() {
        super(Paths.get("ShieldSpigot","config.yml"), SPIGOT_CONFIG);
    }

}
