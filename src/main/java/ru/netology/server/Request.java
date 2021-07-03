package ru.netology.server;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final InputStream in;
    private static final URLEncodedUtils QUERY_STRING_PARSER = new URLEncodedUtils();
    private static String lineOfRequest;

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

    public static String getLineOfRequest() {
        return lineOfRequest;
    }

    public static Request getIncomingRequest(InputStream inputStream) throws IOException {
        var reader = new BufferedReader(new InputStreamReader(inputStream));
        var requestLine = reader.readLine();
        lineOfRequest = requestLine;
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

    public NameValuePair getQueryParam(String name) throws IOException {
        List<NameValuePair> queryParams = getQueryParams();
        NameValuePair queryParamForReturn = null;
        for (int i = 0; i < queryParams.size(); i++) {
            NameValuePair queryParam = queryParams.get(i);
            if (name.equals(queryParam.getName())) {
                queryParamForReturn = queryParam;
            }
        }
        if (queryParamForReturn == null) {
            throw new IOException("Отсутствует искомый параметр");
        }
        else return queryParamForReturn;
    }

    public static List<NameValuePair> getQueryParams() throws IOException {
        if (lineOfRequest == null) {
            throw new IOException("Некорректный запрос");
        }
        List<NameValuePair> allQueryParams = QUERY_STRING_PARSER.parse(lineOfRequest, Charset.forName("UTF-8"));
        if (allQueryParams.size() == 0) {
            throw new IOException("Пустая строка параметров");
        }
        return allQueryParams;
    }
}
