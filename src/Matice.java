/**
 * Instance třídy {@code Matice} představuje jedináčka, která obsahuje matici sousednosti
 * @author Mikuláš Mach
 * @version 1.00 17-10-2022
 */
public class Matice {

    private static final Matice INSTANCE = new Matice();

    private static double[][] maticeSousednosti;

    public void createMaticeSousednosti(int velikost){
        maticeSousednosti = new double[velikost][velikost];
    }

    public void addToMaticeSousednosti(AMisto zacatek, int indexZacatku, AMisto konec, int indexKonce){

        double x1 = zacatek.getPozice().getX();
        double y1 = zacatek.getPozice().getY();
        double x2 = konec.getPozice().getX();
        double y2 = konec.getPozice().getY();

        double vzdalenost = Math.sqrt( ((x2 - x1) * (x2 - x1) ) + ( (y2 - y1) * (y2 - y1)));
        maticeSousednosti[indexZacatku][indexKonce] = vzdalenost;
        maticeSousednosti[indexKonce][indexZacatku] = vzdalenost;
    }

    public static Matice getInstance(){
        return INSTANCE;
    }

    public double[][] getMaticeSousednosti() {
        return maticeSousednosti;
    }

}
