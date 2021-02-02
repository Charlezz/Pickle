package com.charlezz.pickle.util

import com.charlezz.pickle.PickleActivity

class SystemUIController constructor(private val activity: PickleActivity) {
//    private val window = activity.window
//    private val binding = activity.binding
//    fun updateUI(fullscreenMode: Boolean) {
//        val constraintSet = ConstraintSet()
//        constraintSet.clone(binding.rootLayout)
//        if (fullscreenMode) {
//            constraintSet.connect(
//                binding.fragmentContainerView.id, ConstraintSet.TOP,
//                ConstraintSet.PARENT_ID, ConstraintSet.TOP
//            )
//        } else {
//            constraintSet.connect(
//                binding.fragmentContainerView.id, ConstraintSet.TOP,
//                binding.toolBar.id, ConstraintSet.BOTTOM
//            )
//        }
//        constraintSet.applyTo(binding.rootLayout)
//
//        when {
//            DeviceUtil.isAndroid11Later() -> {
//                handleAndroid11Later(fullscreenMode)
//            }
//            DeviceUtil.isAndroid5Later() -> {
//                handleAndroid5Later(fullscreenMode)
//            }
//            else -> {
//                handleOthers(fullscreenMode)
//            }
//        }
//    }
//
//
//    @RequiresApi(api = Build.VERSION_CODES.R)
//    private fun handleAndroid11Later(fullscreenMode: Boolean) {
//
//        val systemUIBackgroundColor = if (fullscreenMode) {
//            ContextCompat.getColor(activity, R.color.black_50)
//        } else {
//            ContextCompat.getColor(activity, R.color.black)
//        }
//        window.statusBarColor = systemUIBackgroundColor
//        window.navigationBarColor = systemUIBackgroundColor
//        binding.toolBar.setBackgroundColor(systemUIBackgroundColor)
//
//        if (activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            window.insetsController?.hide(WindowInsets.Type.statusBars())
//        } else {
//            window.insetsController?.show(WindowInsets.Type.statusBars())
//        }
//
//
//        window.setDecorFitsSystemWindows(false)
//
//        if(fullscreenMode){
//            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
//                binding.fragmentContainerView.setMarginLeft(0)
//                binding.fragmentContainerView.setMarginRight(0)
//                binding.fragmentContainerView.setMarginTop(0)
//                binding.fragmentContainerView.setMarginBottom(0)
//                when (view.display.rotation) {
//                    Surface.ROTATION_90 -> {
//                        binding.toolBar.setMarginLeft(0)
//                        binding.toolBar.setMarginRight(insets.systemWindowInsetRight)
//                    }
//                    Surface.ROTATION_270 -> {
//                        binding.toolBar.setMarginLeft(insets.systemWindowInsetLeft)
//                        binding.toolBar.setMarginRight(0)
//                    }
//                    else -> {
//                        binding.toolBar.setMarginLeft(insets.systemWindowInsetLeft)
//                        binding.toolBar.setMarginRight(insets.systemWindowInsetRight)
//                    }
//                }
//                binding.toolBar.setMarginTop(insets.systemWindowInsetTop)
//                binding.toolBar.setMarginBottom(insets.systemWindowInsetBottom)
//                insets
//            }
//        }else{
//            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insetsCompat ->
//                val insets = binding.toolBar.rootWindowInsets.getInsets(WindowInsets.Type.systemBars())
//
//                when (binding.root.display.rotation) {
//                    Surface.ROTATION_90 -> {
//                        binding.toolBar.setMarginLeft(0)
//                        binding.toolBar.setMarginRight(insets.right)
//                        binding.fragmentContainerView.setMarginLeft(0)
//                        binding.fragmentContainerView.setMarginRight(insets.right)
//                    }
//                    Surface.ROTATION_270 -> {
//                        binding.toolBar.setMarginLeft(insets.left)
//                        binding.toolBar.setMarginRight(0)
//                        binding.fragmentContainerView.setMarginLeft(insets.left)
//                        binding.fragmentContainerView.setMarginRight(0)
//                    }
//                    else -> {
//                        binding.toolBar.setMarginLeft(insets.left)
//                        binding.toolBar.setMarginRight(insets.right)
//                    }
//                }
//                binding.toolBar.setMarginTop(insets.top)
//                binding.fragmentContainerView.setMarginBottom(insets.bottom)
//
//                insetsCompat
//            }
//        }
//
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private fun handleAndroid5Later(fullscreenMode: Boolean) {
//        val systemUIBackgroundColor = if (fullscreenMode) {
//            ContextCompat.getColor(activity, R.color.black_50)
//        } else {
//            ContextCompat.getColor(activity, R.color.black)
//        }
//        window.statusBarColor = systemUIBackgroundColor
//        window.navigationBarColor = systemUIBackgroundColor
//        binding.toolBar.setBackgroundColor(systemUIBackgroundColor)
//        if (activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
//        } else {
//            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
//        }
//
//        if (fullscreenMode) {
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
//                binding.fragmentContainerView.setMarginLeft(0)
//                binding.fragmentContainerView.setMarginRight(0)
//                binding.fragmentContainerView.setMarginTop(0)
//                binding.fragmentContainerView.setMarginBottom(0)
//                when (view.display.rotation) {
//                    Surface.ROTATION_90 -> {
//                        binding.toolBar.setMarginLeft(0)
//                        binding.toolBar.setMarginRight(insets.systemWindowInsetRight)
//                    }
//                    Surface.ROTATION_270 -> {
//                        binding.toolBar.setMarginLeft(insets.systemWindowInsetLeft)
//                        binding.toolBar.setMarginRight(0)
//                    }
//                    else -> {
//                        binding.toolBar.setMarginLeft(insets.systemWindowInsetLeft)
//                        binding.toolBar.setMarginRight(insets.systemWindowInsetRight)
//                    }
//                }
//                binding.toolBar.setMarginTop(insets.systemWindowInsetTop)
//                binding.toolBar.setMarginBottom(insets.systemWindowInsetBottom)
//                insets
//            }
//        } else {
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
//                when (binding.root.display.rotation) {
//                    Surface.ROTATION_90 -> {
//                        binding.toolBar.setMarginLeft(0)
//                        binding.toolBar.setMarginRight(insets.systemWindowInsetRight)
//                        binding.fragmentContainerView.setMarginLeft(0)
//                        binding.fragmentContainerView.setMarginRight(insets.systemWindowInsetRight)
//                    }
//                    Surface.ROTATION_270 -> {
//                        binding.toolBar.setMarginLeft(insets.systemWindowInsetLeft)
//                        binding.toolBar.setMarginRight(0)
//                        binding.fragmentContainerView.setMarginLeft(insets.systemWindowInsetLeft)
//                        binding.fragmentContainerView.setMarginRight(0)
//                    }
//                    else -> {
//                        binding.toolBar.setMarginLeft(insets.systemWindowInsetLeft)
//                        binding.toolBar.setMarginRight(insets.systemWindowInsetRight)
//                    }
//                }
//                binding.toolBar.setMarginTop(insets.systemWindowInsetTop)
//                binding.toolBar.setMarginBottom(insets.systemWindowInsetBottom)
//                insets
//            }
//        }
//
//    }
//
//    private fun handleOthers(fullscreenMode: Boolean) {
//
//    }

}