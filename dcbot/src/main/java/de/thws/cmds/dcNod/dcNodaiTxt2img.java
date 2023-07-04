package de.thws.cmds.dcNod;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class dcNodaiTxt2img extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        switch (event.getName()) {

            case "gen":

                EmbedBuilder eb3 = new EmbedBuilder();
                String mode = event.getOption("mode").getAsString();

                switch (mode) {

                    case "default":

                        String finalPayload = getFinalPayload(event);

                        dcNodaiMisc.sendToLogger(event, dcNodaiMisc.getUsername(event), finalPayload, eb3);

                        try {
                            event.reply("Image is being Generated...").queue();

                            HttpURLConnection connection = sendRequestAndReceiveResponse(finalPayload);

                            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                                String jsonResponse = getJsonResponse(connection);

                                ObjectMapper objectMapper = new ObjectMapper();
                                JsonNode jsonNode = objectMapper.readTree(jsonResponse);

                                if (jsonNode.has("info") && jsonNode.get("info").isTextual()) {

                                    String info = jsonNode.get("info").asText();

                                    String currentSeed = getSeed(info);

                                    String[] splitInfo = info.split("ckpt_loc=", 2);

                                    String finalDate = getFinalDate();

                                    File folder = new File("D:/customsharksd/SHARK/apps/stable_diffusion/web/generated_imgs/" + finalDate);
                                    File[] listOfFiles = folder.listFiles();

                                    for (File listOfFile : listOfFiles) {
                                        if (listOfFile.isFile() && listOfFile.toString().contains(currentSeed)) {

                                            String FileName = "D:/customsharksd/SHARK/apps/stable_diffusion/web/generated_imgs/" + finalDate + "/" + listOfFile.getName();
                                            String upscale = event.getOption("upscale") == null ? dcNodaiconfig.defaults[3] : event.getOption("upscale").getAsString();

                                            switch (upscale) {
                                                case "no":
                                                    FileUpload aiImage = FileUpload.fromData(new File(FileName));
                                                    eb3.setTitle("Image informations:", null);
                                                    if (splitInfo[0].length() > 1023) {
                                                        eb3.addField("", "Too long!", false);
                                                    } else {
                                                        eb3.addField("", splitInfo[0], false);
                                                    }
                                                    eb3.addField("", splitInfo[1], false);
                                                    eb3.addField("Upscaling", "If you want to upscale the generated image please use /gen mode:Upscale path:" + finalDate + "/" + listOfFile.getName() + " seed:" + currentSeed + "", false);
                                                    event.getChannel().sendMessageEmbeds(eb3.build()).queue();
                                                    eb3.clear();
                                                    event.getChannel().sendFiles(aiImage).queue();
                                                    break;
                                                case "yes":

                                                    event.getChannel().sendMessage("Image generated! Now upscaling...").queue();
                                                    dcNodaiUpscaler.nodaiUpscale(FileName, currentSeed);
                                                    File[] newlistOfFiles = folder.listFiles();

                                                    for (File newlistOfFile : newlistOfFiles) {
                                                        if (newlistOfFile.toString().contains("upscale") && newlistOfFile.toString().contains(currentSeed)) {
                                                            FileName = "D:/customsharksd/SHARK/apps/stable_diffusion/web/generated_imgs/" + finalDate + "/" + newlistOfFile.getName();
                                                            FileUpload aiImageUpscaled = FileUpload.fromData(new File(FileName));
                                                            eb3.setTitle("Image informations:", null);
                                                            if (splitInfo[0].length() > 1023) {
                                                                eb3.addField("", "Too long!", false);
                                                            } else {
                                                                eb3.addField("", splitInfo[0], false);
                                                            }
                                                            eb3.addField("", splitInfo[1], false);
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

                            connection.disconnect();

                        } catch (Exception e) {
                        }

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

    @NotNull
    private static HttpURLConnection sendRequestAndReceiveResponse(String finalPayload) throws IOException {
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
        return connection;
    }

    private static String getSeed(String info) {
        String[] explode = info.split("seed=\\[", 2);
        String currentSeed = explode[1].split("\\]", 2)[0];
        return currentSeed;
    }

    @NotNull
    private static String getJsonResponse(HttpURLConnection connection) throws IOException {
        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder responseBuilder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            responseBuilder.append(line);
        }
        reader.close();

        String jsonResponse = responseBuilder.toString();
        return jsonResponse;
    }

    @NotNull
    private static String getFinalDate() {
        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        String stringDate = date.toString();
        String[] dateArray = stringDate.split("-", 3);
        String finalDate = dateArray[0] + dateArray[1] + dateArray[2];
        return finalDate;
    }

    @NotNull
    private static String getFinalPayload(SlashCommandInteractionEvent event) {
        String aPrompt;
        if (event.getOption("danid") != null) {
            aPrompt = dcDanbooruAPI.getDanbooruTags(event.getOption("danid").getAsInt());
        } else {
            if (event.getOption("prompt") == null) {
                aPrompt = "";
            } else {
                aPrompt = event.getOption("prompt").getAsString();
            }
        }

        String model = event.getOption("model") == null ? dcNodaiconfig.defaults[0] : event.getOption("model").getAsString();
        String size = event.getOption("size") == null ? dcNodaiconfig.defaults[1] : event.getOption("size").getAsString();
        int iter = event.getOption("iter") == null ? Integer.parseInt(dcNodaiconfig.defaults[2]) : Math.max(0, Math.min(60, event.getOption("iter").getAsInt()));
        int schedulerID = event.getOption("scheduler") == null ? Integer.parseInt(dcNodaiconfig.defaults[4]) : event.getOption("scheduler").getAsInt();
        String lora = event.getOption("lora") == null ? dcNodaiconfig.defaults[5] : event.getOption("lora").getAsString();
        double cfgscale = event.getOption("cfgscale") == null ? Double.parseDouble(dcNodaiconfig.defaults[6]) : Double.parseDouble(event.getOption("cfgscale").getAsString());

        String cPrompt = dcNodaiconfig.defaultPrompt + aPrompt;
        cPrompt = replaceSpecialCharacters(cPrompt);

        String[] splitSize = size.split("x", 2);
        int cHeight = Integer.parseInt(splitSize[0]);
        int cWidth = Integer.parseInt(splitSize[1]);

        String prompt = "\"prompt\": \"" + cPrompt + "\",";
        String negative_prompt = "\"negative_prompt\": \"" + dcNodaiconfig.defaultNegativePrompt + "\",";
        String steps = "\"steps\": " + iter + ",";
        String seed = "\"seed\": -1,";
        String height = "\"height\": " + cHeight + ",";
        String width = "\"width\": " + cWidth + ",";
        String cfg_scale = "\"cfg_scale\":" + cfgscale + ",";
        String hf_model_id = "\"hf_model_id\": \"" + model + "\",";
        String scheduler = "\"sampler\": \"" + dcNodaiconfig.scheduler_list_cpu_only[schedulerID] + "\",";
        String custom_lora_file = "\"custom_lora\": \"" + lora + "\"";
        String finalPayload = "{" + prompt + negative_prompt + steps + seed + height + width + cfg_scale + hf_model_id + scheduler + custom_lora_file + "}";
        return finalPayload;
    }

    @NotNull
    private static String replaceSpecialCharacters(String cPrompt) {
        cPrompt = cPrompt
                .replace("ä", "ae")
                .replace("ü", "ue")
                .replace("ä", "oe");
        return cPrompt;
    }
}
