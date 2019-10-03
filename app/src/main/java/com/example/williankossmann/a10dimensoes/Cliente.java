package com.example.williankossmann.a10dimensoes;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Cliente extends Thread{
    //Recebe o endereco e a porta digitadas pelo app.
    public String endereco;
    public int porta;
    //Ativa a saida e entrada.
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
            //Tenta conexao com o servidor no PC.
            conexao = new Socket(endereco, porta);
            // ligando as conexoes de saida e de entrada
            entrada = new ObjectInputStream(conexao.getInputStream());
            saida = new ObjectOutputStream(conexao.getOutputStream());
            saida.flush();
            String mensagem = "";

            do{
                if(!conexao.isConnected()){
                    existencia = false;
                }
            }while (existencia);

            encerrar();

        } catch (Exception e) {
            Log.e("Cliente", "Erro ao tentar conectar");
        }
    }

    public boolean isConect(){
        return conexao.isConnected();
    }
    //escreve a mensagem na saida.
    public void sendMsg(String _msg){
        try {
            saida.writeObject(_msg);
        } catch (IOException e) {
            Log.e("Cliente", "Erro ao enviar..");
        }
    }
    //encerra a conexao com o servidor.
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
