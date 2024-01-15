package com.example.registerfirebasebinding.fragments

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.registerfirebasebinding.R
import com.example.registerfirebasebinding.constants
import com.example.registerfirebasebinding.databinding.FragmentDetailsBinding
import com.example.registerfirebasebinding.models.pay
import com.example.registerfirebasebinding.models.users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.musfickjamil.snackify.Snackify


class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private var idtext = ""
    private var myid: Int = 0


//    private lateinit var binding: FragmentDetailsBinding

    private val img1: ImageView?
        get() = view?.findViewById(R.id.imageView_one)

    private val title1: TextView?
        get() = view?.findViewById(R.id.one_title)

    private val info1: TextView?
        get() = view?.findViewById(R.id.one_info)

    private val rating1: RatingBar?
        get() = view?.findViewById(R.id.rating)

    private val ratingNumber1: TextView?
        get() = view?.findViewById(R.id.kolichestvo_oqenok)

    private var title: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // сюда пишутся все действия с компонентами
//        super.onViewCreated(view, savedInstanceState)
//
//        FirebaseFirestore.getInstance().collection("apps").document("one").get()
//            .addOnSuccessListener {
//                val data = it.toObject(Details::class.java)
//                title1!!.text = data?.title
//                info1!!.text = data?.info
//            }
//        binding.skachka!!.setOnClickListener {
//            val storegeApk = Firebase.storage.reference
//            storegeApk.child("apk/file.apk").downloadUrl.addOnSuccessListener {
//                val myFile = DownloadManager.Request(Uri.parse(it.toString()))
//                myFile.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE) // загрузка через файфай или моб интернет
//                myFile.setTitle("file")
//                myFile.setMimeType("application/vnd.android.package-archive")
//                myFile.setDescription("скачивание файла")
//                myFile.allowScanningByMediaScanner() // сканирование файла
//                myFile.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // вывод сообщение о завершение скачки
//                myFile.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "myFile") // добавление в загрузки
//                val manager = requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager // позволяет скачать фаил
//                manager.enqueue(myFile) // завершение скачивания
//            }
//        }
//    }

    fun getCurrntUserId(): String { // ПОЛУЧЕНИЕ ID ЮЗЕРА
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId
    }


    fun getDetail(id: String) {
        var ratingInt = 0
        val storegeImg = Firebase.storage.reference // путь к хранилищу

        FirebaseFirestore.getInstance().collection("apps").document(id).get() //чтение
            .addOnSuccessListener {
                val data = it.toObject(Details::class.java)
                title1!!.text = data?.title
                info1!!.text = data?.info
                ratingInt = data!!.kolichestvo_oqenok
                ratingNumber1!!.text = ratingInt.toString()
                rating1!!.rating = data?.rating!!
                title = data.title.toString()

            }
        storegeImg.child("img/${id}.jpg").downloadUrl.addOnSuccessListener { // скачка фото
            Glide.with(img1!!).load(it) // загрузка изображения в imageview
                .fitCenter() // маштабирование
                .into(img1!!) //загрузка фотки в imageview
        }

        binding.skachka!!.setOnClickListener {
            val storegeApk = Firebase.storage.reference
            storegeApk.child("apk/${id}.apk").downloadUrl.addOnSuccessListener {
                Snackify.info(requireActivity().findViewById(android.R.id.content),
                    "Началось скачивания приложения ${title}", Snackify.LENGTH_SHORT).show()

                val myFile = DownloadManager.Request(Uri.parse(it.toString()))
                myFile.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE) // загрузка через файфай или моб интернет
                myFile.setTitle("file")
                myFile.setMimeType("application/vnd.android.package-archive")
                myFile.setDescription("скачивание файла")
                myFile.allowScanningByMediaScanner() // сканирование файла
                myFile.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // вывод сообщение о завершение скачки
                myFile.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title) // добавление в загрузки
                val manager = requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager // позволяет скачать фаил
                manager.enqueue(myFile) // завершение скачивания
            }

            FirebaseFirestore.getInstance().collection(constants.USERS) // чтение юзера в бд
                .document(getCurrntUserId()) // получение uid пользователя
                .get()
                .addOnSuccessListener {
                    val data = it.toObject(users::class.java) // под it подразумевается джейсон объект конвертируемый
                    if (data!!.pay_month > 0 || data!!.pay_year > 0) {
                        binding.skachka.isVisible = true
                        binding.podpiska1.isVisible  = false
                        binding.podpiska2.isVisible  = false
                    } else {
                        binding.skachka.isVisible = false
                        Toast.makeText(
                            requireActivity(),
                            "оплатите подписку для скачивания",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

        }



        binding.podpiska1!!.setOnClickListener{
            FirebaseFirestore.getInstance().collection("pay")
                .document("1")
                .get()
                .addOnSuccessListener { task ->
                    val data = task.toObject(pay::class.java)
                    if (data!= null) {
                        val pay = data.month
                        val i = Intent(Intent.ACTION_VIEW) // открывается диалоговое окно с выбором браузера для перехода по ссылке для оплаты
                        i.data = Uri.parse(pay) // открытие ссылки
                        startActivity(i) // для перехода по ссылке
                    }
                }
        }
        binding.podpiska2!!.setOnClickListener{
            FirebaseFirestore.getInstance().collection("pay")
                .document("1")
                .get()
                .addOnSuccessListener { task ->
                    val data = task.toObject(pay::class.java)
                    if (data!= null) {
                        val pay = data.year
                        val i = Intent(Intent.ACTION_VIEW) // открывается диалоговое окно с выбором браузера для перехода по ссылке для оплаты
                        i.data = Uri.parse(pay) // открытие ссылки
                        startActivity(i) // для перехода по ссылке
                }
        }

    }
    }

    override fun onResume() { // запускается когда пользователь вернулся после оплаты
        super.onResume()
        FirebaseFirestore.getInstance().collection("users")
            .document(getCurrntUserId()) // получение uid пользователя
            .get()
            .addOnSuccessListener {
                val data = it.toObject(users::class.java) // под it подразумевается джейсон объект конвертируемый
                if (data != null) {
                if (data!!.pay_month > 0 || data!!.pay_year > 0) {
                    binding.skachka.isVisible = true
                    binding.podpiska1.isVisible = false
                    binding.podpiska2.isVisible = false
                    Toast.makeText(
                        requireActivity(),
                        "оплата прошла успешно",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                } else {
                    Toast.makeText(
                        requireActivity(),
                        "после оплаты подписки напишите админу",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    fun setDetail(id: Int) {
        var ratingInt = 0
        val storegeImg = Firebase.storage.reference // путь к хранилищу
            when (id) {
                R.id.conteiner1 -> {
                    idtext = "one"
                    getDetail(idtext)

                }
                R.id.conteiner2 -> {
                    idtext = "two"
                    getDetail(idtext)

                }
                R.id.conteiner3 -> {
                    idtext = "three"
                    getDetail(idtext)

                }
                R.id.conteiner4 -> {
                    idtext = "four"
                    getDetail(idtext)

                }
                R.id.conteiner5 -> {
                    idtext = "five"
                    getDetail(idtext)

                }
            }


        binding.oqenkaKlick.setOnClickListener { // добавление в базу данных рейтинга
//                        binding.oqenkaKlick.text="подтвердить" // зменение кнопки в лайф режиме

            Toast.makeText(
                requireActivity(),
                "оценка добавилась",
                Toast.LENGTH_SHORT
            ).show()

            FirebaseFirestore.getInstance().collection("apps")
                .document(idtext)
                .get()
                .addOnSuccessListener {
                    val data = it.toObject(Details::class.java)
                    var rating: Float = rating1!!.rating!!.toFloat() // новая оценка поставленная пользователем
                    var saveKolichOqenok = data?.kolichestvo_oqenok!!.toFloat() // количество оценок
                    var DBoqenka = data?.rating!!.toFloat() // оценка из базы данных
                    var result = 0.0F
                    if (saveKolichOqenok == 0.0F) {
                        result = rating
                    }

                    result = (DBoqenka * saveKolichOqenok + rating) / (saveKolichOqenok + 1)


                    var userHashMap: HashMap<String, Any> = HashMap() //hashMap  создается всегда при обавление чего-либо в БД
                    userHashMap["rating"] = result //
                    FirebaseFirestore.getInstance().collection("apps")
                        .document(idtext).update(userHashMap) // обновление в БД
                    rating1!!.rating = result //обновление рейтинг бара

                    FirebaseFirestore.getInstance().collection("apps")
                        .document(idtext).get() // обновление в приложении
                        .addOnSuccessListener {
                            val data = it.toObject(Details::class.java)
                            ratingNumber1!!.text = data?.kolichestvo_oqenok.toString()
                        }

                    FirebaseFirestore.getInstance().collection("apps")
                        .document(idtext).update("kolichestvo_oqenok", FieldValue.increment(1)) // увеличение на 1

                }
        }
    }





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }


}



