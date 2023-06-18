package com.example.tellso.ui.detail

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.tellso.R
import com.example.tellso.data.local.entity.Story
import com.example.tellso.databinding.ActivityDetailStoryBinding
import com.example.tellso.utils.setDateFormat

class DetailStoryActivity : AppCompatActivity() {
    private var _binding: ActivityDetailStoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportPostponeEnterTransition()

        val story = intent.getParcelableExtra<Story>(EXTRA_DETAIL)
        parseStoryData(story)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun parseStoryData(story: Story?) {
        if (story != null) {
            binding.apply {
                tvStoryUsername.text = story.name
                tvStoryDesc.text = story.description
                toolbar.title = getString(R.string.detail_toolbar_title, story.name)
                tvStoryDate.setDateFormat(story.createdAt)

                Glide
                    .with(this@DetailStoryActivity)
                    .load(story.photoUrl)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            supportStartPostponedEnterTransition()
                            return false
                        }
                    })
                    .placeholder(R.drawable.image_loading_placeholder)
                    .error(R.drawable.image_load_error)
                    .into(ivStoryImage)
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
    }
}