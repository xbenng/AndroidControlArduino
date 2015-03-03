/*
 Ben Ng 2015

 The circuit:
 * RX is digital pin 10 (connect to TX of esp8266)
 * TX is digital pin 11 (connect to RX of esp8266)


 */
#include <SoftwareSerial.h>

SoftwareSerial esp(10, 11); // RX, TX

int dbg = 13;

char AP[] = "AlexAlferyFTW";
char password[] = "NgSenor410";
char serverPort[] = "20000";

void setup()
{
  Serial.begin(9600);
  Serial.println("Initializing Wifi Controlled Arduino");
  
  pinMode(dbg, OUTPUT);
  //Make sure you still set up your output pins here.

  // set the data rate for the SoftwareSerial port
  esp.begin(9600);
  while (!esp.find("OK")) {
    esp.println("AT+RST");
  }
  digitalWrite(dbg, HIGH);
  delay(10);
  digitalWrite(dbg, LOW);

  Serial.println("setting wireless mode...");
  while (!esp.find("OK")) {
    esp.println("AT+CWMODE=1"); //station only
  }
  digitalWrite(dbg, HIGH);
  delay(10);
  digitalWrite(dbg, LOW);

  esp.println("AT+CWJAP?");
  if (!esp.find(AP)){
    Serial.println("connecting to wifi...");
    while (!esp.find("OK")) {
      esp.print("AT+CWJAP=\"");
      esp.print(AP);
      esp.print("\",\"");
      esp.print(password);
      esp.println("\"");
    }
  }
  digitalWrite(dbg, HIGH);
  delay(10);
  digitalWrite(dbg, LOW);
  
  Serial.println("setting connection mode...");
  while (!esp.find("OK")) {
    esp.println("AT+CIPMUX=1");
  }
  digitalWrite(dbg, HIGH);
  delay(10);
  digitalWrite(dbg, LOW);

  Serial.print("setting up server to listen on port ");
  Serial.print(serverPort);
  Serial.println("...");
  while (!esp.find("OK")) {
    esp.print("AT+CIPSERVER=1,");
    esp.println(serverPort);  //listen on port
  }
  digitalWrite(dbg, HIGH);
  delay(1000);
  digitalWrite(dbg, LOW);
  
 Serial.println("Done.");

}

void loop() // poll for data
{
  while (esp.available()) {
    if (esp.find("+IPD,")) {  //data recieved header ex: "+IPD,id,length:"
      int id;
      id = esp.parseInt();
      esp.find(":");  //skip length
      
      Serial.println("got request");

      int pinToSwitch = esp.parseInt();
      int state = esp.parseInt();
      if ((state == 0 || state == 1)){  //change state
        Serial.println(pinToSwitch);
        Serial.println(state);
        digitalWrite(pinToSwitch,state);
      } else {  //tell state
        //Serial.println("sending state");
        int c = 1;
        while(!esp.find(">") && c++ < 5){
           //wait until esp is ready to send
           esp.print("AT+CIPSEND=");
           esp.print(id);
           esp.println(",1");
        }
        //Serial.println(digitalRead(pinToSwitch)); 
        esp.println(digitalRead(pinToSwitch));  //send state
      }

      //esp.println("AT+CIPCLOSE=");
      //esp.println(id);

    }
  }
}

