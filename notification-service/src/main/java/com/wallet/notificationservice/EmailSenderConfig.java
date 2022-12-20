package com.wallet.notificationservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailSenderConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        // JavaMailSender is an Interface,
        // we are using default implementation (JavaMailSenderImpl), we can define and use our implementation also
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost("smtp.gmail.com");
        // usually the sender account is as no reply like noreply@paytm.com, noreply@g4g.com
        javaMailSender.setUsername("rahuld7219@gmail.com");
        javaMailSender.setPassword("gfuxwbthnuoflvpj");
        javaMailSender.setPort(587);

        Properties properties = javaMailSender.getJavaMailProperties();

        // StartTLS is a protocol command used to inform the email server that the email client wants to upgrade
        // from an insecure connection to a secure one using TLS or SSL.
        // STARTTLS is a command used to upgrade an existing standard (non-encrypted) connection into an encrypted one.
        // This allows for secure connections over the non-encrypted port for a service.
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.debug", true); // for debugging purpose

        return javaMailSender;
    }
}
