package com.example.myvideoplayer

import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.myvideoplayer.databinding.ActivityPlayerBinding
import com.example.myvideoplayer.databinding.MoreFeaturesBinding
import com.example.myvideoplayer.model.Video
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*
import kotlin.collections.ArrayList

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    lateinit var runnable: Runnable

    companion object {
        lateinit var videosPlayer: ExoPlayer
        lateinit var playerList: ArrayList<Video>
        var position: Int = -1
        private var repeat: Boolean = false
        private var isFullScreen: Boolean = false
        private var isLocked: Boolean = false
        lateinit var trackSelector: DefaultTrackSelector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setTheme(R.style.playerActivityTheme)
        setContentView(binding.root)
        //for full screen mode
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        initializeLayout()
        initializeBinding()
    }

    private fun initializeLayout() {
        when (intent.getStringExtra("class")) {
            "AllVideos" -> {
                playerList = java.util.ArrayList()
                playerList.addAll(MainActivity.videoListNew)
                createVideosPlayer()
            }
            "FolderActivity" -> {
                playerList = ArrayList()
                playerList.addAll(FolderActivity.currentFolderVideo)
                createVideosPlayer()
            }
        }
        if (repeat) {
            binding.repeatBtn.setImageResource(R.drawable.exo_controls_repeat_all)
        } else {
            binding.repeatBtn.setImageResource(R.drawable.exo_controls_repeat_off)
        }

    }

    private fun initializeBinding() {
        binding.backBtn.setOnClickListener {
            finish()
        }
        binding.playPauseBtn.setOnClickListener {
            if (videosPlayer.isPlaying) pauseVideo()
            else playVideo()
        }
        binding.nextBtn.setOnClickListener { nextprevVideo() }
        binding.prevBtn.setOnClickListener { nextprevVideo(isNext = false) }
        binding.repeatBtn.setOnClickListener {
            if (repeat) {
                repeat = false
                videosPlayer.repeatMode = Player.REPEAT_MODE_OFF
                binding.repeatBtn.setImageResource(R.drawable.exo_controls_repeat_off)
            } else {
                repeat = true
                videosPlayer.repeatMode = Player.REPEAT_MODE_ONE
                binding.repeatBtn.setImageResource(R.drawable.exo_controls_repeat_all)
            }
        }
        //for full screen
        binding.fullScreenBtn.setOnClickListener {
            if (isFullScreen) {
                isFullScreen = false
                playFullscreen(enable = false)
            } else {

                isFullScreen = true
                playFullscreen(enable = true)
            }
        }
        binding.lockBtn.setOnClickListener {
            if (!isLocked) {
                //for hiding controlls
                isLocked = true
                binding.viewExoPlayer.hideController()
                binding.viewExoPlayer.useController = false
                binding.lockBtn.setImageResource(R.drawable.ic_close_lock_icon)


            } else {

                //for showing controlls
                isLocked = false
                binding.viewExoPlayer.useController = true
                binding.viewExoPlayer.showController()

                binding.lockBtn.setImageResource(R.drawable.ic_lock_open_icon)

            }
        }


        binding.moreFeatureBtn.setOnClickListener {
            pauseVideo()
            val customDialog =
                LayoutInflater.from(this).inflate(R.layout.more_features, binding.root, false)
            val bindingMF = MoreFeaturesBinding.bind(customDialog)
            val dialog = MaterialAlertDialogBuilder(this).setView(customDialog)
                .setOnCancelListener { playVideo() }
                .setBackground(ColorDrawable(0x803700B3.toInt()))
                .create()
            dialog.show()
            bindingMF.audioTrack.setOnClickListener {
                dialog.dismiss()
                createVideosPlayer()
                val audioTrack = ArrayList<String>()
                for (i in 0 until videosPlayer.currentTrackGroups.length) {
                    if (videosPlayer.currentTrackGroups.get(i)
                            .getFormat(0).selectionFlags == C.SELECTION_FLAG_DEFAULT
                    ) {
                        audioTrack.add(Locale(videosPlayer.currentTrackGroups.get(i)
                            .getFormat(0).language.toString()).displayLanguage)
                    }
                }
                val tempTracks = audioTrack.toArray(arrayOfNulls<CharSequence>(audioTrack.size))
                MaterialAlertDialogBuilder(this, R.style.alertDialog)
                    .setTitle("Select Language")
                    .setOnCancelListener { playVideo() }
                    .setBackground(ColorDrawable(0x803700B3.toInt()))
                    .setItems(tempTracks) { _, position ->
                        Toast.makeText(this, audioTrack[position] + "Selected", Toast.LENGTH_LONG)
                            .show()
                        trackSelector.setParameters(trackSelector.buildUponParameters()
                            .setPreferredAudioLanguage(audioTrack[position]))

                    }
                    .create()
                    .show()
            }
        }
    }

    private fun createVideosPlayer() {
        //to check that playerr already create if not tham catch Exception
        try {
            videosPlayer.release()
        } catch (e: Exception) {
        }
        trackSelector = DefaultTrackSelector(this)
        binding.viewVideoTitle.text = playerList[position].title
        binding.viewVideoTitle.isSelected = true
        videosPlayer = ExoPlayer.Builder(this).setTrackSelector(trackSelector).build()
        binding.viewExoPlayer.player = videosPlayer
        val mediaItem = MediaItem.fromUri(playerList[position].artUri)
        videosPlayer.setMediaItem(mediaItem)
        videosPlayer.prepare()
        playVideo()
        videosPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == Player.STATE_ENDED) nextprevVideo()
            }
        })
        playFullscreen(enable = isFullScreen)
        setVisibility()
    }

    private fun playVideo() {
        binding.playPauseBtn.setImageResource(R.drawable.ic_pause_icon)
        videosPlayer.play()
    }

    private fun pauseVideo() {
        binding.playPauseBtn.setImageResource(R.drawable.ic_play_arrow_icon)
        videosPlayer.pause()
    }

    private fun nextprevVideo(isNext: Boolean = true) {
        videosPlayer.release()
        if (isNext) setPosition()
        else setPosition(isIncrement = false)
        createVideosPlayer()
    }

    private fun setPosition(isIncrement: Boolean = true) {
        if (!repeat) {

            if (isIncrement) {
                if (playerList.size - 1 == position) position = 0
                else ++position
            } else {
                if (position == 0) position = playerList.size - 1
                else --position
            }
        }
    }

    private fun playFullscreen(enable: Boolean) {
        if (enable) {
            binding.viewExoPlayer.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            videosPlayer.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            binding.fullScreenBtn.setImageResource(R.drawable.ic_fullscreen_exit_icon)
        } else {
            binding.viewExoPlayer.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            videosPlayer.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
            binding.fullScreenBtn.setImageResource(R.drawable.ic_fullscreen_icon)
        }
    }

    private fun setVisibility() {
        runnable = Runnable {
            if (binding.viewExoPlayer.isControllerVisible) {
                changeVisibility(View.VISIBLE)
            } else {
                changeVisibility(View.INVISIBLE)
            }
            Handler(Looper.getMainLooper()).postDelayed(runnable, 300)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }

    private fun changeVisibility(visibility: Int) {
        binding.viewTopController.visibility = visibility
        binding.viewBottomController.visibility = visibility
        binding.playPauseBtn.visibility = visibility
        if (isLocked) {
            binding.lockBtn.visibility = View.VISIBLE
        } else {
            binding.lockBtn.visibility = visibility
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        videosPlayer.release()
    }
}