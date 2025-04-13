package org.futo.inputmethod.theme

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class ClipOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val maskPaint = Paint().apply {
        color = Color.parseColor("#A6000000") // 半透明黑色遮罩
    }

    private val transparentPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private var targetCropWidth: Int = 0
    private var targetCropHeight: Int = 0

    fun setCropSize(width: Int, height: Int) {
        targetCropWidth = width
        targetCropHeight = height
        invalidate()
    }


    private val clipRect = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val saved = canvas.saveLayer(null, null)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), maskPaint)

        // 保持和键盘一样的尺寸
        val rectWidth = targetCropWidth.toFloat().coerceAtMost(width.toFloat())
        val rectHeight = targetCropHeight.toFloat().coerceAtMost(height.toFloat())

        val left = (width - rectWidth) / 2
        val top = (height - rectHeight) / 2
        clipRect.set(left, top, left + rectWidth, top + rectHeight)

        canvas.drawRect(clipRect, transparentPaint)
        canvas.restoreToCount(saved)
    }


    fun getClipRect(): RectF = clipRect
}
