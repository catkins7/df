package com.hatfat.dota.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by scottrick on 2/21/14.
 */
public class DotaResult<T> {

    @SerializedName("result")
    public T result;
}
