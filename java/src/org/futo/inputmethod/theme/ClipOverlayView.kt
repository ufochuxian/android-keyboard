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

    private val clipRect = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 使用离屏缓冲
        val saved = canvas.saveLayer(null, null)

        // 绘制遮罩
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), maskPaint)

        // 计算裁剪框位置（例如屏幕中央 3:4 区域）
        val rectWidth = width * 0.8f
        val rectHeight = rectWidth * 4 / 3
        val left = (width - rectWidth) / 2
        val top = (height - rectHeight) / 2

        clipRect.set(left, top, left + rectWidth, top + rectHeight)

        // 擦除中间透明区域
        canvas.drawRect(clipRect, transparentPaint)

        canvas.restoreToCount(saved)
    }

    fun getClipRect(): RectF = clipRect
}
