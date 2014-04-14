package com.hatfat.dota.model.user;

import android.util.Log;

import com.hatfat.dota.model.game.Hero;
import com.hatfat.dota.model.game.Heroes;
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

    private static final int MAX_FAVORITE_ITEMS = 3;
    private static final int MAX_FAVORITE_HEROES = 5;
    private static final int MAX_HERO_FAVORITE_ITEMS = 3;

    private SteamUser user;

    List<ItemStats> favoriteItems;
    List<HeroStats> favoriteHeroes;

    public SteamUserStatistics(SteamUser user) {
        this.user = user;
        this.favoriteItems = new LinkedList();
        this.favoriteHeroes = new LinkedList();

        calculateStatistics();
    }

    private void calculateStatistics() {
        long startTime = System.currentTimeMillis();

        //a hashmap of items with the number of times they've been purchased
        final HashMap<Item, ItemStats> itemStatsMap = new HashMap();
        final HashMap<Hero, HeroStats> heroStatsMap = new HashMap();

        for (String matchId : user.getMatches()) {
            Match match = Matches.get().getMatch(matchId);

            if (match != null && match.shouldBeUsedInStatistics()) {
                Player player = match.getPlayerForSteamUser(user);
                Hero hero = Heroes.get().getHero(player.getHeroIdString());

                if (hero == null) {
                    //no hero for this match, so just skip to the next match
                    continue;
                }

                HeroStats heroStats = heroStatsMap.get(hero);

                if (heroStats == null) {
                    heroStats = new HeroStats(hero);
                    heroStatsMap.put(hero, heroStats);
                }

                heroStats.heroCount++;

                for (int i = 0; i < 6; i++) {
                    Item item = player.getItem(i);
                    if (item != null) {
                        ItemStats itemStats = itemStatsMap.get(item);

                        if (itemStats == null) {
                            itemStats = new ItemStats(item);
                            itemStatsMap.put(item, itemStats);
                        }

                        itemStats.purchaseCount++;

                        heroStats.addItem(item);
                    }
                }
            }
        }

        List<HeroStats> sortedHeroStats = new LinkedList(heroStatsMap.values());
        Collections.sort(sortedHeroStats, new Comparator<HeroStats>() {
            @Override
            public int compare(HeroStats lhs, HeroStats rhs) {
                return rhs.heroCount - lhs.heroCount;
            }
        });

        int maxFavoriteHeroes = Math.min(sortedHeroStats.size(), MAX_FAVORITE_HEROES);
        favoriteHeroes = new LinkedList(sortedHeroStats.subList(0, maxFavoriteHeroes));

        for (HeroStats heroStats : favoriteHeroes) {
            heroStats.sort();
        }

        List<ItemStats> sortedItemStats = new LinkedList(itemStatsMap.values());
        Collections.sort(sortedItemStats, new Comparator<ItemStats>() {
            @Override
            public int compare(ItemStats lhs, ItemStats rhs) {
                return rhs.getTotalCost() - lhs.getTotalCost();
            }
        });

        int maxFavoriteItems = Math.min(sortedItemStats.size(), MAX_FAVORITE_ITEMS);
        favoriteItems = new LinkedList(sortedItemStats.subList(0, maxFavoriteItems));

        long endTime = System.currentTimeMillis();
        Log.v("SteamUserStatistics", "calculateStatistics runtime " + (endTime - startTime) + " milliseconds");
    }

    public List<ItemStats> getFavoriteItems() {
        return favoriteItems;
    }

    public List<HeroStats> getFavoriteHeroes() {
        return favoriteHeroes;
    }

    public static class ItemStats {
        public Item item;
        public int purchaseCount;

        public ItemStats(Item item) {
            this.item = item;
            purchaseCount = 0;
        }

        public int getTotalCost() {
            return item.getItemCost() * purchaseCount;
        }
    };

    public static class HeroStats {
        public Hero hero;
        public int heroCount;

        public HashMap<Item, ItemStats> itemStatsMap = new HashMap();
        public List<ItemStats> favoriteHeroItems = new LinkedList();

        public HeroStats(Hero hero) {
            this.hero = hero;
            heroCount = 0;
        }

        public void addItem(Item item) {
            ItemStats itemStats = itemStatsMap.get(item);

            if (itemStats == null) {
                itemStats = new ItemStats(item);
                itemStatsMap.put(item, itemStats);
            }

            itemStats.purchaseCount++;
        }

        public void sort() {
            List<ItemStats> sortedItemStats = new LinkedList(itemStatsMap.values());
            Collections.sort(sortedItemStats, new Comparator<ItemStats>() {
                @Override
                public int compare(ItemStats lhs, ItemStats rhs) {
                    return rhs.getTotalCost() - lhs.getTotalCost();
                }
            });

            int maxFavoriteItems = Math.min(sortedItemStats.size(), MAX_HERO_FAVORITE_ITEMS);
            favoriteHeroItems = new LinkedList(sortedItemStats.subList(0, maxFavoriteItems));
        }
    }
}

