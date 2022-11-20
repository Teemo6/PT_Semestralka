/**
 * Semestrální práce PT
 * @author Štěpán Faragula, Mikuláš Mach
 * @version 1.28 20-11-2022
 */
public class Main {
    // Testovací spuštění
    private static final String VSTUP_SOUBORU = "data2/weird_small.txt";
    private static final Simulace SIMULACE = Simulace.getInstance();

    /**
     * Načte data a spustí simulaci
     * @param args vstupní data
     */
    public static void main(String[] args) {
        SIMULACE.spustSimulaci(VSTUP_SOUBORU);
    }
}
