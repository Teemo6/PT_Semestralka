/**
 *  Stavy velblouda
 *  {@link #VOLNY}
 *  {@link #NAKLADANI}
 *  {@link #CESTA}
 *  {@link #CESTAZPATKY}
 *  {@link #VYKLADANI}
 */
public enum VelbloudAkce {
    /**
     * Velbloud zrovna nic nedělá
     */
    VOLNY,

    /**
     * Velbloud nakládá koše ve skladu
     */
    NAKLADANI,

    /**
     * Velbloud cestuje do oázy
     */
    CESTA,

    /**
     * Velbloud se vrací z oázy
     */
    CESTAZPATKY,

    /**
     * Velbloud vykládá koše v oáze
     */
    VYKLADANI
}
