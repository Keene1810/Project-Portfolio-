package com.innovisiontechnology.ilkfoundation

import android.content.Context
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class CircularImageView (context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {

    private val paint = Paint().apply {
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        val drawable = drawable ?: return
        val bitmap = (drawable as BitmapDrawable).bitmap
        val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader

        val radius = Math.min(width / 2.0f, height / 2.0f)
        canvas.drawCircle(width / 2.0f, height / 2.0f, radius, paint)
    }

}