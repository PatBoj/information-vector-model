package ProgramingTools;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class Mailer {
	private static String username = "animatronic.pal@gmail.com";
	private static String password = "Kaloryferek0";
	private static String to = "pbojarski94@gmail.com";

    public static void send(String username, String password, String to, String subject, String msg) {

        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
          new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
          });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(msg);

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void send(String to, String subject, String msg) {send(username, password, to, subject, msg);}
    public static void send(String subject, String msg) {send(username, password, to, subject, msg);}
    public static void send() {send(username, password, to, "Koniec symulacji", "Tak jak w temacie");}
}