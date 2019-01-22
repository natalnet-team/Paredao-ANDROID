package com.example.williankossmann.a10dimensoes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    public Cliente cliente;
    boolean conectado = false;

    private TextView tv_Central;
    private EditText et_ip, et_porta;
    double moduloCru;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_Central = findViewById(R.id.tv_Central);
        et_ip = findViewById(R.id.et_ip);
        et_porta = findViewById(R.id.et_porta);
        final TextView tx = findViewById(R.id.tX);
        final TextView ty = findViewById(R.id.tY);
        final TextView tz = findViewById(R.id.tZ);
        final EditText msg = findViewById(R.id.field_msg);
        final Button btEnviar = findViewById(R.id.bt_msg);
        final Button btLigar = findViewById(R.id.btLigar);
        final Button btDesligar = findViewById(R.id.btDesligar);

        final Button button = findViewById(R.id.bt_conectar);
        button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            public void onClick(View v) {
                if(conectado) {
                    cliente.encerrar();
                    conectado = false;
                    button.setText("Conectar e Enviar");
                    tv_Central.setText("Desconectado");
                }else{
                    try {
                        String x = String.valueOf(et_ip.getText());

                        int y = Integer.valueOf(String.valueOf(et_porta.getText()));
                        Log.e("Main", "Ip: " + x + " Porta: " + y);
                        cliente = new Cliente(x , y);
                        cliente.start();
                        Thread.sleep(500);
                        conectado = true;
                        button.setText("Desconectar");
                        tv_Central.setText("Conectado e Enviando");
                    }catch (Exception e){
                        conectado = false;
                        button.setText("Conectar e Enviar");
                        tv_Central.setText("Desconectado");
                    }
                }
            }
        });

        btEnviar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    String txt = msg.getText().toString();
                    cliente.sendMsg(txt);
                } catch (Exception e) {
                    Log.e("Main", "Erro ao enviar" + e);
                }

            }
        });
        btLigar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    cliente.sendMsg("GET /L");
                } catch (Exception e) {
                    Log.e("Main", "Erro ao enviar" + e);
                }

            }
        });
        btDesligar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    cliente.sendMsg("GET /D");
                } catch (Exception e) {
                    Log.e("Main", "Erro ao enviar" + e);
                }

            }
        });

        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert mSensorManager != null;
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

                tx.setText("X: " + (event.values[0]));
                ty.setText("Y: " + (event.values[1]));
                tz.setText("Z: " + (event.values[2]));

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
