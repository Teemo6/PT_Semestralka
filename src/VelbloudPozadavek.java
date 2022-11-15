/**
 * Instance třídy {@code VelbloudPozadavek} představuje požadavek,
 * který je předán velbloudovi do fronty ke splnění
 * @author Mikuláš Mach, Štěpán Faragula
 * @version 1.23 15-11-2022
 */
public class VelbloudPozadavek {
    private final Pozadavek pozadavek;
    //private final List<Cesta> cestaPoCastech;
    private CestaCasti cestaCasti;

    private double celkovaVzdalenostCesty;
    private int splnenoKosu;
    private final int pocetPotrebnychKosu;

    /**
     * Konstruktor
     * @param pozadavek požadavek na obsloužení
     * @param cestaCasti cesta ze skladu do oázy
     */
    public VelbloudPozadavek(Pozadavek pozadavek, CestaCasti cestaCasti){
        this.pozadavek = pozadavek;
        this.cestaCasti = cestaCasti;

        celkovaVzdalenostCesty = cestaCasti.getVzdalenost();

        splnenoKosu = 0;
        pocetPotrebnychKosu = pozadavek.getPozadavekKosu();
    }

    /**
     * Přidá koše k počtu doručených košů
     * @param pocetDorucenychKosu kolik košů se má přidat
     */
    public void doruceneKose(int pocetDorucenychKosu){
        splnenoKosu += pocetDorucenychKosu;
    }

    /**
     * Vrátí počet již doručeních košů
     * @return pocetDorucenychKosu počet doručených košů
     */
    public int getPocetPotrebnychKosu(){
        return pocetPotrebnychKosu;
    }

    /**
     *  Zkontroluje zda byl dany pozadavek velbloudem splneni
     *  @return true: pokud byl splnen
     */
    public boolean zkontrolujSplnenyPozadavek(){
        if(pozadavek.getPozadavekKosu() <= splnenoKosu){
            pozadavek.setSplnen();
            return true;
        }
        return false;
    }

    /**
     * Vrátí požadavek
     * @return požadavek
     */
    public Pozadavek getPozadavek() {
        return pozadavek;
    }

    /**
     * Vrátí vzdálenost cesty
     * @return vzdálenost cesty
     */
    public double getCelkovaVzdalenostCesty() {
        return celkovaVzdalenostCesty;
    }

    /**
     * Vrátí celou cestu jako pole instancí {@code Cesta}
     * @return pole instancí cest
     */
    public CestaCasti getCestaCasti() {
        return cestaCasti;
    }
}
