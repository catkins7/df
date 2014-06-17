package com.hatfat.dota.model;

import com.google.gson.annotations.SerializedName;

public class DotaResponse<T> {

    @SerializedName("response")
    public T response;
}
