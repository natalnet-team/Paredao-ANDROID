package com.example.williankossmann.a10dimensoescontrole;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Cliente cliente;
    boolean conectado = false;

    private EditText et_ip, et_porta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        et_porta = (EditText) findViewById(R.id.et_porta);
        et_ip = (EditText) findViewById(R.id.et_ip);
        final TextView textView0 = (TextView) findViewById(R.id.et_enviar);
        final Button btEnviar = (Button) findViewById(R.id.btEnviar);
        final Button btCoca = (Button) findViewById(R.id.btCoca);
        final Button button = (Button) findViewById(R.id.bt_conectar);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(conectado) {
                    cliente.encerrar();
                    conectado = false;
                    button.setText("Conectar");
                }else{
                    try {
                        String x = String.valueOf(et_ip.getText());
                        int y = Integer.valueOf(String.valueOf(et_porta.getText()));
                        Log.e("Main", "Ip: " + x + " Porta: " + y);
                        cliente = new Cliente(x , y);
                        cliente.start();
                        cliente.sendMsg("@texto@");
                        Thread.sleep(500);
                        conectado = true;
                        button.setText("Desconectar");
                        cliente.sendMsg("10");
                    }catch (Exception e){
                        Log.e("Main", "Erro ao criar conexao");
                        conectado = false;
                        button.setText("Conectar");

                    }
                }
            }
        });
        btEnviar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try{
                    String txt = textView0.getText().toString();
                    cliente.sendMsg(txt);
                }catch (Exception e){
                    Log.e("Main", "Erro ao enviar" + e);
                }

            }
        });
        btCoca.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                try{
                    cliente.sendMsg(".");
                }
                catch (Exception e){
                    Log.e("Main", "Erro ao enviar");
                }
            }
        });
    }
}
