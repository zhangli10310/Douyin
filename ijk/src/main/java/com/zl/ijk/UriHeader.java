package com.zl.ijk;

import android.net.Uri;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * <p></p>
 * <p>
 * Created by zhangli on 2019/2/18 15:45.<br/>
 */
public class UriHeader {

    @NonNull
    public Uri uri;
    @Nullable
    public Map<String, String> headers;

    public UriHeader(@NonNull Uri uri) {
        this.uri = uri;
    }

    public UriHeader(@NonNull Uri uri, @Nullable Map<String, String> headers) {
        this.uri = uri;
        this.headers = headers;
    }
}
