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

    public Client(String clientId, String nume, String prenume, String cnp) {
        this.clientId = clientId;
        this.nume = nume;
        this.prenume = prenume;
        this.cnp = cnp;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public String getCnp() {
        return cnp;
    }

    public void setCnp(String cnp) {
        this.cnp = cnp;
    }
}
