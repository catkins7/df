package com.hatfat.dota.model;

import com.google.gson.annotations.SerializedName;

public class DotaResult<T> {

    @SerializedName("result")
    public T result;
}
