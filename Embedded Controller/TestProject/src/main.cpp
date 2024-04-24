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
  // put your setup code here, to run once:
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
  
  // Establish functions on same core:
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
    xSemaphoreTake(xSemaphore, portMAX_DELAY);
    Serial.print("Toggling LED: ");
    Serial.println(xPortGetCoreID());
    // Turn the LED on
    if (volt > 0) {
      digitalWrite(ledPin, HIGH);
    } 
    if (volt > 1.6) {
      digitalWrite(greenLED, HIGH);
    } 
    if (volt > 2.3) {
      digitalWrite(yellowLED, HIGH);
    } 

    // Pause the task (use "val" for variable speed)
    xSemaphoreGive(xSemaphore);
    
    vTaskDelay(250 / portTICK_PERIOD_MS);

    // Turn the LED off
    digitalWrite(ledPin, LOW);
    digitalWrite(greenLED, LOW);
    digitalWrite(yellowLED, LOW);

    // // Pause the task again
    // vTaskDelay(250 / portTICK_PERIOD_MS);
    
  }
}

void getPotentVal(void * parameter) {
  for(;;) {
    xSemaphoreTake(xSemaphore, portMAX_DELAY);
    Serial.print("Getting Potential value: ");
    Serial.println(xPortGetCoreID());
    val = analogRead(potPin);
    volt = floatMap(val, 0, 4095, 0, 3.3);
    xSemaphoreGive(xSemaphore);
    vTaskDelay(250 / portTICK_PERIOD_MS);
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



