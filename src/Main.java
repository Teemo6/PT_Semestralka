public class Main {
    private static final String VSTUP_SOUBORU = "data/noComment.txt";

    private static final VstupDat VSTUP_DAT = VstupDat.getInstance();

    public static void main(String[] args) {
        VSTUP_DAT.nactiData(VSTUP_SOUBORU);
    }
}
