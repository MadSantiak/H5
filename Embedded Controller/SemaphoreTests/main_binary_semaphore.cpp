#include <Arduino.h>
// Define LED pin
const int ledPin = 2;

// Define binary semaphore
SemaphoreHandle_t mySemaphore;

// Task function to blink LED
void taskBlink(void *param) {
  Serial.println("TEST");
  while (1) {
    Serial.println("Blink TASK");
    // Take the semaphore (wait if not available)
    if (xSemaphoreTake(mySemaphore, portMAX_DELAY) == pdTRUE) {
      digitalWrite(ledPin, HIGH);
      delay(500);
      digitalWrite(ledPin, LOW);
      // Release the semaphore after use
      xSemaphoreGive(mySemaphore);
    }
    vTaskDelay(pdMS_TO_TICKS(100)); // Delay between attempts
  }
}

void setup() {
  Serial.begin(9200);
  Serial.println("TEST2");
  pinMode(ledPin, OUTPUT);

  // Create a binary semaphore with initial value 1 (available)
  mySemaphore = xSemaphoreCreateBinary();
  xSemaphoreGive(mySemaphore);
  // Check for creation error (handle if needed)
  if (mySemaphore == NULL) {
    Serial.println("Semaphore creation failed!");
    while (1); // Block if creation fails (replace with error handling)
  }

  // Start blinking task
  xTaskCreate(taskBlink, "Blink Task", 1024, NULL, 1, NULL);
  Serial.println("Started Blinking Task");
}

void loop() {
  // Serial.println("TEST3");
  // This loop is intentionally left empty 
  // as the main task doesn't need to do anything here
}