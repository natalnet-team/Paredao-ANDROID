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
Os dados obtidos pelo MQTT são utilizados no android pelo codigo abaixo. Utilizando estes dados, é possivel tentar uma conexão com o servidor e escrever no topico esp/test. O *Topico* é onde todos os dispositivos conversam, é preciso designar os microcontroladores para conversar neste mesmo topico descrito em _topicStr_:
<br>

```Java
static String MQTTHOST = "tcp://m16.cloudmqtt.com:12046";
    static String USERNAME = "znyeyfdl";
    static String PASSWORD = "ufFO-2eQarFz";
    String topicStr = "esp/test";
    MqttAndroidClient client;
```
<br>

O modo nuvem passa a funcionar e pode escrever no topico, enviando uma string de mensagem, ou os valores ON e OFF para os botões Ligar e Desligar respectivamente.


<center>
<img src="https://user-images.githubusercontent.com/31252524/58599342-40f35d00-8256-11e9-9c84-a0acbb860dee.jpg">
</center>
  
Na opção WEBSOCKETUI do cloudmqtt podemos ver as mensagens chegando conforme enviamos. Estas mensagens são tratadas pelo microcontrolador para realizar uma ação especifica.

<br>
<img src="https://user-images.githubusercontent.com/31252524/58599654-9d0ab100-8257-11e9-901e-99fad5c04487.png">
