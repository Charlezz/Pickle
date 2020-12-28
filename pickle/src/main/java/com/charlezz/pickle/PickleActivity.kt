package com.charlezz.pickle

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.charlezz.pickle.dagger.DaggerPickleComponent
import com.charlezz.pickle.dagger.SharedViewModelProvider
import com.charlezz.pickle.data.PickleContentObserver
import com.charlezz.pickle.databinding.ActivityPickleBinding
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject


class PickleActivity : AppCompatActivity(), HasAndroidInjector {

    @Inject
    @JvmField
    @Volatile
    var androidInjector: DispatchingAndroidInjector<Any>? = null

    @Inject
    @SharedViewModelProvider
    lateinit var sharedViewModelProvider: ViewModelProvider

    @Inject
    lateinit var pickleContentObserver: PickleContentObserver

    private val binding: ActivityPickleBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_pickle)
    }

    private lateinit var sharedViewModel: PickleSharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        injectIfNecessary()
        super.onCreate(savedInstanceState)
        Timber.plant(DebugTree())

        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                setup()
            } else {
                finish()
            }
        }.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun setup() {
        binding.lifecycleOwner = this
        setSupportActionBar(binding.toolBar)
        this.sharedViewModel = sharedViewModelProvider.get(PickleSharedViewModel::class.java)
        lifecycle.addObserver(sharedViewModel)
        lifecycle.addObserver(pickleContentObserver)
        pickleContentObserver.getContentChangedEvent().observe(this) {
            Timber.e("getContentChangedEvent()")
            sharedViewModel.repository.invalidate()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuItem = menu?.add(0, 0, 0, "Done")
        menuItem?.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            0 -> {
                setResult(Activity.RESULT_OK, Intent().apply {
                    putParcelableArrayListExtra(
                        "media",
                        ArrayList(sharedViewModel.getSelectedMediaList())
                    )
                })
                finish()
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