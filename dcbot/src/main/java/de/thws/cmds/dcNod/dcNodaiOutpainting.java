package de.thws.cmds.dcNod;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class dcNodaiOutpainting extends ListenerAdapter {

    public static void outpainter(SlashCommandInteractionEvent event) {

        event.reply("WIP").queue();

    }

    public static void expandImg(SlashCommandInteractionEvent event) {

        try {

            String JsonObject = buildJsonObject(event);
            event.reply("Extending...").queue();

            URL endpointUrl = new URL(dcNodaiconfig.outpaintingURL);
            HttpURLConnection connection3 = sendRequestAndReceiveResponse(JsonObject);

            System.out.println(connection3.getResponseCode());

            connection3.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String buildJsonObject(SlashCommandInteractionEvent event) {

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

        String model = "stabilityai/stable-diffusion-2-inpainting";
        //String FilePath = "D:/customsharksd/SHARK/apps/stable_diffusion/web/generated_imgs/20230706/decorated__high_387506457_230706_182739.png";
        String FilePath = "D:/customsharksd/SHARK/apps/stable_diffusion/web/generated_imgs/" + event.getOption("path").getAsString();
        //String size = event.getOption("size") == null ? dcNodaiconfig.defaults[1] : event.getOption("size").getAsString();
        int iter = event.getOption("iter") == null ? Integer.parseInt(dcNodaiconfig.defaults[2]) : Math.max(0, Math.min(60, event.getOption("iter").getAsInt()));
        int schedulerID = event.getOption("scheduler") == null ? Integer.parseInt(dcNodaiconfig.defaults[4]) : event.getOption("scheduler").getAsInt();

        String dirtest = "[\"right\",\"left\"]";
        double cfgscale = event.getOption("cfgscale") == null ? Double.parseDouble(dcNodaiconfig.defaults[6]) : Double.parseDouble(event.getOption("cfgscale").getAsString());
        double color_v = 0.03;
        int mask_b = 8;
        String cPrompt = dcNodaiconfig.defaultPrompt + aPrompt;
        String[] base64Data = {dcNodaiMisc.imageToBase64(FilePath)};

        String prompt = "\"prompt\": \"" + cPrompt + "\",";
        String negative_prompt = "\"negative_prompt\": \"" + dcNodaiconfig.defaultNegativePrompt + "\",";
        String steps = "\"steps\": " + iter + ",";
        String pixels = "\"pixels\": " + 256 + ",";
        String noise_q = "\"noise_q\": " + 1 + ",";
        String mask_blur = "\"mask_blur\": " + mask_b + ",";
        String color_variations = "\"color_variation\": " + color_v + ",";
        String directions = "\"directions\": " + dirtest + ",";
        String seed = "\"seed\": -1,";
        String height = "\"height\": " + 768 + ",";
        String width = "\"width\": " + 512 + ",";
        String init_imagesjson = "\"init_images\": [\"" + base64Data[0] + "\"],";
        String cfg_scale = "\"cfg_scale\":" + cfgscale + ",";
        String hf_model_id = "\"hf_model_id\": \"" + model + "\",";
        String scheduler = "\"sampler\": \"" + dcNodaiconfig.scheduler_list_cpu_only[schedulerID] + "\",";
        String custom_lora_file = "\"custom_lora\": \"" + dcNodaiconfig.defaults[5] + "\"";

        String JsonObject = "{" + prompt + negative_prompt + steps + seed + height + width + cfg_scale + hf_model_id + scheduler + init_imagesjson + directions + noise_q + color_variations + mask_blur + pixels + custom_lora_file + "}";
        return JsonObject;
    }

    private static HttpURLConnection sendRequestAndReceiveResponse(String finalPayload) throws IOException {

        URL endpointUrl = new URL(dcNodaiconfig.outpaintingURL);
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

}
