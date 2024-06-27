package com.example.soundbar;

import android.os.Build;

import androidx.annotation.NonNull;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {

    private static final String METHOD_CHANNEL = "dev.crazelu.soundbar";
    private static final String EVENT_CHANNEL = "dev.crazelu.soundbar/event";
    private SoundBarApi soundBar;
    private EventChannel eventChannel;
    private EventChannel.EventSink eventSink;
    private SoundBarStreamHandler streamHandler;

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
    super.configureFlutterEngine(flutterEngine);
    soundBar = new SoundBarApi(getActivity());
    streamHandler = new SoundBarStreamHandler();
    eventChannel = new EventChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), EVENT_CHANNEL);
    eventChannel.setStreamHandler(streamHandler);
      new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), METHOD_CHANNEL)
          .setMethodCallHandler(
            (call, result) -> {
             switch (call.method) {
                 case "start":
                     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                         soundBar.start();
                     }
                     result.success(true);
                     break;
                 case "resume":
                     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                         soundBar.resume();
                     }
                     result.success(true);
                     break;
                 case "stop":
                 soundBar.stop();
                 result.success(true);
                     break;
                 case "dispose":
                 soundBar.close();
                 result.success(true);
                     break;
             
                 default:
                 result.notImplemented();
                     break;
             }
            }
          );
    }

    @Override
    public void cleanUpFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        soundBar.close();
    }

    class SoundBarStreamHandler implements  EventChannel.StreamHandler{

        @Override
        public void onListen(Object arguments, EventChannel.EventSink sink) {
            eventSink = sink;
            soundBar.setEventSink(eventSink);
        }

        @Override
        public void onCancel(Object arguments) {
            eventChannel = null;
            eventSink = null;
        }
    }
}
