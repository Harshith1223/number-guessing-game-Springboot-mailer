package com.example.Number_guess.NumberController;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;

@Controller
public class GameController {
	
	@Autowired
	JavaMailSender mailSender;
	
	@GetMapping("/") 
	public String welcome(HttpSession session) {
		session.invalidate(); 
		return "welcome";
	}
	
	//When Submit button in Welcome.html works 
	@PostMapping("/start")
	public String startGame(@RequestParam String name , @RequestParam String email, HttpSession session, Model model) {
		session.setAttribute("name", name);
		session.setAttribute("email", email);
		session.setAttribute("number", new Random().nextInt(101));
		session.setAttribute("attempts", 0);
		model.addAttribute("message","Welocme "+name+" !Guess a number between 1 and 100.");
		return "game";
	}
	
	//This controller starts when u press submit in game page
	@PostMapping("/guess")
	public String guess(@RequestParam int guess , HttpSession session, Model model) {
		int number = (int) session.getAttribute("number");
		int attempts = (int) session.getAttribute("attempts");
		
		attempts++;
		session.setAttribute("attempts", attempts);
		
		if(guess<number) {
			model.addAttribute("message","Too Low");
		} else if(guess>number) {
			model.addAttribute("message","Too High");
		} else {
			int points = Math.max(100-(attempts-1)*10, 10);
			String name = (String) session.getAttribute("name");
			String email =  (String) session.getAttribute("email");
			
			model.addAttribute("name",name);
			model.addAttribute("email",email);
			model.addAttribute("points",points);
			model.addAttribute("attempts",attempts);
			
			sendMail(name,attempts,points,email);
			
			return "result";
		}
		return  "game";
	}

	private void sendMail(String name, int attempts, int points, String email) {
	    try {
	        MimeMessage message = mailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true);

	        helper.setFrom("harshith.hrithik001@gmail.com");
	        helper.setTo(email);
	        helper.setSubject("🎉 Your Score in Number Guessing Game");

	        String content = """
	                <html>
	                <body style="font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;">
	                    <h2 style="color: #2E86C1;">🎉 Hello %s!</h2>
	                    <p>You guessed the number in <strong>%d</strong> attempts.</p>
	                    <p>Based on your performance, your score is:</p>
	                    <h1 style="color: #28a745;">%d Points 🏆</h1>
	                    <br/>
	                    <p>Thanks for playing the <strong>Number Guessing Game</strong>! 🎮</p>
	                    <p style="font-size: 0.9em; color: gray;">— With ❤️ from Harsh</p>
	                </body>
	                </html>
	                """.formatted(name, attempts, points);

	        helper.setText(content, true); // Enable HTML

	        mailSender.send(message);

	    } catch (Exception e) {
	        System.err.println("Error sending HTML email: " + e.getMessage());
	    }
	}

}
