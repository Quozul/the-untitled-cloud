package dev.quozul.user

import dev.quozul.smtpSession
import io.ktor.server.application.*
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


// https://www.journaldev.com/2532/javamail-example-send-mail-in-java-smtp

/**
 * Utility method to send simple HTML email
 * @param session
 * @param toEmail
 * @param subject
 * @param body
 */
fun sendEmail(toEmail: String, subject: String, body: String, session: Session = smtpSession) {
	try {
		val fromEmail = session.getProperty("mail.from.mail")
		val fromName = session.getProperty("mail.from.name")
		println("sending to $toEmail $fromEmail $fromName")

		val msg = MimeMessage(session)
		//set message headers
		msg.addHeader("Content-type", "text/HTML; charset=UTF-8")
		msg.addHeader("format", "flowed")
		msg.addHeader("Content-Transfer-Encoding", "8bit")

		msg.setFrom(InternetAddress(fromEmail, fromName))
//		msg.replyTo = InternetAddress.parse("no_reply@example.com", false)
		msg.setSubject(subject, "UTF-8")
		msg.setText(body, "UTF-8")
		msg.sentDate = Date()


		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false))

		Transport.send(msg)
		println("sent")
	} catch (e: Exception) {
		e.printStackTrace()
	}
}

fun Application.configureSmtp(): Session {
	val user = this@configureSmtp.environment.config.property("smtp.user").getString()
	val pass = this@configureSmtp.environment.config.property("smtp.pass").getString()
	val host = this@configureSmtp.environment.config.property("smtp.host").getString()
	val port = this@configureSmtp.environment.config.property("smtp.port").getString()
	val fromEmail = this@configureSmtp.environment.config.property("smtp.from.email").getString()
	val fromName = this@configureSmtp.environment.config.property("smtp.from.name").getString()

	val props = Properties()
	props["mail.smtp.host"] = host // SMTP Host
	props["mail.smtp.port"] = port // TLS Port
	props["mail.smtp.auth"] = "true" // Enable authentication
	props["mail.smtp.starttls.enable"] = "true" // Enable STARTTLS
	props["mail.from.mail"] = fromEmail
	props["mail.from.name"] = fromName

	val auth: Authenticator = object : Authenticator() {
		override fun getPasswordAuthentication(): PasswordAuthentication {
			return PasswordAuthentication(user, pass);
		}
	}

	return Session.getInstance(props, auth)
}