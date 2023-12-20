package com.server.sso.mail;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailServiceImpl implements EmailService {

  @Value("${spring.mail.username}")
  private String fromEmail;

  @Autowired
  private JavaMailSender javaMailSender;

  @Autowired
  TemplateEngine templateEngine;

  @Override
  public String sendMail( String to, String subject,String confirmationLink) {
    try {
      MimeMessage mimeMessage = javaMailSender.createMimeMessage();

      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

      mimeMessageHelper.setFrom(fromEmail);
      mimeMessageHelper.setTo(to);
      mimeMessageHelper.setSubject(subject);

      String htmlContent = loadTemplate("mail-confirmation", confirmationLink);

      mimeMessageHelper.setText(htmlContent,true);

      javaMailSender.send(mimeMessage);
      return "mail send";
    } catch (Exception e) {
      throw new RuntimeException(e);
    }


  }

  private String loadTemplate(String templateName, String confirmationLink) {
    Context context = new Context();
    context.setVariable("confirmationLink", confirmationLink);

    try {
      return templateEngine.process("templates/" + templateName + ".html", context);
    } catch (Exception e) {
      throw new RuntimeException("Error loading template", e);
    }
  }
}