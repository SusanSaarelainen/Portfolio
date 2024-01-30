package harjoitustyo.matopeli;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.*;
import java.util.*;

/**
 * Luokka Matopeli luo graafisen käyttöliittymän, jossa pelataan matopeliä, hoitaa tiedoston
 * luomisen, sinne kirjoittamisen sekä tiedostosta lukemisen.
 */
public class Matopeli extends Application implements Serializable {
    /**
     * pisteiden määrä
     */
    int pisteet = 0;

    /**
     * teksti pisteiden esitykseen
     */
    Text pisteen_lasku = new Text("Pisteet: " + pisteet);

    /**
     * paneeli, johon pelin osat kootaan
     */
    Pane paneeli = new Pane();

    /**
     * ruokana toimiva ympyrä, jonka mato syö
     */
    Circle ruoka = new Circle();

    /**
     * matona toimii linkitetty lista
     */
    private LinkedList<Rectangle> vartalo = new LinkedList<>();

    /**
     * suuntavaihtoehdot enum-luokassa, inspiraationa:
     * https://leetcode.com/problems/spiral-matrix-ii/solutions/613681/java-snake-solution-with-changing-directions/
     */
    public enum Suunta {
        YLOS, ALAS, VASEN, OIKEA
    }

    /**
     * suunnan asetus
     */
    public Suunta suunta = Suunta.OIKEA;

    /**
     * totuusarvomuuttujan asetus
     */
    boolean paattyi = false;

    /**
     * animaatio, joka ajastaa pelin tapahtumia
     */
    Timeline animaatio;

    /**
     * Tulos-luokan hyödyntämistä varten luotu olio
     */
    Tulos olio = new Tulos();

    /**
     * tiedoston luominen pelin pisteitä varten
     */
    File tiedosto = new File("matopelipisteet.dat");

    /**
     * listan luominen pisteiden esittämistä varten
     */
    ArrayList<Object> tuloslista = new ArrayList<Object>();

    /**
     * listanäkymän luominen
     */
    ListView pistetaulukko = new ListView<>();

