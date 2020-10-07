//package com.example.cashlessfuel
//
//import android.Manifest
//import android.app.Activity
//import android.content.Intent
//import android.content.Intent.ACTION_CALL
//import android.content.pm.PackageManager
//import android.net.Uri
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import co.intentservice.chatui.ChatView
//import co.intentservice.chatui.ChatView.OnSentMessageListener
//import co.intentservice.chatui.ChatView.TypingListener
//import co.intentservice.chatui.models.ChatMessage
//import com.example.cashlessfuel.chat.RequestTask
//import com.google.api.gax.core.FixedCredentialsProvider
//import com.google.auth.oauth2.GoogleCredentials
//import com.google.auth.oauth2.ServiceAccountCredentials
//import com.google.cloud.dialogflow.v2.*
//import java.io.InputStream
//
//
//class ChatActivity : AppCompatActivity() {
//
//    private val TAG = MainActivity::class.java.simpleName
//    private val USER = 10001
//    private val BOT = 10002
//    private val uuid: String = java.util.UUID.randomUUID().toString()
//    private var sessionsClient: SessionsClient? = null
//    private var session: SessionName? = null
//    private var chat_view : ChatView? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_chat)
//
//        initV2Chatbot();
//
//        chat_view = findViewById<View>(R.id.chat_view) as ChatView
//        chat_view?.addMessage(
//            ChatMessage(
//                "Hi, How can i help you?",
//                System.currentTimeMillis(),
//                ChatMessage.Type.RECEIVED
//            )
//        )
//
////        chat_view?.addMessage(
////            ChatMessage(
////                "A message with a sender name",
////                System.currentTimeMillis(), ChatMessage.Type.RECEIVED, "Cashless Fuel"
////            )
////        )
//
//        chat_view?.setOnSentMessageListener { true }
//        chat_view?.setOnSentMessageListener(OnSentMessageListener {
//            sendMessage(it.message)
//            return@OnSentMessageListener true
//        })
//
//        chat_view?.setTypingListener(object : TypingListener {
//            override fun userStartedTyping() {}
//            override fun userStoppedTyping() {}
//        })
//
//
//    }
//    private fun initV2Chatbot() {
//        try {
//            val stream: InputStream = resources.openRawResource(R.raw.test_agent_credentials)
//            val credentials = GoogleCredentials.fromStream(stream)
//            val projectId = (credentials as ServiceAccountCredentials).projectId
//            val settingsBuilder: SessionsSettings.Builder = SessionsSettings.newBuilder()
//            val sessionsSettings: SessionsSettings =
//                settingsBuilder.setCredentialsProvider(FixedCredentialsProvider.create(credentials))
//                    .build()
//            sessionsClient = SessionsClient.create(sessionsSettings)
//            session = SessionName.of(projectId, uuid)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//    private fun sendMessage(msg: String) {
//        //val msg: String = chat_view?.inputEditText.toString()
//        if (msg.trim { it <= ' ' }.isEmpty()) {
//            Toast.makeText(this@ChatActivity, "Please enter your query!", Toast.LENGTH_LONG).show()
//        } else {
//            //showTextView(msg, USER)
//            chat_view?.inputEditText?.setText("")
//            // Android client for older V1 --- recommend not to use this
////            aiRequest.setQuery(msg);
////            RequestTask requestTask = new RequestTask(MainActivity.this, aiDataService, customAIServiceContext);
////            requestTask.execute(aiRequest);
//
//            // Java V2
//            val queryInput: QueryInput = QueryInput.newBuilder()
//                .setText(TextInput.newBuilder().setText(msg).setLanguageCode("en-US")).build()
//                RequestTask(this@ChatActivity, session, sessionsClient, queryInput).execute()
//        }
//    }
//    fun callbackV2(response: DetectIntentResponse?) {
//        if (response != null) {
//            // process aiResponse here
//            val botReply: String = response.getQueryResult().getFulfillmentText()
//            Log.d(TAG, "V2 Bot Reply: $botReply")
//            showTextView(botReply, BOT)
//        } else {
//            Log.d(TAG, "Bot Reply: Null")
//            showTextView("There was some communication issue. Please Try again!", BOT)
//        }
//    }
//    private fun showTextView(message: String, type: Int) {
//
//        chat_view?.addMessage(
//            ChatMessage(
//                "$message",
//                System.currentTimeMillis(), ChatMessage.Type.RECEIVED, "Cashless Fuel"
//            )
//        )
//        if(message.contains("contact us at ")){
//            val callIntent : android.content.Intent = android.content.Intent(Intent.ACTION_CALL)
//            callIntent.setData(Uri.parse("tel:1800000000"))
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                if (ActivityCompat.shouldShowRequestPermissionRationale(this as Activity,
//                        Manifest.permission.CALL_PHONE)) {
//                } else {
//                    ActivityCompat.requestPermissions(this,
//                        arrayOf(Manifest.permission.CALL_PHONE),
//                        1)
//                }
//            }
//            startActivity(callIntent)
//        }
//    }
//
//
//
//
//
//
//}