package pack_java;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import pack_java.colibri.Colibri_Actions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Fonction principale du programme, pour appeler toutes les autres
 *
 * @author Erwan Le Batard--Polès
 * @version 1.0
 */
public class Main {

    /**
     * Appelle les fonctions nécessaires à l'exécution du programme
     * - Télécharge le fichier
     * - Parse le fichier dans la mémoire d'exécution
     * - Crée une tache pour chaque ligne de demande
     *
     * @param args Arguments d'entrée du programme
     */
    public static void main(String[] args) {

        //Récupération du temps au début du programme pour calculer le temps d'exécution
        long timeDeb = System.currentTimeMillis();

        //Formation d'une chaine contenant la date d'aujourd'hui pour le log
        String today_date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());

        ecrire_log("******************************* DEBUT " + today_date + " *******************************");

        System.setProperty("file.encoding", "UTF-8");

            /*----------------*
             * Initialisation *
             *----------------*/

        //Initialisation du driver|
        WebDriver driver = configurationDriver();

        //Suppresion des fichiers temporaires
        suppressionFichiersTmp();


            /*-------------------------------------------*
             * Ajout des lignes de la liste dans Colibri *
             *-------------------------------------------*/

        //Recherche des pseudos ayant une saisie incomplete
        Set<String> pseudos = Colibri_Actions.saisiesIncompletes(driver);

        if (!pseudos.isEmpty()) {
            Map<String, String> mMails = Colibri_Actions.chercherMails(driver, pseudos);

            //Envoi du mail récapitulatif si il y a des demandes
            Mails.envoyer_mail(mMails);
        }


             /*----------*
             * Fermeture *
             *-----------*/

        quitter(driver);


        //Affichage du temps d'exécution
        ecrire_log("Temps d'execution total : " + ((System.currentTimeMillis() - timeDeb) / 1000) + "s");

        ecrire_log("******************************** FIN " + today_date + " ********************************\n");
    }


    /**
     * @return Le driver contenant la fenêtre Firefox
     */
    private static WebDriver configurationDriver() {

        WebDriver driver;

        //Lancement d'un firefox portable avec version downgrade pour eviter les problèmes de compatibilife entre selenium et firefox
        System.setProperty("webdriver.firefox.bin", ".\\Firefox\\firefox.exe");

        //Creation d'un profil pour accepter le telechargement CSV sans confirmation
        FirefoxProfile profile = new FirefoxProfile();

        //Mise en place du proxy
        profile.setPreference("network.proxy.type", 2);
        profile.setPreference("network.proxy.autoconfig_url", "http://wpad.manh.fr.sopra/wpad.dat");

        //Creation de la fenetre Firefox
        driver = new FirefoxDriver(profile);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        return driver;
    }


    /**
     * Ferme tous les fluxs ouvert pour quitter proprement et supprime le fichier CSV temporaire
     *
     * @param driver Contient la fenêtre firefox à fermer
     */
    private static void quitter(WebDriver driver) {

        //Fermeture de la fenetre Firefox
        driver.quit();

        //Suppresion des fichiers temporaires
        suppressionFichiersTmp();
    }


    /**
     * Ecris le message en argument dans la sortie de log
     *
     * @param log Log a ecrire dans le fichier de log
     */
    public static void ecrire_log(String log) {
        System.out.println(log);
        try (BufferedWriter bos = Files.newBufferedWriter(Paths.get("data\\logRelanceActivite.txt"), Charset.forName("ISO-8859-1"), StandardOpenOption.APPEND)) {
            bos.write(log + "\n");
        } catch (IOException e) {
            System.out.println("[X]\tEchec de l'ecriture du log");
            e.printStackTrace();
        }
    }


    /**
     * Supprime les fichiers crees pour les mails
     */
    private static void suppressionFichiersTmp() {
        try (DirectoryStream<Path> listing = Files.newDirectoryStream(Paths.get("data\\mails"), "*.txt")) {
            for (Path nom : listing)
                Files.deleteIfExists(nom);
        } catch (IOException e) {
            ecrire_log("[X]\tEchec de la suppresion des fichiers temporaires");
            e.printStackTrace();
        }
    }
}
