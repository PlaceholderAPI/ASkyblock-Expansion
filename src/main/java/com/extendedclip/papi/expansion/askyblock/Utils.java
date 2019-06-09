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

import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.*;

public class Utils {

    private ASkyBlockExpansion ex;

    private String k, m, b, t, q;
    private int digit;

    public Utils(ASkyBlockExpansion ex) {
        digit = ex.getInt("formatting.digits", 2);
        k = ex.getString("formatting.thousands", "k");
        m = ex.getString("formatting.millions", "M");
        b = ex.getString("formatting.billions", "B");
        t = ex.getString("formatting.trillions", "T");
        q = ex.getString("formatting.quadrillions", "Q");
        this.ex = ex;
    }


    private Map<UUID, Long> getMapIslandOwner() {
        final Map<UUID, Long> mapIslandOwner = new HashMap<UUID, Long>();

        for (UUID ownerID : ex.askyblock.getGrid().getOwnershipMap().keySet()) {
            final long level = ex.askyblock.getPlayers().getIslandLevel(ownerID);

            mapIslandOwner.put(ownerID, level);
        }

        return mapIslandOwner;
    }

    public Integer getPlayerTop(Player player) {
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

    public Map.Entry<UUID, Long> getEntryTopIslandOwner(int index) {
        final List<Map.Entry<UUID, Long>> listSortIslandOwner = getSortIslandOwner();

        return index < listSortIslandOwner.size() ? listSortIslandOwner.get(index) : null;
    }

    private List<Map.Entry<UUID, Long>> getSortIslandOwner() {
        return sortMap(getMapIslandOwner());
    }

    private List<Map.Entry<UUID, Long>> sortMap(Map<UUID, Long> unsortMap) {
        return sortMap(unsortMap, true);
    }

    private List<Map.Entry<UUID, Long>> sortMap(Map<UUID, Long> unsortMap, boolean ascend) {
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

    public String getFormatted(double level) {

        NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
        format.setMaximumFractionDigits(digit);
        format.setMinimumFractionDigits(0);

        if (level < 1000) {
            return format.format(level);
        }
        if (level < 1000000) {
            return format.format(level / 1000) + k;
        }
        if (level < 1000000000) {
            return format.format(level / 1000000) + m;
        }
        if (level < 1000000000000L) {
            return format.format(level / 1000000000) + b;
        }
        if (level < 1000000000000000L) {
            return format.format(level / 1000000000000L) + t;
        }
        if (level < 1000000000000000000L) {
            return format.format(level / 1000000000000000L) + q;
        }

        return String.valueOf(level);
    }
}
