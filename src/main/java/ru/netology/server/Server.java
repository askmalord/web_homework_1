package ru.netology.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final ExecutorService threadPool;
    private final Map<String, Map<String, Handler>> handlers = new ConcurrentHashMap<>();

    public Server(int countOfThread) {
        this.threadPool = Executors.newFixedThreadPool(countOfThread);
    }

    public void addHandler(String method, String path, Handler handler) {
        if (handlers.get(method) == null) {
            handlers.put(method, new ConcurrentHashMap<>());
        }
        handlers.get(method).put(path, handler);
    }

    public void listen(int port) {
        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                var socket = serverSocket.accept();
                threadPool.submit(() -> handleConnection(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleConnection(Socket clientSocket) {
        try (
            clientSocket;
            final var in = clientSocket.getInputStream();
            final var out = new BufferedOutputStream(clientSocket.getOutputStream());
        ) {

            Request request = Request.getIncomingRequest(in);

            if (handlers.get(request.getMethod()) == null) {
                castNotFoundException(out);
            }

            var path = request.getPath();

            Handler handler = handlers.get(request.getMethod()).get(path);

            if (handler == null) {
                castNotFoundException(out);
                return;
            }

            handler.handle(request, out);
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    private void castNotFoundException(BufferedOutputStream out) throws IOException {
        out.write((
            "HTTP/1.1 404 Not Found\r\n" +
                "Content-Length: 0\r\n" +
                "Connection: close\r\n" +
                "\r\n"
        ).getBytes());
        out.flush();
        return;
    }
}
