import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';

class SoundBarApi {
  SoundBarApi._();

  static const MethodChannel _methodChannel =
      MethodChannel('dev.crazelu.soundbar');
  static const EventChannel _eventChannel =
      EventChannel('dev.crazelu.soundbar/event');

  static Stream<int> get frequencyStream async* {
    await for (int peakFrequency
        in _eventChannel.receiveBroadcastStream().map((freq) => freq)) {
      yield peakFrequency;
    }
  }

  static Future<void> start() async {
    if (!await hasPermission()) return;
    await _methodChannel.invokeMethod('start');
  }

  static Future<void> resume() async {
    await _methodChannel.invokeMethod('resume');
  }

  static Future<void> stop() async {
    await _methodChannel.invokeMethod('stop');
  }

  static Future<void> dispose() async {
    await _methodChannel.invokeMethod('dispose');
  }

  static Future<bool> hasPermission() async {
    final status = await Permission.microphone.status;
    if (status == PermissionStatus.denied) {
      openAppSettings();
    }

    return status == PermissionStatus.granted;
  }
}
