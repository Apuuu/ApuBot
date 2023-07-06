package de.thws.cmds.dcNod;

public class fresh_dcNodaiconfig {
    //^ this is the class btw
    /*!!!!!!!!!!!!
    //BEFORE USING THIS BOT, MAKE SURE TO CHANGE THIS FILES NAME TO "dcNodaiconfig"
    *!!!!!!!!!!!!!
    */

    //Scheduler list
    static String[] scheduler_list_cpu_only = {
            "DDIM",
            "PNDM",
            "LMSDiscrete",
            "KDPM2Discrete",
            "DPMSolverMultistep",
            "EulerDiscrete",
            "EulerAncestralDiscrete",
            "SharkEulerDiscrete"
    };

    //Default settings here
    static String[] defaults = {
            "D:/customsharksd/SHARK/apps/stable_diffusion/web/models/diffusers/meinapastel_v6Pastel", //model huggingface id
            "768x512",   //image pixels, heightxwidth
            "30",   //default iterations
            "no",   //upscaling
            "0", //scheduler
            "", //lora
            "9" //cfg scale
    };

    /*List of all your converted models into diffuser. You can also just simply add a huggingface repo!
     *Don't forget to add those to your choices @dcBot.java
     */
    static String[] keywordsmodel = {"D:/sharksd/models/diffusers/animePastelDream_hardBakedVae",
            "D:/customsharksd/SHARK/apps/stable_diffusion/web/models/diffusers/meinapastel_v6Pastel",
            "D:/customsharksd/SHARK/apps/stable_diffusion/web/models/diffusers/naiplswork",
            "D:/customsharksd/SHARK/apps/stable_diffusion/web/models/diffusers/OrangemixsBakedVaev1",
            "D:/customsharksd/SHARK/apps/stable_diffusion/web/models/diffusers/shinymixbakedvae",
            "Lykon/DreamShaper",
            "prompthero/openjourney"
    };


    //List of all your lora models
    public static String[] keywordslora = {
            "D:/customsharksd/SHARK/apps/stable_diffusion/web/models/lora/niceeyes.safetensors",
            "D:/customsharksd/SHARK/apps/stable_diffusion/web/models/lora/kdllorav15.safetensors"
    };

    //This string will be added to the custom prompt
    //String defaultPrompt = "decorated,ultra detailed,masterpiece,high quality,4k,8k,";
    static String defaultPrompt = "decorated, high quality, (((masterpiece))),(((best quality))),(((extremely detailed))),(((fine details))),illustration,";

    //This string will be added to the custom negative prompt
    static String defaultNegativePrompt = "(((worst quality))), (((low quality))), multiple views, (((multiple legs))), (((missing arms))), missing legs, multiple panels, (((blurry, watermark))), letterbox, text, bad anatomy,(((bad finger))),bad hand,bad eyes,over 5 finger, wrong hand,long finger";

    //This is your local address to which you will send the json object for text to image
    static String txt2imgUrl = "http://127.0.0.1:8080/sdapi/v1/txt2img";

    static String outpaintingURL = "http://127.0.0.1:8080/sdapi/v1/outpaint";

    static String upscalerURL = "http://127.0.0.1:8080/sdapi/v1/upscaler";

    //This is your Discord bot token, keep its highly secret! People with access to this key are able to access your bot and do schabernack
    public static String token = "";

}
