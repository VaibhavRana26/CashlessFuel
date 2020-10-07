//package com.example.cashlessfuel.chat;
//
//import android.app.Activity;
//import android.os.AsyncTask;
//
//import com.example.cashlessfuel.ChatActivity;
//import com.google.cloud.dialogflow.v2.DetectIntentRequest;
//import com.google.cloud.dialogflow.v2.DetectIntentResponse;
//import com.google.cloud.dialogflow.v2.QueryInput;
//import com.google.cloud.dialogflow.v2.SessionName;
//import com.google.cloud.dialogflow.v2.SessionsClient;
//
////import com.google.cloud.dialogflow.v2beta1.DetectIntentRequest;
////import com.google.cloud.dialogflow.v2beta1.DetectIntentResponse;
////import com.google.cloud.dialogflow.v2beta1.QueryInput;
////import com.google.cloud.dialogflow.v2beta1.SessionName;
////import com.google.cloud.dialogflow.v2beta1.SessionsClient;
//
//
//public class RequestTask extends AsyncTask<Void, Void, DetectIntentResponse> {
//
//    Activity activity;
//    private SessionName session;
//    private SessionsClient sessionsClient;
//    private QueryInput queryInput;
//
//
//    public RequestTask(Activity activity, SessionName session, SessionsClient sessionsClient, QueryInput queryInput) {
//        this.activity = activity;
//        this.session = session;
//        this.sessionsClient = sessionsClient;
//        this.queryInput = queryInput;
//    }
//
//    @Override
//    protected DetectIntentResponse doInBackground(Void... voids) {
//        try{
//            DetectIntentRequest detectIntentRequest =
//                    DetectIntentRequest.newBuilder()
//                            .setSession(session.toString())
//                            .setQueryInput(queryInput)
//                            .build();
//            return sessionsClient.detectIntent(detectIntentRequest);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    @Override
//    protected void onPostExecute(DetectIntentResponse response) {
//        ((ChatActivity) activity).callbackV2(response);
//    }
//}