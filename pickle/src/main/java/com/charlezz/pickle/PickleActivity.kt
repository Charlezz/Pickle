package com.charlezz.pickle

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.WindowInsets
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.charlezz.pickle.data.PickleContentObserver
import com.charlezz.pickle.databinding.ActivityPickleBinding
import com.charlezz.pickle.util.DeviceUtil
import com.charlezz.pickle.util.dagger.SharedViewModelProvider
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
    lateinit var config:Config

    private val binding: ActivityPickleBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_pickle)
    }

    private val navHostFragment: NavHostFragment by lazy {
        NavHostFragment.create(R.navigation.pickle_graph)
    }

    private lateinit var sharedViewModel: PickleSharedViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        injectIfNecessary()
        super.onCreate(savedInstanceState)
        if(Timber.treeCount() == 0 && config.debugMode){
            Timber.plant(Timber.DebugTree())
        }
        Timber.d("onCreate hashCode = ${hashCode()} savedInstanceState = $savedInstanceState")
        binding.lifecycleOwner = this
        setSupportActionBar(binding.toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        lifecycle.addObserver(pickleContentObserver)

        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                setupNavigationComponent()
                setupViewModel()
                setupStatusBar()
                pickleContentObserver.getContentChangedEvent().observe(this) {
                    sharedViewModel.repository.invalidate()
                }
            } else {
                finish()
            }
        }.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun setupNavigationComponent() {
        Timber.d("setup")
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, navHostFragment)
            .setPrimaryNavigationFragment(navHostFragment)
            .commitNow()

        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            Timber.d("destination = $destination")
            when (destination.id) {
                R.id.pickleFragment -> { }
                R.id.pickleDetailFragment -> { }
            }
        }
    }

    private fun setupViewModel(){

//        val factory = injectingSavedStateViewModelFactory.create(this)
//        val viewModelProvider = ViewModelProvider(navHostFragment, factory)
//        this.sharedViewModel = viewModelProvider.get(PickleSharedViewModel::class.java)
        this.sharedViewModel = sharedViewModelProvider.get(PickleSharedViewModel::class.java)
        binding.toolbarViewModel = sharedViewModel.toolbarViewModel
        lifecycle.addObserver(sharedViewModel)
    }

    private fun setupStatusBar() {
        Timber.d("setupStatusBar")
        when {
            DeviceUtil.isAndroid11Later() -> {
                window.statusBarColor = Color.TRANSPARENT
                window.setDecorFitsSystemWindows(false)
                window.insetsController?.show(WindowInsets.Type.systemBars())
            }
            DeviceUtil.isAndroid5Later() -> {
                window.statusBarColor = Color.TRANSPARENT
                binding.rootLayout.systemUiVisibility =
                    SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            }
            else -> {
                val constraintSet = ConstraintSet()
                constraintSet.clone(binding.rootLayout)
                constraintSet.connect(
                    binding.fragmentContainerView.id, ConstraintSet.TOP,
                    binding.toolBar.id, ConstraintSet.BOTTOM)
                binding.fragmentContainerView
                constraintSet.applyTo(binding.rootLayout)

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuItem = menu?.add(MENU_GROUP_ID, MENU_ITEM_DONE_ID, MENU_ITEM_DONE_ID, R.string.done)
        menuItem?.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.groupId) {
            MENU_GROUP_ID -> {
                when (item.itemId) {
                    android.R.id.home -> onBackPressed()
                    MENU_ITEM_DONE_ID -> {
                        setResult(Activity.RESULT_OK, Intent().apply {
                            putParcelableArrayListExtra(
                                "media",
                                ArrayList(sharedViewModel.getSelectedMediaList())
                            )
                        })
                        finish()
                    }
                }
            }
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


}