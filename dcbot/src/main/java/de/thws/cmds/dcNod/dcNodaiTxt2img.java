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

    public static void requestImage(SlashCommandInteractionEvent event, EmbedBuilder eb3) {
        String finalPayload = getFinalPayload(event);

        dcNodaiMisc.sendToLogger(event, dcNodaiMisc.getUsername(event), finalPayload, eb3);

        try {
            event.reply("Image is being Generated...").queue();

            HttpURLConnection connection = sendRequestAndReceiveResponse(finalPayload);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                String jsonResponse = getJsonResponse(connection);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(jsonResponse);

                String info = jsonNode.get("info").asText();
                String imageData = getImageData(jsonNode);

                String[] splitInfo = info.split("ckpt_loc=", 2);

                String upscale = event.getOption("upscale") == null ? dcNodaiconfig.defaults[3] : event.getOption("upscale").getAsString();

                switch (upscale) {

                    case "no":
                        eb3.setTitle("Image informations:", null);
                        if (splitInfo[0].length() > 1023) {
                            eb3.addField("", "Too long!", false);
                        } else {
                            eb3.addField("", splitInfo[0], false);
                        }
                        eb3.addField("", splitInfo[1], false);
                        event.getChannel().sendMessageEmbeds(eb3.build()).queue();
                        eb3.clear();
                        sendImageToChat(event, imageData);
                        break;

                    case "yes":

                        event.getChannel().sendMessage("Image generated! Now upscaling...").queue();
                        dcNodaiUpscaler.nodaiUpscale(event, imageData);

                        break;

                }

            }

            connection.disconnect();

        } catch (Exception e) {
        }
    }

    public static void sendImageToChat(SlashCommandInteractionEvent event, String imageData) {
        File imageBuffered = dcNodaiMisc.convertBase64ToPNG(imageData);
        FileUpload aiImage = FileUpload.fromData(imageBuffered);
        event.getChannel().sendFiles(aiImage).queue();
    }

    public static String getImageData(JsonNode jsonNode) {
        String imageData;
        imageData = jsonNode.get("images").toString()
                .replace("[", "")
                .replace("]", "")
                .replace("\"", "");

        return imageData;
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
    public static String getJsonResponse(HttpURLConnection connection) throws IOException {
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
    private static String getFinalPayload(SlashCommandInteractionEvent event) {

        String aPrompt = getPromptFromSource(event);

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
    private static String getPromptFromSource(SlashCommandInteractionEvent event) {
        String aPrompt;

        if (event.getOption("danid") != null) {
            aPrompt = dcDanbooruAPI.getDanbooruTags(event.getOption("danid").getAsInt());
        } else if (event.getOption("rdmdanid") != null && event.getOption("danid") == null) {
            aPrompt = dcDanbooruAPI.getDanbooruTags((int) (Math.random() * 6400000));
        } else {
            if (event.getOption("prompt") == null) {
                aPrompt = "";
            } else {
                aPrompt = event.getOption("prompt").getAsString();
            }
        }
        return aPrompt;
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
