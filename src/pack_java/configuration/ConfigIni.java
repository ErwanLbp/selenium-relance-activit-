package pack_java.configuration;

import org.ini4j.Ini;
import pack_java.Main;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Cette classe permet le chargement ou la creation d'un fichier de configuration data/config.ini
 * Puis elle contient tous les accesseurs pour recuperer les donnees contenues dans le fichier
 *
 * @author Erwan Le Batard--Poles
 * @version 1.0
 */
public class ConfigIni {

    /**
     * L'objet permettant de traiter le fichier .ini
     */
    private final Ini ini;


    /**
     * Instance du singleton
     */
    private static ConfigIni INSTANCE = new ConfigIni();


    /**
     * Renvoi l'instance du singleton, la cree si elle n'a jamais ete appele
     *
     * @return L'instance du singleton
     */
    public static ConfigIni getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConfigIni();
        }
        return INSTANCE;
    }


    /**
     * Charge le fichier .ini et le cree si il n'existe pas
     */
    private ConfigIni() {

        ini = new Ini();

        try {
            ini.load(new FileReader("data\\configRelanceActiviteColibri.ini"));
        } catch (IOException e) {
            Main.ecrire_log("[X]\tLecture fichier .ini impossible");
            e.printStackTrace();
            System.exit(1);
        }
        Main.ecrire_log("[O]\tLecture du fichier .ini reussie");
    }


    /**
     * Renvoi la valeur configuree pour l'adresse du site Colibri
     *
     * @return L'adresse du site
     */
    public String getAdresseSiteColibri() {

        Ini.Section sct = (Ini.Section) ini.get("Colibri");
        return (String) sct.get("adresseSite");
    }


    /**
     * Renvoi la valeur configuree pour l'adresse de la page de compte rendu d'activite de l'equipe
     *
     * @return L'adresse de la page de resume d'activite
     */
    public String getAdresseActEquipeColibri() {

        Ini.Section sct = (Ini.Section) ini.get("Colibri");
        return (String) sct.get("adresseActEquipe");
    }


    /**
     * Renvoi la valeur configuree pour l'adresse de la page admin des utilisateurs
     *
     * @return L'adresse de la page
     */
    public String getAdresseAdminUtils() {

        Ini.Section sct = (Ini.Section) ini.get("Colibri");
        return (String) sct.get("adresseAdminUtils");
    }


    /**
     * Renvoi la valeur configuree pour le login du site Colibri
     *
     * @return Le login du site
     */
    public String getLoginColibri() {

        Ini.Section sct = (Ini.Section) ini.get("Colibri");
        return (String) sct.get("login");
    }


    /**
     * Renvoi la valeur configuree pour le password du site Colibri
     *
     * @return Le password du site
     */
    public String getPasswordColibri() {

        Ini.Section sct = (Ini.Section) ini.get("Colibri");
        return (String) sct.get("password");
    }


    /**
     * Renvoi le nombre de mois sur lesquels regarder la saisie
     *
     * @return Le nombre de mois
     */
    public int getNbDeMois() {

        Ini.Section sct = (Ini.Section) ini.get("Colibri");
        return Integer.parseInt((String) sct.get("nombreDeMois"));
    }

    /**
     * Renvoi le seuil du mois i
     *
     * @param i Le numero du mois a chercher dans le fichier de config
     * @return Le seuil de saisie de ce mois
     */
    public int getSeuilDuMois(int i) {

        while (i >= 0) {
            try {
                Ini.Section sct = (Ini.Section) ini.get("Colibri");
                return Integer.parseInt((String) sct.get("seuilMois", i));
            } catch (IndexOutOfBoundsException ioobe) {
                i--;
            }
        }
        return 0;
    }

    /**
     * Renvoi la liste des personnes n'ayant pas besoin de remplir l'activite
     *
     * @return La liste de pseudos
     */
    public List<String> getUserExempte() {

        Ini.Section sct = (Ini.Section) ini.get("Colibri");
        return (List<String>) sct.getAll("userExempte");
    }


    /**
     * Renvoi la valeur configuree pour l'envoyeur du mail de recapitulatif
     *
     * @return Le mail de l'envoyeur
     * @throws AddressException en cas de probleme de formattage de l'adresse
     */
    public InternetAddress getEnvoyeurMail() throws AddressException {

        Ini.Section sct = (Ini.Section) ini.get("Mail");
        return new InternetAddress((String) sct.get("envoyeur"));
    }


    /**
     * Renvoi la valeur configuree pour le login de connexion au serveur mail
     *
     * @return Le login du serveur mail
     */
    public String getLoginMail() {

        Ini.Section sct = (Ini.Section) ini.get("Mail");
        return (String) sct.get("login");
    }


    /**
     * Renvoi la valeur configuree pour le password de connexion au serveur mail
     *
     * @return Le login du serveur mail
     */
    public String getPasswordMail() {

        Ini.Section sct = (Ini.Section) ini.get("Mail");
        return (String) sct.get("password");
    }


    /**
     * Renvoi la valeur configuree pour le destinataire du mail recapitulatif
     *
     * @return Le(s) destinataire(s) du mail
     * @throws AddressException en cas de probleme de formattage de l'adresse
     */
    public InternetAddress getDestinataireMail() throws AddressException {

        Ini.Section sct = (Ini.Section) ini.get("Mail");
        return new InternetAddress((String) sct.get("destinataire"));
    }
}
