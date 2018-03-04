package com.extendedclip.papi.expansion.askyblock;

import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.Cacheable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.wasteofplastic.askyblock.ASkyBlockAPI;

public class ASkyBlockExpansion extends PlaceholderExpansion implements Cacheable {
	
	private ASkyBlockAPI api;
	
	@Override
	public boolean canRegister() {
		return Bukkit.getPluginManager().getPlugin(getPlugin()) != null;
	}
	
	@Override
	public boolean register() {
		api = ASkyBlockAPI.getInstance();
		if (api != null) return super.register();
		return false;
	}

	@Override
	public String getAuthor() {
		return "clip";
	}

	@Override
	public String getIdentifier() {
		return "askyblock";
	}

	@Override
	public String getPlugin() {
		return "ASkyBlock";
	}

	@Override
	public String getVersion() {
		return "1.2.0";
	}

	@SuppressWarnings("deprecation")
	@Override
	public String onPlaceholderRequest(Player p, String identifier) {

		if (api == null || p == null) {
			return "";
		}
		
		switch (identifier) {
		
		case "level":
			return String.valueOf(api.getIslandLevel(p.getUniqueId()));
		case "has_island":
			return api.hasIsland(p.getUniqueId()) ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
		case "island_x":
			return String.valueOf(api.getIslandLocation(p.getUniqueId()).getBlockX());
		case "island_y":
			return String.valueOf(api.getIslandLocation(p.getUniqueId()).getBlockY());
		case "island_z":
			return String.valueOf(api.getIslandLocation(p.getUniqueId()).getBlockZ());
		case "island_world":
			return api.getIslandLocation(p.getUniqueId()).getWorld().getName();
		case "team_size":
			return api.getTeamMembers(p.getUniqueId()) != null ? 
					String.valueOf(api.getTeamMembers(p.getUniqueId()).size()) : "0";
		case "coop_islands":
			return api.getCoopIslands(p) != null ? 
					String.valueOf(api.getCoopIslands(p).size()) : "0";
		}
		
		return null;
	}

	@Override
	public void clear() {
		api = null;
	}	
}
