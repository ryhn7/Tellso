package com.example.tellso.ui.create

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tellso.databinding.FragmentCreateStoryBinding

class CreateStoryFragment : Fragment() {

    private var _binding: FragmentCreateStoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val createStoryViewModel =
            ViewModelProvider(this)[CreateStoryViewModel::class.java]

        _binding = FragmentCreateStoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)


//        val textView: TextView = binding.textStory
//        createStoryViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}