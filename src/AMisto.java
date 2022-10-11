public abstract class AMisto {
    protected DoubleVector2D pozice;

    public DoubleVector2D getPozice(){
        return this.pozice;
    }

    @Override
    public String toString() {
        return "AMisto{" +
                "pozice=" + pozice +
                '}';
    }
}
