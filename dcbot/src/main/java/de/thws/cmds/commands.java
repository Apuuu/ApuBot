package de.thws.cmds;

import de.thws.cmds.dcNod.dcDanbooruAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class commands extends ListenerAdapter {

    static List<String> data = new ArrayList<String>();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        String[] smelly = {"Vomit", "a skunk", "Sewage", "Rotten Eggs", "Shit", "a Corpse", "a Dumpster", "Garbage", "a Fart", "unwashed Socks"};
        EmbedBuilder eb2 = new EmbedBuilder();
        Integer num;
        String userId;

        switch (event.getName()) {
            case "stinky":
                userId = event.getMember().getId();
                num = (int) (Math.random() * 10);
                event.reply("<@" + userId + "> smells like " + smelly[num]).queue();
                break;
            case "redks":
                userId = event.getMember().getId();
                if (userId.equals("300595008229212161")) {
                    event.reply("https://cdn.discordapp.com/attachments/887409385729568788/1122639339210428526/image.png").queue();
                } else {
                    event.reply("Sorry you dont have the permissions to execute this command.").queue();
                }
                break;
            case "functions":
                eb2.setTitle("ApuBot", null);
                eb2.setDescription("You are able to use the following commands:");
                eb2.addField("/enhancement", "Enhancement Simulator WIP", false);
                eb2.addField("/stinky", "Very stinky mf", false);
                eb2.addField("/randommsg", "This will return a random messages from the Textchannel", false);
                eb2.addField("/audio", "Will play most sources which have a video or audio (Very WIP)", false);
                eb2.addField("/badword", "You can add, remove or view the list of bad words.", false);
                eb2.addField("/gen", "Generate an Image with given prompts using Nod.ai", false);
                eb2.setThumbnail("https://cdn.discordapp.com/emojis/1112393085016608779.webp?size=96&quality=lossless");
                event.replyEmbeds(eb2.build()).queue();
                eb2.clear();
                break;
            case "randommsg":
                event.reply("Please just use /randommessage as a non Slash command.").queue();
                break;
            case "badword":
                String option = event.getOption("action").getAsString();

                switch (option.toLowerCase()) {
                    case "add":
                        String word = event.getOption("word").getAsString();
                        data.add(word);
                        event.reply("Bad word added!").queue();
                        break;

                    case "remove":
                        event.reply("This function is not supported yet!").queue();
                        break;

                    case "getlist":
                        if (data.size() > 0) {
                            event.reply(data.toString()).queue();
                        } else {
                            event.reply("I dont have any data to print!").queue();
                        }
                        break;

                }
                break;
            case "dantags":
                event.reply(dcDanbooruAPI.getDanbooruTags(event.getOption("id").getAsInt())).queue();
                break;

        }
    }

}