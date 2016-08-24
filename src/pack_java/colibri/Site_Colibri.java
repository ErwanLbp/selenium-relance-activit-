package pack_java.colibri;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pack_java.configuration.ConfigIni;

import java.util.List;

/**
 * La classe Colibri_Site specifie ou trouver les blocs dont on a besoin,
 * pour permettre la mise a jour facile si le code html change
 *
 * @author Erwan Le Batard--Poles
 * @version 1.0
 */
class Site_Colibri {

    /**
     * Renvoi l'adresse du site
     *
     * @return L'adresse du site
     */
    static String adresse_site() {
        return ConfigIni.getInstance().getAdresseSiteColibri();
    }


    /**
     * Renvoi l'adresse de la page d'activite de l'equipe
     *
     * @return L'adresse de la page
     */
    static String adresse_page_activite_equipe() {
        return ConfigIni.getInstance().getAdresseActEquipeColibri();
    }


    /**
     * Renvoi l'adresse de la page admin des utilisateurs
     *
     * @return L'adresse de la page
     */
    static String adresse_page_admin_utilisateurs() {
        return ConfigIni.getInstance().getAdresseAdminUtils();
    }


    /**
     * La sous classe Page_ActiviteEquipe permet de simplifier l'acces aux donnees
     * dont on a besoin sur la page de compte rendu d'activite de l'equipe
     */
    static class Page_ActiviteEquipe {

        /**
         * Renvoi tous les elements du tableau Pas de saisie
         *
         * @param driver Le driver sur lequel on cherche l'element
         * @return Les WebElements trouves
         */
        static List<WebElement> LOC_LIST_LNK_PASSAISIE(WebDriver driver) {

            return driver.findElements(By.xpath("//th[contains(.,'Pas de saisie')]/ancestor::div[@class='span6']//thead/following-sibling::tbody//td[position()=3]/a"));
        }


        /**
         * Renvoi tous les elements du tableau Saisie incomplete
         *
         * @param driver Le driver sur lequel on cherche l'element
         * @return Les WebElements trouves
         */
        static List<WebElement> LOC_LIST_LNK_SAISIEINCOMP(WebDriver driver) {

            return driver.findElements(By.xpath("//th[contains(.,'Pas de saisie')]/ancestor::div[@class='span6']//thead/following-sibling::tbody//td[position()=2]/a[contains(.,'%')]"));
        }


        /**
         * Renvoi le lien vers le mois precedent
         *
         * @param driver Le driver sur lequel on cherche l'element
         * @return Les WebElements trouves
         */
        static WebElement LOC_LNK_MOISPRECEDENT(WebDriver driver) {

            return driver.findElement(By.xpath("//div/ul[@class='nav nav-tabs']/li[position()=3]/a"));
        }

        /**
         * Renvoi le lien vers le mois courant
         *
         * @param driver Le driver sur lequel on cherche l'element
         * @return Les WebElements trouves
         */
        static WebElement LOC_LNK_MOISCOURANT(WebDriver driver) {

            return driver.findElement(By.xpath("//div/ul[@class='nav nav-tabs']/li[@class='active']/a"));
        }

    }


    /**
     * La sous classe Page_AdminUtilisateurs permet de simplifier l'acces aux donnees
     * dont on a besoin sur la page admin de gestion des utilisateurs
     */
    static class Page_AdminUtilisateurs {

        /**
         * Renvoi l'element 'bouton editer utilisateur' de la page
         *
         * @param driver Le driver sur lequel on cherche l'element
         * @param pseudo Le pseudo a chercher sur la page
         * @return Le WebElement trouve
         */
        static WebElement LOC_BTN_EDITERUSER(WebDriver driver, String pseudo) {

            return driver.findElement(By.xpath("//td[contains(.,'" + pseudo + "')]/following-sibling::td/a[position()=1]"));
        }
    }


    /**
     * La sous classe Page_AdminEditionUtilisateurs permet de simplifier l'acces aux donnees
     * dont on a besoin sur la page admin de gestion d'un utilisateur
     */
    static class Page_AdminEditionUtilisateurs {

        /**
         * Renvoi l'element 'Mail' de la page
         *
         * @param driver Le driver sur lequel on cherche l'element
         * @return Le WebElement trouve
         */
        static WebElement LOC_TXT_MAIL(WebDriver driver) {

            return driver.findElement(By.id("form_user_mail"));
        }
    }


    /**
     * La sous classe Login_Page permet de simplifier l'acces dans le code aux donnees
     * dont on a besoin sur la page de connexion
     */
    static class Page_Login {

        /**
         * Renvoi l'element 'identifiant' de la page
         *
         * @param driver Le driver sur lequel on cherche l'element
         * @return Le WebElement trouve
         */
        static WebElement LOC_TXT_USERNAME(WebDriver driver) {
            return driver.findElement(By.id("form_auth_name"));
        }

        /**
         * Renvoi l'element 'mot de passe' de la page
         *
         * @param driver Le driver sur lequel on cherche l'element
         * @return Le WebElement trouve
         */
        static WebElement LOC_TXT_PASSWORD(WebDriver driver) {
            return driver.findElement(By.id("form_auth_password"));
        }

        /**
         * Renvoi l'element bouton de connexion de la page
         *
         * @param driver Le driver sur lequel on cherche l'element
         * @return Le WebElement trouve
         */
        static WebElement LOC_BTN_CONNECT(WebDriver driver) {
            return driver.findElement(By.cssSelector("button.btn.btn-primary"));
        }
    }
}
