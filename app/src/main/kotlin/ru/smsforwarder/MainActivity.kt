package ru.smsforwarder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import ru.smsforwarder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val requireBinding: ActivityMainBinding
        get() = requireNotNull(_binding)

    private val grantedText by lazy {
        getString(R.string.permission_granted)
    }
    private val denyText by lazy {
        getString(R.string.permission_deny)
    }

    private val receiveSmsPermission = Manifest.permission.RECEIVE_SMS

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            setGranted()
        } else {
            setDeny()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(requireBinding.root)

        checkPermission()
    }

    private fun checkPermission() {
        when (ContextCompat.checkSelfPermission(this, receiveSmsPermission)) {
            PackageManager.PERMISSION_GRANTED -> {
                setGranted()
            }
            else -> {
                setDeny()
                requestPermissionLauncher.launch(receiveSmsPermission)
            }
        }
    }

    private fun setGranted() {
        requireBinding.permissionStatus.text = grantedText
        requireBinding.permissionStatus.setBackgroundResource(R.color.granted)
    }

    private fun setDeny() {
        requireBinding.permissionStatus.text = denyText
        requireBinding.permissionStatus.setBackgroundResource(R.color.deny)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
