package de.thws.cmds;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class enhancementsim extends ListenerAdapter{

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
        try{
        EmbedBuilder eb = new EmbedBuilder();
        String item = event.getOption("item").getAsString();
        Integer fs = event.getOption("failstack").getAsInt(); 
        Integer itemlvl = event.getOption("enhancementlevel").getAsInt()-1; 
        double chance, rng, roundRng, roundChance;
        String succ;

        double[] bossBaseChance = {11.76, 7.69, 6.25, 2.00, 0.30};
        double[] bossSoftcap = {50, 82, 102, 340, 2324};

        double[] bsBaseChance = {13.07, 10.62, 3.4, 0.51, 0.2};
        double[] bsSoftcap = {44, 56, 196, 1363, 3490};

        double[] accBaseChance = {25.00, 10.00, 7.50, 2.50, 0.50};
        double[] accSoftcap = {18, 40, 44, 110, 490};
        double[] accBasecap = {70, 50, 40, 30, 25};

        if((itemlvl >= 0) && (itemlvl <= 4) && (item.toLowerCase().equals("acc")||item.toLowerCase().equals("boss")||item.toLowerCase().equals("blackstar")) && fs >= 0){
        switch(item.toLowerCase()){
            case "blackstar":
                chance = bsBaseChance[itemlvl] * (1 + (0.1 * fs));
                rng = Math.random()*100;
                roundRng = Math.round(rng * 100.0)/100.0;

                if(chance > 70){
                    chance = 70 + (bsBaseChance[itemlvl] * (0.02 * (fs - bsSoftcap[itemlvl])));
                }

                if(chance > 90){
                    chance = 90;
                }
                
                if(chance <= rng){succ = "failed";}else{succ = "succeeded";}

                roundChance = Math.round(chance * 100.0)/100.0;

                eb.setTitle("Enhancement Simulator", null);
                eb.setDescription("Enhancement chance: "+roundChance+"%");
                eb.addField("Enhancement "+item,succ, false);
                eb.addField("Generator: "+roundRng,"", false);
                eb.setThumbnail("https://cdn.discordapp.com/attachments/887409385729568788/1122851499148595220/image.png");
                event.replyEmbeds(eb.build()).queue();
                eb.clear();
            break;
            case "boss":
                chance = bossBaseChance[itemlvl] * (1 + (0.1 * fs));
                rng = Math.random()*100;
                roundRng = Math.round(rng * 100.0)/100.0;

                if(chance > 70){
                    chance = 70 + (bossBaseChance[itemlvl] * (0.02 * (fs - bossSoftcap[itemlvl])));
                }

                if(chance > 90){
                    chance = 90;
                }
                
                if(chance <= rng){succ = "failed";}else{succ = "succeeded";}

                roundChance = Math.round(chance * 100.0)/100.0;

                eb.setTitle("Enhancement Simulator", null);
                eb.setDescription("Enhancement chance: "+roundChance+"%");
                eb.addField("Enhancement "+item,succ, false);
                eb.addField("Generator: "+roundRng,"", false);
                eb.setThumbnail("https://cdn.discordapp.com/attachments/887409385729568788/1122851499148595220/image.png");
                event.replyEmbeds(eb.build()).queue();
                eb.clear();

            break;
            case "acc":
                chance = accBaseChance[itemlvl] * (1 + (0.1 * fs));
                rng = Math.random()*100;
                roundRng = Math.round(rng * 100.0)/100.0;

                if(chance > accBasecap[itemlvl]){
                    chance = accBasecap[itemlvl] + (accBaseChance[itemlvl] * (0.02 * (fs - accSoftcap[itemlvl])));
                }

                if(chance > 90){
                    chance = 90;
                }

                if(chance <= rng){succ = "failed";}else{succ = "succeeded";}

                roundChance = Math.round(chance * 100.0)/100.0;

                eb.setTitle("Enhancement Simulator", null);
                eb.setDescription("Enhancement chance: "+roundChance+"%");
                eb.addField("Enhancement "+item,succ, false);
                eb.addField("Generator: "+roundRng,"", false);
                eb.setThumbnail("https://cdn.discordapp.com/attachments/887409385729568788/1122851499148595220/image.png");
                event.replyEmbeds(eb.build()).queue();
                eb.clear();

            break;

            }
        }else{
            eb.setTitle("Wrong settings!", null);
            eb.setDescription("Please use the following possible settings:");
            eb.addField("","Item: Boss, Acc, Blackstar\nItem level: 1 (PRI) - 5 (PEN)\nFailstack: Failstack>=0", false);
            eb.setThumbnail("https://cdn.discordapp.com/emojis/1112393085016608779.webp?size=96&quality=lossless");
            event.replyEmbeds(eb.build()).queue();
            eb.clear();
        }

    } catch (Exception e){

    }

    }
    
}
