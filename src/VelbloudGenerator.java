import java.util.*;

public class VelbloudGenerator {
    private Map<VelbloudTyp, Integer> pocetVelbloudu;
    private VelbloudTyp nejvetsiPomer;
    private double nejvetsiMinVzdalenost;
    private int celkovyPocetVelbloudu;

    /** Instance jedináčka Simulace */
    private static final VelbloudGenerator INSTANCE = new VelbloudGenerator();

    /**
     * Vrátí jedináčka
     * @return instance jedináčka
     */
    public static VelbloudGenerator getInstance(){
        return INSTANCE;
    }

    /**
     * Konstruktor
     * @param typ seznam typu velbloudu
     */
    public void vytvorGenerator(List<VelbloudTyp> typ){
        pocetVelbloudu = new HashMap<>();
        nejvetsiPomer = null;
        nejvetsiMinVzdalenost = 0;

        for(VelbloudTyp vel : typ) {
            pocetVelbloudu.put(vel, 0);

            if(nejvetsiPomer == null){
                nejvetsiPomer = vel;
            }

            if(nejvetsiPomer.getPomer() < vel.getPomer()){
                nejvetsiPomer = vel;
            }

            if(nejvetsiMinVzdalenost < vel.getMinVzdalenost()){
                nejvetsiMinVzdalenost = vel.getMinVzdalenost();
            }
        }
    }

    /**
     * Vrátí jaký by se měl dále generovat velbloud
     * @return typ velblouda který bude generován jako další
     */
    public VelbloudTyp dalsiVelbloudPodlePomeru(){
        VelbloudTyp dalsiVelbloud = nejvetsiPomer;

        for (Map.Entry<VelbloudTyp, Integer> vel : pocetVelbloudu.entrySet()) {
            VelbloudTyp kandidat = vel.getKey();
            double skutecnyPomer = (double)vel.getValue() / celkovyPocetVelbloudu;

            if(skutecnyPomer < kandidat.getPomer()){
                dalsiVelbloud = kandidat;
            }
        }
        return dalsiVelbloud;
    }

    /**
     * Vytvoří vhodného velblouda podle poměru na pozici skladu
     * @param domaciSklad domácí sklad
     * @return velbloud v simulaci
     */
    public VelbloudSimulace generujVelblouda(Sklad domaciSklad){
        Random random = new Random();
        VelbloudTyp typ = dalsiVelbloudPodlePomeru();
        double rychlost = random.nextDouble() * (typ.getMaxRychlost() - typ.getMinRychlost()) + typ.getMinRychlost();

        double stredniHodnota = (typ.getMinVzdalenost() + typ.getMaxVzdalenost()) / 2;          // podle zadání
        double smerodatnaOdchylka = (typ.getMaxVzdalenost() - typ.getMinVzdalenost()) / 4;      // podle zadání
        double vzdalenost = random.nextGaussian() * smerodatnaOdchylka + stredniHodnota;

        pridejVelblouda(typ);

        return new VelbloudSimulace(domaciSklad, rychlost, vzdalenost, typ);
    }

    public double getNejvetsiMinVzdalenost() {
        return nejvetsiMinVzdalenost;
    }

    //////////////////////
    //* Private metody *//
    //////////////////////

    /**
     * Přidá jednoho velblouda do konečného počtu
     * @param typ jaký typ se má přidat
     */
    private void pridejVelblouda(VelbloudTyp typ){
        int pocet = pocetVelbloudu.get(typ);
        pocetVelbloudu.put(typ, pocet + 1);
        celkovyPocetVelbloudu++;
    }
}
