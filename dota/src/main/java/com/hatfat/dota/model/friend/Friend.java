package com.hatfat.dota.model.friend;

import android.content.Context;

import com.hatfat.dota.R;
import com.hatfat.dota.activities.CharltonActivity;

import java.util.List;
public class Friend implements Comparable {

    private final String friendId;

    private final String name;

    private final boolean canBeRandomed;

    private final List<String> descriptions;
    private int randomDescriptionIndex = -1;

    private final List<String> greetings;
    private int randomGreetingIndex = -1;

    private final List<String> starredGreetings;
    private int randomStarredGreetingIndex = -1;

    private final List<String> images;
    private int randomImageIndex = -1;

    public Friend() {
        this.friendId = null;
        this.name = null;
        this.descriptions = null;
        this.greetings = null;
        this.starredGreetings = null;
        this.images = null;
        this.canBeRandomed = false;
    }

    public String getFriendId() {
        return friendId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        if (descriptions == null) {
            return getGreeting();
        }

        if (randomDescriptionIndex < 0) {
            //we haven't randomed the description yet, so lets do that now
            randomDescriptionIndex = CharltonActivity.getSharedRandom().nextInt(descriptions.size());
        }

        return descriptions.get(randomDescriptionIndex);
    }

    public String getGreeting() {
        if (greetings == null) {
            return null;
        }

        if (randomGreetingIndex < 0) {
            //we haven't randomed the description yet, so lets do that now
            randomGreetingIndex = CharltonActivity.getSharedRandom().nextInt(greetings.size());
        }

        return greetings.get(randomGreetingIndex);
    }

    public String getStarredGreeting() {
        if (starredGreetings == null) {
            return null;
        }

        if (randomStarredGreetingIndex < 0) {
            //we haven't randomed the description yet, so lets do that now
            randomStarredGreetingIndex = CharltonActivity.getSharedRandom().nextInt(starredGreetings.size());
        }

        return starredGreetings.get(randomStarredGreetingIndex);
    }

    public int getImageResourceId(Context context) {
        if (images == null) {
            return R.drawable.ic_launcher;
        }

        if (randomImageIndex < 0) {
            //we haven't randomed the image yet, so lets do that now
            randomImageIndex = CharltonActivity.getSharedRandom().nextInt(images.size());
        }

        String drawableName = images.get(randomImageIndex);
        return context.getResources().getIdentifier(drawableName, "drawable",
                context.getPackageName());
    }

    public boolean canBeRandomed() {
        return canBeRandomed;
    }

    public boolean isCurrentFriend() {
        Friend currentFriend = Friends.get().getCurrentFriend();
        return ((Object)this).equals(currentFriend);
    }

    @Override
    public int compareTo(Object o) {
        //Sorts matches by date, so the newest match is first
        if (o != null && o instanceof Friend) {
            Friend otherFriend = (Friend)o;

            if (this.isCurrentFriend()) {
                return -1;
            }

            if (otherFriend.isCurrentFriend()) {
                return 1;
            }

            return getName().compareTo(otherFriend.getName());
        }

        return 0;
    }
}
