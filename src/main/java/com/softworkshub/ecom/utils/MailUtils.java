package com.softworkshub.ecom.utils;

import com.softworkshub.ecom.model.ProductOrder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class MailUtils {

    @Autowired
    private JavaMailSender mailSender;

    String ourEmail = "softworkshub0@gmail.com";
    String companyName = "E-commerce Website";

    public Boolean sendMail(String email, String url) throws MessagingException, UnsupportedEncodingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        mimeMessageHelper.setFrom(ourEmail,companyName);
        mimeMessageHelper.setTo(email);
        String subject = "Password Change ";
        String content = "<p>Hello user,</p>" + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + url
                + "\">Change my password</a></p>";

        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(content, true);
        mailSender.send(mimeMessage);
        return true;
    }


    public Boolean createUserMail(String email, String name) throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        mimeMessageHelper.setFrom(ourEmail,companyName);
        mimeMessageHelper.setTo(email);
        String content = "<p>Dear " + name + ",</p>"
                + "<p>Welcome to <strong>E-commerce Website</strong>! We're excited to have you as part of our shopping community. 🎊</p>"
                + "<p>Get ready for an amazing shopping experience with exclusive deals, top-quality products, and seamless service. 🚀</p>"
                + "<p>Here’s what you can do next:</p>"
                + "<ul>"
                + "<li>✅ Explore our latest collections</li>"
                + "<li>✅ Enjoy special discounts and offers</li>"
                + "<li>✅ Track your orders with ease</li>"
                + "</ul>"
                + "<p>If you have any questions, feel free to reach out to our support team at <strong>er.ankit.vishwakarma@proton.me</strong>.</p>"
                + "<p>Happy Shopping! 🛍️</p>"
                + "<p>Best Regards,<br><strong>E-Commerce Team</strong></p>";

        mimeMessageHelper.setSubject("Welcome to " + companyName);
        mimeMessageHelper.setText(content, true);
        mailSender.send(mimeMessage);
        return true;
    }

    String msg = null;

    public Boolean productSendMail(ProductOrder productOrder, String status) throws MessagingException, UnsupportedEncodingException {

        msg = "<p>Hello [[name]],</p>"
                + "<p>Thank you order <b>[[orderStatus]]</b>.</p>"
                + "<p><b>Product Details:</b></p>"
                + "<p>Name : [[productName]]</p>"
                + "<p>Category : [[category]]</p>"
                + "<p>Quantity : [[quantity]]</p>"
                + "<p>Price : [[price]]</p>"
                + "<p>Payment Type : [[paymentType]]</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message);

        mimeMessageHelper.setFrom(ourEmail,companyName);
        mimeMessageHelper.setTo(productOrder.getOrderAddress().getEmail());

        msg = msg.replace("[[name]]", productOrder.getOrderAddress().getFirstName());
        msg=msg.replace("[[orderStatus]]",status);
        msg=msg.replace("[[productName]]", productOrder.getProduct().getTitle());
        msg=msg.replace("[[category]]", productOrder.getProduct().getCategory());
        msg=msg.replace("[[quantity]]", productOrder.getQuantity().toString());
        msg=msg.replace("[[price]]", productOrder.getPrice().toString());
        msg=msg.replace("[[paymentType]]", productOrder.getPaymentType());

        mimeMessageHelper.setSubject("Product Order Status");
        mimeMessageHelper.setText(msg, true);
        mailSender.send(message);

        return true;
    }

    public static String generateUrl(HttpServletRequest request) {
        String siteUrl = request.getRequestURL().toString();
        return siteUrl.replace(request.getServletPath(), "");
    }
}
