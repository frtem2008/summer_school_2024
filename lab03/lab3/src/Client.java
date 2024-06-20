import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket server = new Socket(args[0], Integer.parseInt(args[1]));
        BufferedReader reader = new BufferedReader(new InputStreamReader(server .getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
        String filename = args[2];
        String request = """
                GET PATH HTTP/1.1
                Host: HOST:PORT
                Connection: keep-alive
                Pragma: no-cache
                Cache-Control: no-cache
                sec-ch-ua: "Chromium";v="124", "YaBrowser";v="24.6", "Not-A.Brand";v="99", "Yowser";v="2.5"
                sec-ch-ua-mobile: ?0
                sec-ch-ua-platform: "Windows"
                Upgrade-Insecure-Requests: 1
                User-Agent: lab 03 weird http client
                Accept: text/html
                Sec-Fetch-Site: none
                Sec-Fetch-Mode: navigate
                Sec-Fetch-User: ?1
                Sec-Fetch-Dest: document
                Accept-Encoding: gzip, deflate, br, zstd
                Accept-Language: ru,en;q=0.9
                
                """
                .replace("HOST", args[0])
                .replace("PORT", args[1])
                .replace("PATH", args[2]);
        writer.write(request);
        writer.flush();

        StringBuilder responseBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            responseBuilder.append(line).append('\n');
        }
        String response = responseBuilder.toString();
        System.out.println(response.substring(response.indexOf("\n\n")).trim());
    }
}
