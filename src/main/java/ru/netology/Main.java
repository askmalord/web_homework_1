package ru.netology;

import ru.netology.server.Server;

public class Main {
    public static void main(String[] args) {
        var myServer = new Server(64);
        myServer.startServer(9999);
    }
}
