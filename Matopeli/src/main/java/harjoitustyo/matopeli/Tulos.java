package harjoitustyo.matopeli;

import java.io.Serializable;
import java.util.Date;

/**
 * Luokka Tulos luo Matopeli-luokan pistelaskuun tarvittavat kentät ja metodit
 */
public class Tulos implements Serializable {
    /**
     * tiedostoon tallennettava seliteteksti
     */
    private String teksti;

    /**
     * pelistä kerätyt pisteet
     */
    private int pisteet;

    /**
     * pelin päivämäärä ja päättymisaika
     */
    private Date pvm;

    /**
     * Alustaja, jonka avulla käsitellään Tulos-luokan tietoja Matopeli-luokassa
     */
    Tulos() {
    }

    /**
     * Alustaja, jolla luodaan tiedostoon kirjattavia olioita
     * @param teksti seliteteksti
     * @param pisteet pelissä kerätyt pisteet
     * @param pvm päivämäärä ja kellonaika, jolloin peli päättyi
     */
    Tulos(String teksti, int pisteet, Date pvm){
        this.teksti = teksti;
        this.pisteet = pisteet;
        this.pvm = pvm;
    }

    /**
     * Teksti-muuttujan palautus
     * @return teksti
     */
    public String getTeksti() {
        return teksti;
    }

    /**
     * Tekstin asetus
     * @param teksti "Pisteet: "
     */
    public void setTeksti(String teksti) {
        this.teksti = teksti;
    }

    /**
     * Pisteet-muuttujan palautus
     * @return pisteet
     */
    public int getPisteet() {
        return pisteet;
    }

    /**
     * Pisteiden asetus
     * @param pisteet pelissä kerätyt pisteet
     */
    public void setPisteet(int pisteet) {
        this.pisteet = pisteet;
    }

    /**
     * Päivämäärä-muuttujan palautus
     * @return pvm
     */
    public Date getPvm() {
        return pvm;
    }

    /**
     * Päivämäärän asetus
     * @param pvm pelisuorituksen päivämäärä ja aika
     */
    public void setPvm(Date pvm) {
        this.pvm = pvm;
    }

    /**
     * toString-metodi, jolla olioiden sisältämä tieto saadaan tekstimuotoiseksi
     * @return String-muotoinen tuloste
     */
    public String toString(){
        return getTeksti() + " " + getPisteet() + " " + getPvm() + "\n";
    }
}
