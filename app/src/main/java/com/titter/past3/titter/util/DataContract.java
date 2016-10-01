package com.titter.past3.titter.util;

import android.provider.BaseColumns;

/**
 * Created by SMILECS on 9/13/16.
 */
public class DataContract {
    public DataContract(){}
    public static abstract class Data implements BaseColumns {
        public static final String TABLE_NAME = "Category";
        public static final String URL = "url";
        public static final String WEBURL = "weburl";
        public static final String TAG = "tag";
        public static final String AVAILABLE = "available";
        public static final String TYPE = "type";
        public static final String TYPE2 = "tamka";

    }
}
