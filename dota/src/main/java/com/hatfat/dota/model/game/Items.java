package com.hatfat.dota.model.game;

import android.content.res.Resources;
import com.google.gson.Gson;
import com.hatfat.dota.R;
import com.hatfat.dota.model.DotaGson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;

public class Items {
    private static Items singleton;

    public static Items get() {
        if (singleton == null) {
            singleton = new Items();
        }

        return singleton;
    }

    private HashMap<String, Item> items; //string itemId --> item object

    private Items() {
        items = new HashMap<>();
    }

    public void load(Resources resources) {
        if (items != null && items.size() > 0) {
            //already loaded
            return;
        }

        //items are not available from the API, so parse local items.json file
        //maybe switch to this if needed?
        //http://www.dota2.com/jsfeed/itemdata
        InputStream inputStream = resources.openRawResource(R.raw.items);
        Reader reader = new InputStreamReader(inputStream);

        Gson gson = DotaGson.getDotaGson();
        ItemResult itemResult = gson.fromJson(reader, ItemResult.class);

        setNewItemList(itemResult.result.items);
    }

    private void setNewItemList(List<Item> itemList) {
        items.clear();

        for (Item item : itemList) {
            items.put(String.valueOf(item.itemId), item);
        }
    }

    public Item getItem(String itemIdString) {
        return items.get(itemIdString);
    }
}
