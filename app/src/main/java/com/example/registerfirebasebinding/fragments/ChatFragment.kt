package com.example.registerfirebasebinding.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.registerfirebasebinding.models.admin_chat
import com.example.registerfirebasebinding.databinding.FragmentChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private var id = "" // id от документа пользовтеля


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setHasOptionsMenu(true)
    }


//    override fun onOptionsItemSelected(item: MenuItem): Boolean { //кнопки меню
//        when (item.itemId) {
//            R.id.chat_back -> {
//                Main.navController.navigate(R.id.chat_to_list)
//                return true
//            }
//            R.id.profile_users -> {
//                if (id != "") {
////                    Main.navController.navigate(R.id.chat_to_profile)
//                    val action = ChatFragmentDirections.chatToProfile(id)
//                    findNavController().navigate(action) // отправка id ползьзователя для перехода в проф
//                    return true
//                }
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.chat, menu)
//        super.onCreateOptionsMenu(menu, inflater)
//    }


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.email.text = getCurrntUserEmail() // получение emaila в едит текст

            if (getCurrntUserEmail() == "admin@email.ru"){
                FirebaseFirestore.getInstance().collection("adminChat")
                    .get()
                    .addOnSuccessListener { // отображение сообщений без ответа
                        for (i in it.documents) {
                            val chat = i.toObject(admin_chat::class.java)
                            if (chat?.messageAdmin == "") {
                                id = chat.uid
                                binding.email.text = chat?.email
                                binding.chat.text ="вопрос пользовтеля: " + chat?.message
                                Toast.makeText(
                                    requireActivity(),
                                    "получено сообщение",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    }
            }

        FirebaseFirestore.getInstance().collection("adminChat") // чтение сообщения
            .document(getCurrntUserId()) // получение uid пользователя
            .get()
            .addOnSuccessListener {
                val data = it.toObject(admin_chat::class.java)
                if (data != null) {
                    binding.email.text = (data!!.email)
                    binding.chat.text = (data!!.message + "\n" + data!!.messageAdmin)

                }
            }.addOnFailureListener{
                binding.email.text = getCurrntUserEmail()
                binding.chat.text = "пока нет сообщений" //для пользователя
            }


                binding.chatBtn.setOnClickListener { //отправка сообщения админом

                    if (getCurrntUserEmail() == "admin@email.ru"&&id != "") {
                        val question = binding.chat.text.toString()
                        var userHashMap: HashMap<String, Any> = HashMap()
                        var Messageadmin: String = binding.message.text.toString()
                        if (Messageadmin != "") {
                            userHashMap["messageAdmin"] = Messageadmin
                            FirebaseFirestore.getInstance().collection("adminChat")
                                .document(id)
                                .update(userHashMap)
                                binding.chat.text = question + "\n" + Messageadmin //вопрос пользователя
                        }


                    }
                    else { //пользовтель
                        val message = admin_chat(
                            getCurrntUserId(),
                            getCurrntUserEmail(),
                            binding.message.text.toString(),
                            ""
                        ) // содержимое мессаджа

                        FirebaseFirestore.getInstance().collection("adminChat") // в бд
                            .document(getCurrntUserId())
                            .set(message, SetOptions.merge())
                            .addOnSuccessListener { // добавляем содержимое объека мессадж
                                Toast.makeText(
                                    requireActivity(),
                                    "сообщение отправилось",
                                    Toast.LENGTH_SHORT
                                ).show()
                                FirebaseFirestore.getInstance().collection("adminChat") // отправка сообщения из бд
                                    .document(getCurrntUserId())
                                    .get().addOnSuccessListener {
                                        val getMessage =
                                            it.toObject(admin_chat::class.java) // конвертация JSON в объект
                                        binding.chat.text = getMessage!!.message
                                    }
                            }
                    }

                }





    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root

    }
}

