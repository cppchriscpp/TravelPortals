package net.cpprograms.minecraft.TravelPortals;

import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * TravelPortals block listener
 * @author cppchriscpp
 */
public class TravelPortalsBlockListener extends BlockListener {
    private final TravelPortals plugin;

    public TravelPortalsBlockListener(final TravelPortals plugin) {
        this.plugin = plugin;
    }

    /**
     * Called when a block is placed; let us know if a portal is being created.
     */
    public void onBlockPlace(BlockPlaceEvent event)
    {
    	if (event.getBlock().getTypeId() == plugin.torchtype)
    	{
    		if (plugin.usepermissions && event.getPlayer().hasPermission("travelportals.portal.create"))
    			return;
    		Player player = event.getPlayer();
			int numwalls = 0;
			int x = event.getBlock().getX();
			int y = event.getBlock().getY();
			int z = event.getBlock().getZ();
			int doordir = 0;

            // Test the area to the right of the portal
            if (player.getWorld().getBlockAt(x + 1, y, z).getTypeId() == plugin.blocktype &&
             player.getWorld().getBlockAt(x + 1, y + 1, z).getTypeId() == plugin.blocktype)
                    numwalls++;
            else if (
                (player.getWorld().getBlockAt(x + 1, y, z).getTypeId() == plugin.doortype &&
                 player.getWorld().getBlockAt(x + 1, y + 1, z).getTypeId() == plugin.doortype)
                 ||
                (player.getWorld().getBlockAt(x + 1, y, z).getTypeId() == plugin.doortype2 &&
                 player.getWorld().getBlockAt(x + 1, y + 1, z).getTypeId() == plugin.doortype2)
            )
             {
                    numwalls += 10;
					doordir = 1;
             }

            // Test the area to the left of the portal
            if (player.getWorld().getBlockAt(x - 1, y, z).getTypeId() == plugin.blocktype &&
             player.getWorld().getBlockAt(x - 1, y + 1, z).getTypeId() == plugin.blocktype)
                    numwalls++;
            else if (
                (player.getWorld().getBlockAt(x - 1, y, z).getTypeId() == plugin.doortype &&
                 player.getWorld().getBlockAt(x - 1, y + 1, z).getTypeId() == plugin.doortype)
                 ||
                (player.getWorld().getBlockAt(x - 1, y, z).getTypeId() == plugin.doortype2 &&
                 player.getWorld().getBlockAt(x - 1, y + 1, z).getTypeId() == plugin.doortype2)
            )
             {
                    numwalls += 10;
                    doordir = 3;
             }

            // Test the area in front of the portal
            if (player.getWorld().getBlockAt(x, y, z + 1).getTypeId() == plugin.blocktype &&
             player.getWorld().getBlockAt(x, y + 1, z + 1).getTypeId() == plugin.blocktype)
                    numwalls++;
            else if (
                (player.getWorld().getBlockAt(x, y, z + 1).getTypeId() == plugin.doortype &&
                 player.getWorld().getBlockAt(x, y + 1, z + 1).getTypeId() == plugin.doortype)
                 ||
                (player.getWorld().getBlockAt(x, y, z + 1).getTypeId() == plugin.doortype2 &&
                 player.getWorld().getBlockAt(x, y + 1, z + 1).getTypeId() == plugin.doortype2)
            )
             {
                    numwalls += 10;
                    doordir = 2;
             }

            // Test the area behind the portal
            if (player.getWorld().getBlockAt(x, y, z - 1).getTypeId() == plugin.blocktype &&
             player.getWorld().getBlockAt(x, y + 1, z - 1).getTypeId() == plugin.blocktype)
                    numwalls++;
            else if (
                (player.getWorld().getBlockAt(x, y, z - 1).getTypeId() == plugin.doortype &&
                 player.getWorld().getBlockAt(x, y + 1, z - 1).getTypeId() == plugin.doortype)
                 ||
                (player.getWorld().getBlockAt(x, y, z - 1).getTypeId() == plugin.doortype2 &&
                 player.getWorld().getBlockAt(x, y + 1, z - 1).getTypeId() == plugin.doortype2)
            )
             {
                    numwalls += 10;
                    doordir = 4;
             }

	    	// Numwalls will be exactly 13 if there is one door alongside 3 walls of obsidian.
	    	// This is what we want. (x, y-1, z) is the coordinate above the torch, and needs to be empty.
	    	if (numwalls == 13 && player.getWorld().getBlockAt(x, y+1, z).getType() == Material.AIR)
	    	{
	    		player.getWorld().getBlockAt(x, y, z).setTypeId(plugin.portaltype);
                player.getWorld().getBlockAt(x,y,z).setData((byte)16);
	    		player.getWorld().getBlockAt(x, y+1, z).setTypeId(plugin.portaltype);
                player.getWorld().getBlockAt(x, y+1, z).setData((byte)16);

	    		player.sendMessage("�4You have created a portal! Type /portal help for help using it.");

	    		this.plugin.addWarp(new WarpLocation(x,y,z, doordir, player.getWorld().getName(), player.getName()));
	    		this.plugin.savedata();
	    	}

    	}
    }

    /**
     * Called when a block is broken; let us know if a portal is being destroyed.
     */
    public void onBlockBreak(BlockBreakEvent event)
    {

        // Is this block important to us?
        if (event.getBlock().getTypeId() == plugin.blocktype || event.getBlock().getTypeId() == plugin.doortype || event.getBlock().getTypeId() == plugin.doortype2)
        {
            Player player = event.getPlayer();
            Block block = event.getBlock();
            for (WarpLocation w : plugin.warpLocations)
            {
                // Check if the user actually hit one of them.
                if (((Math.abs(w.getX() - block.getX()) < 2 && Math.abs(w.getZ() - block.getZ()) < 1) || (Math.abs(w.getZ() - block.getZ()) < 2 && Math.abs(w.getX() - block.getX()) < 1)) && (block.getY() - w.getY() < 2 && block.getY() - w.getY() >= 0))
                {
                    // Permissions test
                    if (plugin.usepermissions) {
                    	if (!event.getPlayer().hasPermission("travelportals.portal.destroy")) {
	                        event.setCancelled(true);
	                        return;
                    	}
                    	if (!w.getOwner().equals("") && !w.getOwner().equals(player)) {
                    		if (!event.getPlayer().hasPermission("travelportals.admin.portal.destroy")) {
                    			event.setCancelled(true);
                    			return;
                    		}
                    	}
                    }
                    // They hit an important warping block..destroy the warp point.
                    // Remove the blocks
                    player.getWorld().getBlockAt(w.getX(), w.getY(), w.getZ()).setTypeId(0); //Material.Air);
                    player.getWorld().getBlockAt(w.getX(), w.getY() + 1, w.getZ()).setTypeId(0); //Material.Air);
                    // Remove it from the list of warps
                    this.plugin.warpLocations.remove(plugin.warpLocations.indexOf(w));
                    this.plugin.savedata();
                    // Let the user know he's done a bad, bad thing. :<
                    player.sendMessage("�4You just broke a portal.");
                    break;
                }
            }

        }
    }
}