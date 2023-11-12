package org.example.model;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Properties;
import java.util.regex.Pattern;

public class Bot extends TelegramLongPollingBot {
public Bot(){super("6741372499:AAGtjq-PT5hBp6KcWvy7v-DNvUTMdpjLLH4");}
   public static SenderEmailDetails senderEmailDetails = new SenderEmailDetails();
   public static SendEmailDetails sendEmailDetails= new SendEmailDetails();
   public static Properties props = new Properties();
    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
            if(update.hasMessage()){
              final  Message message = update.getMessage();
              User user = message.getFrom();
                System.out.println(message.getText());
                if(message.hasText()){
                    final  String text = message.getText();
                        if(text.equals("/start")){
                            SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                                    "Hello "+ user.getUserName()+"\n Enter your email address;");
                            UserRepo.USER_STEPS.put(message.getChatId(),Step.REGISTER_EMAIL);
                            this.execute(sendMessage);
                        }
                        else if (UserRepo.USER_STEPS.get(message.getChatId())==Step.REGISTER_EMAIL || message.getText().equals("/email-send")){
                            String uEmail = message.getText();
                            if(Pattern.matches("[a-z0-9]+.*@gmail.com",uEmail)){
                                UserRepo.USER_STEPS.put(message.getChatId(),Step.USERS_EMAIL_PASSWORD);
                                senderEmailDetails.setUserEmail(uEmail);
                                this.execute(new SendMessage(message.getChatId().toString(),
                                        "Enter password from Google. \nYou can get it here : https://myaccount.google.com/apppasswords "));
                            }else{
                                this.execute(new SendMessage(message.getChatId().toString(),
                                        "Invalid gmail address. please try again. "));
                            }

                        } else if (UserRepo.USER_STEPS.get(message.getChatId())==Step.USERS_EMAIL_PASSWORD){
                            String password = message.getText();
                            if(Pattern.matches("[a-z]{16}",password)){
                                senderEmailDetails.setUserPassword(password);
                                UserRepo.USER_STEPS.put(message.getChatId(),Step.SENDING_EMAIL_RECIPIENT);
                                this.execute(new SendMessage(message.getChatId().toString(),
                                        "Enter Recipient email address."));
                            }else{
                                this.execute(new SendMessage(message.getChatId().toString(),
                                        "Invalid password, it should be letters from English alphabet only.\nDon't forgot remove spaces"));
                            }

                        } else if (UserRepo.USER_STEPS.get(message.getChatId()) == Step.SENDING_EMAIL_RECIPIENT) {
                            String recip = message.getText();
                            if(Pattern.matches("[a-z0-9]+.*@gmail.com",recip)){
                                UserRepo.USER_STEPS.put(message.getChatId(),Step.SENDING_EMAIL_SUBJECT);
                                sendEmailDetails.setRecipient(recip);
                                this.execute(new SendMessage(message.getChatId().toString(),
                                        "Enter Subject."));
                            }else{
                                this.execute(new SendMessage(message.getChatId().toString(),
                                        "Invalid gmail address. please try again. "));
                            }
                        } else if (UserRepo.USER_STEPS.get(message.getChatId())==Step.SENDING_EMAIL_SUBJECT) {
                            String sub = message.getText();
                            sendEmailDetails.setSubject(sub);
                            UserRepo.USER_STEPS.put(message.getChatId(),Step.SENDING_EMAIL_MESSAGE);
                            this.execute(new SendMessage(message.getChatId().toString(),
                                    "Enter message to send. "+senderEmailDetails.getUserEmail()+"   "
                                            +senderEmailDetails.getUserPassword()+"   "
                                            +sendEmailDetails.getRecipient()+"   "
                                            +sendEmailDetails.getSubject()));
                        } else if (UserRepo.USER_STEPS.get(message.getChatId())==Step.SENDING_EMAIL_MESSAGE) {
                            String messageText = message.getText();
                            sendEmailDetails.setText(messageText);
                            EmailSending emailSending = new EmailSending();
                            emailSending.emailSenderMethod(sendEmailDetails,senderEmailDetails);
                            UserRepo.USER_STEPS.put(message.getChatId(),Step.SENDING_EMAIL_FINISHED);
                        } else if (UserRepo.USER_STEPS.get(message.getChatId())==Step.SENDING_EMAIL_FINISHED) {
                            this.execute(new SendMessage(message.getChatId().toString(),"massage sent successfully.\nEnter /email-send to send email."));
                        }
                }
            }
    }

    @Override
    public String getBotUsername() {
        return "my_new_java_bot";
    }
}
