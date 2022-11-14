# RainingAnimationView

This view can rain drawables.

### Supported effects:
- Wind
- 3D
- Wiggle
- Alpha

### Configuration:

Configuration can be done via **XML** or the **AnimationView.Config** class.<br>
The view uses its default values if nothing gets configured.<br>
*A drawable is always needed.*

<pre><code>
    data class Config(
        val direction: Int,
        val duration: Long,
        val spawnDelay: Long,
        val simulate3D: Boolean,
        val simulateWiggle: Boolean,
        val simulateWind: Boolean,
        val interpolator: Interpolator,
        val drawableConfig: DrawableConfig,
        val alphaConfig: AlphaConfig
    )

    data class DrawableConfig(
        val drawable: Drawable?,
        val drawableHeight: Int,
        val drawableWidth: Int
    )

    data class AlphaConfig(
        val alpha: Boolean,
        val from: Float,
        val to: Float,
        val duration: Long,
        val delay: Long
    )
    </code></pre>
    
### Usage

For example, call like this:

<pre><code>
  override fun onResume() {
    super.onResume()
    av.start()
  }

  override fun onPause() {
    super.onPause()
    av.stop()
  }
</code></pre>  
### Dependency

<pre><code>
  implementation 'com.github.fly8899:RainingAnimationView:1.0.2'
  
  allprojects {
    repositories {
      maven { url 'https://jitpack.io' }
    }
  }
</code></pre>

### Preview

![image](https://user-images.githubusercontent.com/85573678/201746617-7bdfb6e9-7619-4368-bf91-102c8ce48c9a.png)
