package de.thws.cmds.dcNod;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class dcNodaiHub extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        switch (event.getName()) {

            case "gen":

                EmbedBuilder eb3 = new EmbedBuilder();
                String mode = event.getOption("mode").getAsString();

                switch (mode) {

                    case "default":
                        dcNodaiTxt2img.requestImage(event, eb3);
                        break;

                    case "models":
                        dcNodaiMisc.sendModels(eb3, event);
                        break;

                    case "usage":
                        dcNodaiMisc.sendUsage(eb3, event);
                        break;

                    case "extend":
                        dcNodaiOutpainting.outpainter(event);
                        break;
                }
                break;

        }
    }

}
