package com.bukkit.toasterktn.SphereWorld;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import com.bukkit.toasterktn.SphereWorld.Chunk.ChunckList;
import com.bukkit.toasterktn.SphereWorld.Chunk.ChunkListener;
import com.bukkit.toasterktn.SphereWorld.Config.SphereWorldConfig;
import com.bukkit.toasterktn.SphereWorld.Player.SpherePlayerListener;

public class SphereWorld extends JavaPlugin {
    // Starts the class
    // Links the ChunkListener
    private ChunkListener chunkListener;// = new ChunkListener(this);
    // Spheres
    public Spheres spheres = new Spheres();
    public ChunckList oldchunks = new ChunckList();

    private File chunkfile;
    private File speheresfile;

    public static final Logger log = Logger.getLogger("Minecraft");

    @Override
    // When the plugin is disabled this method is called.
    public void onDisable() {
	oldchunks.WriteChunkList(chunkfile);
	System.out.println("[SphereWorld] Disabled");
    }

    @Override
    // When the plugin is enabled this method is called.
    public void onEnable() {
	// Create the pluginmanage pm.
	SphereWorldConfig.initialize(getDataFolder());
	// Force Worldload
	if(getServer().getWorld(SphereWorldConfig.world) == null) 
	    getServer().createWorld(SphereWorldConfig.world, Environment.NORMAL);
	// Get Chunk data
	chunkfile = new File(getDataFolder(), "chunklist.data");
	oldchunks.ReadChunkList(chunkfile);
	// Get / Create Sphere data
	speheresfile = new File(getDataFolder(), "spheres.data");
	spheres.ReadSphereList(speheresfile, getServer());
	// Create Attach Listener / Attach MyShereGenerator
	if (spheres.GetSphereList().size() < 1) {
	    log.info("[SphereWorld] NOT Loaded");
	    return;
	}
	chunkListener = new ChunkListener(this);

	//Default Stuff
	PluginManager pm = getServer().getPluginManager();
	PluginDescriptionFile pdfFile = this.getDescription();
	log.info("[SphereWorld] version " + pdfFile.getVersion()
		+ " is enabled!");

	// Register a Chunk Creation, we may want to add a Cache
	pm.registerEvent(Event.Type.CHUNK_LOAD, this.chunkListener, Event.Priority.Normal, this);
	// Kill Players on Floor
	if (SphereWorldConfig.killonfloor)
	    pm.registerEvent(Event.Type.PLAYER_MOVE, new SpherePlayerListener(), Event.Priority.Normal, this);
	log.info("[SphereWorld] Loaded");
    }

    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
	if (command.getName().equalsIgnoreCase("regeneratechunk")) {
	    if (!(sender instanceof Player)) {
		log.info("This command cannot be used in the console.");
		return true;
	    }
	    Player player = (Player) sender;
	    if (!player.isOp()) return false;
	    if (args.length >= 2) {
		try {
		    player.getWorld().regenerateChunk(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		    player.sendMessage("�7Regenerated chunk at " + args[0]
			    + "," + args[1] + "!");
		    log.info(player.getName() + " regenerated chunk at "
			    + args[0] + "," + args[1] + "!");
		} catch (NumberFormatException n) {
		    player.sendMessage("�cUnknown chunk coordinates!");
		}
	    } else {
		player.getWorld().regenerateChunk(player.getLocation().getBlock().getChunk().getX(), player.getLocation().getBlock().getChunk().getZ());
		player.sendMessage("�7Regenerated chunk at "
			+ player.getLocation().getBlock().getChunk().getX()
			+ ","
			+ player.getLocation().getBlock().getChunk().getZ()
			+ "!");
		log.info(player.getName() + " regenerated chunk at "
			+ player.getLocation().getBlock().getChunk().getX()
			+ ","
			+ player.getLocation().getBlock().getChunk().getZ()
			+ "!");
	    }
	    return true;
	}
	return false;
    }

}