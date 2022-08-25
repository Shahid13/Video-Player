package com.example.myvideoplayer.adapter

import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myvideoplayer.PlayerActivity
import com.example.myvideoplayer.R
import com.example.myvideoplayer.databinding.VideoViewBinding
import com.example.myvideoplayer.model.Video

class VideoAdapter(private val context: Context, private var videoList: ArrayList<Video>, private val isFolder:Boolean=false) :RecyclerView.Adapter<VideoAdapter.MyHolder> (){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val viewItem=VideoViewBinding.inflate(LayoutInflater.from(context),parent,false)

        return MyHolder(viewItem)
    }
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.title.text=videoList[position].title
        holder.folder.text=videoList[position].folderName
        holder.duration.text=DateUtils.formatElapsedTime(videoList[position].duration/1000)
        Glide.with(context).asBitmap().
        load(videoList[position].artUri)
            .apply(RequestOptions.placeholderOf(R.mipmap.ic_app_logo).centerCrop())
            .into(holder.imgVideo)

        holder.rootItem.setOnClickListener{

             when{
                 isFolder ->{
                     sendInten(pos = position, ref = "FolderActivity")
                 }
                 else ->{
                     sendInten(pos = position, ref = "AllVideos")
                 }
             }
        }
    }

    override fun getItemCount(): Int {
         return videoList.size
    }
    class MyHolder (viewBinding: VideoViewBinding):RecyclerView.ViewHolder(viewBinding.root) {
      val title=viewBinding.videoName
      val folder=viewBinding.folderName
      val duration=viewBinding.duration
        val imgVideo=viewBinding.videoImg
        val rootItem=viewBinding.root
    }
    private fun sendInten(pos:Int,ref:String){
     PlayerActivity.position =pos
     val intent=Intent(context, PlayerActivity::class.java)
     intent.putExtra("class",ref)
     ContextCompat.startActivity(context,intent,null)
 }

}