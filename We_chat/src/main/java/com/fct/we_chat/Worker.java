package com.fct.we_chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.Key;
import java.sql.Timestamp;
import java.util.HashMap;

import com.fct.we_chat.utils.KeysManager;
import com.fct.we_chat.utils.RSAReceiver;
import com.fct.we_chat.utils.RSASender;


public class Worker extends Thread {

        Socket socketCliente;
        HashMap<Socket, String> listaClientes;
        String usuario;
        Key clavePrivada;
        ObjectInputStream entrada;
        ObjectOutputStream salida;
        Timestamp tiempoUsuario;
        String[] listaComandos = {"!ping", "@user", "!userList", "!deleteUser", "!userTime", "!serverTime", "!listaComandos"};

        public Worker() {}

        public Worker(Socket socketCliente, HashMap<Socket, String> listaClientes) throws Exception {
                this.socketCliente = socketCliente;
                this.listaClientes = listaClientes;
                this.clavePrivada = KeysManager.getClavePrivada();
                this.tiempoUsuario = new Timestamp(System.currentTimeMillis());
        }

        private void contestar(String fraseCliente) throws Exception {

                String fraseAEnviar;
                fraseAEnviar = "\t" + fraseCliente + "<" + usuario;
                System.out.println(fraseAEnviar);
                socketCliente.getOutputStream().write(RSASender.cipher(fraseAEnviar, clavePrivada));

        }

        private void contestarTodos(String fraseCliente) throws Exception {

                String fraseAEnviar = "";
                fraseAEnviar = usuario + "> " + fraseCliente;
                System.out.println(fraseAEnviar);

                for(Socket cliente:listaClientes.keySet()) {

                        try {
                                salida = new ObjectOutputStream(cliente.getOutputStream());
                        } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }

                        if (cliente != socketCliente) {
                                fraseAEnviar = "\n\t" + fraseCliente + " <" + usuario;
                        }else{
                                fraseAEnviar = "\t" + fraseCliente + " <" + usuario;
                        }
                        salida.writeObject(RSASender.cipher(fraseAEnviar, clavePrivada));
                }                

        }


        private void contestarUsuario(String usuario, String fraseCliente) throws Exception {

                String fraseAEnviar;
                Socket socketUsuario = null;

                String u1 = usuario.substring(0, 1).toUpperCase();
                usuario = u1 + usuario.substring(1);
                for(Socket cliente:listaClientes.keySet()) {
                     if (listaClientes.get(cliente).equals(usuario)) {
                        socketUsuario = cliente;
                     }   
                }
                if (socketUsuario == null) {
                        contestar("El usuario " + usuario + " no existe");
                        return;
                }

                try {
                        salida = new ObjectOutputStream(socketUsuario.getOutputStream());
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }

                fraseAEnviar = "\t" + fraseCliente + "<" + usuario;
                System.out.println(fraseAEnviar);
                salida.writeObject(RSASender.cipher(fraseAEnviar, clavePrivada));

        }


        @Override
        public void run() {
                String fraseCliente = "";
                try {
                        System.out.println(listaClientes);
                        System.out.println("a");
                        entrada = new ObjectInputStream(socketCliente.getInputStream());
                        usuario = new String(RSAReceiver.decipher((byte[])entrada.readObject(), clavePrivada)); // Uncommented this line
                        System.out.println("b");
                        System.out.println(usuario);
                        System.out.println("c");
                        listaClientes.put(socketCliente, usuario);
                        System.out.println(listaClientes);
                } catch (IOException | ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                

                do {
                        try {
                                fraseCliente = new String(RSAReceiver.decipher((byte[])entrada.readObject(), clavePrivada));
                                System.out.println("d");
                                System.out.println(fraseCliente);
                                if (fraseCliente.startsWith("!") || fraseCliente.startsWith("@")) {
                                        ejecutarComandos(fraseCliente);
                                } else if (fraseCliente.contains("exit")) {
                                        contestar(fraseCliente);
                                } else {
                                        System.out.println(fraseCliente);
                                        contestarTodos(fraseCliente);
                                }

                        } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }

                } while (!fraseCliente.contains("exit"));

                try {
                        socketCliente.close();
                        listaClientes.remove(socketCliente);
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }

        }

        private void ejecutarComandos(String comando) throws Exception{

                String mensaje;
                //Eliminamos los espacios al inicio y al final de la frase
                comando = comando.trim();
                if (comando.startsWith("!ping")) {
                        mensaje = comando.substring(comando.indexOf(" ") + 1);
                        contestarTodos("**" + mensaje.toUpperCase() + "**");
                }else if (comando.startsWith("@")) {
                        String user = comando.substring(1, comando.indexOf(" "));
                        mensaje = comando.substring(comando.indexOf(" "));
                        contestarUsuario(user, mensaje);
                }else if(comando.startsWith("!userList")){
                        mensaje = "";
                        for(Socket cliente:listaClientes.keySet()) {
                                mensaje += listaClientes.get(cliente) + ", ";
                           }
                        contestar(mensaje.substring(0, mensaje.length() - 2));
                }else if(comando.startsWith("!deleteUser")){
                        Socket socketUsuario = null;
                        String user = comando.substring(comando.indexOf(" ") + 1);
                        System.out.println("-" + user + "-");
                        for(Socket cliente:listaClientes.keySet()) {
                                if (listaClientes.get(cliente).equals(user)) {
                                   socketUsuario = cliente;
                                   System.out.println("-" + listaClientes.get(cliente) + "-");
                                }   
                           }
                           if (socketUsuario == null) {
                                   contestar("El usuario " + user + " no existe");          
                           }else{
                                contestarTodos("El usuario " + user + " va a ser eliminado");
                                contestarUsuario(user, "exit");
                                listaClientes.remove(socketUsuario);
                           }
           
                }else if(comando.startsWith("!userTime")){
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        mensaje = "Llevas conectado " + (timestamp.getTime() - tiempoUsuario.getTime()) / 1000.0 + " segundos";
                        contestar(mensaje);
                }else if(comando.startsWith("!serverTime")){   
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        mensaje = "El servidor lleva activo " + (timestamp.getTime() - ServidorSocket.tiempoServidor.getTime()) / 1000.0 + " segundos";
                        contestar(mensaje);
                }else if(comando.startsWith("!listaComandos")){
                        String listaAEnviar = "";
                        for (int i = 0; i < listaComandos.length; i++) {
                                listaAEnviar += listaComandos[i] + ", ";
                        }
                        contestar(listaAEnviar.substring(0, listaAEnviar.length() - 2));
                }else {
                        mensaje = "El comando " + comando + " es desconocido";
                        contestar(mensaje);
                }

        }

}