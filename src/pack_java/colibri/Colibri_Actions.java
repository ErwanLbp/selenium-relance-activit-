package pack_java.colibri;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import pack_java.Main;
import pack_java.configuration.ConfigIni;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;


/**
 * Cette classe va creer une nouvelle tache dans Colibri par ligne du fichier CSV recupere
 *
 * @author Erwan Le Batard--Poles
 * @version 1.0
 */
public class Colibri_Actions {

    /**
     * Va chercher les pseudos des saisies incompletes
     *
     * @param driver La fenêtre firefox
     * @return La map contenant les pseudos et pourcentage de saisis incomplets
     */
    public static Set<String> saisiesIncompletes(WebDriver driver) {

        //Execution des actions pour acceder a la page de compte rendu d'activites
        accesURLpageConnexion(driver);
        remplissageChampsConnexion(driver);
        accesURLPageSaisie(driver);

        //Récupération des saisies incompletes
        return chercherSaisiesInsuffisantes(driver);
    }


    /**
     * Va chercher le mail de chaque pseudo contenu dans la map en argument
     *
     * @param driver  La fenêtre firefox
     * @param pseudos Pseudos des saisies incompletes pour qui il faut trouver le mail
     * @return La map contenant les pseudos et les mails des utilisateurs ayant une saisie incomplete
     */
    public static Map<String, String> chercherMails(WebDriver driver, Set<String> pseudos) {

        Map<String, String> mails = new HashMap<>();

        for (String user : pseudos) {
            //Execution des actions pour acceder a la page admin des utilisateurs
            accesURLAdminUtilisateurs(driver);
            Site_Colibri.Page_AdminUtilisateurs.LOC_BTN_EDITERUSER(driver, user).sendKeys(Keys.RETURN);
            String mail = Site_Colibri.Page_AdminEditionUtilisateurs.LOC_TXT_MAIL(driver).getAttribute("value");
            mails.put(user, mail);
        }

        return mails;
    }

    private static void accesURLAdminUtilisateurs(WebDriver driver) {
        driver.get(Site_Colibri.adresse_site() + Site_Colibri.adresse_page_admin_utilisateurs());
    }


    /**
     * Acces a la page http://172.17.177.175/colibri/
     *
     * @param driver La fenêtre firefox
     */
    private static void accesURLpageConnexion(WebDriver driver) {

        Main.ecrire_log("[O]\tAcces au site " + Site_Colibri.adresse_site());
        driver.get(Site_Colibri.adresse_site());
    }


    /**
     * Acces a la page contenant les pourcentages de saisie d'activite
     *
     * @param driver La fenetre firefox
     */
    private static void accesURLPageSaisie(WebDriver driver) {

        Main.ecrire_log("[O]\tAcces à la page " + Site_Colibri.adresse_page_activite_equipe());
        driver.get(Site_Colibri.adresse_site() + Site_Colibri.adresse_page_activite_equipe());
    }


    /**
     * Remplissage des champs de connexion et clic sur le bouton login
     *
     * @param driver La fenêtre firefox
     */
    private static void remplissageChampsConnexion(WebDriver driver) {

        Main.ecrire_log("[O]\tRemplissage des champs de connexion");
        new Select(Site_Colibri.Page_Login.LOC_TXT_USERNAME(driver)).selectByVisibleText(ConfigIni.getInstance().getLoginColibri());
        Site_Colibri.Page_Login.LOC_TXT_PASSWORD(driver).clear();
        Site_Colibri.Page_Login.LOC_TXT_PASSWORD(driver).sendKeys(ConfigIni.getInstance().getPasswordColibri());
        Site_Colibri.Page_Login.LOC_BTN_CONNECT(driver).click();
    }

