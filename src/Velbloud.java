public class Velbloud {
    private String nazev;
    private double minRychlost;
    private double maxRychlost;
    private double minVzdalenost;
    private double maxVzdalenost;
    private int casNapit;
    private int maxKose;
    private double pomer;

    public Velbloud(String nazev, double minRychlost, double maxRychlost, double minVzdalenost, double maxVzdalenost, int casNapit, int maxKose, double pomer){
        this.nazev = nazev;
        this.minRychlost = minRychlost;
        this.maxRychlost = maxRychlost;
        this.minVzdalenost = minVzdalenost;
        this.maxVzdalenost = maxVzdalenost;
        this.casNapit = casNapit;
        this.maxKose = maxKose;
        this.pomer = pomer;
    }

    public String getNazev() {
        return nazev;
    }

    public double getMinRychlost() {
        return minRychlost;
    }

    public double getMaxRychlost() {
        return maxRychlost;
    }

    public double getMinVzdalenost() {
        return minVzdalenost;
    }

    public double getMaxVzdalenost() {
        return maxVzdalenost;
    }

    public int getCasNapit() {
        return casNapit;
    }

    public int getMaxKose() {
        return maxKose;
    }

    public double getPomer() {
        return pomer;
    }

    @Override
    public String toString() {
        return "Velbloud{" +
                "nazev='" + nazev + '\'' +
                ", minRychlost=" + minRychlost +
                ", maxRychlost=" + maxRychlost +
                ", minVzdalenost=" + minVzdalenost +
                ", maxVzdalenost=" + maxVzdalenost +
                ", casNapit=" + casNapit +
                ", maxKose=" + maxKose +
                ", pomer=" + pomer +
                '}';
    }
}
