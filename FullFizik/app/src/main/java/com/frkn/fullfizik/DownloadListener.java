package com.frkn.fullfizik;

/**
 * Created by frkn on 22.06.2017.
 */

public interface DownloadListener {
    void onTaskCompleted(String response);

    void onTaskFailed(String response);
}
