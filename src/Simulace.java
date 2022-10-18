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
    public Matice vytvorDistancniMatici(){
        Matice distancniMatice = vstupDat.getMaticeSousednosti();
/*
        distancniMatice = new Matice(9);
        distancniMatice.vyplnNekonecnem();

        distancniMatice.setCisloXY(0, 1, 1);
        distancniMatice.setCisloXY(0, 2, 2);
        distancniMatice.setCisloXY(0, 3, 3);
        distancniMatice.setCisloXY(0, 4, 4);
        distancniMatice.setCisloXY(0, 5, 5);
        distancniMatice.setCisloXY(0, 6, 6);
        distancniMatice.setCisloXY(0, 7, 7);
        distancniMatice.setCisloXY(0, 8, 8);

        distancniMatice.setCisloXY(1, 0, 1);
        distancniMatice.setCisloXY(2, 0, 2);
        distancniMatice.setCisloXY(3, 0, 3);
        distancniMatice.setCisloXY(4, 0, 4);
        distancniMatice.setCisloXY(5, 0, 5);
        distancniMatice.setCisloXY(6, 0, 6);
        distancniMatice.setCisloXY(7, 0, 7);
        distancniMatice.setCisloXY(8, 0, 8);
*/
        // Potřeba optimalizovat
        // sparse_bit_large, MaticeSymetricka -> 415351 ms -> 6,9 minut :(
        // sparse_bit_large, Matice -> 103746 ms -> 1,7 minut

        for(int k = 0; k < distancniMatice.getVelikostX(); k++){
            for(int i = 0; i < distancniMatice.getVelikostX(); i++){
                for(int j = i; j < distancniMatice.getVelikostX(); j++){
                    if(i == j){
                        distancniMatice.setCisloXY(i, j, 0);
                        continue;
                    }
                    if(distancniMatice.getCisloXY(i, j) > distancniMatice.getCisloXY(i, k) + distancniMatice.getCisloXY(k ,j)){
                        double velikost = distancniMatice.getCisloXY(i, k) + distancniMatice.getCisloXY(k, j);
                        distancniMatice.setCisloXY(i, j, velikost);
                    }
                }
            }
        }
        return distancniMatice;
    }
}
