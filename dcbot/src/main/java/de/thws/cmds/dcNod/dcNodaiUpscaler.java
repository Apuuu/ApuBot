package de.thws.cmds.dcNod;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class dcNodaiUpscaler extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        switch (event.getName()) {

            case "gen":

                String mode = event.getOption("mode").getAsString();

                switch (mode) {

                    /*case "soloupscale":

                        String FilePath = "D:/customsharksd/SHARK/apps/stable_diffusion/web/generated_imgs/" + event.getOption("path").getAsString();
                        String FileSeed = event.getOption("seed").getAsString();

                        long millis = System.currentTimeMillis();
                        java.sql.Date date = new java.sql.Date(millis);
                        String stringDate = date.toString();
                        String[] dateArray = stringDate.split("-", 3);
                        String finalDate = dateArray[0] + dateArray[1] + dateArray[2];

                        event.reply("Image being upscaled...").queue();

                        dcNodaiUpscaler.nodaiUpscale(FilePath, FileSeed);
                        System.out.println(FilePath);

                        File folder = new File("D:/customsharksd/SHARK/apps/stable_diffusion/web/generated_imgs/" + finalDate);
                        File[] newlistOfFiles = folder.listFiles();

                        for (int j = 0; j < newlistOfFiles.length; j++) {
                            if (newlistOfFiles[j].toString().contains("upscale") && newlistOfFiles[j].toString().contains(FileSeed)) {

                                FilePath = "D:/customsharksd/SHARK/apps/stable_diffusion/web/generated_imgs/" + finalDate + "/" + newlistOfFiles[j].getName();
                                FileUpload aiImageUpscaled = FileUpload.fromData(new File(FilePath));
                                event.getChannel().sendFiles(aiImageUpscaled).queue();
                                System.out.println(FilePath);

                            }
                        }

                        break;*/
                }

                break;

        }


    }

    public static void nodaiUpscale(SlashCommandInteractionEvent event, String imageData) {

        try {

            String finalPayload = JsonBuilder(imageData);

            HttpURLConnection connection2 = sendRequestAndReceiveResponse(finalPayload);

            String jsonResponse = dcNodaiTxt2img.getJsonResponse(connection2);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);

            String imageDataNew = dcNodaiTxt2img.getImageData(jsonNode);

            dcNodaiTxt2img.sendImageToChat(event, imageDataNew);

            connection2.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @NotNull
    private static HttpURLConnection sendRequestAndReceiveResponse(String finalPayload) throws IOException {
        URL endpointUrlUp = new URL(dcNodaiconfig.upscalerURL);

        HttpURLConnection connection2 = (HttpURLConnection) endpointUrlUp.openConnection();

        connection2.setRequestMethod("POST");
        connection2.setRequestProperty("Content-Type", "application/json");
        connection2.setRequestProperty("Accept", "application/json");
        connection2.setDoOutput(true);

        OutputStream outputStream = connection2.getOutputStream();
        outputStream.write(finalPayload.getBytes());
        outputStream.flush();
        outputStream.close();
        return connection2;
    }

    @NotNull
    private static String JsonBuilder(String imageData) {
        String model = "stabilityai/stable-diffusion-x4-upscaler";

        String prompt = "\"prompt\": \"upscale\",";
        String negative_prompt = "\"negative_prompt\": \"" + dcNodaiconfig.defaultNegativePrompt + "\",";
        String seed = "\"seed\": " + -1 + ",";
        String height = "\"height\": " + 512 + ",";
        String width = "\"width\": " + 768 + ",";
        String cfg_scale = "\"cfg_scale\": 7.5,";
        String init_imagesjson = "\"init_images\": [\"" + imageData + "\"],";
        String steps = "\"steps\": " + 20 + ",";
        String noise_level = "\"noise_level\": " + 20 + ",";
        String hf_model_id = "\"hf_model_id\": \"" + model + "\",";
        String scheduler = "\"sampler\": \"EulerDiscrete\"";

        String finalPayload = "{" + prompt + negative_prompt + seed + height + width + cfg_scale + init_imagesjson + steps + noise_level + hf_model_id + scheduler + "}";
        return finalPayload;
    }

}
