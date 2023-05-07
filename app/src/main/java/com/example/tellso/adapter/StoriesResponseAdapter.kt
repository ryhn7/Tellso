package com.example.tellso.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tellso.data.remote.response.Story
import com.example.tellso.databinding.StoryItemBinding
import com.example.tellso.ui.detail.DetailStoryActivity
import com.example.tellso.ui.detail.DetailStoryActivity.Companion.EXTRA_DETAIL
import com.example.tellso.utils.setDateFormat
import com.example.tellso.utils.setImageFromUrl
import dagger.hilt.android.internal.managers.FragmentComponentManager

class StoriesResponseAdapter :
    ListAdapter<Story, StoriesResponseAdapter.ViewHolder>(StoryDiffCallback) {

    class ViewHolder(private val binding: StoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, story: Story) {
            binding.apply {
                tvStoryUsername.text = story.name
                tvStoryDesc.text = story.description
                tvStoryDate.text = story.createdAt
                ivStoryImage.setImageFromUrl(context, story.photoUrl)
                tvStoryDate.setDateFormat(story.createdAt)

                root.setOnClickListener {
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            FragmentComponentManager.findActivity(it.context) as Activity,
                            Pair(ivStoryImage, "story_image"),
                            Pair(tvStoryUsername, "name"),
                            Pair(tvStoryDate, "date"),
                            Pair(tvStoryDesc, "description")
                        )

                    Intent(context, DetailStoryActivity::class.java).also { intent ->
                        intent.putExtra(EXTRA_DETAIL, story)
                        context.startActivity(intent, optionsCompat.toBundle())
                    }
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(holder.itemView.context, story)
    }


    companion object {
        private val StoryDiffCallback = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }
        }
    }
}