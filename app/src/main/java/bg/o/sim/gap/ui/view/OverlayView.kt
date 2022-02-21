package bg.o.sim.gap.ui.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class OverlayView : View {

    private val paint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }


    private var rectangles: List<Rect> = emptyList()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)


    public fun setRectangles(rectangles: List<Rect>) {
        this.rectangles = rectangles
        this.invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (canvas == null)
            return

        for (rect in this.rectangles)
            canvas.drawRect(rect, paint)
    }
}