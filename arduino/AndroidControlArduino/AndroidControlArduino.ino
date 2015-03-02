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

  // set the data rate for the SoftwareSerial port
  esp.begin(9600);
  while (!esp.find("OK")) {
    esp.println("AT+RST");
  }
  digitalWrite(dbg, HIGH);
  delay(10);
  digitalWrite(dbg, LOW);

  while (!esp.find("OK")) {
    esp.println("AT+CWMODE=1"); //station only
  }
  digitalWrite(dbg, HIGH);
  delay(10);
  digitalWrite(dbg, LOW);

  esp.println("AT+CWJAP=?");
  if (!esp.find(AP)){
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

  while (!esp.find("OK")) {
    esp.println("AT+CIPMUX=1");
  }
  digitalWrite(dbg, HIGH);
  delay(10);
  digitalWrite(dbg, LOW);

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

