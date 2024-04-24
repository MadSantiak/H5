#include <Arduino.h>

// Button definitions:
#define buttonPin 15
int currentState = 0;

// Potential definitions:
#define potPin 4
#define ledPin 2
#define greenLED 13
#define yellowLED 12
int val;
float volt;

TaskHandle_t Task1;
TaskHandle_t Task2;

SemaphoreHandle_t xSemaphore = xSemaphoreCreateMutex();

// Prototype functions:
void potentLoop();
void buttonLoop();
void potAndButtonLoop();
void toggleLED(void * parameter);
void getPotentVal(void * parameter);
void blinkGreen(void * parameter);
void blinkYellow(void * parameter);
float floatMap(float x, float in_min, float in_max, float out_min, float out_max);




void setup() {
  Serial.begin(115200);
  pinMode(ledPin, OUTPUT);
  pinMode(greenLED, OUTPUT);
  pinMode(yellowLED, OUTPUT);

  // Button setup:
  pinMode(buttonPin, INPUT_PULLUP);
  
  // Establish functions on separate cores:
  // xTaskCreatePinnedToCore(
  //   toggleLED,
  //   "Toggle LED",
  //   1000,
  //   NULL,
  //   1,
  //   &Task1,
  //   0
  // );
  // xTaskCreatePinnedToCore(
  //   getPotentVal,
  //   "Get Value of Potentiometer",
  //   1000,
  //   NULL,
  //   1,
  //   &Task2,
  //   1
  // );
  
  // Establish functions on unspecified cores:
  xTaskCreate(
    toggleLED,
    "Toggle LED",
    1000,
    NULL,
    1,
    NULL);
  xTaskCreate(
    getPotentVal,
    "Get Potent Val",
    1000,
    NULL,
    1,
    NULL);
}

void loop() {
  //potAndButtonLoop();
  //potentLoop();
  //buttonLoop();

}

void blinkGreen(void * parameter) {
  for (;;) {
    digitalWrite(greenLED, HIGH);
    Serial.println("green");
    delay(400);
    digitalWrite(greenLED, LOW);
  }
}

void blinkYellow(void * parameter) {
  for (;;) {
    digitalWrite(greenLED, HIGH);
    Serial.println("yellow");
    delay(400);
    digitalWrite(greenLED, LOW);
  }
}

void toggleLED(void * parameter){
  for(;;) { 
    if (xSemaphoreTake(xSemaphore, portMAX_DELAY) == pdTRUE) {
      Serial.print("Toggling LED: ");
      Serial.println(xPortGetCoreID());
      
      // Turn the LED on via analogue to allow for brightness control
      // constrain min and max values to ranges within 255, based on the recorded
      // digital signal (just for granularity's sake).
      int blue = constrain(map(val, 0, 1365, 0, 255), 0, 255);
      int green = constrain(map(val, 1365, 2730, 0, 255), 0, 255);
      int yellow = constrain(map(val, 2730, 4095, 0, 255), 0, 255);

      analogWrite(ledPin, blue);
      analogWrite(greenLED, green);  
      analogWrite(yellowLED, yellow);
  
      xSemaphoreGive(xSemaphore);
      
      // Pause the task (use "val" for variable speed)
      vTaskDelay(20 / portTICK_PERIOD_MS);

      // Turn the LED off
      digitalWrite(ledPin, LOW);
      digitalWrite(greenLED, LOW);
      digitalWrite(yellowLED, LOW);
    }
    else {
      Serial.println("LED couldn't take..");
    }
    // // Pause the task again
    // vTaskDelay(250 / portTICK_PERIOD_MS);
    
  }
}

void getPotentVal(void * parameter) {
  for(;;) {
    if (xSemaphoreTake(xSemaphore, portMAX_DELAY) == pdTRUE) {
      Serial.print("Getting Potential value: ");
      Serial.println(xPortGetCoreID());
      val = analogRead(potPin);
      volt = floatMap(val, 0, 4095, 0, 3.3);
    
      xSemaphoreGive(xSemaphore);
      vTaskDelay(20 / portTICK_PERIOD_MS);
    } else {
      Serial.println("Potent couldn't take..");
    }
  }
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



