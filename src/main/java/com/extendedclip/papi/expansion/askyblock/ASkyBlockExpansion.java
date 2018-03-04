/*
 *
 * ASkyblock-Expansion
 * Copyright (C) 2018 Ryan McCarthy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package com.extendedclip.papi.expansion.askyblock;

import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.Cacheable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.Island;
import com.wasteofplastic.askyblock.Players;

public class ASkyBlockExpansion extends PlaceholderExpansion implements Cacheable {
	
	private ASkyBlockAPI api;
	
	private ASkyBlock askyblock;
	
	@Override
	public boolean canRegister() {
		return Bukkit.getPluginManager().getPlugin(getPlugin()) != null;
	}
	
	@Override
	public boolean register() {
		askyblock = ASkyBlock.getPlugin();
		if (askyblock == null) return false;
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
		return "1.2.1";
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
		case "owner_at_location":
			Island is = api.getIslandAt(p.getLocation());
			if (is == null) return "";
			Players pl = askyblock.getPlayers().get(is.getOwner());
			return pl != null ? pl.getPlayerName() : "";
		}
		return null;
	}

	@Override
	public void clear() {
		askyblock = null;
		api = null;
	}	
}
