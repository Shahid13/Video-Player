package com.example.myvideoplayer

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myvideoplayer.adapter.FoldersAdapter
import com.example.myvideoplayer.databinding.FragmentFoldersBinding


class FoldersFragment : Fragment() {
    private var _bindnig: FragmentFoldersBinding? = null
    private val binding get() = _bindnig!!

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ):
            View? {
         _bindnig = FragmentFoldersBinding.inflate(inflater, container, false)
        binding.recyclerViewFolder.setHasFixedSize(true)
        binding.recyclerViewFolder.setItemViewCacheSize(10)
        binding.recyclerViewFolder.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFolder.adapter =
            FoldersAdapter(requireContext(), MainActivity.folderList)
        binding.tvTotalFolder.text = "Total Folders: ${MainActivity.folderList.size}"
        return binding.root
    }

}