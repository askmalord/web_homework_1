package ru.netology.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final InputStream in;

    private Request(String method, String path, Map<String, String> headers, InputStream in) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.in = in;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public InputStream getIn() {
        return in;
    }

    public static Request getIncomingRequest(InputStream inputStream) throws IOException {
        var reader = new BufferedReader(new InputStreamReader(inputStream));
        var requestLine = reader.readLine();
        var parts = requestLine.split(" ");

        if (parts.length != 3) {
            throw new IOException("Некорректный запрос");
        }

        var method = parts[0];
        var path = parts[1];

        HashMap<String, String> headers = new HashMap<>();
        String lineOfHeaders;
        while (!(lineOfHeaders = reader.readLine()).equals("")) {
            var partsOfHeader = lineOfHeaders.split(": ");
            var headerName = partsOfHeader[0];
            var headerValue = partsOfHeader[1];
            headers.put(headerName, headerValue);
        }
        return new Request(method, path, headers, inputStream);
    }
}
