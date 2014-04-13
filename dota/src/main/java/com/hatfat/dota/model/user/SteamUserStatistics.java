package com.hatfat.dota.model.user;

import android.util.Log;

import com.hatfat.dota.model.game.Item;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.model.player.Player;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SteamUserStatistics {
    private SteamUser user;

    List<ItemStats> favoriteItems;

    public SteamUserStatistics(SteamUser user) {
        this.user = user;
        this.favoriteItems = new LinkedList();

        calculateStatistics();
    }

    private void calculateStatistics() {
        long startTime = System.currentTimeMillis();

        //a hashmap of items with the number of times they've been purchased
        final HashMap<Item, ItemStats> itemPurchaseMap = new HashMap();

        for (String matchId : user.getMatches()) {
            Match match = Matches.get().getMatch(matchId);

            if (match != null) {
                Player player = match.getPlayerForSteamUser(user);

                for (int i = 0; i < 6; i++) {
                    Item item = player.getItem(i);
                    if (item != null) {
                        ItemStats itemStats = itemPurchaseMap.get(item);

                        if (itemStats == null) {
                            itemStats = new ItemStats(item);
                            itemPurchaseMap.put(item, itemStats);
                        }

                        itemStats.purchaseCount++;
                    }
                }
            }
        }

        List<ItemStats> sortedStats = new LinkedList(itemPurchaseMap.values());
        Collections.sort(sortedStats, new Comparator<ItemStats>() {
            @Override
            public int compare(ItemStats lhs, ItemStats rhs) {
                return rhs.purchaseCount - lhs.purchaseCount;
            }
        });

        int maxFavoriteItems = Math.min(sortedStats.size(), 3);
        favoriteItems = new LinkedList(sortedStats.subList(0, maxFavoriteItems));

        long endTime = System.currentTimeMillis();
        Log.v("SteamUserStatistics", "calculateStatistics runtime " + (endTime - startTime) + " milliseconds");
    }

    public List<ItemStats> getFavoriteItems() {
        return favoriteItems;
    }

    public static class ItemStats {
        public Item item;
        public int purchaseCount;

        public ItemStats(Item item) {
            this.item = item;
            purchaseCount = 0;
        }
    };
}

