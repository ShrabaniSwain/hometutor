package com.flyngener.tutorhub

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class GuardianBanner(private val banners: List<TopBannerItem>, val context: Context) : RecyclerView.Adapter<GuardianBanner.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sliderImageView: ImageView = itemView.findViewById(R.id.bannerImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_banner, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(banners[position].banner_image)
            .into(holder.sliderImageView)
        holder.sliderImageView.setOnClickListener {
            val bannerLink = banners[position].external_link
            if (bannerLink.isNotEmpty()) {
                var validUrl = bannerLink
                if (!bannerLink.startsWith("http://") && !bannerLink.startsWith("https://")) {
                    validUrl = "http://$bannerLink"
                }

                if (Patterns.WEB_URL.matcher(validUrl).matches()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(validUrl))
                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(
                            context,
                            "No application can handle this request.",
                            Toast.LENGTH_SHORT
                        ).show()
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(context, "Invalid URL", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return banners.size
    }
}
