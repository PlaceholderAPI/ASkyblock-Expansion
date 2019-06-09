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
import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import java.util.*;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.CoopPlay;
import com.wasteofplastic.askyblock.Settings;

public class ASkyBlockExpansion extends PlaceholderExpansion implements Cacheable, Configurable {

	public ASkyBlock askyblock;

	private Utils utils = new Utils(this);

	@Override
	public boolean canRegister() {
		return Bukkit.getPluginManager().getPlugin(getPlugin()) != null;
	}

	@Override
	public boolean register() {
		
		this.askyblock = ASkyBlock.getPlugin();
		
		return this.askyblock != null && super.register();
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
	public Map<String, Object> getDefaults() {
		Map<String, Object> defaults = new HashMap<String, Object>();
		defaults.put("formatting.digits", 2);
		defaults.put("formatting.thousands", "k");
		defaults.put("formatting.millions", "M");
		defaults.put("formatting.billions", "B");
		defaults.put("formatting.trillions", "T");
		defaults.put("formatting.quadrillions", "Q");
		return defaults;
	}

	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		if (askyblock != null) {
			final String[] parts = identifier.split("_");
			
			if (parts.length > 0) {
				final String key = parts[0];
				
				if (key.equalsIgnoreCase("top")) {
					if (parts.length == 1) {
						final Integer playerTop = utils.getPlayerTop(player);
						
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
							final Map.Entry<UUID, Long> entry = utils.getEntryTopIslandOwner(rank-1);
							
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
				final int size;

				if (parts.length > 0) {
					final String key = parts[0];

					switch (key) {
						case "island":
							final Location location = askyblock.getPlayers().getIslandLocation(playerID);

							if (location != null) {
								if (parts.length > 1) {
									final String data = parts[1];

									switch (data) {
										case "x":
											return String.valueOf(location.getBlockX());
										case "y":
											return String.valueOf(location.getBlockY());
										case "z":
											return String.valueOf(location.getBlockZ());
										case "world":
											return String.valueOf(location.getWorld().getName());
									}
								}
							}
						case "members":
							if (parts.length > 1) {
								final String data = parts[1];
								switch (data) {
									case "max":
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
									case "online":
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
				switch (identifier) {
					case "level":
						return String.valueOf(askyblock.getPlayers().getIslandLevel(playerID));
					case "has_island":
						return askyblock.getPlayers().hasIsland(playerID) ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
					case "team_size":
						List<UUID> members = askyblock.getPlayers().getMembers(playerID);
						size = members != null ? members.size() : 0;

						return String.valueOf(size);
					case "coop_islands":
						Set<Location> coopIslands = CoopPlay.getInstance().getCoopIslands(player);
						size = coopIslands != null ? coopIslands.size() : 0;

						return String.valueOf(size);
					case "owner":
						final Location location = player.getLocation();
						final UUID ownerID = askyblock.getPlayers().getPlayerFromIslandLocation(location);

						if (ownerID != null) {
							final OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerID);

							if (owner != null) {
								return owner.getName();
							}
						}
						return "";
					case "team_leader":
						final UUID leaderID = askyblock.getPlayers().getTeamLeader(playerID);

						if (leaderID != null) {
							final OfflinePlayer leader = Bukkit.getOfflinePlayer(leaderID);

							if (leader != null) {
								return leader.getName();
							}
						}
						return "";
					case "has_team":
						return askyblock.getPlayers().inTeam(playerID) ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
				}
			}
		}
		
		return null;
	}

	@Override
	public void clear() {
		askyblock = null;
	}
}
