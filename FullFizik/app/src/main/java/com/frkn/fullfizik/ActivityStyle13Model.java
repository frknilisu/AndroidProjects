package com.frkn.fullfizik;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Wahyu on 06/08/2015.
 */
public class ActivityStyle13Model {


    private int id;
    private int imageId;
    private String name, description;
    private int imageCount;
    private int fileLength;
    private String link;

    public ActivityStyle13Model(JSONObject jsonObject) {
        try {
            this.id = jsonObject.getInt("id");
            this.name = jsonObject.getString("name");
            this.description = jsonObject.getString("description");
            this.imageId = Integer.parseInt(jsonObject.getString("imageId"));
            this.imageCount = jsonObject.getInt("imageCount");
            this.fileLength = jsonObject.getInt("fileLength");
            this.link = jsonObject.getString("link");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public int getImageId() {
        return imageId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getImageCount() {
        return imageCount;
    }

    public int getFileLength() {
        return fileLength;
    }

    public String getLink() {
        return link;
    }
}
