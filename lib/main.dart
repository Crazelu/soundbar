import 'package:flutter/material.dart';
import 'package:soundbar/sound_bar_api.dart';
import 'package:soundbar/sound_bar_widget.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const Demo(),
    );
  }
}

class Demo extends StatefulWidget {
  const Demo({Key? key}) : super(key: key);

  @override
  State<Demo> createState() => _DemoState();
}

class _DemoState extends State<Demo> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: const [
              TextButton(
                onPressed: SoundBarApi.start,
                child: Text("Start"),
              ),
              TextButton(
                onPressed: SoundBarApi.stop,
                child: Text("Stop"),
              ),
            ],
          ),
          StreamBuilder(
              stream: SoundBarApi.frequencyStream,
              builder: (context, snapshot) {
                int level = 0;
                if ((snapshot.data as int?) != null) {
                  level = ((snapshot.data as int) / 12.7).floor();
                  if (level < 0) {
                    level == 0;
                  }
                }
                print("LEVEL -> $level");
                return const SizedBox();
                return SoundBarWidget(peakFrequency: level);
              }),
        ],
      ),
    );
  }
}
