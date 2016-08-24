package pack_java;

import pack_java.configuration.ConfigIni;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Cette classe gere l'envoi du mail recapitulatif des ajouts dans Colibri
 *
 * @author Erwan Le Batard--Polès
 * @version 1.0
 */
class Mails {

    /**
     * Renvoi le contenu du mail, forme a partir de la map des differents ajouts
     *
     * @param pseudo Le pseudo de l'utilisateur courant
     * @return La chaine du corps du mail
     */
    private static String former_contenu_mail(String pseudo) {

        String saisies = chargerContenuMail(pseudo);

        return "Bonjour " + pseudo + "</br></br>" +
                "Votre saisie d'activité Colibri est insuffisante pour les mois suivants : </br>" + saisies + "</br>" +
                "Merci de saisir votre activité manquante.</br></br>" +
                "Cordialement.";
    }


    /**
     * Renvoi le contenu du mail generique, forme a partir de la map des differents ajouts
     *
     * @param saisiesIncomp Les saisies incompletes des utilisateurs sans mail
     * @return La chaine du corps du mail
     */
    private static String former_contenu_mail_generique(String saisiesIncomp) {

        return "Bonjour, </br></br>" +
                "Les saisies suivantes sont incomplètes :</br></br>" +
                saisiesIncomp + "</br></br>" +
                "Ces utilisateurs n'ont pas de mail déclaré sur la page admin.</br></br>" +
                "Vous êtes priés de saisir votre activité et de transmettre votre adresse mail à un admin Colibri.</br></br>" +
                "Cordialement.";
    }


    /**
     * Configure le serveur et les differents parametres et envoi le mail
     *
     * @param mMails La map contenant les pseudos et mail des utilisateurs
     */
    static void envoyer_mail(Map<String, String> mMails) {

        try {
            String smtpHost = "ptx.send.corp.sopra";

            Properties props = new Properties();
            props.setProperty("mail.smtp.auth", "true");
            props.setProperty("mail.smtp.host", smtpHost);
            props.setProperty("mail.smtp.port", "587");

            Session session = Session.getInstance(props);

            boolean auMoins1MailVide = false;
            String sMailGenerique = "";

            Set<String> users = new HashSet<>();
            try (DirectoryStream<Path> listing = Files.newDirectoryStream(Paths.get("data\\mails"), "*.txt")) {
                for (Path nom : listing)
                    users.add(nom.toString().split("\\\\")[2].split("\\.")[0]);
            } catch (IOException e) {
                Main.ecrire_log("[X]\tEchec lecture du répertoire data/mails");
                e.printStackTrace();
            }

            for (String pseudo : users) {

                if (mMails.get(pseudo).isEmpty()) {
                    auMoins1MailVide = true;
                    sMailGenerique += pseudo + " :</br>" + chargerContenuMail(pseudo) + "</br>-- -- -- --</br>";
                } else {
                    //Le message
                    Message message = new MimeMessage(session);
                    message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(mMails.get(pseudo)));
                    message.setFrom(ConfigIni.getInstance().getEnvoyeurMail());
                    message.setSubject("Relance pour la saisie d'activité sur Colibri");
                    message.setContent(former_contenu_mail(pseudo), "text/html; charset=UTF-8");

                    Transport tr = session.getTransport("smtp");

                    tr.connect(smtpHost, ConfigIni.getInstance().getLoginMail(), ConfigIni.getInstance().getPasswordMail());
                    message.saveChanges();

                    tr.sendMessage(message, message.getAllRecipients());
                    tr.close();
                }
            }

            if (auMoins1MailVide) {
                //Le message
                Message message = new MimeMessage(session);
                message.setRecipient(Message.RecipientType.TO, ConfigIni.getInstance().getDestinataireMail());
                message.setFrom(ConfigIni.getInstance().getEnvoyeurMail());
                message.setSubject("Relance pour la saisie d'activité sur Colibri");
                message.setContent(former_contenu_mail_generique(sMailGenerique), "text/html; charset=UTF-8");

                Transport tr = session.getTransport("smtp");

                tr.connect(smtpHost, ConfigIni.getInstance().getLoginMail(), ConfigIni.getInstance().getPasswordMail());
                message.saveChanges();

                tr.sendMessage(message, message.getAllRecipients());
                tr.close();
            }

        } catch (AuthenticationFailedException afe) {
            Main.ecrire_log("[X]\tEchec de l'authentification sur le serveur mail");
        } catch (NoSuchProviderException e) {
            Main.ecrire_log("[X]\tPas de transport disponible pour ce protocole");
            e.printStackTrace();
        } catch (AddressException e) {
            Main.ecrire_log("[X]\tProbleme lors du parse des adresse de destination du mail");
            e.printStackTrace();
        } catch (SendFailedException sfe) {
            Main.ecrire_log("[X]\tErreur lors de l'envoi du mail : Mauvaise(s) adresse(s)");
        } catch (MessagingException e) {
            Main.ecrire_log("[X]\tErreur lors du traitement du mail (MessagingException)");
            e.printStackTrace();
        }
    }

    private static String chargerContenuMail(String pseudo) {
        String line;
        String res = "";

        try (BufferedReader br = Files.newBufferedReader(Paths.get("data\\mails\\" + pseudo + ".txt"), Charset.forName("ISO-8859-1"))) {
            while ((line = br.readLine()) != null) {
                res += line + "</br>";
            }
        } catch (FileNotFoundException e) {
            Main.ecrire_log("[X]\tLe fichier data/mails/" + pseudo + ".txt a lire est introuvable");
        } catch (IOException e) {
            e.printStackTrace();
            Main.ecrire_log("[X]\tErreur lors de la lecture du fichier data/mails/" + pseudo + ".txt");
        }
        return res;
    }
}
