package com.example.soundbar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.nio.ByteBuffer;

import io.flutter.plugin.common.EventChannel;

public class SoundBarApi {

    SoundBarApi(Activity activity){
        this.activity = activity;
    }

    private static final String TAG = "SoundBarApi";
    private Activity activity;
    private PipedReader reader;
    private PipedWriter writer;
    private AudioRecord recorder;
    private EventChannel.EventSink eventSink;
    private boolean shouldStopCapturingAudio = false;

    private int SAMPLE_RATE = 44100;
    private int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private int BUFFER_SIZE = Math.max(SAMPLE_RATE / 2,AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            CHANNEL,
            AUDIO_FORMAT
    ) );
    private int FRAME_RATE = 512;

    void setEventSink(EventChannel.EventSink sink) {
        eventSink = sink;
    }

    private boolean doesAppHavePermission(){
        try{

                int audioRecordingPermissionPermissionStatus = ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
                return audioRecordingPermissionPermissionStatus == PackageManager.PERMISSION_GRANTED;



        }catch(Exception e){
            Log.d(TAG, e.toString());
            return false;
        }
    }

    private void handlePermissionTask(){
        if(!doesAppHavePermission()){

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        PackageManager.PERMISSION_GRANTED);


        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void start() {
        System.out.println("STARTING");

        if (shouldStopCapturingAudio) {
            resume();
            return;
        }
        reader = new PipedReader();
        writer = new PipedWriter();

        try {
            writer.connect(reader);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        captureAudio();
    }

    public void stop() {
        System.out.println("STOPPING");
        shouldStopCapturingAudio = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void resume() {
        if (!shouldStopCapturingAudio) return;
        shouldStopCapturingAudio = false;
        captureAudio();
    }

    public void close() {
        try {
            eventSink = null;
            shouldStopCapturingAudio = true;
           recorder.stop();
           recorder.release();
           reader.close();
           writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Capture data from microphone in a thread
    @SuppressLint({"MissingPermission", "SuspiciousIndentation"})
    private void captureAudio() {
        try {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) return;

            System.out.println("Setting up recorder");
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                    CHANNEL, AUDIO_FORMAT, BUFFER_SIZE);
//                 recorder = new AudioRecord.Builder()
//                         .setAudioSource(MediaRecorder.AudioSource.MIC)
//                        .setAudioFormat(new AudioFormat.Builder()
//                                .setEncoding(AUDIO_FORMAT)
//                                .setSampleRate(SAMPLE_RATE)
//                                .setChannelMask(CHANNEL)
//                                .build())
//                         .setBufferSizeInBytes(BUFFER_SIZE)
//                        .build();

            recorder.startRecording();

//            new Handler(Looper.getMainLooper()).post(() -> {
//                int i;
//                while(!shouldStopCapturingAudio){
//                    try {
//                        if ((i = reader.read()) != -1)  eventSink.success(i);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            });



//            new Handler(Looper.getMainLooper()).post(() -> {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    System.out.println("RUNNING IN THREAD");
//                    int byteSize = AudioRecord.getMinBufferSize(44100,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_8BIT )*2;
//                    byte[] data = new byte[byteSize];
////                    ByteBuffer buffer = ByteBuffer.allocateDirect(byteSize);
//                    while (!shouldStopCapturingAudio) {
//                        int numBytesRead;
//                        numBytesRead = recorder.read(data, 0, byteSize);
//                        System.out.println("NUMBER OF BYTES READ -> "+ numBytesRead);
//                        if (numBytesRead >= 0) {
////                            for (byte b : data) {
////                                System.out.println(b);
////                                }
////                            shouldStopCapturingAudio = true;
//                            byte max = max(data);
//                            eventSink.success(max);
//                            System.out.println("PEAK -> " + max);
//                        }
//                    }
//                }
//            });



            new CaptureThread().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Inner class to capture data from microphone
    // and find the peak frequency for each chunk.
    class CaptureThread extends Thread {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void run() {
            Process.setThreadPriority(
                    Process.THREAD_PRIORITY_URGENT_AUDIO
            );
            try {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    System.out.println("RUNNING IN THREAD");
                    final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
//                    short[] data = new short[FRAME_RATE];
                    while (!shouldStopCapturingAudio) {
                        int numBytesRead;
                        numBytesRead = recorder.read(buffer, BUFFER_SIZE);
//                        numBytesRead = recorder.read(data, 0, data.length);
                        System.out.println("NUMBER OF BYTES READ -> "+ numBytesRead);
                        if (numBytesRead > 0) {
//                                                        for (byte b : data) {
//                                System.out.println(b);
//                                }
//                            shouldStopCapturingAudio = true;
                            byte max = max(buffer.array());
                            writer.write(max);
                            System.out.println("PEAK -> " + max);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

   private byte max(byte[] arr) {
       byte max = -127;

        for (byte b : arr) {
            if (b > max) {
                max = b;
            }
        }

        return max;
    }
}
