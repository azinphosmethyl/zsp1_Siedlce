package ticketserver;

import java.io.*;
import java.net.*;

public class ServerMain {
    public static void main(String[] args) {
        final int PORT = 5000;
        System.out.println("Serwer uruchomiony. Oczekiwanie na połączenie na porcie " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Połączono z klientem: " + clientSocket.getInetAddress());

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                String message = in.readLine();
                System.out.println("Otrzymano: " + message);

                out.println("Serwer: odebrano -> " + message);

                clientSocket.close();
                System.out.println("Połączenie zakończone.\n");
            }
        } catch (IOException e) {
            System.out.println("Błąd serwera: " + e.getMessage());
        }
    }
}
