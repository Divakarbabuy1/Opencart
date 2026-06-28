package utilities;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Shared Email Reporting Utility.
 * Dynamically transmits automated test reports via SMTP securely using
 * configuration-driven pipeline tokens.
 */
public class EmailUtility {

    private static final Logger logger = LogManager.getLogger(EmailUtility.class);

    // Helper method to load credentials from config properties at runtime
    private static Properties loadConfigProperties() {
        Properties prop = new Properties();
        try (FileReader file = new FileReader("./src/test/resources/config.properties")) {
            prop.load(file);
        } catch (Exception e) {
            logger.error("Failed to load config.properties for Email Utility: " + e.getMessage());
        }
        return prop;
    }

    // ========================================================
    // STANDARD REPORT EMAIL PIPELINE
    // ========================================================
    public static void sendEmailWithReport(String reportPath, String recipients) {

        Properties config = loadConfigProperties();
        final String username = config.getProperty("sender_email"); // Read from config.properties
        final String password = config.getProperty("sender_app_password"); // Read from config.properties

        if (username == null || password == null) {
            logger.error("Email transmission aborted: Missing credentials in config.properties!");
            return;
        }

        // SMTP Server Infrastructure Configuration
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Authenticate Session
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));

            // Map Comma-Separated Mailing List
            String[] recipientList = recipients.split(",");
            InternetAddress[] recipientAddresses = new InternetAddress[recipientList.length];
            for (int i = 0; i < recipientList.length; i++) {
                recipientAddresses[i] = new InternetAddress(recipientList[i].trim());
            }
            message.setRecipients(Message.RecipientType.TO, recipientAddresses);

            String subject = "OpenCart Test Execution Report - " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            message.setSubject(subject);

            // Build Context Body
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Hi Team,\n\n" +
                    "Please find attached the OpenCart test execution report.\n\n" +
                    "Report Summary:\n" +
                    "- Report generated on: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "\n" +
                    "- Report file: " + new File(reportPath).getName() + "\n\n" +
                    "This is an automated email sent after test execution completion.\n\n" +
                    "Best Regards,\n" +
                    "OpenCart Test Automation Team");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Map Attachment Evidence
            if (new File(reportPath).exists()) {
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(reportPath);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(new File(reportPath).getName());
                multipart.addBodyPart(messageBodyPart);
            } else {
                logger.warn("Report file not found at: " + reportPath);
            }

            message.setContent(multipart);
            Transport.send(message);
            logger.info("Email report sent successfully to: " + recipients);

        } catch (MessagingException e) {
            logger.error("Failed to transmit email report: " + e.getMessage());
        }
    }

    // ========================================================
    // OVERLOADED CUSTOM REPORT EMAIL PIPELINE
    // ========================================================
    public static void sendEmailWithReport(String reportPath, String recipients, String customSubject, String customBody) {

        Properties config = loadConfigProperties();
        final String username = config.getProperty("sender_email");
        final String password = config.getProperty("sender_app_password");

        if (username == null || password == null) {
            logger.error("Custom email transmission aborted: Missing credentials in config.properties!");
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
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

            String[] recipientList = recipients.split(",");
            InternetAddress[] recipientAddresses = new InternetAddress[recipientList.length];
            for (int i = 0; i < recipientList.length; i++) {
                recipientAddresses[i] = new InternetAddress(recipientList[i].trim());
            }
            message.setRecipients(Message.RecipientType.TO, recipientAddresses);

            message.setSubject(customSubject);

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(customBody);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            if (new File(reportPath).exists()) {
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(reportPath);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(new File(reportPath).getName());
                multipart.addBodyPart(messageBodyPart);
            }

            message.setContent(multipart);
            Transport.send(message);
            logger.info("Custom email report sent successfully to: " + recipients);

        } catch (MessagingException e) {
            logger.error("Failed to transmit custom email report: " + e.getMessage());
        }
    }
}