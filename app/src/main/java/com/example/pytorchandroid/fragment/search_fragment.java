package com.example.pytorchandroid.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.pytorchandroid.HomeActivity;
import com.example.pytorchandroid.MainActivity;
import com.example.pytorchandroid.R;
import com.example.pytorchandroid.objectdetection.ObjectDetectionActivity;
import com.example.pytorchandroid.utility.Constants;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class search_fragment extends Fragment implements View.OnClickListener{


    private Context context;
    private Intent intent;
    private SpeechRecognizer mRecognizer;
    private  TextView textView;
    private int mDoubleClickFlag = 0;
    private long delay;
    private MediaPlayer mediaPlayer;
    private int inputSpeak;

    public search_fragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.search_explain);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.search, container, false);
        Button button = (Button) view.findViewById(R.id.search_button);
        button.setOnClickListener(this);
        if(Build.VERSION.SDK_INT >= 23){
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO},Constants.PERMISSION);
        }
        textView = (TextView) view.findViewById(R.id.search_text);

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getActivity().getPackageName()); // ????????? ???
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR"); // ?????? ??????

        inputSpeak = 1;

        return view;
    }
    private int matchObject(String type){

        List<String> txtList = new ArrayList<>(Arrays.asList("beverage.txt", "noodle.txt", "snack.txt"));


        for(String filename :txtList) {

            try{
                BufferedReader br = new BufferedReader(new InputStreamReader(getActivity().getAssets().open(filename)));
                String line;
                while ((line = br.readLine()) != null) {

                    if(line.contains(type.replace(" ",""))){
                        return 1;
                    }
                }
            } catch (IOException e) {
                Log.e("Object Detection", "Error reading assets", e);
            }
        }
        return 0;
    }


    @Override
    public void onClick(View v) {
        mDoubleClickFlag++;
        Handler handler = new Handler();
        Runnable clickRunnable = new Runnable() {
            @Override
            public void run() {
                if (mDoubleClickFlag >= 2) {
                    if (mediaPlayer.isPlaying()){
                        mediaPlayer.stop();
                        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.search_explain);
                    }

                    mRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity()); // ??? SpeechRecognizer ??? ????????? ????????? ?????????
                    mRecognizer.setRecognitionListener(listener); // ????????? ??????
                    mRecognizer.startListening(intent); // ?????? ??????
                }
                else {
                    if(System.currentTimeMillis() <= delay){
                        if(matchObject(textView.getText().toString())==1){
                            ((HomeActivity)context).startTextToString(textView.getText().toString() + "????????? ???????????????!");
                            Intent intent = new Intent(getActivity(), ObjectDetectionActivity.class);
                            intent.putExtra("modelType", textView.getText().toString());
                            startActivity(intent);
                        }
                        else{
                            HomeActivity.textToSpeech.speak("???????????? ?????? ???????????????. ??????????????? ??????????????????.", TextToSpeech.QUEUE_ADD, null);
                            textView.setText("????????????");
                        }
                    }
                    else{
                        if (mediaPlayer == null){
                            mediaPlayer = MediaPlayer.create(getActivity(), R.raw.search_explain);
                        }
                        mediaPlayer.start();
                    }
                }
                mDoubleClickFlag = 0;
            }
        };
        if( mDoubleClickFlag == 1 ) {
            handler.postDelayed( clickRunnable, Constants.CLICK_DELAY );
        }
    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            // ????????? ????????? ??????????????? ??????
            delay = 0;
            Toast.makeText(getActivity().getApplicationContext(),"???????????? ??????",Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onBeginningOfSpeech() {
            // ????????? ???????????? ??? ??????
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            // ???????????? ????????? ????????? ?????????
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            // ?????? ???????????? ????????? ??? ????????? buffer??? ??????
        }

        @Override
        public void onEndOfSpeech() {
            // ???????????? ???????????? ??????
        }

        @Override
        public void onError(int error) {
            // ???????????? ?????? ?????? ????????? ???????????? ??? ??????
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "????????? ??????";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "??????????????? ??????";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "????????? ??????";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "???????????? ??????";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "????????? ????????????";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "?????? ??? ??????";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER ??? ??????";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "????????? ?????????";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "????????? ????????????";
                    break;
                default:
                    message = "??? ??? ?????? ?????????";
                    break;
            }

            Toast.makeText(getActivity().getApplicationContext(), "?????? ?????? : " + message,Toast.LENGTH_SHORT).show();
            ((HomeActivity)context).startTextToString("?????? ?????? : " + message);
        }

        @Override
        public void onResults(Bundle results) {
            // ?????? ????????? ???????????? ??????
            // ?????? ?????? ArrayList??? ????????? ?????? textView??? ????????? ?????????
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for(int i = 0; i < matches.size() ; i++){
                textView.setText(matches.get(i));
            }



            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((HomeActivity)context).startTextToStringAdd("???????????? ?????????");
                    ((HomeActivity)context).startTextToStringAdd(textView.getText().toString());
                    ((HomeActivity)context).startTextToStringAdd("???????????? 3????????? ????????? ????????? ???????????????");
                }
            },500);
            delay = System.currentTimeMillis()+9000;
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            // ?????? ?????? ????????? ????????? ??? ?????? ??? ??????
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            // ?????? ???????????? ???????????? ?????? ??????
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity)context).startTextToString("???????????? ?????????.");


        if(mRecognizer!=null){
            mRecognizer.destroy();
            mRecognizer.cancel();
            mRecognizer=null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mediaPlayer.stop();
        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.search_explain);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer !=null){
            mediaPlayer.release();
            mediaPlayer =null;
        }
    }

}
