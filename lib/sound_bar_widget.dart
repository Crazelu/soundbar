import 'package:flutter/material.dart';

List<SoundLevel> _levels = [
  SoundLevel(frequency: 0, color: Colors.lime),
  SoundLevel(frequency: 1, color: Colors.yellow),
  SoundLevel(frequency: 2, color: Colors.greenAccent),
  SoundLevel(frequency: 3, color: Colors.lightBlue),
  SoundLevel(frequency: 4, color: Colors.amber),
  SoundLevel(frequency: 5, color: Colors.orange),
  SoundLevel(frequency: 6, color: Colors.pinkAccent),
  SoundLevel(frequency: 7, color: Colors.red),
  SoundLevel(frequency: 8, color: Colors.brown),
  SoundLevel(frequency: 9, color: Colors.blueGrey),
  SoundLevel(frequency: 10, color: Colors.teal),
];

class SoundBarWidget extends StatelessWidget {
  final int peakFrequency;
  final List<SoundLevel> levels;

  SoundBarWidget({
    Key? key,
    required this.peakFrequency,
  })  : levels = _levels.sublist(0, peakFrequency).reversed.toList(),
        super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisAlignment: MainAxisAlignment.end,
      children: [
        for (var level in levels)
          Container(
            height: 40,
            width: MediaQuery.of(context).size.width,
            color: level.color,
          )
      ],
    );
  }
}

class SoundLevel {
  final int frequency;
  final Color color;

  SoundLevel({
    required this.frequency,
    required this.color,
  });
}
