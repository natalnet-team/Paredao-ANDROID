package com.example.williankossmann.a10dimensoes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    public Cliente cliente;
    boolean conectado = false;
    private TextView tv_Central;
    private TextView nuvem;
    private EditText et_ip, et_porta;
    //Inicializando o MQTT, o host depende da sua interface.
    static String MQTTHOST = "tcp://m16.cloudmqtt.com:12046";
    //O usuario gerado pelo servidor
    static String USERNAME = "znyeyfdl";
    //A senha gerada pelo servidor.
    static String PASSWORD = "ufFO-2eQarFz";
    //O topico serve para sincronizar os dispositivos.
    //Direcione-os para conversar no mesmo topico, para que todos recebam a mensagem
    String topicStr = "esp/test";
    MqttAndroidClient client;
    double moduloCru;
    Switch sw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nuvem = findViewById(R.id.nuvem);
        tv_Central = findViewById(R.id.tv_Central);
        et_ip = findViewById(R.id.et_ip);
        et_porta = findViewById(R.id.et_porta);
        final TextView tx = findViewById(R.id.tx);
        final TextView ty = findViewById(R.id.ty);
        final TextView tz = findViewById(R.id.tz);
        
        final EditText msg = findViewById(R.id.field_msg);
        final Button btEnviar = findViewById(R.id.bt_msg);
        final Button btLigar = findViewById(R.id.btLigar);
        final Button btDesligar = findViewById(R.id.btDesligar);
        sw = (Switch)findViewById(R.id.sw);


        final Button button = findViewById(R.id.bt_conectar);
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId);
        final MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());
        tv_Central.setTextColor(Color.RED);
        boolean connected = false;
        nuvem.setText("Desconectado");
        nuvem.setTextColor(Color.RED);
        btLigar.setEnabled(!btLigar.isEnabled());
        btDesligar.setEnabled(!btDesligar.isEnabled());
        //Verifica o aperto do botao NUVEM.
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    //Ao ser ligado, ative os botoes de ligar e desligar o paredao.
                    btLigar.setEnabled(true);
                    btDesligar.setEnabled(true);
                    //Verifique no sistema se existe conexao, caso exista, sera possivel realizar a comunicaao.
                    //Caso nao haja internet, o sistema emitira uma mensagem vista no else.
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        //we are connected to a network

                        try {
                            //Tente uma conexao com o MQTT nas especificacoes descritas nas variaveis acima.
                            IMqttToken token = client.connect(options);
                            token.setActionCallback(new IMqttActionListener() {
                                @Override
                                public void onSuccess(IMqttToken asyncActionToken) {
                                    //Emite uma mensagem caso tenha sucesso.
                                    Toast.makeText(MainActivity.this, "Conectado a nuvem! :) ", Toast.LENGTH_LONG).show();
                                    nuvem.setText("Conectado");
                                    nuvem.setTextColor(Color.GREEN);

                                }

                                @Override
                                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                    Toast.makeText(MainActivity.this, "Falha na conexao com a nuvem! :( ", Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(MainActivity.this, "Falha na conexao com a nuvem! :( ", Toast.LENGTH_LONG).show();
                        sw.setChecked(false);
                        btLigar.setEnabled(false);
                        btDesligar.setEnabled(false);
                    }
                }else{
                    //Caso a rotina nao seja satisfeita ou o botao nao seja apertado, desabilite os botoes referents a nuvem.
                    nuvem.setText("Desconectado");
                    nuvem.setTextColor(Color.RED);
                    btLigar.setEnabled(false);
                    btDesligar.setEnabled(false);
                }
            }
        });
        //Tentando a conexao local, por meio do servidor. Caso o botao seja pressionado, muda-se as cores e troca-se os texztos do botao.
        button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            public void onClick(View v) {
                if(conectado) {
                    cliente.encerrar();
                    conectado = false;
                    button.setText("Conectar");
                    tv_Central.setText("Desconectado");
                    tv_Central.setTextColor(Color.RED);
                }else{
                    try {
                        //Pega as informacoes nos textView relacionadas a porta e ao ip.
                        //Caso tenha sucesso, mude a cor do texto.
                        String x = String.valueOf(et_ip.getText());
                        int y = Integer.valueOf(String.valueOf(et_porta.getText()));
                        Log.e("Main", "Ip: " + x + " Porta: " + y);
                        cliente = new Cliente(x , y);
                        cliente.start();
                        Thread.sleep(500);
                        conectado = true;
                        button.setText("Desconectar");
                        tv_Central.setText("Conectado");
                        tv_Central.setTextColor(Color.GREEN);
                        btEnviar.setEnabled(true);
                    }catch (Exception e){
                        conectado = false;
                        button.setText("Conectar");
                        tv_Central.setText("Desconectado");
                        tv_Central.setTextColor(Color.RED);
                        btEnviar.setEnabled(false);
                    }
                }
            }
        });

        btEnviar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Caso o botao nuvem esteja apertado, significa que tentaremos conexao via MQTT.
                if (sw.isChecked()) {
                    String topic = topicStr;
                    String mes = msg.getText().toString();
                    byte[] enc = new byte[0];
                    try {
                        //A mensagem e encriptada e enviada para o servidor no topico descrito acima.
                        enc = mes.getBytes("UTF-8");
                        MqttMessage message = new MqttMessage(enc);
                        message.setRetained(true);
                        client.publish(topic, message);
                        msg.setText("");
                        Toast.makeText(MainActivity.this, "Mensagem enviada", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e("Main", "Erro ao enviar" + e);
                    }
                }else{
                try {
                    //Caso o modo nuvem nao esteja apertado, tentaremos conexao local pelo servidor JAVA.
                    String txt = msg.getText().toString() +";";
                    msg.setText("");
                    cliente.sendMsg(txt);
                    Toast.makeText(MainActivity.this, "Mensagem enviada", Toast.LENGTH_LONG).show();
                } catch (Exception e) {

                }
            }
            }
        });
        //Realiza o envio do sinal de ligar para o servidor MQTT.
        btLigar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    String topic = topicStr;
                    String mes = "ON";
                    byte[] enc = new byte[0];
                    enc = mes.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(enc);
                    message.setRetained(true);
                    client.publish(topic, message);
                    Toast.makeText(MainActivity.this, "Paredão Ligado", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.e("Main", "Erro ao enviar" + e);
                }

            }
        });
         //Realiza o envio do sinal de desligar para o servidor MQTT.
        btDesligar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    String topic = topicStr;
                    String mes = "OFF";
                    byte[] enc = new byte[0];
                    enc = mes.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(enc);
                    message.setRetained(true);
                    client.publish(topic, message);
                    Toast.makeText(MainActivity.this, "Paredão Desligado", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.e("Main", "Erro ao enviar" + e);
                }

            }
        });
        //Realiza a leitura do Acelerometro do dispositivo.
        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert mSensorManager != null;
        //Carrega as medidas do sensor.
        Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorManager.registerListener(new SensorEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSensorChanged(SensorEvent event) {
                //Captação dos dados:
                float x = (event.values[0]/event.sensor.getMaximumRange()/2) * 50;
                float y = (event.values[1]/event.sensor.getMaximumRange()/2) * 50;
                float z = (event.values[2]/event.sensor.getMaximumRange()/2) * 50;
                event.sensor.getMaximumRange();
                moduloCru = Math.sqrt(x*x + y*y + z*z);
                //Printa os dados na tela da Main Activity.
                tx.setText("X: " + (event.values[0]));
                ty.setText("Y: " + (event.values[1]));
                tz.setText("Z: " + (event.values[2]));
                //Filtra o sinal.
                if(moduloCru > 50){
                    moduloCru = 50;
                }else if(moduloCru<0){
                    moduloCru = 0;
                }

            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        }, mSensor, SensorManager.SENSOR_DELAY_UI);


        //Declare the timer
        Timer t = new Timer();
        //Set the schedule function and rate
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //Called each time when 100 milliseconds (the period parameter)
                if (conectado) {
                    try {
                        if (cliente.isConect()) {
                            cliente.sendMsg(String.valueOf(moduloCru));
                        }
                    } catch (Exception e) {
                        Log.i("Main", "Erro ao checar conexao");
                        conectado = false;
//                        button.setText("Conectar e Enviar");
//                        tv_Central.setText("Desconectado");
                    }
                }
            }
        }, 1000, 100);
    }

}
