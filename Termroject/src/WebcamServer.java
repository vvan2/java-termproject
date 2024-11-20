import java.net.*;
import java.io.*;
import java.util.*;

public class WebcamServer {
    private List<ClientHandler> clients = new ArrayList<>();
    private ServerSocket serverSocket;
    
    public WebcamServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("서버 시작됨: " + port);
        
        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler client = new ClientHandler(clientSocket);
            clients.add(client);
            System.out.println("새로운 클라이언트 연결됨. 현재 클라이언트 수: " + clients.size());
            new Thread(client).start();
        }
    }
    
    class ClientHandler implements Runnable {
        private Socket socket;
        private DataInputStream input;
        private DataOutputStream output;
        
        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            this.input = new DataInputStream(socket.getInputStream());
            this.output = new DataOutputStream(socket.getOutputStream());
        }
        
        public void run() {
            try {
                while (true) {
                    // 이미지 데이터 크기 읽기
                    int length = input.readInt();
                    byte[] imageData = new byte[length];
                    input.readFully(imageData);
                    
                    // 다른 모든 클라이언트에게 전송
                    for (ClientHandler client : clients) {
                        if (client != this) {  // 자신을 제외한 다른 클라이언트에게만 전송
                            try {
                                client.output.writeInt(length);
                                client.output.write(imageData);
                                client.output.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                clients.remove(this);
                System.out.println("클라이언트 연결 종료. 현재 클라이언트 수: " + clients.size());
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            new WebcamServer(5000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}