
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
        try (ServerSocket serverSocket = new ServerSocket(port);) {
            
            Socket clientSocket = serverSocket.accept();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
            System.out.println("Установлено новое соединение.");
            while (true) {
                out.println("Введите ключевое слово для поиска в формате /{слово} или <end> для завершения сеанса.");
                String request = in.readLine().trim().split(" ")[0];
                if (request.equals("<end>")) {
                    break;
                } else if (request.startsWith("/{") && request.endsWith("}")) {
                    request = request.substring(2, request.length() - 1);
                    out.println(mapper.writeValueAsString(searchEngine.search(request)));
                } else {
                    out.println("Неверный синтаксис запроса.");
                }
            }
            
            out.println("Прощайте, Ибрагим-паша из Парги...");
            
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
