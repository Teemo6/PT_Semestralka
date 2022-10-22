import java.util.ArrayList;
import java.util.Random;

/**
 * Instance třídy {@code Simulace} představuje jedináčka ve kterém běží celá simulace
 * @author Mikuláš Mach, Štěpán Faragula
 * @version 1.12 21-10-2022
 */
public class Simulace {
    /** Instance jedináčka Simulace */
    private static final Simulace INSTANCE = new Simulace();
    private final Random random = new Random();

    /** Atributy */
    private VstupDat data;

    /** Matice se kterými pracuje simulace */
    IMaticeSymetricka distancniMatice;
    MaticeInteger maticePredchudcu;

    /**
     * Vrátí jedináčka
     * @return instance jedináčka
     */
    public static Simulace getInstance(){
        return INSTANCE;
    }

    /**
     * Spustí simulaci
     * @param data Vstupní data
     */
    public void spustSimulaci(VstupDat data){
        this.data = data;

        vytvorPotrebneMatice();
        System.out.println("Matice nacteny");

        VelbloudSimulace vel = vytvorVelblouda(data.getVelbloudi().get(0));
        AMisto zacatek = data.getMista().get(3);
        AMisto konec = data.getMista().get(2);
        ArrayList<Cesta> cesta = najdiNejkratsiCestu(zacatek, konec);
        zacatek.pridejVelblouda(vel);

        System.out.println("----------------");
        System.out.println(zacatek);
        System.out.println(konec);

        for(Cesta c : cesta){
            vysliVelbloudaNaCestu(vel, c);
        }

        System.out.println("----------------");
        System.out.println(zacatek);
        System.out.println(konec);
    }

    /**
     * Vytvoří a vrátí distanční matici
     * Využití Floyd-Warshall algoritmu
     */
    public void vytvorPotrebneMatice(){
        distancniMatice = new MaticeCtvercova(data.getMista().size());
        distancniMatice.vyplnNekonecnem();

        for (Cesta cesta : data.getCesty()) {
            distancniMatice.setCisloSymetricky(cesta.getMistoA().getID() - 1, cesta.getMistoB().getID() - 1, cesta.getVzdalenost());
        }
        int velikostMatice = distancniMatice.getVelikost();

        maticePredchudcu = new MaticeInteger(velikostMatice);
        maticePredchudcu.vyplnNekonecnem();

        // matice předchůdců
        for (int i = 0; i < velikostMatice; i++) {
            for (int j = 0; j < velikostMatice; j++) {
                if (distancniMatice.getCislo(i, j) != Double.MAX_VALUE){
                    maticePredchudcu.setCislo(i, j, i + 1);
                }
            }
        }

        // Potřeba optimalizovat
        // sparse_bit_large, MaticeCtvercova -> 130283 ms -> 2.1 minuty
        // sparse_bit_large, MaticeTrojuhelnikova -> 415351 ms -> 6,9 minut :(

        // Po přidání výpočtu matice předchůdců
        // sparse_bit_large, MaticeCtvercova ->  792778 -> 13.2 minut
        // sparse_bit_large, MaticeTrojuhelnikova -> 1645730 -> 27.4 minut xd

        // Distancni matice
        for(int k = 0; k < velikostMatice; k++){
            for(int i = 0; i < velikostMatice; i++){
                for(int j = 0; j < velikostMatice; j++){
                    double hodnotaCesty = distancniMatice.getCislo(i, k) + distancniMatice.getCislo(k, j);
                    if((distancniMatice.getCislo(i, j) > hodnotaCesty)){
                        distancniMatice.setCislo(i, j, hodnotaCesty);
                        maticePredchudcu.setCislo(i, j, k + 1);
                    }
                }
            }
        }
        maticePredchudcu.vyplnNulyNaDiagonalu();

        /*
        System.out.println("\n----------\nDistancni matice:");
        distancniMatice.printMatice();
        System.out.println("\n----------\nMatice predchudcu:");
        maticePredchudcu.printMatice();
         */

    }

    /**
     * Vrátí sezman všech mezicest z bodu A do bodu B
     * @param zacatek začínající bod grafu
     * @param konec konečný bod grafu
     * @return seznam mezicest A, B
     */
    public ArrayList<Cesta> najdiNejkratsiCestu(AMisto zacatek, AMisto konec){
        ArrayList<Cesta> nejkratsiCesta = new ArrayList<>();

        int IDmisto;
        AMisto predcudce;

        while(true) {
            IDmisto = maticePredchudcu.getCislo(konec.getID() - 1, zacatek.getID() - 1);
            if(IDmisto == Integer.MAX_VALUE){
                break;
            }
            predcudce = data.getMista().get(IDmisto);
            nejkratsiCesta.add(new Cesta(zacatek, predcudce));
            if (predcudce == konec) {
                break;
            }
            zacatek = predcudce;
        }
        return nejkratsiCesta;
    }

    /**
     * Vytvoří konkrétního velblouda
     * @param typ typ velblouda
     * @return entita
     */
    public VelbloudSimulace vytvorVelblouda(VelbloudTyp typ){
        double rychlost = random.nextDouble() * (typ.getMaxRychlost() - typ.getMinRychlost()) + typ.getMinRychlost();

        double stredniHodnota = (typ.getMinVzdalenost() + typ.getMaxVzdalenost()) / 2;          // podle zadání
        double smerodatnaOdchylka = (typ.getMaxVzdalenost() - typ.getMinVzdalenost()) / 4;      // podle zadání
        double vzdalenost = random.nextGaussian() * smerodatnaOdchylka + stredniHodnota;

        return new VelbloudSimulace(rychlost, vzdalenost, typ.getCasNapit(), typ.getMaxKose());
    }

    /**
     * Přemístí velblouda
     * @param velbloud jaký velbloud se má přemístit
     * @param cesta cesta kam jde
     */
    public void vysliVelbloudaNaCestu(VelbloudSimulace velbloud, Cesta cesta){
        // Vzdálenost se vypočítá když se zrovna potřebuje
        // Distanční matice je zbytečná??
        double vzdalenost = cesta.getVzdalenost();

        velbloud.setEnergie(velbloud.getEnergie() - vzdalenost);
        cesta.getMistoA().odeberVelblouda(velbloud);
        cesta.getMistoB().pridejVelblouda(velbloud);
    }
}
