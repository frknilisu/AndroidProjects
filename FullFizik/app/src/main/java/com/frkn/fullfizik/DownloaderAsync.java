package com.frkn.fullfizik;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by frkn on 26.01.2017.
 */

public class DownloaderAsync extends AsyncTask<String, String, String> {

    ProgressDialog mProgressDialog;
    boolean processDialogFlag;

    Context context;
    DownloadListener listener;
    String processMessage;
    int fileLength = 0;
    String parentFolderName;
    String fileName, fileExtension;

    String srcPath, destPath;

    int timeout = 10000;
    boolean downloadIsOk = false;
    String taskMessage;

    public DownloaderAsync() {
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setTitle("DownloaderAsync");
        mProgressDialog.setMessage(processMessage);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgress(0);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        if(processDialogFlag) {
            mProgressDialog.show();
        }
    }

    @Override
    protected String doInBackground(String... fileUrl) {
        int count;
        try {
            URL url = new URL(fileUrl[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            //urlConnection.setDoOutput(true);
            urlConnection.setConnectTimeout(timeout);
            urlConnection.setReadTimeout(timeout);
            //urlConnection.setChunkedStreamingMode(100);
            urlConnection.connect();
            // show progress bar 0-100%
            Log.d("doInBack", "fileLength: " + fileLength);
            Log.d("doInBack", "responseCode: " + urlConnection.getResponseCode());
            Log.d("doInBack", "responseMessage: " + urlConnection.getResponseMessage());
            Log.d("doInBack", "requestMethod: " + urlConnection.getRequestMethod());
            Log.d("doInBack", "contentType: " + urlConnection.getContentType());
            Log.d("doInBack", "contentEncoding: " + urlConnection.getContentEncoding());
            InputStream inputStream = new BufferedInputStream(url.openStream(), 65536);
            //InputStream inputStream = urlConnection.getInputStream();
            OutputStream outputStream = null;
            if (parentFolderName != null) {
                File file = new File(context.getFilesDir(), parentFolderName);
                file.mkdirs();
                File file2 = File.createTempFile(fileName, fileExtension, file);
                Log.d("doInBackGround", "downloadPath: " + file2.getAbsolutePath());
                srcPath = file2.getAbsolutePath();
                destPath = file.getAbsolutePath() + "/" + fileName + "/";
                outputStream = new FileOutputStream(file2);
            } else {
                File file = new File(context.getFilesDir(), "inception.json");
                file.createNewFile();
                Log.d("doInBackGround", "downloadPath: " + file.getAbsolutePath());
                outputStream = new FileOutputStream(file);
                srcPath = null;
                destPath = null;
            }


            byte data[] = new byte[BUFFER_SIZE];
            long total = 0;
            while ((count = inputStream.read(data)) != -1) {
                total += count;
                Log.d("Total", "tot: " + total);
                publishProgress("" + (int) ((total * 100) / (fileLength)));
                outputStream.write(data, 0, count);
            }
            // flushing output
            outputStream.flush();
            // closing streams
            outputStream.close();
            inputStream.close();

            downloadIsOk = true;
            taskMessage = "Success: Download completed.";

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
            downloadIsOk = false;
            taskMessage = e.getMessage();
        }
        return null;
    }

    protected void onProgressUpdate(String... progress) {
        Log.d("onProgressUpdate", progress[0]);
        mProgressDialog.setProgress(Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        mProgressDialog.dismiss();
        if (downloadIsOk) {
            Toast.makeText(context, taskMessage, Toast.LENGTH_SHORT).show();
            boolean flag = false;
            if (srcPath != null && destPath != null)
                flag = unzip(srcPath, destPath);
            Toast.makeText(context, "Unzipping: " + String.valueOf(flag), Toast.LENGTH_SHORT).show();
            listener.onTaskCompleted(taskMessage);
        } else {
            Toast.makeText(context, "Error: Download failed.", Toast.LENGTH_SHORT).show();
            listener.onTaskFailed(taskMessage);
        }
    }

    @Override
    protected void onCancelled(String result) {
        super.onCancelled(result);
        Toast.makeText(context, "Error: Download failed.", Toast.LENGTH_SHORT).show();
        listener.onTaskFailed("Error: Download failed: " + result);
    }


    private final static int BUFFER_SIZE = 2048;
    private final static String ZIP_EXTENSION = ".zip";

    public boolean unzip(String srcZipFileName, String destDirectoryName) {
        Log.d("unzip", "src: " + srcZipFileName + ", dest: " + destDirectoryName);
        try {
            BufferedInputStream bufIS = null;
            // create the destination directory structure (if needed)
            File destDirectory = new File(destDirectoryName);
            Log.d("unzip", "destName: " + destDirectory.getName());
            destDirectory.mkdirs();
            Log.d("unzip", "destPath: " + destDirectory.getAbsolutePath());
            Log.d("unzip", "dest.isExist(): " + destDirectory.exists());

            // open archive for reading
            File srcFile = new File(srcZipFileName);
            ZipFile zipFile = new ZipFile(srcFile, ZipFile.OPEN_READ);

            //for every zip archive entry do
            Enumeration<? extends ZipEntry> zipFileEntries = zipFile.entries();
            while (zipFileEntries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
                System.out.println("\tExtracting entry: " + entry);

                //create destination file
                File destFile = new File(destDirectory, entry.getName());
                destFile.createNewFile();
                //File destFile = File.createTempFile(destDirectory.getAbsolutePath(), entry.getName());
                Log.d("unzip", "destFile: " + destFile.getAbsolutePath() + ", dir: " + destFile.isDirectory());

                //create parent directories if needed
                File parentDestFile = destFile.getParentFile();
                parentDestFile.mkdirs();

                if (!entry.isDirectory()) {
                    bufIS = new BufferedInputStream(
                            zipFile.getInputStream(entry));
                    int currentByte;

                    // buffer for writing file
                    byte data[] = new byte[BUFFER_SIZE];

                    // write the current file to disk
                    FileOutputStream fOS = new FileOutputStream(destFile);
                    BufferedOutputStream bufOS = new BufferedOutputStream(fOS, BUFFER_SIZE);

                    while ((currentByte = bufIS.read(data, 0, BUFFER_SIZE)) != -1) {
                        bufOS.write(data, 0, currentByte);
                    }

                    // close BufferedOutputStream
                    bufOS.flush();
                    bufOS.close();

                    // recursively unzip files
                    if (entry.getName().toLowerCase().endsWith(ZIP_EXTENSION)) {
                        String zipFilePath = destDirectory.getPath() + File.separatorChar + entry.getName();

                        unzip(zipFilePath, zipFilePath.substring(0, zipFilePath.length() - ZIP_EXTENSION.length()));
                    }
                }
            }
            bufIS.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /************************************************************
     * SETTERS
     ************************************************************/

    public void setContext(Context context) {
        this.context = context;
    }

    public void setListener(DownloadListener listener) {
        this.listener = listener;
    }

    public void setProcessDialogFlag(boolean processDialogFlag) {this.processDialogFlag = processDialogFlag; }

    public void setProcessMessage(String processMessage) {
        this.processMessage = processMessage;
    }

    public void setFileLength(int fileLength) {
        this.fileLength = fileLength;
    }

    public void setParentFolderName(String parentFolderName) {
        this.parentFolderName = parentFolderName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }
}
