package edu.unc.sjyan.locationawareplayer;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Color;
import android.content.Intent;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.view.View;
import android.graphics.Paint;
import java.util.Random;
import android.util.Log;

/**
 * Created by Stephen on 3/24/16.
 */

public class LocationPlot extends View {

    private int x = 0;
    private int y = 0;
    private float r = 0;

    public LocationPlot(Context c) {
        super(c);
    }

    public LocationPlot(Context c, AttributeSet attrs) {
        super(c, attrs);
    }

    public LocationPlot(Context c, AttributeSet attrs, int s) {
        super(c, attrs, s);
    }

    public LocationPlot(Context c, AttributeSet attrs, int s1, int s2) {
        super(c, attrs, s1);
    }

    @Override
    protected void onDraw(Canvas c) {
        /*
        if coordinates within geofence1
            drawCircle
        else
            clear canvas
        */

        super.onDraw(c);
        Paint gf = new Paint();
        Paint cl = new Paint();
        gf.setColor(Color.RED);
        cl.setColor(Color.BLUE);
        gf.setAlpha(100);
        cl.setAlpha(100);

        // draw current location
        // c.drawCircle(getWidth() - convertedLat, getHeight() - convertedLong, 10f, cl);

        c.drawCircle(x, y, r, gf);

    }


    public void drawSitterson() {
        x = 95;
        y = 800;
        r = 92f;
    }

    public void drawOldWell() {
        x = 490;
        y = 285;
        r = 50f;
    }

    public void drawPolkPlace() {
        x = 650;
        y = 620;
        r = 94f;
    }

    public void clear() {
        x = 0;
        y = 0;
        r = 0;

    }

}