    /**
     * Päämetodi, jossa kutsutaan muita metodeita, asetetaan näppäimistön toiminnot
     * ja luodaan Timeline animaatio Matopelin toiminnallisuutta varten.
     * @param stage graafinen käyttöliittymä
     */
    @Override
    public void start(Stage stage) {
        ruoan_asetus();
        luonti();
        paneeli.getChildren().add(pisteen_lasku);
        pisteen_lasku.relocate(150, 0);
        Scene scene = new Scene(paneeli, 360, 400);

        paneeli.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DOWN) {
                suunta = Suunta.ALAS;
            }
            if (e.getCode() == KeyCode.UP) {
                suunta = Suunta.YLOS;
            }
            if (e.getCode() == KeyCode.LEFT) {
                suunta = Suunta.VASEN;
            }
            if (e.getCode() == KeyCode.RIGHT) {
                suunta = Suunta.OIKEA;
            }
        });
        paneeli.requestFocus();

        animaatio = new Timeline(new KeyFrame(Duration.millis(250), e -> {
            if (pelin_paattyminen()) {
                tiedostoon_kirjoittaminen();
                pistelistaus();
                animaatio.stop();
            }
            if (syo()) {
                ruoan_poisto();
                kasva();
                pisteet++;
                pisteen_lasku.setText("Pisteet: " + pisteet);
                ruoan_asetus();
            }
            liiku();
            rajat();
        }));
        animaatio.setCycleCount(Timeline.INDEFINITE);
        animaatio.play();
        stage.setTitle("Matopeli");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Pääohjelma, joka ajaa koodin.
     * @param args
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Metodi luonti luo pelin alussa olevan madon kahdesta neliöstä,
     * lisää ne linkitettyyn listaan ja lopuksi paneeliin.
     */
    private void luonti() {
        Rectangle mato = new Rectangle(100, 200, 20, 20);
        mato.setFill(Color.RED);
        vartalo.add(mato);
        Rectangle mato2 = new Rectangle(mato.getX() - 20, mato.getY() - 20, 20, 20);
        mato2.setFill(Color.RED);
        vartalo.add(mato2);
        paneeli.getChildren().addAll(vartalo);
    }

    /**
     * Ruoan_asetus luo ympyrän, joka toimii pelin ruokana. Ruoan koordinaatit valitaan satunnaisesti
     * pelialueen sisältämistä koordinaateista.
     */
    private void ruoan_asetus() {
        Random random = new Random();
        ruoka.setCenterX(random.nextInt(360));
        ruoka.setCenterY(random.nextInt(400));
        ruoka.setRadius(7);
        ruoka.setFill(Color.BLUE);
        paneeli.getChildren().add(ruoka);
    }

    /**
     * Ruoka-ympyrä poistetaan pelialueelta.
     */
    private void ruoan_poisto() {
        paneeli.getChildren().remove(ruoka);
    }

    /**
     * Liiku-metodi hoitaa madon ensimmäisen osan eli pään liikuttamisen. Muut osat seuraavat pään perässä.
     * Mato liikkuu nuolinäppäimiltä tulleen suuntavalinnan mukaisesti yhden neliön verran.
     * Lopuksi muiden osien koordinaatit saadaan käymällä ne läpi yksi kerrallaan foreach-loopissa, jossa
     * seuraavan osan x- ja y-koordinaatit asetetaan sitä edellä olevan osan aiempiin koordinaatteihin.
     */
    protected void liiku() {
        Rectangle paa = vartalo.getFirst();
        double dx = 0;
        double dy = 0;

        switch (suunta) {
            case YLOS -> {
                dy = -20;
            }
            case ALAS -> {
                dy = +20;
            }
            case VASEN -> {
                dx = -20;
            }
            case OIKEA -> {
                dx = +20;
            }
        }

        double viime_y = paa.getY();
        double viime_x = paa.getX();
        paa.setY(paa.getY() + dy);
        paa.setX(paa.getX() + dx);

        for (Rectangle nelio : vartalo) {
            if (nelio != paa) {
                double y = nelio.getY();
                double x = nelio.getX();
                nelio.setY(viime_y);
                nelio.setX(viime_x);
                viime_y = y;
                viime_x = x;
            }
        }
    }

    /**
     * Syo-metodi tarkastelee, osuuko madon pää ruokaan.
     * @return true/false
     */
    private boolean syo() {
        return ruoka.intersects(vartalo.getFirst().getBoundsInLocal());
    }

    /**
     * Syo_itseaan tutkii, osuuko madon pää muihin vartalon osiin
     * @return paattyi
     */
    private boolean syo_itseaan() {
        for (int i = 0; i < vartalo.size(); i++) {
            if (vartalo.getFirst().getX() == vartalo.get(i).getX() && vartalo.getFirst().getY() == vartalo.get(i).getY()) {
                paattyi = true;
            } else {
                paattyi = false;
            }
        }
        return paattyi;
    }

    /**
     * Kasva-metodilla madon vartaloon lisätään uusi osa, joka lisätään myös paneeliin, jotta se näkyisi.
     */
    protected void kasva() {
        Rectangle uusi_osa = new Rectangle(20, 20, Color.RED);
        vartalo.add(uusi_osa);
        paneeli.getChildren().add(uusi_osa);
    }

    /**
     * Rajat asettaa madon pään koordinaatit siten, ettei se voi ylittää pelialueen rajoja.
     */
    private void rajat() {
        if (vartalo.getFirst().getX() < -1) {
            vartalo.getFirst().setX(-1);
        } else if (vartalo.getFirst().getX() > 341) {
            vartalo.getFirst().setX(341);
        } else if (vartalo.getFirst().getY() < -1) {
            vartalo.getFirst().setY(-1);
        } else if (vartalo.getFirst().getY() > 381) {
            vartalo.getFirst().setY(381);
        }
    }

    /**
     * Tutkitaan, täyttyävätkö ehdot pelin päättymiselle. Ehdot ovat:
     * - mato syö itseään
     * - mato osuu pelialueen reunoihin
     * @return paattyi
     */
    private boolean pelin_paattyminen() {
        if (syo_itseaan()) {
            paattyi = true;
        } else if (vartalo.getFirst().getX() < 0) {
            paattyi = true;
        } else if (vartalo.getFirst().getX() > 340) {
            paattyi = true;
        } else if (vartalo.getFirst().getY() < 0) {
            paattyi = true;
        } else if (vartalo.getFirst().getY() > 380) {
            paattyi = true;
        } else {
            paattyi = false;
        }
        return paattyi;
    }

    /**
     * Tulos-luokan tietojen kirjoittaminen oliotiedostoon. Tiedostomuoto on dat, jotta pelaaja
     * ei pääse muuttamaan tuloksia. Ensin tiedostosta luetaan aiemmat tiedot, mikäli tiedosto löytyy,
     * minkä jälkeen lisätään päättyneen pelin tulos.
     */
    private void tiedostoon_kirjoittaminen() {
        olio.setPvm(new Date());
        olio.setPisteet(pisteet);
        olio.setTeksti("Pisteet: ");

        ObjectInputStream lTiedosto = null;
        try{
            if (!tiedosto.exists()){
                File tiedosto = new File("matopelipisteet.dat");
            }
            else {
                lTiedosto = new ObjectInputStream(new FileInputStream(tiedosto));

                ArrayList<Tulos> tuloslista2 = (ArrayList<Tulos>) lTiedosto.readObject();
                lTiedosto.close();
                for (Tulos tulos : tuloslista2) {
                    tuloslista.add(tulos);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        tuloslista.add(new Tulos(olio.getTeksti(), olio.getPisteet(), olio.getPvm()));

        ObjectOutputStream kTiedosto = null;

        try{
            kTiedosto = new ObjectOutputStream(new FileOutputStream(tiedosto));
            kTiedosto.writeObject(tuloslista);
            kTiedosto.close();

        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pistelistauksen luominen ja näyttäminen graafisessa näyttöliittymässä lukemalla tiedoston tiedot
     * ja asettamalla ne paneeliin ListView:n ja VBoxin avulla.
     */
    private void pistelistaus() {
        paneeli.getChildren().clear();

        ObjectInputStream lTiedosto = null;
        try{
            lTiedosto = new ObjectInputStream(new FileInputStream(tiedosto));

            ArrayList<Tulos> tuloslista2 = (ArrayList<Tulos>)lTiedosto.readObject();
            lTiedosto.close();
            for (Tulos tulos : tuloslista2) {
                System.out.println(tulos.toString());
                pistetaulukko.getItems().add(tulos);
                VBox vbox = new VBox(pistetaulukko);
                paneeli.getChildren().add(vbox);
                vbox.prefHeightProperty().bind(paneeli.heightProperty());
                vbox.prefWidthProperty().bind(paneeli.widthProperty());
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}