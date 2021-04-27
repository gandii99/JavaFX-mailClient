package org.example;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The JavaMailUtil class represents an object with which you can pay for mail.
 * We can create a JavaMailUtil object like this:
 * Connect connect = new Connect ("mail addres", "password").
 */
public class JavaMailUtil {
    private String myAccountEmail;
    private String password;

    /**
     * The constructor for creating objects of the JavaMailUtil class, holds a connection to the address mail.
     *
     * @param login String variable taking address email.
     * @param password String variable taking password for an address email.
     */
    public JavaMailUtil(String login, String password){
        try {
            InternetAddress emailAddr = new InternetAddress(login);
            emailAddr.validate();
            myAccountEmail = login;
            this.password = password;
        } catch (AddressException e) {
            //e.printStackTrace();
            e.getMessage();
        }

    }

    /**
     * This method send mail on address email.
     *
     * @param recepient String variable taking address email recepient.
     * @param temat String variable taking topic of email.
     * @param text String variable taking content of email.
     * @throws Exception
     */
    public void sendMail(String recepient, String temat, String text) throws Exception {
        System.out.println("Preparing to send email");
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myAccountEmail, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(myAccountEmail));


        message.addRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(recepient)
        );

        message.setSubject(temat);
        message.setText(text);
        //Message message = prepareMessage(session, myAccountEmail, recepient, temat, text);

        Transport.send(message);
        System.out.println("Message sent succesfuly");
    }

    /**
     * This method send mail to a few recepient.
     *
     * @param recepient An two-dimensional array of String taking recepients of the email.
     * @param topic String variable taking topic of email.
     * @param text String variable taking content of email.
     * @throws Exception
     */
    public void sendMailToMailFromDatabases(String[] recepient, String topic, String text) throws Exception {
        System.out.println("Preparing to send email");
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myAccountEmail, password);
            }
        });
        for(int i=0; i<recepient.length; i++){
            Transport.send(prepareMessage(session, myAccountEmail, recepient[i], topic, text));
        }

        System.out.println("Message sent succesfuly");
    }

    private static Message prepareMessage(Session session, String myAccountEmail, String recepient, String temat, String text){
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(myAccountEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recepient));
            message.setSubject(temat);
            String htmlCode = text;
            message.setContent(htmlCode, "text/html");
            return message;
        } catch (Exception ex) {
            Logger.getLogger(JavaMailUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * This method send mail to a few recepients from databases.
     *
     * @param connect Connect variable taking connect to databases
     * @param topic String variable taking topic of email.
     * @param text String variable taking content of email.
     * @throws SQLException
     */
    public void sendEmailDataBase(Connect connect, String topic, String text) throws SQLException {
        String[][] tab;
        tab = connect.takeContentTable("roznosci.newsletter");
        System.out.println(tab[0][1]);
        for(int i = 0; i < tab.length; i++){
            if(tab[i][1].equals("t")){
                try {
                    sendMail(tab[i][0], topic, text);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        connect.close();
    }


}
