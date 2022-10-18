import java.util.List;

public class Simulace {
    /** Instance jedináčka Simulace */
    private static final Simulace INSTANCE = new Simulace();

    private VstupDat vstupDat;
    private List<VelbloudSimulace> velbloudiSimulace;

    /**
     * @return instance jedináčka
     */
    public static Simulace getInstance(){
        return INSTANCE;
    }

    public void spustSimulaci(VstupDat vstupDat){
        this.vstupDat = vstupDat;
    }

    public Matice vytvorDistancniMatici(){
        Matice distancniMatice = vstupDat.getMaticeSousednosti();
        List<AMisto> vrchol = vstupDat.getMisto();
        List<Cesta> cesta = vstupDat.getCesty();

        distancniMatice.vyplnNekonecnem();
        vrchol.forEach(v->{

        });
        return new Matice(1);
    }
}
