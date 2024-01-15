package com.example.registerfirebasebinding

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import com.example.registerfirebasebinding.databinding.FragmentProfileBinding
import com.example.registerfirebasebinding.fragments.ProfileFragment
import com.google.common.io.Files.getFileExtension
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class GlideActivity(val context: Context) : AppCompatActivity() {

    val PICK_IMAGE_REQUEST_CODE = 1
    val USER_PROFILE_IMAGE: String = "user_profile_image"
    var UserPhoto: ImageView? = null

    private var imageFileUri: Uri? = null
    private var userProfileImageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glide)

    }


    fun phoneStorageImg (activity: Activity, prifilePhoto: ImageView) { // заход в файлы телефона
        if (prifilePhoto != null) {
            UserPhoto = prifilePhoto
        }
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // полуаем сыылку на фото в памяти телефона
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST_CODE) {
            if (data!!.data != null && UserPhoto != null ) {
                try {
                    imageFileUri = data.data
                    GlideLoader(this).loadUserProfile(
                        imageFileUri!!,
                        UserPhoto!!

                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(
                        this,
                        "загрузка не удалась",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun uploadImageToCloudStorage () { //загрузка фото в базу данных
        val imgStore : StorageReference = FirebaseStorage.getInstance().reference.child(
            USER_PROFILE_IMAGE
                    + System.currentTimeMillis() + "."
                    + GlideLoader(this).getFileExtension(this, imageFileUri)
        )
        imgStore.putFile(imageFileUri!!)
            .addOnSuccessListener { it ->
                
                var url =  it!!.getMetadata()!!.getReference()!!.getDownloadUrl().toString()    // скачиваем ссылку фотки
//                it.metadata!!.reference!!.downloadUrl // скачиваем ссылку фотки

                        var userHashMap: HashMap<String, Any> = HashMap()
                        userHashMap["img"] = url // клуюч, значение
                        FirebaseFirestore.getInstance().collection("users")
                            .document(ProfileFragment().getCurrntUserId()) //получаем айди к документу(каждый пользовотель)
                            .update(userHashMap)
                    }
            }

}
