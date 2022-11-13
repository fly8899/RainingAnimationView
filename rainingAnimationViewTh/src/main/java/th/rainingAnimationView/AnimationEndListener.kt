package th.rainingAnimationView

import android.view.animation.Animation

interface AnimationEndListener : Animation.AnimationListener {
    override fun onAnimationStart(p0: Animation?) = Unit
    override fun onAnimationRepeat(p0: Animation?) = Unit
}