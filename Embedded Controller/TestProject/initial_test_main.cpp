#include <Arduino.h>

// Button definitions:
#define buttonPin 15
int currentState = 0;

// Potential definitions:
#define potPin 4
#define ledPin 2
int val;
float volt;

// Prototype functions:
void potentLoop();
void buttonLoop();
void potAndButtonLoop();
float floatMap(float x, float in_min, float in_max, float out_min, float out_max);




void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  pinMode(ledPin, OUTPUT);

  // Button setup:
  pinMode(buttonPin, INPUT_PULLUP);

}

void loop() {
  potAndButtonLoop();
  //potentLoop();
  //buttonLoop();
}

// Potentiometer loop that blinks LED dependent on potential
void potentLoop() {
  val = analogRead(potPin);
  volt = floatMap(val, 0, 4095, 0, 3.3);
  Serial.print("Value: ");
  Serial.print(val);
  Serial.print(" Volt: ");
  Serial.println(volt);
  
  digitalWrite(ledPin, HIGH);
  delay(100);
  digitalWrite(ledPin, LOW);
  delay(volt * 1000);
}

// Button loop:
void buttonLoop() {
  currentState = digitalRead(buttonPin);
  if (currentState == HIGH)
  {
    digitalWrite(ledPin, HIGH);
  } else {
    digitalWrite(ledPin, LOW);
  }
  delay(100);
}

void potAndButtonLoop() {
  currentState = digitalRead(buttonPin);
  if (currentState == LOW) {
    val = analogRead(potPin);
    volt = floatMap(val, 0, 4095, 0, 3.3);
    Serial.println("Getting value...");
    delay(1000);
  } else {
    Serial.print("Value: ");
    Serial.print(val);
    Serial.print(" Volt: ");
    Serial.println(volt);
    
    digitalWrite(ledPin, HIGH);
    delay(100);
    digitalWrite(ledPin, LOW);
    delay(volt * 1000);
  }
}

// Function to translate digital value to analogue:
float floatMap(float x, float in_min, float in_max, float out_min, float out_max) {
  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
}



