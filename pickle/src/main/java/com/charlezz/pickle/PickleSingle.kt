package com.charlezz.pickle

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.charlezz.pickle.data.entity.Media
import com.charlezz.pickle.util.PickleActivityContract

class PickleSingle private constructor() {
    private lateinit var launcher: ActivityResultLauncher<Config>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var config: Config

    companion object {
        @JvmStatic
        fun register(
            fragment: Fragment,
            callback: Callback,
            fallback: Fallback
        ): PickleSingle {
            val pickle = PickleSingle()
            pickle.launcher = fragment.registerForActivityResult(PickleActivityContract()){ result->
                callback.onResult(result?.firstOrNull())
            }
            pickle.requestPermissionLauncher =
                fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                    if (granted) {
                        pickle.internalLaunch()
                    } else {
                        fallback.onFailed()
                    }
                }
            return pickle
        }

        @JvmStatic
        fun register(
            fragment: Fragment,
            callback: Callback
        ): PickleSingle {
            return register(fragment, callback, object:Fallback{
                override fun onFailed() {
                    showSystemSettingDialog(fragment.requireActivity())
                }

            })
        }

        @JvmStatic
        fun register(
            activity: ComponentActivity,
            callback: Callback,
            fallback: Fallback
        ): PickleSingle {
            val pickle = PickleSingle()
            pickle.launcher = activity.registerForActivityResult(PickleActivityContract()){ result->
                callback.onResult(result?.firstOrNull())
            }
            pickle.requestPermissionLauncher =
                activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted: Boolean ->
                    if (granted) {
                        pickle.internalLaunch()
                    } else {
                        fallback.onFailed()
                    }
                }
            return pickle
        }

        @JvmStatic
        fun register(
            activity: ComponentActivity,
            callback: Callback
        ): PickleSingle {
            return register(activity, callback, object:Fallback{
                override fun onFailed() {
                    showSystemSettingDialog(activity)
                }
            })
        }

        private fun showSystemSettingDialog(activity: Activity) {
            AlertDialog.Builder(activity)
                .setMessage(R.string.pickle_dialog_permission_message)
                .setPositiveButton(
                    R.string.pickle_dialog_permission_settings
                ) { dialog, which ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri: Uri = Uri.fromParts("package", activity.packageName, null)
                    intent.data = uri
                    activity.startActivity(intent)
                }
                .setNegativeButton(R.string.pickle_cancel, null)
                .show()
        }

    }

    private fun internalLaunch() {
        launcher.launch(config)
    }

    fun launch(config: Config) {
        this.config = config
        this.config.singleMode = true
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    interface Callback {
        fun onResult(media:Media?)
    }

    interface Fallback{
        fun onFailed()
    }

}