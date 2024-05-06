
/**
 * @file
 * Project: WiFi Thermometer with visualization
 * Author: Mads Søndergaard
 * Manage: Bo Elbæk Steffensen
 *
 * Added functionality beyond requirement specifications:
 * - Added download option for stored temperature data (entire .txt file)
 * - Added slider to widen or narrow the range (temporal) of data
 *
*/
#include <Arduino.h>

// Libraries for SD card
#include "FS.h"
#include "SD.h"
#include <SPI.h>

// DS18B20 libraries
#include <OneWire.h>
#include <DallasTemperature.h>

// Libraries to get time from NTP Server
#include <WiFi.h>
#include <NTPClient.h>
#include <WiFiUdp.h>
#include <AsyncTCP.h>
#include <ESPAsyncWebServer.h>

// Library for
#include <SPIFFS.h>

// Define port number for AsyncWebServer
AsyncWebServer server(80);

// Prototype functions
void setTimezone(String timezone);
void initSPIFFS();
void writeFile(fs::FS &fs, const char * path, const char * message);
void appendFile(fs::FS &fs, const char * path, const char * message);
String readFile(fs::FS &fs, const char * path);
String readSDFile(const char* path);
String getReadings();
String readLastLine(const char* path);
String tempHistory();
void getTimeStamp();
void logSDCard();
void deleteLog();
bool initWiFi();

// Variables to access and fetch SSID and Password supplied by the user during initial run.
//! Path to configuration file containing SSID
const char* ssidPath = "/ssid.txt";
//! Path to configuration file containing Password
const char* passPath = "/pass.txt";
//! Variable used to check input is from the SSID field
const char* PARAM_INPUT_1 = "ssid";
//! Variable used to check input is from the Password field
const char* PARAM_INPUT_2 = "pass";
String pass;
String ssid;

//! Define CS pin for the SD card module
#define SD_CS 5

//! Save reading number on RTC memory
RTC_DATA_ATTR int readingID = 0;

String dataMessage;

//! Data wire is connected to ESP32 GPIO 21
#define ONE_WIRE_BUS 21
//! Setup a oneWire instance to communicate with a OneWire device on the designated data wire
OneWire oneWire(ONE_WIRE_BUS);
//! Pass our oneWire reference to Dallas Temperature sensor
DallasTemperature sensors(&oneWire);

//! Temperature Sensor variables
float temperature;

//! Define NTP Client to get time
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP);

//! Variables to save date and time
String formattedDate;
String dayStamp;
String timeStamp;

//! Define task stack size
#define TASK_STACK_SIZE 4096

//! Define task delay (5 minutes) // testing: 30 seconds
#define TASK_DELAY_MS 30000

//! Define task handle:
TaskHandle_t taskHandle = NULL;

/**
 * Task to call getReadings() periodically.
*/
void getReadingsTask(void *pvParameters) {
  for (;;) {
    // Call getReadings()
    getReadings();
    // Delay task execution
    vTaskDelay(pdMS_TO_TICKS(TASK_DELAY_MS));
  }
}

