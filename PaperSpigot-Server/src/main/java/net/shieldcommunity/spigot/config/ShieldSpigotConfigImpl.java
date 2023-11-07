package net.shieldcommunity.spigot.config;

import net.elytrium.serializer.NameStyle;
import net.elytrium.serializer.SerializerConfig;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
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

    @Comment(value = {
            @CommentValue("ShieldSpigot - Performant & Anti-Exploit tunned for 1.8.8"),
            @CommentValue(""),
            @CommentValue("Version: 0.0.5"),
            @CommentValue("Author: xism4 (discord)"),
            @CommentValue(""),
            @CommentValue("Support discord: https://discord.shieldcommunity.net/"),
            @CommentValue("Polymart: https://polymart.org/resource/shieldspigot-30-off.4906"),
    })

    @Comment(value = {
            @CommentValue(
                    "You must change the default field for your license"
            ),
            @CommentValue(
                    "You can get your license in https://licenses.shieldcommunity.net/"
            )
    })
    public String YOUR_LICENSE = "YOUR-LICENSE";
}
