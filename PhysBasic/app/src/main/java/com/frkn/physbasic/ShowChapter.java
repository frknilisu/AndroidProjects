package com.frkn.physbasic;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by frkn on 12.01.2017.
 */

public class ShowChapter extends Activity implements View.OnClickListener {

    public final static String EXTRA_MESSAGE = "com.frkn.physbasic.MESSAGE";

    private ImageView mImageView;
    private Button next, previous;


    private PdfRenderer mPdfRenderer;
    private PdfRenderer.Page mCurrentPage;

    private int currentPage = 0;

    private int chapterId;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_chapter);

        next = (Button) findViewById(R.id.next);
        previous = (Button) findViewById(R.id.previous);
        mImageView = (ImageView) findViewById(R.id.image);

        next.setOnClickListener(this);
        previous.setOnClickListener(this);

        chapterId = Integer.parseInt(getIntent().getExtras().getString(EXTRA_MESSAGE + "_chapterId"));

        try {
            openRenderer(chapterId);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        showPage(currentPage);

    }

    private void openRenderer(int chapterNo) throws IOException {
        File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        try {
            File ff = new File(downloadFolder.getAbsolutePath() + "/PhysBasic/" + chapterNo + ".pdf");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mPdfRenderer = new PdfRenderer(ParcelFileDescriptor.open(ff, ParcelFileDescriptor.MODE_READ_ONLY));
            } else{
                Toast.makeText(getApplicationContext(), "your version does not support pdfrenderer", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void closeRenderer() throws IOException {
        if (null != mCurrentPage) {
            mCurrentPage.close();
        }
        mPdfRenderer.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showPage(int index) {
        if (mPdfRenderer.getPageCount() <= index) {
            return;
        }
        // Make sure to close the current page before opening another one.
        if (null != mCurrentPage) {
            mCurrentPage.close();
        }
        // Use `openPage` to open a specific page in PDF.
        mCurrentPage = mPdfRenderer.openPage(index);
        // Important: the destination bitmap must be ARGB (not RGB).

        int pgWidth = mCurrentPage.getWidth() * 2;
        int pgHeight = mCurrentPage.getHeight() * 2;

        int clipX = 0;
        int clipY = 0;
        int clipWidth = pgWidth;
        int clipHeight = pgHeight;

        Bitmap bitmap = Bitmap.createBitmap(pgWidth, pgHeight,
                Bitmap.Config.ARGB_8888);
        System.out.println(mCurrentPage.getWidth() + " - " + mCurrentPage.getHeight());
        // Here, we render the page onto the Bitmap.
        // To render a portion of the page, use the second and third parameter. Pass nulls to get
        // the default result.
        // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
        //mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);


        //Matrix m = mImageView.getImageMatrix();

        //ViewPager

        Rect rect = new Rect(0, 0, pgWidth, pgHeight);
        mCurrentPage.render(bitmap, rect, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        Bitmap clip = Bitmap.createBitmap(bitmap, clipX, clipY, clipWidth, clipHeight);
        //mImageView.setImageMatrix(m);
        mImageView.setImageBitmap(clip);
        mImageView.invalidate();
        // We are ready to show the Bitmap to user.
        mImageView.setImageBitmap(clip);
        bitmap.recycle();
        updateUi();
    }

    /**
     * Updates the state of 2 control buttons in response to the current page index.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateUi() {
        int index = mCurrentPage.getIndex();
        int pageCount = mPdfRenderer.getPageCount();
        previous.setEnabled(0 != index);
        next.setEnabled(index + 1 < pageCount);
        this.setTitle(getString(R.string.app_name_with_index, index + 1, pageCount));
    }

    /**
     * Gets the number of pages in the PDF. This method is marked as public for testing.
     *
     * @return The number of pages.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public int getPageCount() {
        return mPdfRenderer.getPageCount();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.previous: {
                // Move to the previous page
                showPage(mCurrentPage.getIndex() - 1);
                break;
            }
            case R.id.next: {
                // Move to the next page
                showPage(mCurrentPage.getIndex() + 1);
                break;
            }
        }
    }
}
