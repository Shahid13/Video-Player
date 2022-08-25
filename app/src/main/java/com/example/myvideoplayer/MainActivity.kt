package com.example.myvideoplayer

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.myvideoplayer.databinding.ActivityMainBinding
import com.example.myvideoplayer.model.Folder
import com.example.myvideoplayer.model.Video
import java.io.File
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private var _bindnig: ActivityMainBinding? = null
    private val binding get() = _bindnig!!
    private lateinit var toggle: ActionBarDrawerToggle

    companion object {
        lateinit var videoListNew: ArrayList<Video>
        lateinit var folderList: ArrayList<Folder>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _bindnig = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.coolPinkNav)
        setContentView(_bindnig!!.root)
        //for Nav Drawer
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (requestRuntimePermission()) {
            folderList = ArrayList()
            videoListNew = getAllVideos()
            setFragment(VideosFragment())
        }
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.videoView -> setFragment(VideosFragment())
                R.id.folderView -> setFragment(FoldersFragment())
            }
            return@setOnItemSelectedListener true
        }

        binding.navDrawerView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.feedbackNav -> Toast.makeText(this,
                    it.itemId.toString() + "Feedback",
                    Toast.LENGTH_SHORT).show()
                //setFragment(VideosFragment())
                R.id.themesNav -> setFragment(FoldersFragment())

                R.id.sortOrderNav -> setFragment(VideosFragment())

                R.id.aboutNav -> setFragment(FoldersFragment())

                R.id.exitNav -> setFragment(FoldersFragment())
            }
            return@setNavigationItemSelectedListener true
        }
    }

    private fun setFragment(fragmentDefluat: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_fragment, fragmentDefluat)
        transaction.disallowAddToBackStack().commit()
    }

    //for permission request
    private fun requestRuntimePermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(this,
                WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), 12)
            return false
        }
        return true

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 12) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                folderList = ArrayList()
                videoListNew = getAllVideos()
                setFragment(VideosFragment())
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), 12)

            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }


    @SuppressLint("Range", "InLinedApi", "Recycle")
    private fun getAllVideos(): ArrayList<Video> {
        val tempList = ArrayList<Video>()
        val temFolderList = ArrayList<String>()
        val projection = arrayOf(MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.BUCKET_ID)
        val cursorNew = this.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
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
                    val folderIdC =
                        cursorNew.getString(cursorNew.getColumnIndex(MediaStore.Video.Media.BUCKET_ID))
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
                        //for adding folders to check that already exist
                        if (!temFolderList.contains(folderC)) {
                            temFolderList.add(folderC)
                            folderList.add(Folder(id = folderIdC, folderName = folderC))
                        }
                    } catch (e: Exception) {
                    }
                } while (cursorNew.moveToNext())
        cursorNew?.close()
        // val cursor=this.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,projection,null,null)
        return tempList
    }


}