package com.frkn.pratikfizik;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by frkn on 08.11.2016.
 */

public class Chapter {
    private int chapterId;
    private int imageId;
    private String title, definition;
    private String url;

    public Chapter() {

    }

    public Chapter(JSONObject jsonObject) {
        try {
            this.chapterId = jsonObject.getInt("id");
            this.imageId = Integer.parseInt(jsonObject.getString("imageId"));
            this.definition = jsonObject.getString("definition");
            this.title = jsonObject.getString("title");
            this.url = jsonObject.getString("yuarel");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
