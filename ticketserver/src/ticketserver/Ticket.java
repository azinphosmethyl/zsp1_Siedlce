package ticketserver;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Ticket implements Serializable {
    private final String id;
    private final String reg;
    private final int hours;
    private final double rate;
    private final double total;
    private final String issuedAt;
    private final String expiresAt;

    public Ticket(String reg, int hours, double rate) {
        this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.reg = reg.toUpperCase();
        this.hours = hours;
        this.rate = rate;
        this.total = hours * rate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expire = now.plusHours(hours);
        this.issuedAt = now.format(formatter);
        this.expiresAt = expire.format(formatter);
    }

    public String getId() { return id; }
    public String getReg() { return reg; }
    public int getHours() { return hours; }
    public double getRate() { return rate; }
    public double getTotal() { return total; }
    public String getIssuedAt() { return issuedAt; }
    public String getExpiresAt() { return expiresAt; }

    public boolean isActive() {
        return LocalDateTime.now().isBefore(LocalDateTime.parse(expiresAt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    @Override
    public String toString() {
        return String.format(
            "=========== BILET PARKING ===========\n" +
            "ID:            %s\n" +
            "Rejestracja:   %s\n" +
            "Czas postoju:  %d h\n" +
            "Stawka:        %.2f PLN\n" +
            "Cena razem:    %.2f PLN\n" +
            "Wystawiono:    %s\n" +
            "Wygasa:        %s\n" +
            "=====================================\n",
            id, reg, hours, rate, total, issuedAt, expiresAt
        );
    }
}
