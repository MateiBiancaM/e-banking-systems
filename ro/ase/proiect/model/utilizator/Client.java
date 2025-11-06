package ro.ase.proiect.model.utilizator;
/**
 * Clasa reprezinta un client al bancii. Clasa se ocupă cu stocarea informatiilor de identificare.
 *
 * @author Matei Maria-Bianca
 * @version 1.1
 * @since 27.10.2025
 */
public class Client {
    private String clientId;
    private String nume;
    private String prenume;
    private String cnp;
    /**
     * Constructor pentru clasa Client.Inițializează un client cu datele sale de identificare.
     *
     * @param clientId ID-ul unic al clientului.
     * @param nume     Numele de familie al clientului.
     * @param prenume  Prenumele clientului.
     * @param cnp      Codul Numeric Personal (CNP) al clientului.
     */
    public Client(String clientId, String nume, String prenume, String cnp) {
        this.clientId = clientId;
        this.nume = nume;
        this.prenume = prenume;
        this.cnp = cnp;
    }

    /**
     * Returnează ID-ul unic al clientului.
     *
     * @return ID-ul clientului (String).
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Returnează numele de familie al clientului.
     *
     * @return Numele clientului (String).
     */
    public String getNume() {
        return nume;
    }

    /**
     * Returnează prenumele clientului.
     *
     * @return Prenumele clientului (String).
     */
    public String getPrenume() {
        return prenume;
    }

    /**
     * Returnează CNP-ul al clientului.
     *
     * @return CNP-ul clientului (String).
     */
    public String getCnp() {
        return cnp;
    }

}