void setup() {
  /**
   * Sets up the ESP32. Specifically:
  */
  //! Start serial communication for debugging purposes
  Serial.begin(115200);
  initSPIFFS();
  ssid = readFile(SPIFFS, ssidPath);
  pass = readFile(SPIFFS, passPath);

  //! Initialize SD card
  SD.begin(SD_CS);
  if(!SD.begin(SD_CS)) {
    Serial.println("Card Mount Failed");
    return;
  }
  uint8_t cardType = SD.cardType();
  if(cardType == CARD_NONE) {
    Serial.println("No SD card attached");
    return;
  }
  Serial.println("Initializing SD card...");
  if (!SD.begin(SD_CS)) {
    Serial.println("ERROR - SD card initialization failed!");
    return;    // init failed
  }

  //! If the data.txt file doesn't exist
  //! Create a file on the SD card and write the data labels
  File file = SD.open("/data.txt");
  if(!file) {
    Serial.println("File doens't exist");
    Serial.println("Creating file...");
    writeFile(SD, "/data.txt", "Reading ID, Date, Hour, Temperature \r\n");
  }
  else {
    Serial.println("File already exists");
    while (file.available())
    {
      String line = file.readStringUntil('\n');
      readingID = line.substring(0, line.indexOf(",")).toInt();
    }
    Serial.print("Last ID: ");
    Serial.println(readingID);
  }
  file.close();
  
  //! Initaite task to run periodically:
  xTaskCreate(getReadingsTask, "getReadingsTask", TASK_STACK_SIZE, NULL, 1, &taskHandle);

  //! Attempt to initialize WiFi, and if succesful, serve endpoints (/temperature, /history, /download). Used in index.html script.
  if(initWiFi()) {
    server.on("/", HTTP_GET, [](AsyncWebServerRequest *request){
      request->send(SPIFFS, "/index.html");
    });
    server.on("/temperature", HTTP_GET, [](AsyncWebServerRequest *request){
      String latestReading = readLastLine("/data.txt");
      request->send(200, "text/plain", latestReading);
    });
    server.on("/history", HTTP_GET, [](AsyncWebServerRequest *request){
      request->send_P(200, "text/plain", tempHistory().c_str());
    });
    server.on("/download", HTTP_GET, [](AsyncWebServerRequest *request){
      request->send(SD, "/data.txt", String(), "text/plain");
    });
    server.on("/servercode.js", HTTP_GET, [](AsyncWebServerRequest *request){
      request->send(SPIFFS, "/servercode.js");
    });
    server.on("/deletelog", HTTP_POST, [](AsyncWebServerRequest *request) {
      deleteLog(); // Call the deleteLog function to delete the file
      // Respond to the client based on the deletion success
      if (SD.exists("/data.txt")) {
        request->send(404, "text/plain", "Log file not found (might be already deleted)");
      } else {
        request->send(200, "text/plain", "Log file deleted successfully");
      }
    });

    server.begin();
  }
  //! If unsuccesful, starts local open AP for the user to connect to and configure
  else {
    // Connect to Wi-Fi network with SSID and password
    Serial.println("Setting AP (Access Point)");
    // NULL sets an open Access Point
    WiFi.softAP("MASO41-WIFI", NULL);

    IPAddress IP = WiFi.softAPIP();
    Serial.print("AP IP address: ");
    Serial.println(IP);

    // Web Server Root URL
    server.on("/", HTTP_GET, [](AsyncWebServerRequest *request){
      request->send(SPIFFS, "/wifimanager.html", "text/html");
    });

    server.serveStatic("/", SPIFFS, "/");

    //! Get user configurations and store them in their respective files.
    server.on("/", HTTP_POST, [](AsyncWebServerRequest *request) {
      int params = request->params();
      for(int i=0;i<params;i++){
        AsyncWebParameter* p = request->getParam(i);
        if(p->isPost()){
          // HTTP POST ssid value
          if (p->name() == PARAM_INPUT_1) {
            ssid = p->value().c_str();
            Serial.print("SSID set to: ");
            Serial.println(ssid);
            // Write file to save value
            writeFile(SPIFFS, ssidPath, ssid.c_str());
          }
          // HTTP POST pass value
          if (p->name() == PARAM_INPUT_2) {
            pass = p->value().c_str();
            Serial.print("Password set to: ");
            Serial.println(pass);
            // Write file to save value
            writeFile(SPIFFS, passPath, pass.c_str());
          }

          //Serial.printf("POST[%s]: %s\n", p->name().c_str(), p->value().c_str());
        }
      }
      request->send(200, "text/plain", "Done. ESP will restart, connect to your router");
      delay(3000);
      ESP.restart();
    });
    server.begin();

  }


  //! Start the DallasTemperature library
  sensors.begin();

  //! Get readings, timestamp, and log data to SD card.
  // getReadings();
}

void loop() {
  /**
   * Loop() is unused.
  */
}


void setTimezone(String timezone){
  /**
   * Sets the timezone for the environment using the String passed
  */
  Serial.printf("  Setting Timezone to %s\n",timezone.c_str());
  setenv("TZ",timezone.c_str(),1);  //  Now adjust the TZ.  Clock settings are adjusted to show the new local time
  tzset();
}

void initSPIFFS() {
  /**
   * Attempts to initialize SPI filesystem and checks whether it fails or not.
  */
  if (!SPIFFS.begin(true)) {
    Serial.println("An error has occurred while mounting SPIFFS");
  }
  Serial.println("SPIFFS mounted successfully");
}

String readLastLine(const char* path) {
  File file = SD.open(path);
  if (!file) {
    Serial.println("Error opening file for reading");
    return "";
  }

  String lastLine;
  while (file.available()) {
    lastLine = file.readStringUntil('\n');
  }
  file.close();
  return lastLine;
}

String getReadings(){
  /**
   * Gets temperature from DS18B20 Sensor and returns the string value to the javascript reponsible for plotting the new readings.
   * Additionally gets timestamp, increments the readingID variable, and logs it to the data.txt file on the SD card.
  */
  sensors.requestTemperatures();
  temperature = sensors.getTempCByIndex(0); // Temperature in Celsius
  //! Get timestamp
  getTimeStamp();
  //! Increment readingID.
  readingID++;
  //! Log to SD Card
  logSDCard();
  return String(temperature);
}

