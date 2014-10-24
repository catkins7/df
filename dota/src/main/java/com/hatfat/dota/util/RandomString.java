package com.hatfat.dota.util;

import com.hatfat.dota.activities.CharltonActivity;

import java.util.List;

//Given a list of strings, it will randomly select one and always return it
public class RandomString {
    private List<String> strings;
    private int randomStringIndex = -1;

    public RandomString(List<String> strings) {
        this.strings = strings;
    }

    public String getString() {
        if (randomStringIndex < 0) {
            //we haven't randomed yet
            randomStringIndex = CharltonActivity.getSharedRandom().nextInt(strings.size());
        }

        return strings.get(randomStringIndex);
    }
}
