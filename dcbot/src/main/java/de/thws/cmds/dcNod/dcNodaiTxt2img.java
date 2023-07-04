package de.thws.cmds.dcNod;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

                String model, size, aPrompt, upscale, lora;
                int iter, schedulerID;

                model = event.getOption("model") == null ? dcNodaiconfig.defaults[0] : event.getOption("model").getAsString();
                size = event.getOption("size") == null ? dcNodaiconfig.defaults[1] : event.getOption("size").getAsString();
                iter = event.getOption("iter") == null ? Integer.parseInt(dcNodaiconfig.defaults[2]) : Math.max(0, Math.min(60, event.getOption("iter").getAsInt()));
                upscale = event.getOption("upscale") == null ? dcNodaiconfig.defaults[3] : event.getOption("upscale").getAsString();
                schedulerID = event.getOption("scheduler") == null ? Integer.parseInt(dcNodaiconfig.defaults[4]) : event.getOption("scheduler").getAsInt();
                lora = event.getOption("lora") == null ? dcNodaiconfig.defaults[5] : event.getOption("lora").getAsString();

                if(event.getOption("danid") != null){
                    aPrompt = dcDanbooruAPI.getDanbooruTags(event.getOption("danid").getAsInt());
                }else{
                    if(event.getOption("prompt") == null){aPrompt = "";}else{aPrompt = event.getOption("prompt").getAsString();}
                }

                String[] splitSize = size.split("x",2);
                int cHeight = Integer.parseInt(splitSize[0]);
                int cWidth = Integer.parseInt(splitSize[1]);

                event.reply("Image is being Generated...").queue();

                String cPrompt = dcNodaiconfig.defaultPrompt+aPrompt;
                cPrompt = cPrompt.replace("ä","ae").replace("ü","ue").replace("ä","oe");

                String prompt = "\"prompt\": \""+cPrompt+"\",";
                String negative_prompt = "\"negative_prompt\": \""+dcNodaiconfig.defaultNegativePrompt+"\",";
                String steps = "\"steps\": "+iter+",";
                String seed = "\"seed\": -1,";
                String height = "\"height\": "+cHeight+",";
                String width = "\"width\": "+cWidth+",";
                String cfg_scale = "\"cfg_scale\": 11,";
                String hf_model_id = "\"hf_model_id\": \""+model+"\",";
                String scheduler = "\"sampler\": \""+dcNodaiconfig.scheduler_list_cpu_only[schedulerID]+"\",";
                String custom_lora_file = "\"custom_lora\": \""+lora+"\"";
                String finalPayload = "{"+prompt+negative_prompt+steps+seed+height+width+cfg_scale+hf_model_id+scheduler+custom_lora_file+"}";
                
                dcNodaiMisc.sendToLogger(event, dcNodaiMisc.getUsername(event), finalPayload, eb3);

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

                                                for(int j = 0; j < newlistOfFiles.length; j++){
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
                                                }
                                            break;
                                        }
                                    }
                                } 
                            }
                        } 
                    } 

                connection.disconnect();

                }catch (Exception e){}

                break;

                case "models":
                    dcNodaiMisc.sendModels(eb3, event);
                break;

                case "usage":
                    dcNodaiMisc.sendUsage(eb3, event);
                break;
                }
            break;
            
        }
    } 
}
