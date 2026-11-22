package com.kamlesh.myvideoplayer

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class MainActivity : Activity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var urlInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(12, 12, 12, 12)
            setBackgroundColor(0xFF101010.toInt())
        }

        urlInput = EditText(this).apply {
            hint = "Enter video URL"
            setSingleLine(true)
            setTextColor(0xFFFFFFFF.toInt())
            setHintTextColor(0xFFAAAAAA.toInt())
        }

        val urlButton = Button(this).apply {
            text = "Play URL"
            setOnClickListener {
                val url = urlInput.text.toString().trim()
                if (url.startsWith("http://") ||
                    url.startsWith("https://")) {
                    playVideo(Uri.parse(url))
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Please enter a valid HTTP or HTTPS URL",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        playerView = PlayerView(this).apply {
            useController = true
        }

        val selectButton = Button(this).apply {
            text = "Select Video"
            setOnClickListener { selectVideo() }
        }

        val playButton = Button(this).apply {
            text = "Play / Pause"
            setOnClickListener {
                player?.let {
                    if (it.isPlaying) it.pause() else it.play()
                }
            }
        }

        val backButton = Button(this).apply {
            text = "⏪ 10 Sec"
            setOnClickListener {
                player?.seekBack()
            }
        }

        val forwardButton = Button(this).apply {
            text = "10 Sec ⏩"
            setOnClickListener {
                player?.seekForward()
            }
        }

        root.addView(urlInput)
        root.addView(
            urlButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(
            playerView,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        root.addView(selectButton)

        val controls = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        controls.addView(backButton,
            LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

        controls.addView(playButton,
            LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

        controls.addView(forwardButton,
            LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

        root.addView(controls)

        setContentView(root)

        player = ExoPlayer.Builder(this).build()
        playerView.player = player
    }

    private fun selectVideo() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "video/*"
        }
        startActivityForResult(intent, 100)
    }

    @Deprecated("Using legacy activity result API")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 &&
            resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                playVideo(uri)
            }
        }
    }

    private fun playVideo(uri: Uri) {
        player?.let {
            it.setMediaItem(MediaItem.fromUri(uri))
            it.prepare()
            it.play()
        }
    }

    override fun onDestroy() {
        playerView.player = null
        player?.release()
        player = null
        super.onDestroy()
    }
}
