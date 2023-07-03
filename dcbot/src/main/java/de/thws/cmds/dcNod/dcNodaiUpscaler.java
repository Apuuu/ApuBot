package de.thws.cmds.dcNod;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;

public class dcNodaiUpscaler extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
        
        switch(event.getName()){

            case "gen":
            
                String mode = event.getOption("mode").getAsString();

                switch(mode){

                    case "soloupscale":

                        String FilePath = "D:/customsharksd/SHARK/apps/stable_diffusion/web/generated_imgs/"+event.getOption("path").getAsString();
                        String FileSeed = event.getOption("seed").getAsString();

                        long millis = System.currentTimeMillis();
                        java.sql.Date date = new java.sql.Date(millis);
                        String stringDate = date.toString();
                        String[] dateArray = stringDate.split("-",3);
                        String finalDate = dateArray[0]+dateArray[1]+dateArray[2];

                        event.reply("Image being upscaled...").queue();

                        dcNodaiUpscaler.nodaiUpscale(FilePath, FileSeed);
                        System.out.println(FilePath);

                        File folder = new File("D:/customsharksd/SHARK/apps/stable_diffusion/web/generated_imgs/"+finalDate);
                        File[] newlistOfFiles = folder.listFiles();

                        for(int j = 0; j < newlistOfFiles.length; j++){
                            if(newlistOfFiles[j].toString().contains("upscale") && newlistOfFiles[j].toString().contains(FileSeed)){

                                FilePath = "D:/customsharksd/SHARK/apps/stable_diffusion/web/generated_imgs/"+finalDate+"/"+newlistOfFiles[j].getName();
                                FileUpload aiImageUpscaled = FileUpload.fromData(new File(FilePath));
                                event.getChannel().sendFiles(aiImageUpscaled).queue();
                                System.out.println(FilePath);

                            }
                        }

                    break;
                }

            break;

        }
    
    
    }
    
    public static String imageToBase64(String imagePath) {
        try {
            Path path = Paths.get(imagePath);
            byte[] imageBytes = Files.readAllBytes(path);
            String base64Image = "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
            return base64Image;
        } catch (IOException e) {
            return null;
        }
    }

    public static void nodaiUpscale(String imagePath, String cSeed){
       
        //String Neg = "worst quality, low quality, multiple views, multiple legs, missing arms, missing legs, multiple panels, blurry, watermark, letterbox, text, bad anatomy,bad finger,bad hand,bad eyes,over 5 finger, wrong hand,long finger";       
        String Neg = "";
        String url = "http://127.0.0.1:8080/";

         try{   

            String[] base64Data = {imageToBase64(imagePath)};
            String model = "stabilityai/stable-diffusion-x4-upscaler";

            Long seedUp = Long.parseLong(cSeed);

            String prompt = "\"prompt\": \"upscale\",";
            String negative_prompt = "\"negative_prompt\": \""+Neg+"\",";
            String seed = "\"seed\": "+seedUp+",";
            String height = "\"height\": "+512+",";
            String width = "\"width\": "+768+",";
            String cfg_scale = "\"cfg_scale\": 7.5,";
            String init_imagesjson = "\"init_images\": [\""+base64Data[0]+"\"],";
            String steps = "\"steps\": "+10+",";
            String noise_level = "\"noise_level\": "+5+",";
            String hf_model_id = "\"hf_model_id\": \""+model+"\",";
            String scheduler = "\"sampler\": \"EulerDiscrete\"";

            String finalPayload = "{"+prompt+negative_prompt+seed+height+width+cfg_scale+init_imagesjson+steps+noise_level+hf_model_id+scheduler+"}";

            String urlwhole = url+"sdapi/v1/upscaler";
            URL endpointUrlUp = new URL(urlwhole);

            HttpURLConnection connection2 = (HttpURLConnection) endpointUrlUp.openConnection();

            connection2.setRequestMethod("POST");
            connection2.setRequestProperty("Content-Type", "application/json");
            connection2.setRequestProperty("Accept", "application/json");
            connection2.setDoOutput(true);

            OutputStream outputStream = connection2.getOutputStream();
            outputStream.write(finalPayload.getBytes());
            outputStream.flush();
            outputStream.close();

            int responseCode = connection2.getResponseCode();

            System.out.println(responseCode);

            connection2.disconnect();

            }catch (Exception e) {
                e.printStackTrace();
            }
    
    }
    
}
