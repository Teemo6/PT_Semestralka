import java.util.ArrayList;

/**
 * Instance třídy {@code VelbloudPozadavek} představuje požadavek,
 * který je předán velbloudovi ke splnění
 * @author Mikuláš Mach, Štěpán Faragula
 * @version 1.19 02-11-2022
 */
public class VelbloudPozadavek {
    private Pozadavek pozadavek;
    private ArrayList<Cesta> cestaPoCastech;

    private double celkovaVzdalenostCesty;
    private int splnenoKosu;
    private final int pocetPotrebnychKosu;

    public VelbloudPozadavek(Pozadavek po, ArrayList<Cesta> ce){
        pozadavek = po;
        cestaPoCastech = ce;

        celkovaVzdalenostCesty = 0;
        for(Cesta c : ce){
            celkovaVzdalenostCesty += c.getVzdalenost();
        }
        splnenoKosu = 0;
        pocetPotrebnychKosu = po.getPozadavekKosu();
    }

    /**
     *  Zkontroluje zda byl dany pozadavek velbloudem splneni
     *  @return true: pokud byl splnen
     */
    public boolean zkontrolujSplnenyPozadavek(){
        if(pozadavek.getPozadavekKosu() <= splnenoKosu){
            pozadavek.setJeSplnen(true);
            return true;
        }
        return false;
    }

    public Pozadavek getPozadavek() {
        return pozadavek;
    }

    public double getCelkovaVzdalenostCesty() {
        return celkovaVzdalenostCesty;
    }

    public void setPozadavek(Pozadavek pozadavek) {
        this.pozadavek = pozadavek;
    }

    public ArrayList<Cesta> getCestaPoCastech() {
        return cestaPoCastech;
    }

    public void setCestaPoCastech(ArrayList<Cesta> cestaPoCastech) {
        this.cestaPoCastech = cestaPoCastech;
    }

    public void doruceneKose(int pocetDorucenychKosu){
        splnenoKosu += pocetDorucenychKosu;
    }

    public int getPocetPotrebnychKosu(){return pocetPotrebnychKosu;}
}
