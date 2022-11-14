/**
 * Semestrální práce PT
 * @author Štěpán Faragula, Mikuláš Mach
 * @version 1.12 21-10-2022
 */
public class Main {
    // Testovací spuštění
    private static final String VSTUP_SOUBORU = "data/tutorial - kopie.txt";

    private static final VstupDat VSTUP_DAT = VstupDat.getInstance();
    private static final Simulace SIMULACE = Simulace.getInstance();

    /**
     * Načte data a spustí simulaci
     * @param args vstupní data
     */
    public static void main(String[] args) {
        long a = System.currentTimeMillis();

        //VSTUP_DAT.nactiData(args[0]);
        VSTUP_DAT.vytvorObjekty(VSTUP_SOUBORU);
        System.out.println("\nData nactena v case: " + (System.currentTimeMillis() - a) + " ms.\n");

        SIMULACE.spustSimulaci(VSTUP_DAT);
        System.out.println("\nRuntime: " + (System.currentTimeMillis() - a) + " ms.");
    }
}
