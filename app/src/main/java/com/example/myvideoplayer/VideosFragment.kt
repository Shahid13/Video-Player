package com.example.myvideoplayer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myvideoplayer.adapter.VideoAdapter
import com.example.myvideoplayer.databinding.FragmentVideosBinding
class VideosFragment : Fragment() {
    private var _binding: FragmentVideosBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
         _binding = FragmentVideosBinding.inflate(inflater, container, false)
        binding.recyclerViewVideos.setHasFixedSize(true)
        binding.recyclerViewVideos.setItemViewCacheSize(10)
        binding.recyclerViewVideos.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewVideos.adapter = VideoAdapter(requireContext(), MainActivity.videoListNew)
        binding.tvTotalVideos.text = "Total Videos: : ${MainActivity.videoListNew.size}"
        return binding.root
    }
}