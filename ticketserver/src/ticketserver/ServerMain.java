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

                if (parts.length == 2 && parts[0].equalsIgnoreCase("SHOW")) {
                    String reg = parts[1].trim().toUpperCase();
                    List<Ticket> found = new ArrayList<>();
                    synchronized (tickets) {
                        for (Ticket t : tickets) {
                            if (t.getReg().equals(reg)) {
                                found.add(t);
                            }
                        }
                    }
                    if (found.isEmpty()) {
                        out.println("Brak biletów dla rejestracji: " + reg);
                    } else {
                        for (Ticket t : found) {
                            out.println(t);
                            out.println("--------------------------------------------------------");
                        }
                    }
                    return;
                }

                if (parts.length == 2) {
                    String reg = parts[0].trim().toUpperCase();
                    double amount = Double.parseDouble(parts[1].trim());

                    if (amount <= 0) {
                        out.println("Błąd: kwota musi być większa od zera!");
                        return;
                    }

                    int hours = (int) Math.floor(amount / RATE);
                    double change = amount - (hours * RATE);

                    if (hours == 0) {
                        out.println("Kwota jest zbyt mała na pełną godzinę postoju!");
                        if (change > 0) {
                            out.printf("Reszta do wydania: %.2f PLN\n", change);
                        }
                        return;
                    }

                    Ticket ticket = new Ticket(reg, hours, RATE);
                    tickets.add(ticket);

                    out.println(formatSimpleTicket(ticket));
                    if (change > 0) {
                        out.printf("Reszta do wydania: %.2f PLN\n", change);
                    }

                    System.out.println("Wystawiono bilet: " + reg + " (" + hours + "h), reszta: " + change);

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
