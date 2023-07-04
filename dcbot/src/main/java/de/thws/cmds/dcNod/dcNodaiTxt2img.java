package de.thws.cmds.dcNod;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.io.File;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class dcNodaiTxt2img extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){

        EmbedBuilder eb3 = new EmbedBuilder();

        switch(event.getName()){

            case "gen":

            String mode = event.getOption("mode").getAsString();

            switch(mode){

                case "default":

                String model, size, aPrompt, upscale;
                int iter, schedulerID;

                model = event.getOption("model") == null ? dcNodaiconfig.defaults[0] : event.getOption("model").getAsString();
                size = event.getOption("size") == null ? dcNodaiconfig.defaults[1] : event.getOption("size").getAsString();
                iter = event.getOption("iter") == null ? Integer.parseInt(dcNodaiconfig.defaults[2]) : Math.max(0, Math.min(60, event.getOption("iter").getAsInt()));
                upscale = event.getOption("upscale") == null ? dcNodaiconfig.defaults[3] : event.getOption("upscale").getAsString();
                schedulerID = event.getOption("scheduler") == null ? Integer.parseInt(dcNodaiconfig.defaults[4]) : event.getOption("scheduler").getAsInt();

                if(event.getOption("danid") != null){
                    aPrompt = dcDanbooruAPI.getDanbooruTags(event.getOption("danid").getAsInt());
                }else{
                    if(event.getOption("prompt") == null){aPrompt = "";}else{aPrompt = event.getOption("prompt").getAsString();}
                }

                String[] splitSize = size.split("x",2);
                int cHeight = Integer.parseInt(splitSize[0]);
                int cWidth = Integer.parseInt(splitSize[1]);

                if(Arrays.asList(dcNodaiconfig.keywordsmodel).contains(model)){

                event.reply("Image is being Generated...").queue();

                String cPrompt = dcNodaiconfig.defaultPrompt+aPrompt;
                cPrompt = cPrompt.replace("ä","ae").replace("ü","ue").replace("ä","oe");

                String prompt = "\"prompt\": \""+cPrompt+"\",";
                String negative_prompt = "\"negative_prompt\": \""+dcNodaiconfig.defaultNegativePrompt+"\",";
                String steps = "\"steps\": "+iter+",";
                String seed = "\"seed\": -1,";
                String height = "\"height\": "+cHeight+",";
                String width = "\"width\": "+cWidth+",";
                String cfg_scale = "\"cfg_scale\": 8.5,";
                String hf_model_id = "\"hf_model_id\": \""+model+"\",";
                String scheduler = "\"sampler\": \""+dcNodaiconfig.scheduler_list_cpu_only[schedulerID]+"\"";
                String finalPayload = "{"+prompt+negative_prompt+steps+seed+height+width+cfg_scale+hf_model_id+scheduler+"}";
                
                sendToLogger(event, event.getUser().getEffectiveName(), finalPayload, eb3);

                try{
                    URL endpointUrl = new URL(dcNodaiconfig.txt2imgUrl);
                    HttpURLConnection connection = (HttpURLConnection) endpointUrl.openConnection();

                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setDoOutput(true);

                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(finalPayload.getBytes());
                    outputStream.flush();
                    outputStream.close();

                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder responseBuilder = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            responseBuilder.append(line);
                        }
                        reader.close();
                        
                        String jsonResponse = responseBuilder.toString();

                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode jsonNode = objectMapper.readTree(jsonResponse.toString());
                        
                        if (jsonNode.has("info") && jsonNode.get("info").isTextual()) {
                            String info = jsonNode.get("info").asText();
                            String[] explode = info.split("seed=\\[", 2);
                            String currentSeed = explode[1].split("\\]", 2)[0];

                            String[] splitInfo = info.split("ckpt_loc=", 2);

                            long millis = System.currentTimeMillis();
                            java.sql.Date date = new java.sql.Date(millis);
                            String stringDate = date.toString();
                            String[] dateArray = stringDate.split("-",3);
                            String finalDate = dateArray[0]+dateArray[1]+dateArray[2];

                            File folder = new File("D:/customsharksd/SHARK/apps/stable_diffusion/web/generated_imgs/"+finalDate);
                            File[] listOfFiles = folder.listFiles();

                            for (int i = 0; i < listOfFiles.length; i++){
                                if (listOfFiles[i].isFile()) {
                                    if(listOfFiles[i].toString().contains(currentSeed)){

                                        String FileName = "D:/customsharksd/SHARK/apps/stable_diffusion/web/generated_imgs/"+finalDate+"/"+listOfFiles[i].getName();

                                        switch(upscale){
                                            case "no":
                                                FileUpload aiImage = FileUpload.fromData(new File(FileName));
                                                eb3.setTitle("Image informations:", null);
                                                if(splitInfo[0].length()>1023){
                                                    eb3.addField("","Too long!", false);
                                                }else{
                                                    eb3.addField("",splitInfo[0], false);
                                                }
                                                eb3.addField("",splitInfo[1], false);
                                                eb3.addField("Upscaling", "If you want to upscale the generated image please use /gen mode:Upscale path:"+finalDate+"/"+listOfFiles[i].getName()+" seed:"+currentSeed+"", false);
                                                event.getChannel().sendMessageEmbeds(eb3.build()).queue();
                                                eb3.clear();
                                                event.getChannel().sendFiles(aiImage).queue();
                                            break;
                                            case "yes":

                                                event.getChannel().sendMessage("Image generated! Now upscaling...").queue();
                                                dcNodaiUpscaler.nodaiUpscale(FileName, currentSeed);
                                                File[] newlistOfFiles = folder.listFiles();

                                                for(int j = 0; j < newlistOfFiles.length; j++)
                                                    if(newlistOfFiles[j].toString().contains("upscale") && newlistOfFiles[j].toString().contains(currentSeed)){
                                                        FileName = "D:/customsharksd/SHARK/apps/stable_diffusion/web/generated_imgs/"+finalDate+"/"+newlistOfFiles[j].getName();
                                                        FileUpload aiImageUpscaled = FileUpload.fromData(new File(FileName));
                                                        eb3.setTitle("Image informations:", null);
                                                        if(splitInfo[0].length()>1023){
                                                            eb3.addField("","Too long!", false);
                                                        }else{
                                                            eb3.addField("",splitInfo[0], false);
                                                        }
                                                        eb3.addField("",splitInfo[1], false);
                                                        event.getChannel().sendMessageEmbeds(eb3.build()).queue();
                                                        eb3.clear();
                                                        event.getChannel().sendFiles(aiImageUpscaled).queue();
                                                }

                                            break;
                                        }

                                    }
                                } else if (listOfFiles[i].isDirectory()) {
                                    //System.out.println("Directory " + listOfFiles[i].getName());
                                }
                            }

                        } else {
                            event.reply("Bro something got really fucked up").queue();
                        }
                        
                        } else {
                           
                        }
                        connection.disconnect();

                }catch (Exception e){}

                }else{
                    sendHelp(eb3, event);
                }
                
                break;

                case "models":
                    eb3.setTitle("Nod.Ai models", null);
                    eb3.setDescription("Use one of the following models!");
                    eb3.addField("Default if none provided: Lykon/DreamShaper", "", false);
                    eb3.addField("sinkinai/anime-pastel-dream-hard-baked-vae", "Fully supports Danbooru", false);
                    eb3.addField("FFusion/di.ffusion.ai.Beta512r", "", false);
                    eb3.addField("hakurei/waifu-diffusion", "Fully supports Danbooru", false);
                    eb3.setThumbnail("https://nod.ai/wp-content/uploads/2020/06/Nod-Logo-1-16-400x100-1-300x105.png");
                    event.replyEmbeds(eb3.build()).queue();
                    eb3.clear();
                break;

                case "usage":
                    sendHelp(eb3, event);
                break;
                }
            break;
            
        }
    } 

    public void sendHelp(EmbedBuilder eb, SlashCommandInteractionEvent event){
        eb.setTitle("Nod.Ai usage", null);
        eb.setDescription("Instruction on how to use this bot!");
        eb.addField("/gen", "This is the basic command.", false);
        eb.addField("mode", "This option is mandatory!\n'default': start a normal image generation\n'models': Will print all usable models (some might take a while for specific settings)\n'usage': Will print this embed", false);
        eb.addField("prompt","Provide a description of what you want to generate, for example 'Woman, beach, lying on the ground'", false);
        eb.addField("danid","ONLY USE WHEN NO PROMPTS ARE GIVEN! Provide a Image ID from Danbooru, this will extract its Tags and use those as a Prompt!", false);
        eb.addField("size","Only use the following sizes: 512x512, 512x768, 768x512 (some might take a while for specific settings)", false);
        eb.addField("model","Check /gen models for available models", false);
        eb.addField("iter","Set the amount of steps", false);
        eb.setThumbnail("https://nod.ai/wp-content/uploads/2020/06/Nod-Logo-1-16-400x100-1-300x105.png");
        event.replyEmbeds(eb.build()).queue();
        eb.clear();
    }

    public void sendToLogger(SlashCommandInteractionEvent event, String User, String Message, EmbedBuilder eb3){
        JDA jda = event.getJDA();
        Guild guild = jda.getGuildById("835185253399789568");
        MessageChannelUnion txtChannel = (MessageChannelUnion) guild.getGuildChannelById("1125437609590657084");
        eb3.setTitle("Logger", null);
        eb3.setDescription("Logging the usage of this bot");
        eb3.addField("Image requested by: ", User, false);
        try{
            eb3.addField("Server name: ",event.getGuild().getName(), false);
        }catch(Exception e){
            eb3.addField("Server name: ","Direct Message", false);
        }
        if(Message.length()>1023){
            eb3.addField("","Too long!", false);
        }else{
            eb3.addField("Json request: ", Message, false);
        }
        txtChannel.sendMessageEmbeds(eb3.build()).queue();
        eb3.clear();
    }


}
