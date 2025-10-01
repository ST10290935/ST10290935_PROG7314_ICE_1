package com.example.snake2025

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.random.Random

class GameView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    enum class Direction { UP, DOWN, LEFT, RIGHT }

    private val bgPaint = Paint().apply { color = Color.parseColor("#EFEFEF") }
    private val snakePaint = Paint().apply { color = Color.parseColor("#1565C0") }
    private val foodPaint = Paint().apply { color = Color.parseColor("#D32F2F") }

    private var cellSize = 40
    private var numCols = 20
    private var numRows = 30

    private val snake = ArrayDeque<Point>()
    private var dir = Direction.RIGHT
    private var food = Point(5, 5)
    private var score = 0

    private val handler = Handler(Looper.getMainLooper())
    private var running = false
    private var speedMs = 150L

    private val detector = GestureDetector(context, GestureListener())

    private var gameEvents: GameEvents? = null

    init {
        // Start new game once the view is ready
        post { startNewGame() }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        cellSize = (w / numCols).coerceAtMost(h / numRows)
        setMeasuredDimension(numCols * cellSize, numRows * cellSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // background
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // food
        canvas.drawRect(
            (food.x * cellSize).toFloat(),
            (food.y * cellSize).toFloat(),
            ((food.x + 1) * cellSize).toFloat(),
            ((food.y + 1) * cellSize).toFloat(),
            foodPaint
        )

        // snake
        for (p in snake) {
            canvas.drawRect(
                (p.x * cellSize).toFloat(),
                (p.y * cellSize).toFloat(),
                ((p.x + 1) * cellSize).toFloat(),
                ((p.y + 1) * cellSize).toFloat(),
                snakePaint
            )
        }
    }

    private val stepRunnable = object : Runnable {
        override fun run() {
            if (!running) return
            step()
            invalidate()
            handler.postDelayed(this, speedMs)
        }
    }

    private fun step() {
        val head = snake.first()
        val newHead = when (dir) {
            Direction.UP -> Point(head.x, head.y - 1)
            Direction.DOWN -> Point(head.x, head.y + 1)
            Direction.LEFT -> Point(head.x - 1, head.y)
            Direction.RIGHT -> Point(head.x + 1, head.y)
        }

        // hit wall → game over
        if (newHead.x < 0 || newHead.y < 0 || newHead.x >= numCols || newHead.y >= numRows) {
            gameOver()
            return
        }

        // hit self → game over
        if (snake.any { it.x == newHead.x && it.y == newHead.y }) {
            gameOver()
            return
        }

        // move snake
        snake.addFirst(newHead)
        if (newHead == food) {
            score++
            placeFood()
            if (speedMs > 50) speedMs -= 4 // increase speed
        } else {
            snake.removeLast()
        }
    }

    private fun gameOver() {
        running = false
        handler.removeCallbacks(stepRunnable)
        gameEvents?.onGameOver(score)
    }

    private fun placeFood() {
        var p: Point
        do {
            p = Point(Random.nextInt(numCols), Random.nextInt(numRows))
        } while (snake.any { it == p })
        food = p
    }

    fun changeDirection(d: Direction) {
        // block 180° turns
        if ((dir == Direction.UP && d == Direction.DOWN) ||
            (dir == Direction.DOWN && d == Direction.UP) ||
            (dir == Direction.LEFT && d == Direction.RIGHT) ||
            (dir == Direction.RIGHT && d == Direction.LEFT)
        ) return
        dir = d
    }

    fun setGameEventsListener(listener: GameEvents) {
        this.gameEvents = listener
    }

    fun startNewGame() {
        snake.clear()
        val cx = numCols / 2
        val cy = numRows / 2
        snake.add(Point(cx, cy))
        snake.add(Point(cx - 1, cy))
        snake.add(Point(cx - 2, cy))
        dir = Direction.RIGHT
        score = 0
        speedMs = 150L
        placeFood()
        running = true
        handler.removeCallbacks(stepRunnable)
        handler.postDelayed(stepRunnable, speedMs)
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return detector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler.removeCallbacks(stepRunnable)
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESH = 50
        private val SWIPE_VEL_THRESH = 50

        override fun onDown(e: MotionEvent): Boolean = true

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1 == null || e2 == null) return false

            val diffX = e2.x - e1.x
            val diffY = e2.y - e1.y

            if (kotlin.math.abs(diffX) > kotlin.math.abs(diffY)) {
                if (kotlin.math.abs(diffX) > SWIPE_THRESH && kotlin.math.abs(velocityX) > SWIPE_VEL_THRESH) {
                    if (diffX > 0) changeDirection(Direction.RIGHT) else changeDirection(Direction.LEFT)
                    return true
                }
            } else {
                if (kotlin.math.abs(diffY) > SWIPE_THRESH && kotlin.math.abs(velocityY) > SWIPE_VEL_THRESH) {
                    if (diffY > 0) changeDirection(Direction.DOWN) else changeDirection(Direction.UP)
                    return true
                }
            }
            return false
        }
    }

    interface GameEvents {
        fun onGameOver(score: Int)
    }
}
