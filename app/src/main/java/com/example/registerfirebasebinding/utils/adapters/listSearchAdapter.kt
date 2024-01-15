package com.example.registerfirebasebinding.utils.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.registerfirebasebinding.GlideLoader
import com.example.registerfirebasebinding.R
import com.example.registerfirebasebinding.databinding.ActivitySearchBinding
import com.example.registerfirebasebinding.models.InfoApps

class ListSearchAdapter(val context: Activity, val list: ArrayList<InfoApps>) : RecyclerView.Adapter<ListSearchAdapter.ViewHolder>() {

    class ViewHolder( val binding: ActivitySearchBinding) : RecyclerView.ViewHolder(binding.root) // привязали акктивити xml

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_search, parent, false)
        return ViewHolder(ActivitySearchBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) { //ссылаемся на элементы из layout
        val model: InfoApps = list[position]
        holder.binding.titleOne.text = model.title
        GlideLoader(context).loadUserProfile(model.image, holder.binding.img)
    }

    override fun getItemCount(): Int { //подчсет количесвта элементов списка
        return list.size
    }


}
