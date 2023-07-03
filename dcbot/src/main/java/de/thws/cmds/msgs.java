package de.thws.cmds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class msgs extends ListenerAdapter{

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        Message newMessage = event.getMessage();
        String txtMessage = newMessage.getContentDisplay();
        int msgAtch = newMessage.getAttachments().size();
        MessageChannelUnion txtChannel = newMessage.getChannel();
        String lowercaseMessage = txtMessage.toLowerCase();

        if(!newMessage.getAuthor().isBot()){

            String[] imageFormats = {".png", ".jpg", ".webm", ".gif"};
            boolean isImage = false;

            for (String format : imageFormats) {
                if (txtMessage.contains(format)) {
                    isImage = true;
                    break;
                }
            }

            if (isImage || msgAtch >= 1) {
                txtMessage = "img";
            }   

            String[] keywords1 = {"bruh", "bruv", "bru"};

            for (String keyword1 : keywords1) {
                if (lowercaseMessage.contains(keyword1)) {
                    txtMessage = "bruh";
                    break;
                }
            }

            String[] keywords2 = {"carried", "boosted"};

            for (String keyword2 : keywords2) {
                if (lowercaseMessage.contains(keyword2)) {
                    txtMessage = "carried";
                    break;
                }
            }

            String[] defaultKeywords3 = {"nigga", "nigger", "fuck", "pussy", "retard", "retarded", "cunt", "hurensohn", "faggot", "shit", "crackhead", "crack", "scheiß", "scheiße", "schnauze", "wtf", "kys"};

            String[] keywords3 = ArrayUtils.addAll(defaultKeywords3, commands.data.toArray(new String[commands.data.size()]));

            for (String keyword3 : keywords3) {
                if (lowercaseMessage.contains(keyword3)) {
                    txtMessage = "badword";
                    break;
                }
            }

            switch(txtMessage.toLowerCase()){
                case "o kurwa":
                    txtChannel.sendMessage("ja pierdole").queue();
                break;
                case "retard bot":
                    txtChannel.sendMessage("Fuck off retard "+newMessage.getAuthor().getGlobalName()+" i hope you die").queue();
                break;
                case "img":
                    //txtChannel.sendMessage("Damn bro, that's a nice fat cock!").queue();
                break;
                case "bruh":
                    txtChannel.sendMessage("https://tenor.com/view/bruh424019499-gif-25675566").queue();
                break;
                case "carried":
                    txtChannel.sendMessage("some people...").queue();
                break;
                case "badword":
                    txtChannel.sendMessage("bro watch out auf deine Wortwahl du hurensohn").queue();
                break;
                case "/randommessage":
                    int val = 1000;
                    txtChannel.sendMessage("This can take some time!").queue();
                    List<Message> messageList = new ArrayList<>();
                    try {
                        messageList = txtChannel.getIterableHistory().takeAsync(val)
                            .thenApply(list -> list.stream()
                                .collect(Collectors.toList()))
                            .get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
        
                    Collections.reverse(messageList);
                    Integer randy = (int)(Math.random()*messageList.size());
                    System.out.println(randy);
                    String msg = messageList.get(randy).getContentDisplay();
                    
                    if(msg.isEmpty()){
                        txtChannel.sendMessage("Apu sucks doing this and i commited a fucking fat ass error which he doesnt understand XDXD").queue();
                    }else{
                        txtChannel.sendMessage(msg).queue();   
                    }
                break;
            }
        }

    }
    
}
