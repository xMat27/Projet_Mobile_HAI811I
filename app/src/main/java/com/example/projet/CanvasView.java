package com.example.projet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class defines fields and methods for drawing.
 */
public class CanvasView extends View {

    // Enumeration for Mode
    public enum Mode {
        DRAW,
        TEXT,
        ERASER;
    }

    // Enumeration for Drawer
    public enum Drawer {
        PEN,
        LINE,
        RECTANGLE,
        CIRCLE,
        ELLIPSE,
        QUADRATIC_BEZIER,
        CUBIC_BEZIER;
    }

    private Context context = null;
    private Canvas canvas = null;
    private Bitmap bitmap = null;

    private Paint paint;

    private List<Path> pathLists = new ArrayList<Path>();
    private List<Paint> paintLists = new ArrayList<Paint>();

    // for Eraser
    private int baseColor = Color.WHITE;

    // for Undo, Redo
    private int historyPointer = 0;

    // Flags
    private Mode mode = Mode.DRAW;
    private Drawer drawer = Drawer.PEN;
    private boolean isDown = false;

    // for Paint
    private Paint.Style paintStyle = Paint.Style.STROKE;
    private int paintStrokeColor = Color.BLACK;
    private int paintFillColor = Color.BLACK;
    private float paintStrokeWidth = 3F;
    private int opacity = 255;
    private float blur = 0F;
    private Paint.Cap lineCap = Paint.Cap.ROUND;

    // for Text
    private String text = "";
    private Typeface fontFamily = Typeface.DEFAULT;
    private float fontSize = 32F;
    private Paint.Align textAlign = Paint.Align.RIGHT;  // fixed
    private Paint textPaint = new Paint();
    private float textX = 0F;
    private float textY = 0F;

    // for Drawer
    private float startX = 0F;
    private float startY = 0F;
    private float controlX = 0F;
    private float controlY = 0F;

