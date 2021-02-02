package com.charlezz.pickle

import android.Manifest
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.charlezz.pickle.data.PickleContentObserver
import com.charlezz.pickle.data.entity.SystemUIType
import com.charlezz.pickle.databinding.ActivityPickleBinding
import com.charlezz.pickle.util.DeviceUtil
import com.charlezz.pickle.util.dagger.SharedViewModelProvider
import com.charlezz.pickle.util.lifecycle.SingleLiveEvent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber
import javax.inject.Inject


class PickleActivity : AppCompatActivity(), HasAndroidInjector {

    companion object {
        private const val MENU_GROUP_ID = 0
        private const val MENU_ITEM_DONE_ID = 0
    }

    @Inject
    @JvmField
    @Volatile
    var androidInjector: DispatchingAndroidInjector<Any>? = null

    @Inject
    @SharedViewModelProvider
    lateinit var sharedViewModelProvider: ViewModelProvider

    @Inject
    lateinit var pickleContentObserver: PickleContentObserver

    @Inject
    lateinit var config: Config

    @Inject
    lateinit var systemUIEvent: SingleLiveEvent<SystemUIType>


    val binding: ActivityPickleBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_pickle)
    }

    private val navHostFragment: NavHostFragment by lazy {
        supportFragmentManager.primaryNavigationFragment as NavHostFragment
    }

    private lateinit var sharedViewModel: PickleSharedViewModel

    private var doneMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        injectIfNecessary()
        super.onCreate(savedInstanceState)
        if (Timber.treeCount() == 0 && config.debugMode) {
            Timber.plant(Timber.DebugTree())
        }
        Timber.d("onCreate hashCode = ${hashCode()} savedInstanceState = $savedInstanceState")
        binding.lifecycleOwner = this
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        lifecycle.addObserver(pickleContentObserver)

        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                setupViewModel()
                setupSystemUI()
                pickleContentObserver.getContentChangedEvent().observe(this) {
                    sharedViewModel.repository.invalidate()
                }
                navHostFragment.navController.addOnDestinationChangedListener { controller, destination, arguments ->
                    invalidateOptionsMenu()
                }
                sharedViewModel.selection.getCount().observe(this) { invalidateOptionsMenu() }
            } else {
                finish()
            }
        }.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

        systemUIEvent.observe(this) {
            when (it) {
                SystemUIType.NORMAL -> disableFullscreen()
                SystemUIType.FULLSCREEN -> enableFullscreen()
                SystemUIType.FULLSCREEN_WITHOUT_SYSTEM_UI -> hideSystemUI()
            }
        }

    }

    private fun disableFullscreen() {
        if (DeviceUtil.isAndroid5Later()) {
            window.apply {
                val systemUIBackgroundColor =
                    ContextCompat.getColor(this@PickleActivity, R.color.black)
                statusBarColor = systemUIBackgroundColor
                navigationBarColor = systemUIBackgroundColor
                clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
        }
    }

    private fun enableFullscreen() {
        if(DeviceUtil.isAndroid5Later()){
            val colorRes = R.color.black_a50
            val systemUIBackgroundColor = ContextCompat.getColor(this, colorRes)
            window.statusBarColor = systemUIBackgroundColor
            window.navigationBarColor = systemUIBackgroundColor
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }

            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
    }

    private fun hideSystemUI() {
        if(DeviceUtil.isAndroid5Later()){
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)

        }
    }

    private fun setupViewModel() {
        this.sharedViewModel = sharedViewModelProvider.get(PickleSharedViewModel::class.java)
        lifecycle.addObserver(sharedViewModel)
    }

    private fun setupSystemUI() {
        Timber.d("setupStatusBar")
//        sharedViewModel.currentDestinationId.observe(this) { destinationId ->
//            when (navHostFragment.navController.currentDestination?.id) {
//                R.id.pickleDetailFragment -> {
//                    systemUIController.updateUI(true)
//                }
//                else -> {
//                    systemUIController.updateUI(false)
//
//                }
//            }
//        }
    }

//    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
//        when (navHostFragment.navController.currentDestination?.id) {
//            R.id.pickleFragment, R.id.pickleDetailFragment -> {
//                doneMenuItem =
//                    menu?.add(MENU_GROUP_ID, MENU_ITEM_DONE_ID, MENU_ITEM_DONE_ID, R.string.done)
//                doneMenuItem?.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
//                doneMenuItem?.isEnabled = !sharedViewModel.selection.isEmpty()
//            }
//        }
//        return super.onPrepareOptionsMenu(menu)
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun injectIfNecessary() {
        if (androidInjector == null) {
            synchronized(this) {
                if (androidInjector == null) {
                    createPickleInjector().inject(this)
                    checkNotNull(androidInjector) {
                        ("The AndroidInjector returned from applicationInjector() did not inject the "
                                + "DaggerApplication")
                    }
                }
            }
        }
    }

    private fun createPickleInjector(): AndroidInjector<PickleActivity> {
        return DaggerPickleComponent.factory().create(this)
    }

    override fun androidInjector(): AndroidInjector<Any?>? {
        injectIfNecessary()
        return androidInjector
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Timber.e("onConfigurationChanged")
    }
}