package com.jocoos.flipflop.sample.live

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.jocoos.flipflop.sample.R
import com.jocoos.flipflop.sample.databinding.StreamingActivityBinding
import com.jocoos.flipflop.sample.utils.PreferenceManager
import com.jocoos.flipflop.sample.utils.makeStatusBarTransparent

/**
 * Live : StreamingFragment + [StreamingPrepareFragment, StreamingLiveFragment]
 * PopUp : CameraOptionFragment, MoreOptionFragment
 */
class StreamingActivity : AppCompatActivity() {
    private lateinit var binding: StreamingActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = StreamingActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        makeStatusBarTransparent()

        val extras = intent.extras
        val accessToken = extras!!.getString(PreferenceManager.KEY_ACCESS_TOKEN)!!
        val bundle = Bundle().apply {
            putString(PreferenceManager.KEY_ACCESS_TOKEN, accessToken)
        }
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
            .navController.setGraph(R.navigation.nav_streaming, bundle)
    }

    override fun onResume() {
        super.onResume()
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onStop() {
        super.onStop()
        window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onSupportNavigateUp()
            = findNavController(R.id.nav_host_fragment).navigateUp()
}
