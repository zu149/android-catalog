package com.example.registerfirebasebinding

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.gms.common.internal.Constants
import com.google.common.io.Files.getFileExtension
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import androidx.appcompat.app.AppCompatActivity
import com.example.registerfirebasebinding.databinding.FragmentProfileBinding


class GlideLoader (val context: Context ) : AppCompatActivity() { // ссылка посылается сюда

    private var fragmentBinding: FragmentProfileBinding? = null
    private val binding get() = fragmentBinding!!


        fun loadUserProfile(image: Any, imageView: ImageView) {
            try {
                Glide // изображение загружается по ссылке в имэдж вью
                    .with(context)
                    .load(image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(imageView) //загрузка в imageView
            }catch (e : IOException) {
                e.printStackTrace() //отображение ошибки
            }
        }


    fun getFileExtension(activity: Activity, uri: Uri?) : String? { // для получение типа фотки
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(activity.contentResolver.getType(uri!!))
    }




}