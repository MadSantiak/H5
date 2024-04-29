#include <Arduino.h>
//! Potentiometer 1, controls frequency (heart rate)
#define freqPin 4
//! Potentiometer 2, controls amplitude (systolic/diastolic pressure)
#define ampliPin 14
#define ledPin 2
#define greenLED 13
#define yellowLED 12 
#define buttonPin 15

hw_timer_t *My_timer = NULL;

const int maxArrays = 3;
int selectedArray = 0;
//! Contains 3 rhytms: Normal, Tachycardia, Fibrilation:
int arrBeats[maxArrays][40] = {
  {66, 66, 66, 66, 66, 72, 78, 82, 78, 72, 70, 66, 66, 66, 66, 66, 90, 132, 180, 255, 160, 132, 72, 0, 31, 66, 66, 66, 66, 66, 70, 82, 90, 86, 72, 66, 66, 66, 66, 66},
  {66, 66, 66, 90, 180, 255, 60, 66, 78, 60, 66, 66, 66, 90, 180, 255, 60, 66, 78, 60, 66, 66, 66, 90, 180, 255, 60, 66, 78, 60, 66, 66, 66, 90, 180, 255, 60, 66, 78, 60},
  {66, 88, 92, 76, 66, 60, 78, 92, 110, 92, 78, 66, 80, 98, 76, 60, 66, 82, 92, 66, 66, 88, 92, 76, 66, 60, 78, 92, 110, 92, 78, 66, 80, 98, 76, 60, 66, 82, 92, 66}
};

// Variables used to determine frequency, amplitude, and base information in regards to heartrate:
float bpm = 40;
float signalStr = 1;
float corrigatedStr = 1;
float baseline = 66;
int freq;
int amp;
bool flatline = false;

//! Bool used for debounce of button press.
volatile bool state = LOW;

// Prototyping of functions:
void IRAM_ATTR onTimer();
void heartBeat();
void switchBeat();
void flatlineCheck();
float floatMap(float x, float in_min, float in_max, float out_min, float out_max);

//! Set up relevant pins, timer interrupt, and event interrupt.
void setup() {
  Serial.begin(115200);

  pinMode(ledPin, OUTPUT);
  pinMode(greenLED, OUTPUT);
  pinMode(yellowLED, OUTPUT);
  pinMode(buttonPin, INPUT_PULLUP);

  My_timer = timerBegin(0, 80, true);
  timerAttachInterrupt(My_timer, &onTimer, true);
  timerAlarmWrite(My_timer, 1000000, true);
  timerAlarmEnable(My_timer); //Just Enable

  attachInterrupt(digitalPinToInterrupt(buttonPin), switchBeat, FALLING);
}

//! Reattach interrupt event if it occured (to prevent debounce), call heartBeat() continuously.
void loop() {
  
  if (state) {
    if (digitalRead(buttonPin)) {
      attachInterrupt(digitalPinToInterrupt(buttonPin), switchBeat, FALLING);
      state = false;
    }
  }
  delay(10); // Inserted delay to avoid collision no core resulting in panic reset.
  heartBeat();
}

void changeTimer() {
  
}

//! Timer interrupt that reads input (potentiometers) and translates these to useful values
void IRAM_ATTR onTimer(){
  digitalWrite(greenLED, !digitalRead(greenLED));
  // digitalWrite(yellowLED, !digitalRead(yellowLED));
  freq = analogRead(freqPin);
  bpm = 60000 / floatMap(freq, 0, 4095, 40, 240);
  amp = analogRead(ampliPin);
  signalStr = floatMap(amp, 0, 4095, 0, 1);
  if (signalStr < 0.1) signalStr = 0.1;

  int timer = bpm * 1000;
  timerAlarmWrite(My_timer, timer, true);
}

//! Primary function for mimicing heartbeat. Only uses Serial Plotter (due to lack of output for DAC).
void heartBeat() {
  //! Get the number of entries in the array:  
  int beats = sizeof(arrBeats[selectedArray])/sizeof(int);
  float interval = bpm/beats;
  //! Iterate across the array, printing the beat value for each.
  for (byte i = 0; i < beats; i++) 
  {       
          //! Normalize heartbeat
          float actualStr = signalStr * corrigatedStr;
          float baselineStr = baseline * actualStr;
          float beatStr = arrBeats[selectedArray][i] * actualStr;
          float normalizedBeatStr = beatStr - baselineStr;

          // Light up yellow LED in inverted relation. I.e. the lower the amplitude, the brighter the LED:
          int LEDoutput = floatMap(amp, 0, 4095, 255, 0);
          analogWrite(yellowLED, LEDoutput);
          

          //! Make the range static:
          Serial.print("Min:");
          Serial.print(-100);
          Serial.print(",");
          Serial.print("Max:");
          Serial.print(200);

          //! Plot the heart beat
          Serial.print(",");
          Serial.print("BPM:");          
          Serial.println(normalizedBeatStr);
          delay(interval);
  }
  flatlineCheck();
}

//! If the "patient" is flatlining, the signal strenght (amplitude of the heartbeat) is gradually diminshed.
void flatlineCheck() {
  if (flatline) {
    corrigatedStr = corrigatedStr * 0.75;
    if (corrigatedStr < 0.1) digitalWrite(ledPin, HIGH);
  }
  //! Else, it is gradually increased until normal: 
  else {
    if (corrigatedStr < signalStr) {
    corrigatedStr = corrigatedStr * 1.25;
    } else {
      corrigatedStr = 1;
    }
    if (corrigatedStr > 0.1) digitalWrite(ledPin, LOW);
  }
}

//! Helper function for translating digital value (x) to a value within a given range (out_min, out_max).
//! Where in_min is the minimum value, and in_max the maximum, e.g. 12 bit => 0 - 4095.
float floatMap(float x, float in_min, float in_max, float out_min, float out_max) {
  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
}

//! Function called on button press, changing the selected heartbeat
void switchBeat() {
  if (!state) {
    if (selectedArray < maxArrays-1)
    {
      selectedArray += 1;
      flatline = true;
    } else if (selectedArray == maxArrays-1) {
      selectedArray = 0;
      flatline = false;
      corrigatedStr = 0.5;
    }
    //! Avoid debounce by detaching interrupt after initial press, reattach in loop():
    state = true;
    detachInterrupt(digitalPinToInterrupt(buttonPin));
  }
}

