/*
  Software serial multple serial test

 Receives from the hardware serial, sends to software serial.
 Receives from software serial, sends to hardware serial.

 The circuit:
 * RX is digital pin 10 (connect to TX of other device)
 * TX is digital pin 11 (connect to RX of other device)

 Note:
 Not all pins on the Mega and Mega 2560 support change interrupts,
 so only the following can be used for RX:
 10, 11, 12, 13, 50, 51, 52, 53, 62, 63, 64, 65, 66, 67, 68, 69

 Not all pins on the Leonardo support change interrupts,
 so only the following can be used for RX:
 8, 9, 10, 11, 14 (MISO), 15 (SCK), 16 (MOSI).

 */
#include <SoftwareSerial.h>

SoftwareSerial esp(10, 11); // RX, TX

int ledPin = 12;
int dbg = 13;

void setup()
{
  Serial.begin(9600);
  pinMode(ledPin, OUTPUT);
  pinMode(dbg, OUTPUT);

  // set the data rate for the SoftwareSerial port
  esp.begin(9600);
  while (!esp.find("OK")) {
    esp.println("AT+RST");
  }
  digitalWrite(dbg, HIGH);
  delay(10);
  digitalWrite(dbg, LOW);

  while (!esp.find("OK")) {
    esp.println("AT+CWMODE=1");
  }
  digitalWrite(dbg, HIGH);
  delay(10);
  digitalWrite(dbg, LOW);

  while (!esp.find("OK")) {
    esp.println("AT+CWJAP=\"AlexAlferyFTW\",\"NgSenor410\"");
  }
  digitalWrite(dbg, HIGH);
  delay(10);
  digitalWrite(dbg, LOW);

  while (!esp.find("OK")) {
    esp.println("AT+CIPMUX=1");
  }
  digitalWrite(dbg, HIGH);
  delay(10);
  digitalWrite(dbg, LOW);

  while (!esp.find("OK")) {
    esp.println("AT+CIPSERVER=1,20000");
  }
  digitalWrite(dbg, HIGH);
  delay(1000);
  digitalWrite(dbg, LOW);
}

void loop() // run over and over
{
  while (esp.available()) {
    if (esp.find("+IPD,")) {
      int id;
      id = esp.parseInt();
      esp.find(":");  //skip length
      
      Serial.println("got data");
  
      int pinToSwitch = esp.parseInt();
      int state = esp.parseInt();
      
      Serial.println(pinToSwitch);
      Serial.println(state);
      digitalWrite(pinToSwitch,state);

      //esp.println("AT+CIPCLOSE=");
      //esp.println(id);

    }
  }
}

