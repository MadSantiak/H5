package com.example.gowork.Controllers;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigHelper {
    private static final String CONFIG_FILE_NAME = "api_config.properties";

    /**
     * Helper function that fetches the configuration property for communicating with the Weather API.
     * @param context
     * @return
     */
    public static String getApiKey(Context context) {
        try {
            Properties properties = new Properties();
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(CONFIG_FILE_NAME);
            properties.load(inputStream);
            return properties.getProperty("api_key");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