    /**
     * Copy Constructor
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public CanvasView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setup(context);
        this.setupPaint();
    }

    private void setupPaint() {
        paint = new Paint();
        paint.setColor(Color.BLACK); // Couleur par défaut
        paint.setStrokeWidth(8); // Épaisseur du trait par défaut
        paint.setStyle(Paint.Style.STROKE);
    }

    /**
     * Copy Constructor
     *
     * @param context
     * @param attrs
     */
    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setup(context);
    }

    /**
     * Copy Constructor
     *
     * @param context
     */
    public CanvasView(Context context) {
        super(context);
        this.setup(context);
    }

    /**
     * Common initialization.
     *
     * @param context
     */
    private void setup(Context context) {
        this.context = context;

        this.pathLists.add(new Path());
        this.paintLists.add(this.createPaint());
        this.historyPointer++;

        this.textPaint.setARGB(0, 255, 255, 255);
    }

    public void setStrokeWidth(float width) {
        paint.setStrokeWidth(width);
        invalidate();
    }

    public void setStrokeColor(int color) {
        paint.setColor(color);
        invalidate();
    }

    /**
     * This method creates the instance of Paint.
     * In addition, this method sets styles for Paint.
     *
     * @return paint This is returned as the instance of Paint
     */
    private Paint createPaint() {
        Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setStyle(this.paintStyle);
        paint.setStrokeWidth(this.paintStrokeWidth);
        paint.setStrokeCap(this.lineCap);
        paint.setStrokeJoin(Paint.Join.MITER);  // fixed

        // for Text
        if (this.mode == Mode.TEXT) {
            paint.setTypeface(this.fontFamily);
            paint.setTextSize(this.fontSize);
            paint.setTextAlign(this.textAlign);
            paint.setStrokeWidth(0F);
        }

        if (this.mode == Mode.ERASER) {
            // Eraser
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            paint.setARGB(0, 0, 0, 0);
        } else {
            // Otherwise
            paint.setColor(this.paintStrokeColor);
            paint.setShadowLayer(this.blur, 0F, 0F, this.paintStrokeColor);
            paint.setAlpha(this.opacity);
        }

        return paint;
    }

    /**
     * This method initialize Path.
     * Namely, this method creates the instance of Path,
     * and moves current position.
     *
     * @param event This is argument of onTouchEvent method
     * @return path This is returned as the instance of Path
     */
    private Path createPath(MotionEvent event) {
        Path path = new Path();

        // Save for ACTION_MOVE
        this.startX = event.getX();
        this.startY = event.getY();

        path.moveTo(this.startX, this.startY);

        return path;
    }

    /**
     * This method updates the lists for the instance of Path and Paint.
     * "Undo" and "Redo" are enabled by this method.
     *
     * @param path the instance of Path
     */
    private void updateHistory(Path path) {
        if (this.historyPointer == this.pathLists.size()) {
            this.pathLists.add(path);
            this.paintLists.add(this.createPaint());
            this.historyPointer++;
        } else {
            // On the way of Undo or Redo
            this.pathLists.set(this.historyPointer, path);
            this.paintLists.set(this.historyPointer, this.createPaint());
            this.historyPointer++;

            for (int i = this.historyPointer, size = this.paintLists.size(); i < size; i++) {
                this.pathLists.remove(this.historyPointer);
                this.paintLists.remove(this.historyPointer);
            }
        }
    }

    /**
     * This method gets the instance of Path that pointer indicates.
     *
     * @return the instance of Path
     */
    private Path getCurrentPath() {
        return this.pathLists.get(this.historyPointer - 1);
    }

    /**
     * This method draws text.
     *
     * @param canvas the instance of Canvas
     */
    private void drawText(Canvas canvas) {
        if (this.text.length() <= 0) {
            return;
        }

        if (this.mode == Mode.TEXT) {
            this.textX = this.startX;
            this.textY = this.startY;

            this.textPaint = this.createPaint();
        }

        float textX = this.textX;
        float textY = this.textY;

        Paint paintForMeasureText = new Paint();

        // Line break automatically
        float textLength = paintForMeasureText.measureText(this.text);
        float lengthOfChar = textLength / (float) this.text.length();
        float restWidth = this.canvas.getWidth() - textX;  // text-align : right
        int numChars = (lengthOfChar <= 0) ? 1 : (int) Math.floor((double) (restWidth / lengthOfChar));  // The number of characters at 1 line
        int modNumChars = (numChars < 1) ? 1 : numChars;
        float y = textY;

        for (int i = 0, len = this.text.length(); i < len; i += modNumChars) {
            String substring = "";

            if ((i + modNumChars) < len) {
                substring = this.text.substring(i, (i + modNumChars));
            } else {
                substring = this.text.substring(i, len);
            }

            y += this.fontSize;

            canvas.drawText(substring, textX, y, this.textPaint);
        }
    }

    /**
     * This method defines processes on MotionEvent.ACTION_DOWN
     *
     * @param event This is argument of onTouchEvent method
     */
    private void onActionDown(MotionEvent event) {
        switch (this.mode) {
            case DRAW:
            case ERASER:
                if ((this.drawer != Drawer.QUADRATIC_BEZIER) && (this.drawer != Drawer.CUBIC_BEZIER)) {
                    // Otherwise
                    this.updateHistory(this.createPath(event));
                    this.isDown = true;
                } else {
                    // Bezier
                    if ((this.startX == 0F) && (this.startY == 0F)) {
                        // The 1st tap
                        this.updateHistory(this.createPath(event));
                    } else if ((this.controlX == 0F) && (this.controlY == 0F)) {
                        // The 2nd tap
                        this.controlX = event.getX();
                        this.controlY = event.getY();
                    }

                    this.isDown = true;
                }

                break;
            case TEXT:
                this.startX = event.getX();
                this.startY = event.getY();

                this.drawText(this.canvas);
                break;
        }

        invalidate(); // Ajouter cet appel à invalidate() ici
    }

    private void onActionMove(MotionEvent event) {
        switch (this.mode) {
            case DRAW:
            case ERASER:
                if (this.isDown) {
                    Path path = this.getCurrentPath();

                    switch (this.drawer) {
                        case PEN:
                            path.lineTo(event.getX(), event.getY());
                            break;
                        case LINE:
                            path.reset();
                            path.moveTo(this.startX, this.startY);
                            path.lineTo(event.getX(), event.getY());
                            break;
                        case RECTANGLE:
                            path.reset();
                            path.addRect(this.startX, this.startY, event.getX(), event.getY(), Path.Direction.CCW);
                            break;
                        case CIRCLE:
                            float radius = (float) Math.hypot((event.getX() - this.startX), (event.getY() - this.startY));
                            path.reset();
                            path.addCircle(this.startX, this.startY, radius, Path.Direction.CCW);
                            break;
                        case ELLIPSE:
                            RectF rect = new RectF(this.startX, this.startY, event.getX(), event.getY());
                            path.reset();
                            path.addOval(rect, Path.Direction.CCW);
                            break;
                        case QUADRATIC_BEZIER:
                            if (this.controlX > 0F) {
                                path.reset();
                                path.moveTo(this.startX, this.startY);
                                path.quadTo(this.controlX, this.controlY, event.getX(), event.getY());
                            }
                            break;
                        case CUBIC_BEZIER:
                            if (this.controlX > 0F) {
                                path.reset();
                                path.moveTo(this.startX, this.startY);
                                path.cubicTo(this.startX, this.startY, this.controlX, this.controlY, event.getX(), event.getY());
                            }
                            break;
                    }

                    invalidate(); // Ajouter cet appel à invalidate() ici
                }

                break;
            case TEXT:
                this.startX = event.getX();
                this.startY = event.getY();

                break;
        }
    }

    private void onActionUp(MotionEvent event) {
        switch (this.mode) {
            case DRAW:
            case ERASER:
                if (this.isDown) {
                    Path path = this.getCurrentPath();

                    switch (this.drawer) {
                        case PEN:
                            path.lineTo(event.getX(), event.getY());
                            break;
                        case LINE:
                            path.reset();
                            path.moveTo(this.startX, this.startY);
                            path.lineTo(event.getX(), event.getY());
                            break;
                        case RECTANGLE:
                            path.reset();
                            path.addRect(this.startX, this.startY, event.getX(), event.getY(), Path.Direction.CCW);
                            break;
                        case CIRCLE:
                            float radius = (float) Math.hypot((event.getX() - this.startX), (event.getY() - this.startY));
                            path.reset();
                            path.addCircle(this.startX, this.startY, radius, Path.Direction.CCW);
                            break;
                        case ELLIPSE:
                            RectF rect = new RectF(this.startX, this.startY, event.getX(), event.getY());
                            path.reset();
                            path.addOval(rect, Path.Direction.CCW);
                            break;
                        case QUADRATIC_BEZIER:
                            if (this.controlX > 0F) {
                                path.reset();
                                path.moveTo(this.startX, this.startY);
                                path.quadTo(this.controlX, this.controlY, event.getX(), event.getY());
                            }
                            break;
                        case CUBIC_BEZIER:
                            if (this.controlX > 0F) {
                                path.reset();
                                path.moveTo(this.startX, this.startY);
                                path.cubicTo(this.startX, this.startY, this.controlX, this.controlY, event.getX(), event.getY());
                            }
                            break;
                    }

                    this.isDown = false;
                    invalidate(); // Ajouter cet appel à invalidate() ici
                }

                break;
            case TEXT:
                this.startX = event.getX();
                this.startY = event.getY();

                this.drawText(this.canvas);
                invalidate(); // Ajouter cet appel à invalidate() ici
                break;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.onActionDown(event);
                this.invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                this.onActionMove(event);
                this.invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                this.onActionUp(event);
                this.invalidate();
                return true;
        }

        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Initialize
        if (this.bitmap == null) {
            this.bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
            this.canvas = new Canvas(this.bitmap);
        }

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); // Clear the canvas

        // Draw bitmap
        canvas.drawBitmap(this.bitmap, 0F, 0F, null);

        for (int i = 0; i < this.historyPointer; i++) {
            canvas.drawPath(this.pathLists.get(i), this.paintLists.get(i));
        }

        // Draw text
        if (this.mode == Mode.TEXT) {
            this.drawText(canvas);
        }
    }

    /**
     * This method returns the instance of Bitmap as byte array.
     *
     * @param compressFormat This is a compress format for bitmap
     * @param quality        This is a quality for bitmap
     * @return stream.toByteArray() This is returned as the instance of byte array
     */
    public byte[] getBitmapAsByteArray(Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        this.bitmap.compress(compressFormat, quality, stream);

        return stream.toByteArray();
    }

    /**
     * This method sets the instance of Bitmap.
     *
     * @param bitmap the instance of Bitmap
     */
    public void setBitmap(Bitmap bitmap) {
        // Ensure the bitmap is mutable
        this.bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        this.canvas = new Canvas(this.bitmap);
        this.invalidate();
    }

    public Bitmap getBitmap() {
        return this.bitmap;
    }

    public void loadBitmapFromFile(String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        if (bitmap != null) {
            setBitmap(bitmap);
        }
    }



    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setDrawer(Drawer drawer) {
        this.drawer = drawer;
    }

    public void setBaseColor(int baseColor) {
        this.baseColor = baseColor;
        this.canvas.drawColor(this.baseColor);
        this.invalidate();
    }

    public void clearCanvas() {
        this.canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); // Clear the bitmap
        this.pathLists.clear();
        this.paintLists.clear();
        this.historyPointer = 0;
        this.invalidate();
    }
}
