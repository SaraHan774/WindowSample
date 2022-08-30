package com.example.window

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import com.example.window.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val currentInsetTypes = mutableSetOf<Int>()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("===", "onCreate()")

        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.setOnApplyWindowInsetsListener { _, _ ->
            Log.d("===", "setOnApplyWindowInsetsListener()")
            applyInsets().toWindowInsets()
        }

        val itemTypes = listOf(
            binding.toggleCaptionBar,
            binding.toggleIme,
            binding.toggleMandatorySystemGestures,
            binding.toggleStatusBars,
            binding.toggleSystemBars,
            binding.toggleSystemGestures,
            binding.toggleTappableElement
        )

        binding.toggleDecorFitSystemWindows.setOnCheckedChangeListener { _, checked ->
            WindowCompat.setDecorFitsSystemWindows(window, checked)
            binding.insetTypes.isEnabled = checked
            if (checked) {
                itemTypes.forEach { checkBox ->
                    checkBox.isChecked = false
                    checkBox.isEnabled = false
                }
                currentInsetTypes.clear()
            } else {
                itemTypes.forEach { checkBox ->
                    checkBox.isEnabled = true
                }
            }
        }
        binding.toggleCaptionBar.addChangeListener(WindowInsetsCompat.Type.captionBar())
        binding.toggleIme.addChangeListener(WindowInsetsCompat.Type.ime())
        binding.toggleMandatorySystemGestures.addChangeListener(WindowInsetsCompat.Type.mandatorySystemGestures())
        binding.toggleStatusBars.addChangeListener(WindowInsetsCompat.Type.statusBars())
        binding.toggleSystemBars.addChangeListener(WindowInsetsCompat.Type.systemBars())
        binding.toggleSystemGestures.addChangeListener(WindowInsetsCompat.Type.systemGestures())
        binding.toggleTappableElement.addChangeListener(WindowInsetsCompat.Type.tappableElement())

        val controller = WindowCompat.getInsetsController(window, binding.root)
        // Just to disable re-showing system bars on touch
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
        binding.hideSystemBar.setOnCheckedChangeListener { _, checked ->
            if (checked) controller.hide(WindowInsetsCompat.Type.systemBars())
            else controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("===", "onResume()")
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.d("===", "onAttachedToWindow()")
    }

    private fun CheckBox.addChangeListener(type: Int) =
        setOnCheckedChangeListener { _, checked -> toggleType(type, checked) }

    private fun toggleType(type: Int, required: Boolean) {
        if (required) {
            currentInsetTypes.add(type)
        } else {
            currentInsetTypes.remove(type)
        }
        applyInsets()
    }

    private fun applyInsets(): WindowInsetsCompat {
        val currentInsetTypeMask = currentInsetTypes.fold(0) { accumulator, type ->
            accumulator or type
        }
        // getRootWindowInsets : On devices running API 20 and below, this method always returns null.
        val insets = ViewCompat.getRootWindowInsets(binding.root)!!.getInsets(currentInsetTypeMask)

        binding.root.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMargins(insets.left, insets.top, insets.right, insets.bottom)
        }

        return WindowInsetsCompat.Builder().setInsets(currentInsetTypeMask, insets).build()
    }
}