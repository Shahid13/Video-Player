package com.example.myvideoplayer.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myvideoplayer.FolderActivity
import com.example.myvideoplayer.databinding.FoldersViewBinding
import com.example.myvideoplayer.model.Folder

class FoldersAdapter(private val context: Context, private var folderList: ArrayList<Folder>) :
    RecyclerView.Adapter<FoldersAdapter.MyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = FoldersViewBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyHolder(view)
    }
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.folderName.text = folderList[position].folderName
        holder.rootItem.setOnClickListener() {
            val intent = Intent(context, FolderActivity::class.java)
            intent.putExtra("position", position)
            ContextCompat.startActivity(context, intent, null)
        }
    }
    override fun getItemCount(): Int {
        return folderList.size
    }
    class MyHolder(foldersViewBinding: FoldersViewBinding) :
        RecyclerView.ViewHolder(foldersViewBinding.root) {
        val folderName = foldersViewBinding.folderNameFV
        val rootItem = foldersViewBinding.root
    }
}