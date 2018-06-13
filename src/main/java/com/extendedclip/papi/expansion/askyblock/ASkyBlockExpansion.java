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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.CoopPlay;
import com.wasteofplastic.askyblock.Settings;

public class ASkyBlockExpansion extends PlaceholderExpansion implements Cacheable {

	private ASkyBlock askyblock;

	@Override
	public boolean canRegister() {
		return Bukkit.getPluginManager().getPlugin(getPlugin()) != null;
	}

	@Override
	public boolean register() {
		
		this.askyblock = ASkyBlock.getPlugin();
		
		return this.askyblock != null ? super.register() : false;
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
		return "2.0";
	}

	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		if (askyblock != null) {
			final String[] parts = identifier.split("_");
			
			if (parts.length > 0) {
				final String key = parts[0];
				
				if (key.equalsIgnoreCase("top")) {
					if (parts.length == 1) {
						final Integer playerTop = getPlayerTop(player);
						
						if (playerTop != null) {
							return String.valueOf(playerTop);
						} else {
							return "";
						}
					} else if (parts.length == 3) {
						final String data = parts[1];
						final String textRank = parts[2];
						
						if (NumberUtils.isNumber(textRank)) {
							final int rank = Integer.valueOf(textRank);
							final Map.Entry<UUID, Long> entry = getEntryTopIslandOwner(rank-1);
							
							if (entry != null) {
								if (data.equalsIgnoreCase("level")) {
									return String.valueOf(entry.getValue());
								} else if (data.equalsIgnoreCase("name")) {
									final UUID ownerID = entry.getKey();
									final OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerID);
									
									if (owner != null) {
										return owner.getName();
									}
								}
							}
							
							return "";
						}
					}
				}
			}
			
