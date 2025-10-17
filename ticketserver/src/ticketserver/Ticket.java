package ticketserver;

public class Ticket {
    private static int counter = 1;
    private final int id;
    public final String reg;
    public final int hours;
    public final double rate;
    public final double total;

    public Ticket(String reg, int hours, double rate) {
        this.id = counter++;
        this.reg = reg;
        this.hours = hours;
        this.rate = rate;
        this.total = hours * rate;
    }

    @Override
    public String toString() {
        return String.format("ID:%d Rej:%s Godz:%d Stawka:%.2f PLN Suma:%.2f PLN", id, reg, hours, rate, total);
    }
}
