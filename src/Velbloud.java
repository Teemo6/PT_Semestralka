public class Velbloud {
    private String nazev;
    private int minRychlost;
    private int maxRychlost;
    private int minVzdalenost;
    private int maxVzdalenost;
    private int casNapit;
    private int maxKose;
    private double pomer;

    public Velbloud(String nazev, int minRychlost, int maxRychlost, int minVzdalenost, int maxVzdalenost, int casNapit, int maxKose, double pomer){
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

    public int getMinRychlost() {
        return minRychlost;
    }

    public int getMaxRychlost() {
        return maxRychlost;
    }

    public int getMinVzdalenost() {
        return minVzdalenost;
    }

    public int getMaxVzdalenost() {
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
