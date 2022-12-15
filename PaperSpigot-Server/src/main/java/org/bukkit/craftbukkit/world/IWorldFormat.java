package org.bukkit.craftbukkit.world;

import net.minecraft.server.IChunkLoader;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.WorldData;
import net.minecraft.server.WorldProvider;

public interface IWorldFormat {

    IChunkLoader createChunkLoader(WorldProvider worldProvider);

    WorldData loadWorldData();

    void saveWorldData(WorldData worldData, NBTTagCompound nbt);

}