			if (player != null) {
				final UUID playerID = player.getUniqueId();
				
				if (identifier.equalsIgnoreCase("level")) {
					return String.valueOf(askyblock.getPlayers().getIslandLevel(playerID));
				} else if (identifier.equalsIgnoreCase("has_island")) {
					return askyblock.getPlayers().hasIsland(playerID) ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
				} else if (identifier.equalsIgnoreCase("team_size")) {
					final List<UUID> members = askyblock.getPlayers().getMembers(playerID);
					final int size = members != null ? members.size() : 0;
					
					return String.valueOf(size);
				} else if (identifier.equalsIgnoreCase("coop_islands")) {
					final Set<Location> coopIslands = CoopPlay.getInstance().getCoopIslands(player);
					final int size = coopIslands != null ? coopIslands.size() : 0; 
					
					return String.valueOf(size);
				} else if (identifier.equalsIgnoreCase("owner")) {
					final Location location = player.getLocation();
					final UUID ownerID = askyblock.getPlayers().getPlayerFromIslandLocation(location);
					
					if (ownerID != null) {
						final OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerID); 
						
						if (owner != null) {
							return owner.getName();
						}
					}
					
					return "";
				} else if (identifier.equalsIgnoreCase("team_leader")) {
					final UUID leaderID = askyblock.getPlayers().getTeamLeader(playerID);
					
					if (leaderID != null) {
						final OfflinePlayer leader = Bukkit.getOfflinePlayer(leaderID);
						
						if (leader != null) {
							return leader.getName();
						}
					}
					
					return "";
				} else if (identifier.equalsIgnoreCase("has_team")) {
					return askyblock.getPlayers().inTeam(playerID) ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
				} else if (parts.length > 0) {
					final String key = parts[0];
					
					if (key.equalsIgnoreCase("island")) {
						final Location location = askyblock.getPlayers().getIslandLocation(playerID);
						
						if (location != null) {
							if (parts.length > 1) {
								final String data = parts[1];
								
								if (data.equalsIgnoreCase("x")) {
									return String.valueOf(location.getBlockX());
								} else if (data.equalsIgnoreCase("y")) {
									return String.valueOf(location.getBlockY());
								} else if (data.equalsIgnoreCase("z")) {
									return String.valueOf(location.getBlockZ());
								}
							}
						}
					} else if (key.equalsIgnoreCase("members")) {
						if (parts.length > 1) {
							final String data = parts[1];
							
							if (data.equalsIgnoreCase("max")) {
								
								int maxTeam = Settings.maxTeamSize;
								
								if (!player.hasPermission("askyblock.team.maxsize.*")) {
									for (PermissionAttachmentInfo perms : player.getEffectivePermissions()) {
										if (perms.getPermission().startsWith("askyblock.team.maxsize.")) {
											final String[] components = perms.getPermission().split("askyblock.team.maxsize.");
											
											if (components.length > 1) {
												final String textValue = components[1];
												
												if (NumberUtils.isNumber(textValue)) {
													maxTeam = Math.max(maxTeam, Integer.valueOf(textValue));
												}
											}
										}
									}
								}
								
								if (maxTeam < 1) {
									maxTeam = 1;
								}
								
								return String.valueOf(maxTeam);
							} else if (data.equalsIgnoreCase("online")) {
								if (askyblock.getPlayers().inTeam(playerID)) {
									final List<UUID> members = askyblock.getPlayers().getMembers(playerID);
									
									int count = 0;
									
									if (members != null) {
										for (UUID member : members) {
											final Player online = Bukkit.getPlayer(member);
											
											if (online != null) {
												count++;
											}
										}
									}
									
									return String.valueOf(count);
								} else {
									return String.valueOf(1);
								}
							}
						}
					}
				}
			}
		}
		
		return null;
	}

	@Override
	public void clear() {
		askyblock = null;
	}
	
	private final Map<UUID, Long> getMapIslandOwner() {
		final Map<UUID, Long> mapIslandOwner = new HashMap<UUID, Long>();
		
		for (UUID ownerID : askyblock.getGrid().getOwnershipMap().keySet()) {
			final long level = askyblock.getPlayers().getIslandLevel(ownerID);
			
			mapIslandOwner.put(ownerID, level);
		}
		
		return mapIslandOwner;
	}
	
	private final Integer getPlayerTop(Player player) {
		if (player != null) {
			final UUID playerID = player.getUniqueId();
			final List<Map.Entry<UUID, Long>> listSortIslandOwner = getSortIslandOwner();
			
			for (int index = 0; index < listSortIslandOwner.size(); index++) {
				final Map.Entry<UUID, Long> entry = listSortIslandOwner.get(index);
				final UUID entryOwnerID = entry.getKey();
				
				if (entryOwnerID.equals(playerID)) {
					return index + 1;
				}
			}
		}
		
		return null;
	}
	
	private final Map.Entry<UUID, Long> getEntryTopIslandOwner(int index) {
		final List<Map.Entry<UUID, Long>> listSortIslandOwner = getSortIslandOwner();
		
		return index < listSortIslandOwner.size() ? listSortIslandOwner.get(index) : null;
	}
	
	private final List<Map.Entry<UUID, Long>> getSortIslandOwner() {
		return sortMap(getMapIslandOwner());
	}
	
	private final List<Map.Entry<UUID, Long>> sortMap(Map<UUID, Long> unsortMap) {
		return sortMap(unsortMap, true);
	}
	
	private final List<Map.Entry<UUID, Long>> sortMap(Map<UUID, Long> unsortMap, boolean ascend) {
		if (unsortMap != null) {
	        final List<Map.Entry<UUID, Long>> list = new LinkedList<Map.Entry<UUID, Long>>(unsortMap.entrySet());
	        final Comparator<Map.Entry<UUID, Long>> comparator = new Comparator<Map.Entry<UUID, Long>>() {
	        	
	        	@Override
	            public int compare(Map.Entry<UUID, Long> map1, Map.Entry<UUID, Long> map2) {
	        		final Long value1 = map1.getValue();
	        		final Long value2 = map2.getValue();
	        		
	                return ascend ? value1.compareTo(value2) : value2.compareTo(value1);
	            }
	        };
	        
	        Collections.sort(list, comparator);
	        
	        return list;
		}
		
		return null;
    }
}
