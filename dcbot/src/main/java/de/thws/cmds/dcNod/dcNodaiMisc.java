package de.thws.cmds.dcNod;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class dcNodaiMisc {

    public static void sendModels(EmbedBuilder eb3, SlashCommandInteractionEvent event) {
        eb3.setTitle("Nod.Ai models", null);
        eb3.setDescription("Use one of the following models!");
        eb3.addField("Default if none provided: Lykon/DreamShaper", "", false);
        eb3.addField("sinkinai/anime-pastel-dream-hard-baked-vae", "Fully supports Danbooru", false);
        eb3.addField("FFusion/di.ffusion.ai.Beta512r", "", false);
        eb3.addField("hakurei/waifu-diffusion", "Fully supports Danbooru", false);
        eb3.setThumbnail("https://nod.ai/wp-content/uploads/2020/06/Nod-Logo-1-16-400x100-1-300x105.png");
        event.replyEmbeds(eb3.build()).queue();
        eb3.clear();
    }


    public static void sendUsage(EmbedBuilder eb, SlashCommandInteractionEvent event) {
        eb.setTitle("Nod.Ai usage", null);
        eb.setDescription("Instruction on how to use this bot!");
        eb.addField("/gen", "This is the basic command.", false);
        eb.addField("mode", "This option is mandatory!\n'default': start a normal image generation\n'models': Will print all usable models (some might take a while for specific settings)\n'usage': Will print this embed", false);
        eb.addField("prompt", "Provide a description of what you want to generate, for example 'Woman, beach, lying on the ground'", false);
        eb.addField("danid", "ONLY USE WHEN NO PROMPTS ARE GIVEN! Provide a Image ID from Danbooru, this will extract its Tags and use those as a Prompt!", false);
        eb.addField("size", "Only use the following sizes: 512x512, 512x768, 768x512 (some might take a while for specific settings)", false);
        eb.addField("model", "Check /gen models for available models", false);
        eb.addField("iter", "Set the amount of steps", false);
        eb.setThumbnail("https://nod.ai/wp-content/uploads/2020/06/Nod-Logo-1-16-400x100-1-300x105.png");
        event.replyEmbeds(eb.build()).queue();
        eb.clear();
    }

    public static void sendToLogger(SlashCommandInteractionEvent event, String User, String Message, EmbedBuilder eb3) {
        JDA jda = event.getJDA();
        Guild guild = jda.getGuildById("835185253399789568");
        MessageChannelUnion txtChannel = (MessageChannelUnion) guild.getGuildChannelById("1125437609590657084");
        eb3.setTitle("Logger", null);
        eb3.setDescription("Logging the usage of this bot");
        eb3.addField("Image requested by: ", User, false);
        try {
            eb3.addField("Server name: ", event.getGuild().getName(), false);
        } catch (Exception e) {
            eb3.addField("Server name: ", "Direct Message", false);
        }
        if (Message.length() > 1023) {
            eb3.addField("", "Too long!", false);
        } else {
            eb3.addField("Json request: ", Message, false);
        }
        txtChannel.sendMessageEmbeds(eb3.build()).queue();
        eb3.clear();
    }

    public static String getUsername(SlashCommandInteractionEvent event) {
        return event.getUser().getEffectiveName();
    }

    public static String imageToBase64(String imagePath) {
        try {
            Path path = Paths.get(imagePath);
            byte[] imageBytes = Files.readAllBytes(path);
            String base64Image = "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
            return base64Image;
        } catch (IOException e) {
            return null;
        }
    }
}
