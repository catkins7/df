package com.hatfat.dota.model.player;

import com.google.gson.annotations.SerializedName;
import com.hatfat.dota.R;
import com.hatfat.dota.model.game.Item;
import com.hatfat.dota.model.game.Items;

/**
 * Created by scottrick on 3/14/14.
 */
public class AdditionalUnit {

    @SerializedName("unitname")
    String unitName;

    @SerializedName("item_0")
    int item0;

    @SerializedName("item_1")
    int item1;

    @SerializedName("item_2")
    int item2;

    @SerializedName("item_3")
    int item3;

    @SerializedName("item_4")
    int item4;

    @SerializedName("item_5")
    int item5;

    public String getItemImageUrl(int itemNum) {
        Item item = getItem(itemNum);

        if (item != null) {
            return item.getLargeHorizontalPortraitUrl();
        }
        else {
            return null;
        }
    }

    public Item getItem(int itemNum) {
        int itemId = -1;

        switch (itemNum) {
            case 0:
                itemId = item0;
                break;
            case 1:
                itemId = item1;
                break;
            case 2:
                itemId = item2;
                break;
            case 3:
                itemId = item3;
                break;
            case 4:
                itemId = item4;
                break;
            case 5:
                itemId = item5;
                break;
        }

        return Items.get().getItem(String.valueOf(itemId));
    }

    public int getIconResource() {
        switch (unitName) {
            case "spirit_bear":
                return R.drawable.spirit_bear;
        }

        return -1;
    }

    public String getUnitName() {
        return unitName;
    }
}
