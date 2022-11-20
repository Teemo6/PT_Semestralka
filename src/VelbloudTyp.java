/**
 * Instance třídy {@code VelbloudTyp} představuje druh velblouda
 * @author Štěpán Faragula, Mikuláš Mach
 * @version 1.28 20-11-2022
 */
public class VelbloudTyp {
    private final String nazev;
    private final double minRychlost;
    private final double maxRychlost;
    private final double minVzdalenost;
    private final double maxVzdalenost;
    private final double vetsiPrumerVzdalenost;
    private final double casNapit;
    private final int maxKose;
    private final double pomer;

    /**
     * Konstruktor
     * @param nazev název druhu
     * @param minRychlost minimální hodnota rozsahu rychlosti
     * @param maxRychlost maximální hodnota rozsahu rychlosti
     * @param minVzdalenost minimální hodnota rozsahu vzdálenosti
     * @param maxVzdalenost maximální hodnota rozsahu vzdálenosti
     * @param casNapit doba pití
     * @param maxKose maximální naložení košů
     * @param pomer poměrné zastoupení v simulaci
     */
    public VelbloudTyp(String nazev, double minRychlost, double maxRychlost, double minVzdalenost, double maxVzdalenost, double casNapit, int maxKose, double pomer){
        this.nazev = nazev;
        this.minRychlost = minRychlost;
        this.maxRychlost = maxRychlost;
        this.minVzdalenost = minVzdalenost;
        this.maxVzdalenost = maxVzdalenost;
        this.casNapit = casNapit;
        this.maxKose = maxKose;
        this.pomer = pomer;

        double prumernaVzdalenost = (minVzdalenost + maxVzdalenost) / 2;
        this.vetsiPrumerVzdalenost = (prumernaVzdalenost + maxVzdalenost) / 2;
    }
    /**
     * Vrátí název druhu
     * @return název druhu
     */
    public String getNazev() {
        return nazev;
    }

    /**
     * Vrátí minimální hodnotu rozsahu rychlost
     * @return minimální hodnota rozsahu rychlosti
     */
    public double getMinRychlost() {
        return minRychlost;
    }

    /**
     * Vrátí maximální hodnotu rozsahu rychlost
     * @return maximální hodnota rozsahu rychlosti
     */
    public double getMaxRychlost() {
        return maxRychlost;
    }

    /**
     * Vrátí minimální hodnotu rozsahu vzdálenosti
     * @return maximální minimální hodnota rozsahu vzdálenosti
     */
    public double getMinVzdalenost() {
        return minVzdalenost;
    }

    /**
     * Vrátí maximální hodnotu rozsahu vzdálenosti
     * @return maximální maximální hodnota rozsahu vzdálenosti
     */
    public double getMaxVzdalenost() {
        return maxVzdalenost;
    }

    /**
     * Vrátí dobu pití druhu
     * @return doba pití druhu
     */
    public double getCasNapit() {
        return casNapit;
    }

    /**
     * Vrátí maximální naložení košů
     * @return maximální naložení košů
     */
    public int getMaxKose() {
        return maxKose;
    }

    /**
     * Vrátí poměrné zastoupení v simulaci
     * @return poměrné zastoupení v simulaci
     */
    public double getPomer() {
        return pomer;
    }

    /**
     * Vrátí hodnotu vzdálenosti se kterou se bude většina velbloudů generovat
     * @return hodnota vzdálenosti se kterou se bude většina velbloudů generovat
     */
    public double getVetsiPrumerVzdalenost() {
        return vetsiPrumerVzdalenost;
    }

    /**
     * toString
     * @return string s atributy druhu velblouda
     */
    @Override
    public String toString() {
        return "typ = " + nazev;
    }
}
