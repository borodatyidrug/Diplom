
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    
    protected SearchEngine searchEngine;
    protected ObjectMapper mapper;
    
    public Server(SearchEngine searchEngine) {
        this.searchEngine = searchEngine;
        mapper = new ObjectMapper();
    }
    
    public void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            PrintWriter out;
            BufferedReader in;
            Socket clientSocket;
            while (true) {
                clientSocket = serverSocket.accept();
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                System.out.println("Установлено новое соединение.");
                out.println("Введите ключевое слово для поиска в формате /{слово} или <shutdown> для завершения работы сервера.");
                String request = in.readLine().trim().split(" ")[0];
                if (request.equals("<shutdown>")) {
                    out.println("Прощайте, Ибрагим-паша из Парги...");
                    out.close();
                    in.close();
                    clientSocket.close();
                    serverSocket.close();
                    break;
                } else if (request.startsWith("/{") && request.endsWith("}")) {
                    request = request.substring(2, request.length() - 1);
                    out.println(mapper.writeValueAsString(searchEngine.search(request)));
                    in.close();
                    out.close();
                    clientSocket.close();
                } else {
                    out.println("Неверный синтаксис запроса.");
                    in.close();
                    out.close();
                    clientSocket.close();
                }
            }
            
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
