package com.example.williankossmann.a10dimensoescontrole;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Willian Kossmann on 11/05/2017.
 */

public class Cliente extends Thread{

    public String endereco;
    public int porta;

    ObjectOutputStream saida;
    ObjectInputStream entrada;
    Socket conexao;
    boolean existencia = true;

    public Cliente(String _endereco, int _porta){
        endereco = _endereco;
        porta = _porta;
    }

    public void run() {


        try {
            conexao = new Socket(endereco, porta);

            // ligando as conexoes de saida e de entrada
            entrada = new ObjectInputStream(conexao.getInputStream());
            saida = new ObjectOutputStream(conexao.getOutputStream());
            saida.flush();

            do{
                if(!conexao.isConnected()){
                    existencia = false;
                }
            }while (existencia);

            encerrar();

        } catch (Exception e) {
            Log.e("Cliente", "Erro ao tentar conectar"+e);

        }
    }

    public boolean isConect(){
        return conexao.isConnected();
    }

    public void sendMsg(String _msg){
        try {
            saida.writeObject(_msg);
        } catch (IOException e) {
            Log.e("Cliente", "Erro ao enviar..");
        }
    }


    public void encerrar(){
        try {
            saida.close();
            entrada.close();
            conexao.close();
            existencia = false;
        }catch (Exception e){
            existencia = false;
            Log.e("Cliente", "Nao foi possivel encerrar");
        }
    }
}
