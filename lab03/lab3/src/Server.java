import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        try (ServerSocket server = new ServerSocket(26780)) {
            System.err.println("""
                        This is one of the worst code samples written by me
                        I know this
                        
                        Pages to visit:
                        localhost:26780/page1.html
                        localhost:26780/page2.html
                        localhost:26780/404.html
                        localhost:26780/
                        """);
            System.out.println("Server started, waiting for connections...");

            while (true) {
                Socket browser = server.accept();
                System.out.println("Browser connection: " + browser.toString());
                BufferedReader reader = new BufferedReader(new InputStreamReader(browser.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(browser.getOutputStream()));
                StringBuilder requestBuilder = new StringBuilder();
                String line;
                System.out.println("waiting for read...");
                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                    requestBuilder.append(line).append('\n');
                    System.out.println("new line: " + line);
                }
                System.out.println("New request: \n" + requestBuilder);
                String request = requestBuilder.toString();
                String path = request.split("GET")[1].split("HTTP/")[0].trim();
                System.out.println("PATH: " + path);


                File page = new File("pages/" + path);
                String response = "";
                if (page.canRead() && page.isFile()) {
                    FileInputStream pageOutput = new FileInputStream(page);
                    String file = new String(pageOutput.readAllBytes());
                    pageOutput.close();

                    response = """
                            HTTP/1.1 200 OK
                            Date: Thu, 32 feb 2034 26:81:64 GMT
                            Content-Type: text/html; charset=utf-8
                            Content-Length: CLEN
                            Connection: close
                            Server: livefish lab 3 test web server
                            Access-Control-Allow-Origin: *
                            Access-Control-Allow-Credentials: true

                            PAGE                            
                            """
                            .replace("CLEN", String.valueOf(file.length()))
                            .replace("PAGE", file);
                } else {
                    FileInputStream pageOutput = new FileInputStream("pages/404.html");
                    String file = new String(pageOutput.readAllBytes());
                    pageOutput.close();
                    response = """
                            HTTP/1.1 404 NOT FOUND
                            Date: Thu, 32 feb 2034 26:81:64 GMT
                            Content-Type: text/html; charset=utf-8
                            Content-Length: CLEN
                            Connection: close
                            Server: livefish lab 3 test web server
                            Access-Control-Allow-Origin: *
                            Access-Control-Allow-Credentials: true

                            PAGE                            
                            """
                            .replace("CLEN", String.valueOf(file.length()))
                            .replace("PAGE", file);
                }

                writer.write(response);
                writer.flush();
            }
        } catch (IOException e) {
            throw e;
        }

    }
}