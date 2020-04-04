package com.colisa.minipaint.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import com.colisa.minipaint.R
import kotlin.math.abs

private const val STROKE_WIDTH = 12f


class CanvasView(context: Context) : View(context) {
    //    private lateinit var extraCanvas: Canvas
//    private lateinit var extraBitmap: Bitmap
    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)
    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)
    private val paint = Paint().apply {
        color = drawColor
        // Smooth edges without affecting shape
        isAntiAlias = true
        // Reduce color range of images down to 256 or fewer
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH //default: Hairline-width (really thin)
    }
    //    private var path = Path()
    // Cache motion event coordinates
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f
    // Cache latest coordinates after touchUp
    private var currentX = 0f
    private var currentY = 0f
    // Wander distance (px) before we think user is scrolling
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop
    private lateinit var frame: Rect
    private val insert = 40
    // Path representing drawing so far
    private val drawing = Path()
    // Path representing what is currently being drawn
    private val currentPath = Path()


    init {
        systemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN
        contentDescription = resources.getString(R.string.canvasContentDescription)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        canvas.drawBitmap(extraBitmap, 0f, 0f, null)
        canvas.drawColor(backgroundColor)
        canvas.drawPath(drawing, paint)
        canvas.drawPath(currentPath, paint)
//        canvas.drawRect(frame, paint)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // To avoid memory loss recycler whenever creating new instances
//        if (::extraBitmap.isInitialized) extraBitmap.recycle()
//        extraBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
//        extraCanvas = Canvas(extraBitmap)
//        extraCanvas.drawColor(backgroundColor)
        frame = Rect(0 + insert, 0 + insert, w - insert, height - insert)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventY = event.y
        motionTouchEventX = event.x
        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }

    private fun touchStart() {
        currentPath.reset()
        currentPath.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun touchMove() {
        val dx = abs(motionTouchEventX - currentX)
        val dy = abs(motionTouchEventY - currentY)
        if (dx >= touchTolerance || dy >= touchTolerance) {
            // Add quadratic from last point (x1, y1) -> (x2, y2)
            currentPath.quadTo(
                currentX,
                currentY,
                (motionTouchEventX + currentX) / 2,
                (motionTouchEventY + currentY) / 2
            )
            currentX = motionTouchEventX
            currentY = motionTouchEventY

//            extraCanvas.drawPath(path, paint)
        }
        invalidate()
    }

    private fun touchUp() {
        drawing.addPath(currentPath)
        currentPath.reset()
    }


}