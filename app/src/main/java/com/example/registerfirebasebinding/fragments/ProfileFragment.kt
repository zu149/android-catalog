package com.example.registerfirebasebinding.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.clear
import coil.load
import com.example.registerfirebasebinding.*
import com.example.registerfirebasebinding.databinding.FragmentProfileBinding
import com.example.registerfirebasebinding.models.users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import okhttp3.internal.cache2.Relay.Companion.edit
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class ProfileFragment : Fragment() {

    private var fragmentBinding: FragmentProfileBinding? = null
    private val mBinding get() = fragmentBinding!!


    private var imageFileUri: Uri? = null
    lateinit var mStorage: StorageReference // для получения доступа к храниилищу

    private val args: ProfileFragmentArgs by navArgs() // аргуменеты для приема id пользователя из чат фрагмента


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)


    }

    fun getCurrntUserEmail(): String { // ПОЛУЧЕНИЕ emaila залогиненого пользователя
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUser != null) {
            currentUserId = currentUser.email.toString()
        }
        return currentUserId
    }

    fun getCurrntUserId(): String { // ПОЛУЧЕНИЕ ID ЮЗЕРА
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentProfileBinding.bind(view) // запрет изменения пофиля
        fragmentBinding = binding
        binding.name.isEnabled = false
        binding.email.isEnabled = false
        binding.phone.isEnabled = false
        binding.img.isEnabled = false
        binding.radioButtonM.isEnabled = false
        binding.radioButtonJ.isEnabled = false

        var userid = getCurrntUserId() // получение id
        val email = FirebaseAuth.getInstance().currentUser?.email.toString()
        val Newuser = users(userid, email)



        if (email == "admin@email.ru") { // админ
//            userid = args.uidUser
            binding.btnVihod.isVisible = true
        }
// это пользователь
            FirebaseFirestore.getInstance().collection("users") // чтение объекта (юзера)
                .document(userid)
                .get()
                .addOnSuccessListener { document ->
                    val user = document.toObject(users::class.java) // заполнение
                    if (user != null) {
                        binding.nameText.text = user.name.toEditable()
                        binding.phoneText.text = user.phone.toEditable()
                        binding.emailText.text = user.email.toEditable()

                        if (user.pay_month > 0 || user.pay_year > 0) {
                            var getDatePodpiska = user.date
                            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                            val time = dateFormat.format(getDatePodpiska.toDate())
                            getDatePodpiska.toString()
                            binding.time.text = time.toString()
                        }
                        if (user.pol == "мужской") {
                            fragmentBinding!!.radioButtonM.isChecked = true
                        } else {
                            fragmentBinding!!.radioButtonJ.isChecked = true
                        }

                        GlideLoader(requireActivity()).loadUserProfile(
                            user.img,
                            binding.profilePhoto
                        ) // загрузка фотки
                    } else {
                        FirebaseFirestore.getInstance().collection("users") // создание нового документа в коллекции(пользователя)
                            .document(Newuser!!.id)
                            .set(Newuser!!, SetOptions.merge()) // объединение данных в колекции
                            .addOnSuccessListener {
//                                        Main.navController.navigate(R.id.profile_to_list)
                            }
                    }

                    if (user != null) {
                        val email = user!!.email // это для админа
//                        FirebaseAuth.getInstance().currentUser?.email.toString() // путь к емаилу в базе
                        binding.emailText.text =
                            email.toEditable() // подтягивание емаила из регистрации в профиль
                    }
                }

            mBinding.btnVihod.setOnClickListener {
                val vihod = AlertDialog.Builder(requireActivity(),R.style.MyDialogTheme) // всплывающие окно
                vihod.setMessage("вы уверены что хотите выйти?")
                vihod.setIcon(android.R.drawable.alert_light_frame)
                vihod.setPositiveButton("да") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    FirebaseAuth.getInstance().signOut()//выход
                    val intent = Intent(requireActivity(), MainActivity2::class.java)
                    startActivity(intent)

                }
                vihod.setNegativeButton("нет") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                val dialog = vihod.create()
                dialog.setCancelable(false) //одно из двух
                dialog.show()
            }





        if (getCurrntUserEmail() == "admin@email.ru" && userid != getCurrntUserId()) { // дается доступ админом



            mBinding.podpiska1.isVisible = true
            mBinding.podpiska2.isVisible = true
            mBinding.podpiska1.setOnClickListener {
                FirebaseFirestore.getInstance().collection("users")
                    .document(userid)
                    .update("pay_month", FieldValue.increment(1)) //без хэшмпэпа потому что числовое значение
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireActivity(),
                            "доступ дан",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                podpiska(userid)
            }
            mBinding.podpiska2.setOnClickListener {
                FirebaseFirestore.getInstance().collection("users")
                    .document(userid)
                    .update("pay_year", FieldValue.increment(1))
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireActivity(),
                            "доступ дан",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                podpiska(userid)
            }

            // дата подсписки

            FirebaseFirestore.getInstance().collection("users")
                .document()


        }

        }


     fun podpiska(userid: String) { // дата начала подписки
        val userHashMap: HashMap<String, Any> = HashMap()
        val DayCalendar = Calendar.getInstance()
        val data = DayCalendar.time
        userHashMap["date"] = data
        FirebaseFirestore.getInstance().collection("users")
            .document(userid)
            .update(userHashMap)
    }




    fun String.toEditable(): Editable =
        Editable.Factory.getInstance().newEditable(this) // помещаем емаил в едит текст


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentBinding = FragmentProfileBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { //кнопки меню


        when (item.itemId) { // переход
//            R.id.back_item -> {
//                Main.navController.navigate(R.id.profile_to_list)
//                return true
//
//            }
            R.id.edit_item -> {
                edit()

                return true

            }
        }

        return super.onOptionsItemSelected(item)
    }


    private fun upload() { // сохраенение фотки в базу данных
        var bmp: Bitmap? = null // bitmap объект куда сохраняется объект и тут происходит сжатие
        try {
            bmp = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageFileUri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val zhatie = ByteArrayOutputStream()
        bmp!!.compress(Bitmap.CompressFormat.JPEG, 30, zhatie)
        var b = byteArrayOf(0x2E, 0x38)//конвертация масивов в байт
        b = zhatie.toByteArray()

        mStorage = FirebaseStorage.getInstance()
            .getReference("FotoProfile/${getCurrntUserId()}") // создается папка с подпапками с айдишниками пользователей
        var mReference = mStorage.child("profile") // перезапись файла

        try {
            mReference.putBytes(b).addOnSuccessListener { // загрузка файла
//                taskSnapshot: UploadTask.TaskSnapshot? -> // скачиваем ссылку файла загруженного в базу данных
//                var url = taskSnapshot!!.getMetadata()!!.getReference()!!.getDownloadUrl().toString() // сохраение обсолютной ссылки в фаербейс которая потом добавляется в коллекцию с помощью HashMap
                it.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { url ->
                        var userHashMap: HashMap<String, Any> = HashMap()

                        userHashMap["img"] = url //ключ значение
                        FirebaseFirestore.getInstance().collection("users")
                            .document(getCurrntUserId())
                            .update(userHashMap)
                    }
            }
            } catch (e: IOException) {

            }

        }



        fun edit() { // изменение прфиля
            mBinding.name.isEnabled = true
            mBinding.phone.isEnabled = true
            mBinding.img.isEnabled = true
            mBinding.radioButtonM.isEnabled = true
            mBinding.radioButtonJ.isEnabled = true
            mBinding.hidenButton.isVisible = true



            mBinding.profilePhoto.setOnClickListener { // отображение фотки в imageView
//            GlideActivity(requireActivity()).phoneStorageImg(requireActivity(), mBinding.profilePhoto)
                pickImage.launch(constants.TYPE_IMAGE)
            }

            mBinding.hidenButton.setOnClickListener { // добавление объекта (юзера) в базу данных
                mBinding.hidenButton.visibility = View.GONE

                var userHashMap: HashMap<String, Any> = HashMap()
                userHashMap["name"] = mBinding.nameText.text.toString() //ключ значение
                userHashMap["phone"] = mBinding.phoneText.text.toString()
                if (mBinding.radioButtonM.isChecked) {
                    userHashMap["pol"] = "мужской"
                } else {
                    userHashMap["pol"] = "женский"
                }

                FirebaseFirestore.getInstance().collection("users")
                    .document(getCurrntUserId())
                    .update(userHashMap)
                mBinding.name.isEnabled = false
                mBinding.phone.isEnabled = false
                mBinding.radioButtonM.isEnabled = false
               mBinding.radioButtonJ.isEnabled = false
                if (imageFileUri != null) {
                    upload()
                }


            }
        }


        override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
            inflater.inflate(R.menu.profile_menu, menu)
            super.onCreateOptionsMenu(menu, inflater)
        }


        private val pickImage = // добавление в хранилище фотки
            registerForActivityResult(ActivityResultContracts.GetContent()) { contentUri ->
                with(mBinding.profilePhoto) { //загрузка в имедж вью
                    clear()
                    load(contentUri) {
                        imageFileUri = contentUri // получаем ссылку на изоюражение
                        listener(

                        )
                    }
                }
            }


    }



