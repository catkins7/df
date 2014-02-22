package com.hatfat.dota.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by scottrick on 2/21/14.
 */
public class DotaResponse<T> {

    @SerializedName("response")
    public T response;
}
