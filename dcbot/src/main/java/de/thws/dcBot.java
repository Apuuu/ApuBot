package de.thws;

import java.io.BufferedReader;
import java.io.FileReader;

import de.thws.cmds.commands;
import de.thws.cmds.enhancementsim;
import de.thws.cmds.msgs;
import de.thws.cmds.dcNod.dcNodaiTxt2img;
import de.thws.cmds.dcNod.dcNodaiUpscaler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class dcBot{

    public static void main(String[] args){

        String tokenPath = "C:/Users/maxim/Desktop/token.txt";

        try(BufferedReader br = new BufferedReader(new FileReader(tokenPath))){
        
        String line;
        StringBuilder content = new StringBuilder();

        while ((line = br.readLine()) != null) {
            content.append(line).append("");
        }

        String token = content.toString();
        
        JDA bot = JDABuilder.createDefault(token)
            .enableIntents(GatewayIntent.MESSAGE_CONTENT)
            .addEventListeners(new commands())
            .addEventListeners(new msgs())
            .addEventListeners(new enhancementsim())
            .addEventListeners(new dcNodaiTxt2img())
            .addEventListeners(new dcNodaiUpscaler())
            .setActivity(Activity.listening("Infinite loops caused by Bruh"))
            .build();
        
        bot.updateCommands().addCommands(
            Commands.slash("dantags","Get Danbooru image Tags")
                .addOption(OptionType.INTEGER, "id", "Image ID"),
            Commands.slash("functions", "Print all functions"),
            Commands.slash("enhance","Simulate an enhancement")
                .addOption(OptionType.STRING, "item", "Boss, Acc, Blackstar", true)
                .addOption(OptionType.INTEGER, "enhancementlevel","1 (PRI) - 5 (PEN)", true)
                .addOption(OptionType.INTEGER, "failstack", "Enhancement Chance increasement", true),
            Commands.slash("badword", "Add or remove a bad word")
                .addOption(OptionType.STRING, "action", "Add, Remove, getlist", true)
                .addOption(OptionType.STRING, "word", "the word you want to add to the Bad words list"),
            Commands.slash("gen","generate an image")
                .addOptions(
                    new OptionData(OptionType.STRING, "mode", "select a mode", true)
                        .addChoice("Generate", "default")
                        .addChoice("Usage", "usage")
                        .addChoice("Models","models")
                        .addChoice("Upscale", "soloupscale")
                    )
                .addOptions(
                    new OptionData(OptionType.STRING, "size", "select a valid size")
                        .addChoice("Default: 768x512","768x512")
                        .addChoice("512x512","512x512")
                        .addChoice("512x768", "512x768")
                    )
                .addOptions(
                    new OptionData(OptionType.STRING, "model", "select a available model")
                        .addChoice("DreamShaper","Lykon/DreamShaper")
                        .addChoice("animePastelDream_hardBakedVae","D:/sharksd/models/diffusers/animePastelDream_hardBakedVae")
                        .addChoice("NovelAI", "D:/customsharksd/SHARK/apps/stable_diffusion/web/models/diffusers/naiplswork")
                        .addChoice("Shinymix", "D:/sharksd/models/diffusers/shinymixbakedvae")
                        .addChoice("AOM3_orangemixs","D:/customsharksd/SHARK/apps/stable_diffusion/web/models/diffusers/OrangemixsBakedVaev1")
                        .addChoice("MeinaPastel_v6","D:/customsharksd/SHARK/apps/stable_diffusion/web/models/diffusers/meinapastel_v6Pastel")
                        .addChoice("Midjorney", "prompthero/openjourney")
                    )
                .addOptions(
                    new OptionData(OptionType.STRING, "upscale", "Should the generated image be upscaled? This feature is currently only available for 768x512 Images!")
                        .addChoice("Default: No","no")
                        .addChoice("Yes","yes")
                    )
                .addOptions(
                    new OptionData(OptionType.INTEGER, "scheduler", "Select a available scheduler")
                        .addChoice("Default: DDIM",0)
                        .addChoice("PNDM",1)
                        .addChoice("LMSDiscrete",2)
                        .addChoice("KDPM2Discrete",3)
                        .addChoice("DPMSolverMultistep",4)
                        .addChoice("EulerDiscrete",5)
                        .addChoice("EulerAncestralDiscrete",6)
                        .addChoice("SharkEulerDiscrete",7)
                    )
                .addOption(OptionType.STRING, "prompt", "custom prompt")
                .addOption(OptionType.INTEGER, "danid", "Use Danbooru tag from ID")
                .addOption(OptionType.INTEGER, "iter", "Amount of steps")
                .addOption(OptionType.STRING, "seed", "Image seed")
                .addOption(OptionType.STRING, "path", "Imagepath")
        ).queue();
        
        }catch(Exception e){
            e.printStackTrace();
        }
    }    
}
