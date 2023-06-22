package com.jocoos.flipflop.sample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.jocoos.flipflop.sample.login.LoginActivity
import com.jocoos.flipflop.sample.main.MainActivity

/**
 * check permissions for live streaming
 */
class LaunchActivity : AppCompatActivity() {
    companion object {
        private const val PERMISSIONS_REQUEST = 10
    }
    private var permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_MEDIA_IMAGES,
        )
    } else {
        arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
    }
    private var permissionGranted = false
    private var initialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionGranted = requestPermission(permissions)
        initialized()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionGranted =
            grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        when (requestCode) {
            PERMISSIONS_REQUEST -> {
                if (permissionGranted) {
                    initialized()
                } else {
                    // need live permission
                }
            }
            else -> {

            }
        }
    }

    private fun requestPermission(permissions: Array<String>): Boolean {
        var mustRequest = false
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                mustRequest = true
                break
            }
        }
        if (mustRequest) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST)
        }
        return !mustRequest
    }

    private fun initialized() {
        if (!permissionGranted || initialized) {
            return
        }

        initialized = true
        if (FlipFlopSampleApp.preferenceManager.username.isNotEmpty()) {
            showMain()
        } else {
            showLogin()
        }

    }

    private fun showMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        finish()
    }

    private fun showLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        finish()
    }
}
