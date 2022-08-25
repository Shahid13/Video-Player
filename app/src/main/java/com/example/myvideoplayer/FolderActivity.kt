package com.example.myvideoplayer

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myvideoplayer.adapter.VideoAdapter
import com.example.myvideoplayer.databinding.ActivityFolderBinding
import com.example.myvideoplayer.model.Video
import java.io.File
import java.lang.Exception

class FolderActivity : AppCompatActivity() {
    private var _bindnig: ActivityFolderBinding? = null
    private val binding get() = _bindnig!!

    companion object {
        lateinit var currentFolderVideo: ArrayList<Video>
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         _bindnig = ActivityFolderBinding.inflate(layoutInflater)
        setTheme(R.style.coolPinkNav)
        setContentView(binding.root)
        val position = intent.getIntExtra("position", 0)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = MainActivity.folderList[position].folderName
        currentFolderVideo = getAllVideos(MainActivity.folderList[position].folderName)
        binding.recyclerViewVideoFA.setHasFixedSize(true)
        binding.recyclerViewVideoFA.setItemViewCacheSize(10)
        binding.recyclerViewVideoFA.layoutManager = LinearLayoutManager(this@FolderActivity)
        binding.recyclerViewVideoFA.adapter =
            VideoAdapter(this@FolderActivity, currentFolderVideo, isFolder = true)
        binding.tvTotalVideosFA.text = "Total Videos: ${currentFolderVideo.size}"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    @SuppressLint("Range", "InLinedApi", "Recycle")
    private fun getAllVideos(folderId: String): ArrayList<Video> {
        val tempList = ArrayList<Video>()
        //slection use for videos sort by folder name
        val selection = MediaStore.Video.Media.BUCKET_DISPLAY_NAME + " like? "
        val projection = arrayOf(
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.BUCKET_ID)
        val cursorNew = this.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, selection, arrayOf(folderId),
            MediaStore.Video.Media.DATE_ADDED)
        if (cursorNew != null)
            if (cursorNew.moveToNext())
                do {
                    val titleC =
                        cursorNew.getString(cursorNew.getColumnIndex(MediaStore.Video.Media.TITLE))
                    val idC =
                        cursorNew.getString(cursorNew.getColumnIndex(MediaStore.Video.Media._ID))
                    val folderC =
                        cursorNew.getString(cursorNew.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
                    val sizeC =
                        cursorNew.getString(cursorNew.getColumnIndex(MediaStore.Video.Media.SIZE))
                    val pathC =
                        cursorNew.getString(cursorNew.getColumnIndex(MediaStore.Video.Media.DATA))
                    val durationC =
                        cursorNew.getString(cursorNew.getColumnIndex(MediaStore.Video.Media.DURATION))
                            .toLong()
                    try {
                        val file = File(pathC)
                        val artUriC = Uri.fromFile(file)
                        val video = Video(title = titleC,
                            id = idC,
                            folderName = folderC,
                            duration = durationC,
                            size = sizeC,
                            path = pathC,
                            artUri = artUriC)
                        if (file.exists()) {
                            tempList.add(video)
                        }
                    } catch (e: Exception) {
                        Log.e("ERROR", e.toString())
                    }
                } while (cursorNew.moveToNext())
        cursorNew?.close()
        // val cursor=this.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,projection,null,null)
        return tempList
    }

}