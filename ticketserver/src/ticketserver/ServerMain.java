package ticketserver;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerMain {
    private static final double RATE = 5.0;
    private static final List<Ticket> tickets = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        final int PORT = 5000;
        System.out.println("Serwer uruchomiony. Oczekiwanie na połączenia na porcie " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.out.println("Błąd serwera: " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                String message = in.readLine();
                if (message == null) return;

                String[] parts = message.split(",");
                if (parts.length == 2) {
                    String reg = parts[0].trim().toUpperCase();
                    int hours = Integer.parseInt(parts[1].trim());

                    Ticket ticket = new Ticket(reg, hours, RATE);
                    tickets.add(ticket);

                    out.println(formatSimpleTicket(ticket));
                    System.out.println("Wystawiono bilet: " + reg);
                } else {
                    out.println("Błąd danych wejściowych");
                }

            } catch (IOException | NumberFormatException e) {
                System.out.println("Błąd klienta: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {}
            }
        }

        private String formatSimpleTicket(Ticket t) {
            StringBuilder sb = new StringBuilder();
            sb.append("=========== BILET PARKING ===========\n");
            sb.append(String.format("ID:            %s\n", t.getId()));
            sb.append(String.format("Rejestracja:   %s\n", t.getReg()));
            sb.append(String.format("Czas postoju:  %d h\n", t.getHours()));
            sb.append(String.format("Stawka:        %.2f PLN\n", t.getRate()));
            sb.append(String.format("Cena razem:    %.2f PLN\n", t.getTotal()));
            sb.append(String.format("Wystawiono:    %s\n", t.getIssuedAt()));
            sb.append(String.format("Wygasa:        %s\n", t.getExpiresAt()));
            sb.append("=====================================\n");
            return sb.toString();
        }
    }
}
