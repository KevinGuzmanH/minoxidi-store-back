package com.kevin.minoxidilback.dto;


import org.springframework.stereotype.Component;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

@Component
public class EnviarMail {
    public String send(String sendTo,String link,boolean verify) {
        final String fromEmail = "apisacount@gmail.com";
        final String password = "fqqhrjaivdgwhzfj";
        final String toEmail = sendTo;
        String body;
        String subject;

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
        Session session = Session.getInstance(props, auth);
        if (verify){
            body = "<div><button> "+link+" </button></div>";
            subject = "Verifica Tu Cuenta";
        }else {
            body = "<div><button> "+link+" </button></div>";
            subject = "Recuperar Contraseña";
        }
        return EmailUtil.sendEmail(session, toEmail,subject, body);

}
}
