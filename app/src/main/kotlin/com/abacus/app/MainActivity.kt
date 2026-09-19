package com.abacus.app

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : ComponentActivity() {
    private lateinit var webView: WebView
    private var insetTop = 0
    private var insetBottom = 0
    private var insetLeft = 0
    private var insetRight = 0

    // The page is drawn edge-to-edge, so tell it how big the system bars / cutout are (in CSS px).
    private fun pushInsets() {
        if (!::webView.isInitialized) return
        val d = resources.displayMetrics.density
        val js = "var s=document.documentElement.style;" +
            "s.setProperty('--sat','${insetTop / d}px');" +
            "s.setProperty('--sab','${insetBottom / d}px');" +
            "s.setProperty('--sal','${insetLeft / d}px');" +
            "s.setProperty('--sar','${insetRight / d}px');"
        webView.evaluateJavascript(js, null)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        webView = WebView(this).apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                useWideViewPort = true
                loadWithOverviewMode = true
                cacheMode = WebSettings.LOAD_NO_CACHE
                mediaPlaybackRequiresUserGesture = false
            }
            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    pushInsets()
                }
            }
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
            addJavascriptInterface(WebAppInterface(this@MainActivity), "AndroidBridge")
            loadUrl("file:///android_asset/abacus.html")
        }

        setContentView(webView)

        ViewCompat.setOnApplyWindowInsetsListener(webView) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val imeBottom = if (imeVisible) insets.getInsets(WindowInsetsCompat.Type.ime()).bottom else 0
            insetTop = bars.top
            insetBottom = if (imeVisible) 0 else bars.bottom
            insetLeft = bars.left
            insetRight = bars.right
            // Shrink the page above the keyboard so focused inputs stay visible.
            (v.layoutParams as? ViewGroup.MarginLayoutParams)?.let { lp ->
                if (lp.bottomMargin != imeBottom) {
                    lp.bottomMargin = imeBottom
                    v.layoutParams = lp
                }
            }
            pushInsets()
            insets
        }
        WindowCompat.getInsetsController(window, webView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = true
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    class WebAppInterface(private val context: Context) {
        @JavascriptInterface
        fun vibrate(milliseconds: Long) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                    manager?.defaultVibrator?.vibrate(
                        VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v?.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        v?.vibrate(milliseconds)
                    }
                }
            } catch (_: Exception) {}
        }
    }
}
