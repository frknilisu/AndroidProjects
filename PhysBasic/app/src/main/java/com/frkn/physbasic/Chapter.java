package com.frkn.physbasic;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by frkn on 08.11.2016.
 */

public class Chapter {
    private int chapterId;
    private int imageId;
    private String title, definition;
    private boolean lock;

    public Chapter() {

    }

    public Chapter(JSONObject jsonObject) {
        try {
            this.chapterId = jsonObject.getInt("id");
            this.imageId = Integer.parseInt(jsonObject.getString("imageId"));
            this.definition = jsonObject.getString("definition");
            this.title = jsonObject.getString("title");
            this.lock = jsonObject.getBoolean("lock");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getChapterId() {
        return chapterId;
    }

    public int getImageId() {
        return imageId;
    }

    public String getTitle() {
        return title;
    }

    public String getDefinition() {
        return definition;
    }

    public boolean isLock() {
        return lock;
    }
}