void getTimeStamp() {
  /**
   * Function to get date and time from NTPClient
  */

  while(!timeClient.update()) {
    timeClient.forceUpdate();
  }
  //! The formattedDate comes with the following format:
  //! 2018-05-28T16:00:13Z
  //! We need to extract date and time
  formattedDate = timeClient.getFormattedDate();
  Serial.println(formattedDate);

  //! Extract date
  int splitT = formattedDate.indexOf("T");
  dayStamp = formattedDate.substring(0, splitT);
  Serial.println(dayStamp);
  //! Extract time
  timeStamp = formattedDate.substring(splitT+1, formattedDate.length()-1);
  Serial.println(timeStamp);
}

void logSDCard() {
  /**
   * Takes the gathered data and appends it to the data.txt file stored on the SD.
  */
  dataMessage = String(readingID) + "," + String(dayStamp) + "," + String(timeStamp) + "," +
                String(temperature) + "\r\n";
  Serial.print("Save data: ");
  Serial.println(dataMessage);
  appendFile(SD, "/data.txt", dataMessage.c_str());
}

void deleteLog() {
  /**
   * Deletes the Thermometer readings if present.
  */
  if(SD.exists("/data.txt"))
  {
    SD.remove("/data.txt");
    Serial.println("File deleted..");
  }
  Serial.println("Regenerating file");
  writeFile(SD, "/data.txt", "Reading ID, Date, Hour, Temperature \r\n");
}

// Write to the SD card (DON'T MODIFY THIS FUNCTION)
void writeFile(fs::FS &fs, const char * path, const char * message) {
  /**
   * Attempts to write to the file at the designated path (path), based on the defined filesystem (fs).
  */
  Serial.printf("Writing file: %s\n", path);

  File file = fs.open(path, FILE_WRITE);
  if(!file) {
    Serial.println("Failed to open file for writing");
    return;
  }
  if(file.print(message)) {
    Serial.println("File written");
  } else {
    Serial.println("Write failed");
  }
  file.close();
}

// Append data to the SD card (DON'T MODIFY THIS FUNCTION)
void appendFile(fs::FS &fs, const char * path, const char * message) {
  Serial.printf("Appending to file: %s\n", path);

  File file = fs.open(path, FILE_APPEND);
  if(!file) {
    Serial.println("Failed to open file for appending");
    return;
  }
  if(file.print(message)) {
    Serial.println("Message appended");
  } else {
    Serial.println("Append failed");
  }
  file.close();
}

String readFile(fs::FS &fs, const char * path) {
  /**
   * Attempts to read the file at a given path, using the designated filesystem.
  */
  Serial.printf("Reading file: %s\r\n", path);

  File file = fs.open(path);
  if(!file || file.isDirectory()){
    Serial.println("- failed to open file for reading");
    return String();
  }

  String fileContent;
  while(file.available()){
    fileContent = file.readStringUntil('\n');
    break;
  }
  return fileContent;
}

String readSDFile(const char* path) {
  /**
   * Reads contents of SD card files.
  */
  File file = SD.open(path);
  String content = "";
  if (file) {
    while (file.available()) {
      content += char(file.read());
    }
  }
  file.close();
  return content;
}

String tempHistory() {
  /**
   * Fetches the entirety of the data.txt contents. To be sent to Javascript for processing (i.e. turning the CSV data into visualized data)
   * This is done to avoid load on the ESP32, but also due to the complexity of attempting to do so on the ESP, rather than with simple Javascript
   * comprehension.
  */
  String data = readSDFile("/data.txt");
  return data;
}

bool initWiFi() {
  /**
   * Attempts to initialize WiFi connection. If SSID or Password is not supplied, returns false so the user is prompted to supply these.
   * Otherwise initiates the connection using those stored data, and gets the time via the NTPClient.
  */
  if(ssid=="" || pass==""){
    Serial.println("Undefined SSID or Password.");
    return false;
  }
  WiFi.mode(WIFI_STA);

  WiFi.begin(ssid.c_str(), pass.c_str());
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("Connecting to WiFi...");
  Serial.println(WiFi.localIP());
  // Initialize a NTPClient to get time
  timeClient.begin();
  // Set offset time in seconds to adjust for your timezone, for example:
  // GMT +1 = 3600
  // GMT +8 = 28800
  // GMT -1 = -3600
  // GMT 0 = 0
  //setenv()
  timeClient.setTimeOffset(7200);
  return true;
}