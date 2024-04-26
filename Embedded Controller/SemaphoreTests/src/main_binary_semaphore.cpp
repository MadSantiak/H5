#include <Arduino.h>
// Define LED pins (assuming two LEDs)
const int ledPin1 = 2;
const int ledPin2 = 4;

// Define mutex using FreeRTOS
SemaphoreHandle_t myMutex = xSemaphoreCreateMutex();

// Task function to blink LED (modify pin and delay for distinction)
void taskBlink(void *param) {
  Serial.println("Print LED");
  int ledPin = (int)param; // Cast param to LED pin number
  while (1) {
    // Acquire mutex (wait if not available)
    if (xSemaphoreTake(myMutex, portMAX_DELAY) == pdTRUE) {
      digitalWrite(ledPin, HIGH);
      delay(1000);
      digitalWrite(ledPin, LOW);
      // Release mutex after use
      xSemaphoreGive(myMutex);
    }
    vTaskDelay(pdMS_TO_TICKS(500)); // Delay between attempts
  }
}

void setup() {
  pinMode(ledPin1, OUTPUT);
  pinMode(ledPin2, OUTPUT);

  // Check for mutex creation error (handle if needed)
  if (myMutex == NULL) {
    Serial.println("Mutex creation failed!");
    while (1); // Block if creation fails (replace with error handling)
  }
  // Make sure to initiate the semaphore (by increasing the initiated by 1, as it starts as 0 (unavailable)).
  xSemaphoreGive(myMutex);

  // Start blinking tasks with different LED pins as parameters
  xTaskCreate(taskBlink, "Blink LED 1", 1024, (void*)ledPin1, 1, NULL);
  xTaskCreate(taskBlink, "Blink LED 2", 1024, (void*)ledPin2, 1, NULL);
  Serial.println("Started Blinking Tasks");
}

void loop() {
  // This loop is intentionally left empty 
  // as the main task doesn't need to do anything here
}