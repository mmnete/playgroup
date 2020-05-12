package util

import java.util.Properties;
import javax.mail._;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

class emailSender {

  def send (toName: String, toEmail: String, subject: String, majorText: String, html: String) : Boolean = {

          var username = "juxj60@gmail.com";
          var password = "vjbtmslzerzvopzd";

          var prop = new Properties();
          prop.put("mail.smtp.host", "smtp.gmail.com");
         prop.put("mail.smtp.port", "587");
         prop.put("mail.smtp.auth", "true");
         prop.put("mail.smtp.starttls.enable", "true"); //TLS

          var session = Session.getInstance(prop,
          new Authenticator {
             protected override def getPasswordAuthentication() = new PasswordAuthentication(username, password)
           });

         try {

             var message = new MimeMessage(session);
             message.setFrom(new InternetAddress("PlayGroup@trinity.edu"));
             message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
             message.setSubject("PlayGroup: "+subject);
             message.setText(majorText);

             Transport.send(message);

             return true;

         }catch {
           case e: MessagingException => { e.printStackTrace;
           return false; }
         }
  }


}
