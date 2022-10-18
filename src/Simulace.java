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
     * Spustí simulace
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
        distancniMatice.setCisloNaSouradnice(0, 1, 1);
        distancniMatice.setCisloNaSouradnice(0, 2, 2);
        distancniMatice.setCisloNaSouradnice(0, 3, 3);
        distancniMatice.setCisloNaSouradnice(0, 4, 4);
        distancniMatice.setCisloNaSouradnice(0, 5, 5);
        distancniMatice.setCisloNaSouradnice(0, 6, 6);
        distancniMatice.setCisloNaSouradnice(0, 7, 7);
        distancniMatice.setCisloNaSouradnice(0, 8, 8);

        distancniMatice.setCisloNaSouradnice(1, 0, 1);
        distancniMatice.setCisloNaSouradnice(2, 0, 2);
        distancniMatice.setCisloNaSouradnice(3, 0, 3);
        distancniMatice.setCisloNaSouradnice(4, 0, 4);
        distancniMatice.setCisloNaSouradnice(5, 0, 5);
        distancniMatice.setCisloNaSouradnice(6, 0, 6);
        distancniMatice.setCisloNaSouradnice(7, 0, 7);
        distancniMatice.setCisloNaSouradnice(8, 0, 8);
*/

        for(int k = 0; k < distancniMatice.getVelikostX(); k++){
            for(int i = 0; i < distancniMatice.getVelikostX(); i++){
                for(int j = 0; j < distancniMatice.getVelikostX(); j++){
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
