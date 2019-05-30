# Paredao-ANDROID
Aplicativo android para controle do paredão eletrônico

O novo aplicativo possui dois modos de funcionamento, o Modo Comum e o MODO NÚVEM, adicionado na nova versão do app.
O modo núvem tem o auxilio de um protocolo de rede chamado de MQTT. Para ele funcionar precisamos de um servidor principal e de uma rede de dispositivos que vão receber a comunicação em forma de Broadcast (lista de transmissão)
Os passos a seguir foram feitos para se obter os dados necessários:
<ul>
  <li>Utilizamos o serviço do https://www.cloudmqtt.com/</li>
  <li>Crie uma nova conta no site
  <li>Crie uma nova instancia e adquira seus dados, sendo estes: Server, User, Password e Port
  
</ul>
Os dados obtidos pelo MQTT são utilizados no android pelo codigo abaixo:
```
static String MQTTHOST = "tcp://m16.cloudmqtt.com:12046";
    static String USERNAME = "znyeyfdl";
    static String PASSWORD = "ufFO-2eQarFz";
    String topicStr = "esp/test";
    MqttAndroidClient client;
```
<center>
<img src="https://user-images.githubusercontent.com/31252524/58599342-40f35d00-8256-11e9-9c84-a0acbb860dee.jpg">
</center>
  
