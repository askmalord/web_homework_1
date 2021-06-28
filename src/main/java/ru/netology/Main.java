package ru.netology;

import ru.netology.server.Server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        var myServer = new Server(64);

        myServer.addHandler("GET", "/classic.html", ((request, outputStream) -> {
            try {
            final var filePath = Path.of(".", "public", "classic.html");
            final var mimeType = Files.probeContentType(filePath);
            final var template = Files.readString(filePath);
            final var content = template.replace(
                "{time}",
                LocalDateTime.now().toString()
            ).getBytes();
            outputStream.write((
                "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: " + mimeType + "\r\n" +
                    "Content-Length: " + content.length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n"
            ).getBytes());
            outputStream.write(content);
            outputStream.flush();
        } catch (IOException e) {
                e.getStackTrace();
            }
        }));

        myServer.listen(9999);
    }
}
