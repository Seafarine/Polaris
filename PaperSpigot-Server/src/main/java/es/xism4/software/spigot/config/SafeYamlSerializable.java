package es.xism4.software.spigot.config;

import net.elytrium.serializer.SerializerConfig;
import net.elytrium.serializer.language.object.YamlSerializable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Path;

public class SafeYamlSerializable extends YamlSerializable {

    public SafeYamlSerializable(Path path, SerializerConfig config) {
        super(path, config);
    }

    public SafeYamlSerializable(SerializerConfig config) {
        super(config);
    }

    @Override
    public boolean load(BufferedReader reader) {
        try {
            return super.load(reader);
        }
        catch (Throwable e) {
            System.out.println("Failed to load config " + getClass().getSimpleName());
            return false;
        }
    }

    @Override
    public void save(BufferedWriter writer) {
        try {
            super.save(writer);
        }
        catch (Throwable e) {
            System.out.println("Failed to save config " + getClass().getSimpleName());
        }
    }
}