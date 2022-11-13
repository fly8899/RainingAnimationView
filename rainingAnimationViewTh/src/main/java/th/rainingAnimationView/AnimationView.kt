package th.rainingAnimationView

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.view.animation.*
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.view.updateMargins
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

class AnimationView : RelativeLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    object Constants {
        object AnimationViewDefault {
            const val DURATION = 5000L
            const val SPAWN_DELAY = 500L
            const val SIMULATE_3D = false
            const val SIMULATE_WIND = false
            const val SIMULATE_WIGGLE = false
            const val DRAWABLE_HEIGHT = 50
            const val DRAWABLE_WIDTH = 50
            const val ALPHA = false
            const val ALPHA_FROM = 1f
            const val ALPHA_TO = 0f
            const val DEFAULT_DELAY = 0L
        }

        object Direction {
            const val DIRECTION_TOP_TO_BOTTOM = 0
            const val DIRECTION_BOTTOM_TO_TOP = 1
        }

        object Interpolator {
            const val LINEAR = 0
            const val SLOW_IN_FAST_OUT = 1
        }
    }

    private val _animationHandler = Handler(Looper.getMainLooper())
    private val _animationRunnable: Runnable = Runnable {
        initAnimation()
        start()
    }
    private val _stopAnimations: Runnable = Runnable {
        _animationHandler.removeCallbacks(_animationRunnable)
        _animationHandler.post {
            children.forEach { it.clearAnimation() }
            removeAllViews()
        }
    }

    data class Config(
        val direction: Int = Constants.Direction.DIRECTION_TOP_TO_BOTTOM,
        val duration: Long = Constants.AnimationViewDefault.DURATION,
        val spawnDelay: Long = Constants.AnimationViewDefault.SPAWN_DELAY,
        val simulate3D: Boolean = Constants.AnimationViewDefault.SIMULATE_3D,
        val simulateWiggle: Boolean = Constants.AnimationViewDefault.SIMULATE_WIGGLE,
        val simulateWind: Boolean = Constants.AnimationViewDefault.SIMULATE_WIND,
        val interpolator: Interpolator? = null,
        val drawableConfig: DrawableConfig = DrawableConfig(),
        val alphaConfig: AlphaConfig = AlphaConfig()
    )

    data class DrawableConfig(
        val drawable: Drawable? = null,
        val drawableHeight: Int = Constants.AnimationViewDefault.DRAWABLE_HEIGHT,
        val drawableWidth: Int = Constants.AnimationViewDefault.DRAWABLE_WIDTH
    )

    data class AlphaConfig(
        val alpha: Boolean = false,
        val from: Float = Constants.AnimationViewDefault.ALPHA_FROM,
        val to: Float = Constants.AnimationViewDefault.ALPHA_TO,
        val duration: Long = Constants.AnimationViewDefault.DURATION,
        val delay: Long = Constants.AnimationViewDefault.DEFAULT_DELAY
    )

    private var _config = Config()
    val config: Config
        get() = _config

    private fun init(attrs: AttributeSet) {
        context?.theme?.obtainStyledAttributes(
            attrs,
            R.styleable.AnimationView,
            0, 0
        )?.apply {
            try {

                @DrawableRes
                val drawableId: Int = getResourceId(
                    R.styleable.AnimationView_drawableId,
                    R.drawable.ic_android_animation_view
                )
                val interpolator = when (getInteger(
                    R.styleable.AnimationView_interpolator,
                    Constants.Interpolator.LINEAR
                )) {
                    Constants.Interpolator.SLOW_IN_FAST_OUT -> FastOutSlowInInterpolator()
                    Constants.Interpolator.LINEAR -> LinearInterpolator()
                    else -> throw AnimationViewException("Undefined interpolator")
                }

                _config = Config(
                    direction = getInteger(
                        R.styleable.AnimationView_direction,
                        Constants.Direction.DIRECTION_TOP_TO_BOTTOM
                    ),
                    duration = getInteger(
                        R.styleable.AnimationView_duration,
                        Constants.AnimationViewDefault.DURATION.toInt()
                    ).toLong(),
                    spawnDelay = getInteger(
                        R.styleable.AnimationView_spawnDelay,
                        Constants.AnimationViewDefault.SPAWN_DELAY.toInt()
                    ).toLong(),
                    simulate3D = getBoolean(
                        R.styleable.AnimationView_simulate3D,
                        Constants.AnimationViewDefault.SIMULATE_3D
                    ),
                    simulateWind = getBoolean(
                        R.styleable.AnimationView_simulateWind,
                        Constants.AnimationViewDefault.SIMULATE_WIND
                    ),
                    simulateWiggle = getBoolean(
                        R.styleable.AnimationView_simulateWiggle,
                        Constants.AnimationViewDefault.SIMULATE_WIGGLE
                    ),
                    interpolator = interpolator,
                    drawableConfig = DrawableConfig(
                        drawable = ContextCompat.getDrawable(
                            context,
                            drawableId
                        ),
                        drawableHeight = getInteger(
                            R.styleable.AnimationView_drawableHeight,
                            Constants.AnimationViewDefault.DRAWABLE_HEIGHT
                        ),
                        drawableWidth = getInteger(
                            R.styleable.AnimationView_drawableWidth,
                            Constants.AnimationViewDefault.DRAWABLE_WIDTH
                        )
                    ),
                    alphaConfig = AlphaConfig(
                        alpha = getBoolean(
                            R.styleable.AnimationView_alphaAnimation,
                            Constants.AnimationViewDefault.ALPHA
                        ),
                        from = getFloat(
                            R.styleable.AnimationView_alphaFromAnimation,
                            Constants.AnimationViewDefault.ALPHA_FROM
                        ),
                        to = getFloat(
                            R.styleable.AnimationView_alphaToAnimation,
                            Constants.AnimationViewDefault.ALPHA_TO
                        ),
                        duration =
                        getInteger(
                            R.styleable.AnimationView_alphaDurationAnimation,
                            Constants.AnimationViewDefault.DURATION.toInt()
                        ).toLong(),
                        delay = getFloat(
                            R.styleable.AnimationView_alphaDelayAnimation,
                            Constants.AnimationViewDefault.DEFAULT_DELAY.toFloat()
                        ).toLong()
                    )
                )
            } finally {
                recycle()
            }
        }
    }

    fun start() {
        _animationHandler.postDelayed(_animationRunnable, config.spawnDelay)
    }

    fun stop() {
        _animationHandler.post(_stopAnimations)
    }

    fun start(config: Config) {
        _config = config
        start()
    }

    fun start(drawableConfig: DrawableConfig) {
        _config = config.copy(drawableConfig = drawableConfig)
        start()
    }

    private fun initAnimation() {
        if (!hasDrawable()) return
        if (measuredWidth == 0 || measuredHeight == 0) return
        val imgV = ImageView(context)
        imgV.id = ViewCompat.generateViewId()
        imgV.isClickable = false
        imgV.isFocusable = false
        val size = if (config.simulate3D) (1..5).random() else 1
        val layoutParams = LayoutParams(
            config.drawableConfig.drawableHeight / size,
            config.drawableConfig.drawableWidth / size
        )
        val minMargin = 0
        val maxMargin = measuredWidth - layoutParams.width
        layoutParams.updateMargins(left = (minMargin..maxMargin).random())
        imgV.setImageDrawable(config.drawableConfig.drawable)
        addView(imgV, layoutParams)
        val animationSet = getAnimationSet()
        animationSet.addAnimation(getTranslateAnimation())
        if (config.alphaConfig.alpha) animationSet.addAnimation(getAlphaAnimation())
        if (config.simulateWind) animationSet.addAnimation(getSimulateWindAnimation())
        if (config.simulateWiggle) animationSet.addAnimation(getSimulateWiggleAnimation())
        animationSet.setAnimationListener(getAnimationEndListener(imgV))
        imgV.animation = animationSet
        animationSet.start()
    }

    private fun getAnimationSet(): AnimationSet {
        val animationSet = AnimationSet(true)
        animationSet.interpolator = config.interpolator
        return animationSet
    }

    private fun getSimulateWiggleAnimation(): AnimationSet {
        val animationSet = AnimationSet(true)
        val animations = 4
        val duration = config.duration / animations
        var start = 0f
        var end = 0f
        repeat(animations) {
            end = if (it % 2 == 0) (2..5).random().toFloat() else -end
            val rotationAnimation = RotateAnimation(start, end)
            rotationAnimation.duration = duration
            rotationAnimation.startOffset = duration * it
            animationSet.addAnimation(rotationAnimation)
            start = end
        }
        return animationSet
    }

    private fun getSimulateWindAnimation(): AnimationSet {
        val animationSet = AnimationSet(true)
        val animations = 4
        val duration = config.duration / animations
        var start = 0f
        var end = 0f
        repeat(animations) {
            end += (2..4).random()
            val rotationAnimation = RotateAnimation(start, end)
            rotationAnimation.duration = duration
            rotationAnimation.startOffset = duration * it
            animationSet.addAnimation(rotationAnimation)
            start = end
        }
        return animationSet
    }

    private fun getTranslateAnimation(): Animation {
        val bottom =
            measuredHeight.toFloat() + config.drawableConfig.drawableHeight
        val top = 0f - config.drawableConfig.drawableHeight
        val fromYDelta: Float
        val toYDelta: Float
        when (config.direction) {
            Constants.Direction.DIRECTION_BOTTOM_TO_TOP -> {
                fromYDelta = bottom
                toYDelta = top * 1.25f
            }
            Constants.Direction.DIRECTION_TOP_TO_BOTTOM -> {
                fromYDelta = top
                toYDelta = bottom * 1.25f
            }
            else -> throw AnimationViewException("Invalid direction")
        }
        val translateAnimation = TranslateAnimation(0f, 0f, fromYDelta, toYDelta)
        translateAnimation.duration = (config.duration * 1.25f).toLong()
        return translateAnimation
    }

    private fun getAlphaAnimation(): AlphaAnimation {
        val alphaAnimation = AlphaAnimation(config.alphaConfig.from, config.alphaConfig.to)
        alphaAnimation.startOffset = config.alphaConfig.delay
        alphaAnimation.duration = config.alphaConfig.duration
        return alphaAnimation
    }

    private fun getAnimationEndListener(v: View): AnimationEndListener {
        return object : AnimationEndListener {
            override fun onAnimationEnd(p0: Animation?) {
                v.clearAnimation()
                v.visibility = View.GONE
                handler.post { removeView(v) }
            }
        }
    }

    private fun hasDrawable() = config.drawableConfig.drawable != null
}