    /**
     * Cherche les utilisateurs ayant une saisie insuffisante sur la page de resume d'activite
     *
     * @param driver          La fenetre Firefox
     * @param nMinSaisie      Le seuil de saisie demande
     * @param listUserExempte La liste des utilisateurs n'ayant pas besoin de saisir
     * @return Une map contenant les pseudos et pourcentages de saisie pour ce mois
     */
    private static Map<String, Integer> chercherSaisiesInsuffisantes1Mois(WebDriver driver, int nMinSaisie, List<String> listUserExempte) {

        Map<String, Integer> mSaisiesInsuf = new HashMap<>();

        List<WebElement> listPasDeSaisie = Site_Colibri.Page_ActiviteEquipe.LOC_LIST_LNK_PASSAISIE(driver);

        for (WebElement we : listPasDeSaisie) {
            String nom = we.getText();

            if (!listUserExempte.contains(we.getText()) && we.isDisplayed())
                mSaisiesInsuf.put(nom, 0);
        }


        List<WebElement> listSaisieIncomp = Site_Colibri.Page_ActiviteEquipe.LOC_LIST_LNK_SAISIEINCOMP(driver);

        for (WebElement we : listSaisieIncomp) {
            String nom = we.getText().split(" ")[0];
            int pourcentage = Integer.parseInt(we.getText().split("\\(")[1].split(" ")[0]);

            if (we.isDisplayed() && !listUserExempte.contains(nom) && pourcentage < nMinSaisie)
                mSaisiesInsuf.put(nom, pourcentage);
        }

        return mSaisiesInsuf;
    }

    /**
     * Va chercher quels utilisateurs ont une saisie incomplete pour le premier mois et les mois precedents
     *
     * @param driver La fenetre Firefox
     * @return Un set contenant les pseudos des profils ayant une saisie incomplete
     */
    private static Set<String> chercherSaisiesInsuffisantes(WebDriver driver) {

        int nbDeMois = ConfigIni.getInstance().getNbDeMois();
        List<String> listUserExempte = ConfigIni.getInstance().getUserExempte();
        Set<String> pseudos = new HashSet<>();

        for (int i = 0; i < nbDeMois; i++) {
            int seuilDuMois = ConfigIni.getInstance().getSeuilDuMois(i);
            String nomMois = Site_Colibri.Page_ActiviteEquipe.LOC_LNK_MOISCOURANT(driver).getText();
            Main.ecrire_log("\t" + nomMois + ": seuil=" + seuilDuMois + "%");

            if (seuilDuMois > 0) {
                Map<String, Integer> mSaisiesInsuf1Mois = chercherSaisiesInsuffisantes1Mois(driver, seuilDuMois, listUserExempte);
                pseudos.addAll(mSaisiesInsuf1Mois.keySet());
                sauvegardeContenusMail(nomMois, mSaisiesInsuf1Mois, seuilDuMois);
            }
            Site_Colibri.Page_ActiviteEquipe.LOC_LNK_MOISPRECEDENT(driver).click();
        }
        return pseudos;
    }


    /**
     * Ajoute ou cree un fichier contenant les saisies incompletes pour chaque utilisateur
     *
     * @param nomMois     Le nom du mois courant
     * @param mSaisies    La map contenant les pseudos et le pourcentage de saisie
     * @param seuilDuMois Le seuil de saisie demande pour ce mois
     */
    private static void sauvegardeContenusMail(String nomMois, Map<String, Integer> mSaisies, int seuilDuMois) {

        Set<String> users = mSaisies.keySet();
        for (String pseudo : users) {
            try {
                if (!Files.exists(Paths.get("data\\mails\\" + pseudo + ".txt")))
                    Files.createFile(Paths.get("data\\mails\\" + pseudo + ".txt"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (BufferedWriter bos = Files.newBufferedWriter(Paths.get("data\\mails\\" + pseudo + ".txt"), Charset.forName("ISO-8859-1"), StandardOpenOption.APPEND)) {
                bos.write(nomMois + " : " + mSaisies.get(pseudo) + "% saisis (" + seuilDuMois + "% requis)\n");
            } catch (IOException e) {
                Main.ecrire_log("[X]\tEchec de l'ecriture dans le fichier : data/mails/" + pseudo + ".txt");
                e.printStackTrace();
            }
        }
    }
}