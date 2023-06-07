# ProxTalk - Bluetooth Low Energy Chat mobile application 
Mobile app that allows you send messages to people around you, approximately 15m. You can send text and images and then when you recive message you can react on that. Its on social network concept.  
  
<a href="https://youtu.be/AtK3QXHF-L4">Video of application</a>  
  
<a href="https://play.google.com/store/apps/details?id=com.lukas.proxtalk&pli=1"><img src="https://raw.githubusercontent.com/lukas8092/proxtalk-ble-mobile-chat-app/main/images/google-play-badge.png" width=20% height=20%></a>
## About project
Mobile app is made in Kotlin in Android Studio.  
Comunication between devices is made by Bluetooth Low Energy.  
Server side is made in python. API is written in Flask. <a href="http://proxtalk.live:8080/dist/">API doc in swagger</a>  
There is also websocket that provides real time reactions to messages.  
Database is created in Postgres.  
## Aplication screenshots
<img src="https://raw.githubusercontent.com/lukas8092/proxtalk-ble-mobile-chat-app/main/images/phones.png" width=70% height=70%>

## Comunication between devices
Every phone has BLE server thats advertise characteristicc with service where is actual message stored.  
Also there is scanning thats finding devices with given characteristic. Phone will read the service and then process the message.  
<img src="https://raw.githubusercontent.com/lukas8092/proxtalk-ble-mobile-chat-app/main/images/diagram.png" width=30% height=30%>

## App build-in features
- Sending messages, text or image
- Receving messages
- Create user account
- Option to upload your own profile picture
- Comments on message by websocket
- You can like message  

<a href="https://github.com/lukas8092/proxtalk-ble-mobile-chat-app/blob/main/images/%C5%A0t%C4%9Bp_C4B_TP_Plakat.jpg">Poster for project in czech</a>


