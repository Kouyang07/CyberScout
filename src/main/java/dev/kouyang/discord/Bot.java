package dev.kouyang.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import net.dv8tion.jda.internal.requests.restaction.CommandCreateActionImpl;

import java.util.ArrayList;
import java.util.List;

public class Bot {
    public static JDA jda;
    //System.getenv("DISCORD_TOKEN")
    public static void initBot(){
        JDA jda = JDABuilder.createDefault("")
                .setActivity(Activity.listening("CyberScouts"))
                .addEventListeners(new BotCommand())
                .build();
        //Guild guild = jda.getGuildById("830510645186920498");
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        List<CommandData> commandData = new ArrayList<>();
        OptionData team = new OptionData(OptionType.STRING, "team_number", "team_number");
        OptionData gemini = new OptionData(OptionType.STRING, "text", "text");
        commandData.add(Commands.slash("ranking", "input team number").addOptions(team));
        commandData.add(Commands.slash("scoutstatus", "input team number").addOptions(team));
        commandData.add(Commands.slash("data", "input team number").addOptions(team));
        commandData.add(Commands.slash("gemini", "input text").addOptions(gemini));
        commandData.add(Commands.slash("team evaluation", "input team number").addOptions(team));


        jda.updateCommands().addCommands(commandData).queue();
    }
}
