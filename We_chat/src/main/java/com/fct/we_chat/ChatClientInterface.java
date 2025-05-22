package com.fct.we_chat;

public interface ChatClientInterface {

    public static String nickname = null;


    default void connectToServer(){}

    default void sendMessage(String message){}

    default void logout(){}

    default void writeServer(String message){}
} 