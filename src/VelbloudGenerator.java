import java.util.*;

public class VelbloudGenerator {
    private final NavigableMap<VelbloudTyp, Integer> pocetVelbloudu;
    private int celkovyPocetVelbloudu;

    /**
     * Konstruktor
     * @param typ seznam typu velbloudu
     */
    public VelbloudGenerator(List<VelbloudTyp> typ){
        // TODO remove treemap, jdu spinkat :|
        pocetVelbloudu = new TreeMap<>(Comparator.comparingDouble(VelbloudTyp::getPomer));

        for(VelbloudTyp vel : typ) {
            System.out.println(vel);
            pocetVelbloudu.putIfAbsent(vel, 0);
        }

        System.out.println("ahoj");
    }

    /**
     * Vrátí jaký by se měl dále generovat velbloud
     * @return typ velblouda který bude generován jako další
     */
    public VelbloudTyp dalsiVelbloudPodlePomeru(){
        VelbloudTyp dalsiVelbloud = pocetVelbloudu.lastKey();
        VelbloudTyp kandidat1;

        for (Map.Entry<VelbloudTyp, Integer> vel : pocetVelbloudu.entrySet()) {
            kandidat1 = vel.getKey();
            double skutecnyPomer = (double)vel.getValue() / celkovyPocetVelbloudu;

            if(skutecnyPomer < kandidat1.getPomer()){
                dalsiVelbloud = kandidat1;
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
