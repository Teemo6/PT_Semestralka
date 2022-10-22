public class VelbloudSimulace{
    private double rychlost;
    private double maxVzdalenost;
    private double dobaPiti;
    private int maxPocetKosu;

    private double energie;
    private int pocetKosu;


    private static int pocet;
    private int ID;

    public VelbloudSimulace(double rychlost, double maxVzdalenost, double dobaPiti, int maxPocetKosu){
        this.rychlost = rychlost;
        this.maxVzdalenost = maxVzdalenost;
        this.dobaPiti = dobaPiti;
        this.maxPocetKosu = maxPocetKosu;

        this.energie = maxVzdalenost;
        this.pocetKosu = 0;

        pocet++;
        this.ID = pocet;
    }

    public void setEnergie(double energie){
        this.energie = energie;
    }

    public double getRychlost() {
        return rychlost;
    }

    public double getMaxVzdalenost() {
        return maxVzdalenost;
    }

    public double getDobaPiti() {
        return dobaPiti;
    }

    public int getMaxPocetKosu() {
        return maxPocetKosu;
    }

    public double getEnergie() {
        return energie;
    }

    public int getPocetKosu() {
        return pocetKosu;
    }

    public static int getPocet() {
        return pocet;
    }

    public int getID() {
        return ID;
    }

    @Override
    public String toString() {
        return "VelbloudSimulace{" +
                "ID=" + ID +
                ", rychlost=" + rychlost +
                ", maxVzdalenost=" + maxVzdalenost +
                ", dobaPiti=" + dobaPiti +
                ", maxPocetKosu=" + maxPocetKosu +
                ", energie=" + energie +
                ", pocetKosu=" + pocetKosu +
                '}';
    }
}
