import java.util.List;

/**
 * Instance třídy {@code Simulace} představuje jedináčka ve kterém běží celá simulace
 * @author Mikuláš Mach, Štěpán Faragula
 * @version 1.10 18-10-2022
 */
public class Simulace {
    /** Instance jedináčka Simulace */
    private static final Simulace INSTANCE = new Simulace();

    /** Atributy */
    private VstupDat vstupDat;
    private List<VelbloudSimulace> velbloudiSimulace;

    /**
     * @return instance jedináčka
     */
    public static Simulace getInstance(){
        return INSTANCE;
    }

    /**
     * Spustí simulaci
     * @param vstupDat Vstupní data
     */
    public void spustSimulaci(VstupDat vstupDat){
        this.vstupDat = vstupDat;
        vytvorDistancniMatici();
    }

    /**
     * Vytvoří a vrátí distanční matici
     * Využití Floyd-Warshall algoritmu
     * @return distanční matice
     */
    public IMaticeSymetricka vytvorDistancniMatici(){
        IMaticeSymetricka distancniMatice = vstupDat.getMaticeSousednosti();
        //distancniMatice.printMatice();
/*
        distancniMatice = new MaticeCtvercova(9);
        distancniMatice.vyplnNekonecnem();
        distancniMatice.vyplnNulyNaDiagonalu();

        distancniMatice.setCislo(0, 1, 1);
        distancniMatice.setCislo(0, 2, 2);
        distancniMatice.setCislo(0, 3, 3);
        distancniMatice.setCislo(0, 4, 4);
        distancniMatice.setCislo(0, 5, 5);
        distancniMatice.setCislo(0, 6, 6);
        distancniMatice.setCislo(0, 7, 7);
        distancniMatice.setCislo(0, 8, 8);
*/
        // Potřeba optimalizovat
        // sparse_bit_large, MaticeTrojuhelnikova -> 415351 ms -> 6,9 minut :(
        // sparse_bit_large, MaticeCtvercova -> 130283 ms -> 2.1 minuty

        int velikostMatice = distancniMatice.getVelikost();

        MaticeInteger maticePredchudcu = new MaticeInteger(velikostMatice);
        maticePredchudcu.vyplnNekonecnem();

        // TODO zatim to nic nedělá :(
        for (int i = 0; i < velikostMatice; i++) {
            for (int j = 0; j < velikostMatice; j++) {
                if (distancniMatice.getCislo(i, j) != Double.MAX_VALUE){
                    maticePredchudcu.setCislo(i, j, i);
                }
            }
        }
        maticePredchudcu.printMatice();

        for(int k = 0; k < velikostMatice; k++){
            for(int i = 0; i < velikostMatice; i++){
                for(int j = 0; j < velikostMatice; j++){
                    double cesta = distancniMatice.getCislo(i, k) + distancniMatice.getCislo(k, j);
                    if(distancniMatice.getCislo(i, j) > cesta){
                        distancniMatice.setCislo(i, j, cesta);
                    }
                }
            }
        }
        System.out.println("\n----------\n");
        maticePredchudcu.printMatice();
        System.out.println("\n----------\n");
        return distancniMatice;
    }
}
