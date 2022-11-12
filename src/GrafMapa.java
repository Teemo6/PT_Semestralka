import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Instance třídy {@code GrafMapa} představuje neorientovaný matematický graf
 * @author Štěpán Faragula, Mikuláš Mach
 * @version 1.21 12-11-2022
 */
public class GrafMapa {
    Map<AMisto, LinkedList<GrafVrchol>> seznamSousednosti;

    /**
     * Konstruktor
     */
    public GrafMapa(){
        seznamSousednosti = new HashMap<>();
    }

    /**
     * Přidá vrchol do grafu
     * @param cesta hrana grafu
     */
    public void pridejVrchol(Cesta cesta){
        AMisto mistoA = cesta.getZacatek();
        AMisto mistoB = cesta.getKonec();
        double vzdalenost = cesta.getVzdalenost();

        seznamSousednosti.putIfAbsent(mistoA, new LinkedList<>());
        seznamSousednosti.putIfAbsent(mistoB, new LinkedList<>());

        seznamSousednosti.get(mistoA).add(new GrafVrchol(mistoB, vzdalenost));
        seznamSousednosti.get(mistoB).add(new GrafVrchol(mistoA, vzdalenost));
    }

    /**
     * Vypíše obsah grafu do konzole
     */
    public void printGraf() {
        for (AMisto key: seznamSousednosti.keySet()) {
            System.out.print(key.getID() + " -> ");
            for(GrafVrchol v : seznamSousednosti.get(key)){
                System.out.print(v.getVrchol().getID() + " ");
            }
            System.out.println();
        }
    }
}
