package org.bukkit.craftbukkit.world;

import net.minecraft.server.*;
import org.github.paperspigot.exception.ServerInternalException;

import java.io.*;

public class AnvilFormatLoader implements IWorldFormat {

    private WorldNBTStorage worldStorage;
    private File worldDir, container;

    public AnvilFormatLoader(WorldNBTStorage worldStorage, File worldDir) {
        this.worldStorage = worldStorage;
        this.worldDir = worldDir;
    }

    @Override
    public IChunkLoader createChunkLoader(WorldProvider worldProvider) {
        File container = worldDir;
        if (worldProvider instanceof WorldProviderHell) {
            container = new File(worldDir, "DIM-1");
            container.mkdirs();
        } else if (worldProvider instanceof WorldProviderTheEnd) {
            container = new File(worldDir, "DIM1");
            container.mkdirs();
        }

        return new ChunkRegionLoader(container);
    }

    @Override
    public WorldData loadWorldData() {
        File file = new File(worldDir, "level.dat");
        NBTTagCompound nbttagcompound;
        NBTTagCompound nbttagcompound1;

        if (file.exists()) {
            try {
                nbttagcompound = NBTCompressedStreamTools.a((InputStream) (new FileInputStream(file)));
                nbttagcompound1 = nbttagcompound.getCompound("Data");
                return new WorldData(nbttagcompound1);
            } catch (Exception exception) {
                exception.printStackTrace();
                ServerInternalException.reportInternalException(exception); // Paper
            }
        }

        file = new File(worldDir, "level.dat_old");
        if (file.exists()) {
            try {
                nbttagcompound = NBTCompressedStreamTools.a((InputStream) (new FileInputStream(file)));
                nbttagcompound1 = nbttagcompound.getCompound("Data");
                return new WorldData(nbttagcompound1);
            } catch (Exception exception1) {
                exception1.printStackTrace();
                ServerInternalException.reportInternalException(exception1); // Paper
            }
        }

        return null;
    }

    @Override
    public void saveWorldData(WorldData worlddata, NBTTagCompound nbttagcompound) {
        NBTTagCompound nbttagcompound1 = worlddata.a(nbttagcompound);
        NBTTagCompound nbttagcompound2 = new NBTTagCompound();

        nbttagcompound2.set("Data", nbttagcompound1);

        try {
            File file = new File(worldDir, "level.dat_new");
            File file1 = new File(worldDir, "level.dat_old");
            File file2 = new File(worldDir, "level.dat");

            NBTCompressedStreamTools.a(nbttagcompound2, (OutputStream) (new FileOutputStream(file)));
            if (file1.exists()) {
                file1.delete();
            }

            file2.renameTo(file1);
            if (file2.exists()) {
                file2.delete();
            }

            file.renameTo(file2);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            ServerInternalException.reportInternalException(exception); // Paper
        }
    }

}