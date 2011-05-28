package com.bukkit.toasterktn.SphereWorld.Chunk;


import java.util.Iterator;
import java.util.List;

import net.minecraft.server.IChunkProvider;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.ChunkLoadEvent;

import com.bukkit.toasterktn.SphereWorld.SphereChunkProvider;
import com.bukkit.toasterktn.SphereWorld.OtherWorldChunkProvider;
import com.bukkit.toasterktn.SphereWorld.SphereWorld;
import com.bukkit.toasterktn.SphereWorld.Config.SphereWorldConfig;

public class ChunkListener extends WorldListener{
	public static SphereWorld plugin;
	private IChunkProvider scp;

	public ChunkListener(SphereWorld instance) {
		plugin = instance;
		World world = plugin.getServer().getWorld(SphereWorldConfig.world); 
		if (SphereWorldConfig.otherworld) {
		    scp = new OtherWorldChunkProvider(((CraftWorld)world).getHandle(), 0L);
		} else {
		    scp = new SphereChunkProvider(((CraftWorld)world).getHandle(), 0L, plugin.spheres, plugin);
		}
		((CraftWorld)world).getHandle().chunkProviderServer.chunkProvider = scp;
		
		if (plugin.oldchunks.thisoldchunks.size() < 10) {
		    System.out.println("First run..");
		    System.out.print("working.. please wait.. this may take several minutes");
		    Chunk chunks[] = world.getLoadedChunks();
		    for(int ci = 0; ci < chunks.length; ci++)
		    {
		     Chunk c = chunks[ci];
		     if (!plugin.oldchunks.isInChunkList(c)) {
			 world.regenerateChunk(c.getX(),c.getZ());
		     }
		     if(ci % 50 == 0)
		          System.gc();
		    }
		    for(int ci = 0; ci < chunks.length; ci++)
		    {
		     Chunk c = chunks[ci];
		     if (!plugin.oldchunks.isInChunkList(c)) {
			 world.regenerateChunk(c.getX(),c.getZ());
			 ((World)plugin.getServer().getWorlds().get(0)).unloadChunk(c.getX(), c.getZ());
			 chunks[ci] = null;
		     }
		     if(ci % 50 == 0)
		          System.gc();
		    }
		    List<Entity> entities = world.getEntities();
		    Entity e;
		    for(Iterator<Entity> iterator = entities.iterator(); iterator.hasNext(); e.remove())
			e = (Entity)iterator.next();
		}
	};
	
	
	public void onChunkLoad(ChunkLoadEvent event) {
		if (event.getType().equals(Event.Type.CHUNK_LOAD)) {
		    // Test if this world is a SphereWorld..
		    if (SphereWorldConfig.world.equalsIgnoreCase(event.getWorld().getName())) {
			    if (!plugin.oldchunks.isInChunkList(event.getChunk())) {
				event.getWorld().regenerateChunk(event.getChunk().getX(), event.getChunk().getZ());
				plugin.oldchunks.AddChunkToList(event.getChunk());	
			   }
			
		    }
		}
	}
}