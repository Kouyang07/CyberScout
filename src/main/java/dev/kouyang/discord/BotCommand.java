package dev.kouyang.discord;

import dev.kouyang.api.APIUtil;
import dev.kouyang.database.GetSheetData;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.collections4.Get;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class BotCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply().queue(); // Acknowledge immediately with a "thinking..." message

        // Perform your long-running task asynchronously
        GetSheetData sheetData = new GetSheetData();
        if (event.getName().equals("ranking")) {
            // event.reply(Objects.requireNonNull(event.getOption("text")).getAsString()).queue();
            event.reply("its not up to date yet :D").queue();
            //reply with the created infos gathered from the API and deep seek

        }
        else if (event.getName().equals("scoutstatus")) {
            if (sheetData.getTeam(String.valueOf(event.getOption("team"))) == -1) {
                event.deferReply().queue();
                event.getHook().sendMessage("Nothing here yet, better get scouting!").queue();
            }
                else {
                event.deferReply().queue();
                event.getHook().sendMessage("Yep! I see it! Great work scouter! :D Now get scouting again!!!").queue();

            }
        }
        else if (event.getName().equals("data")) {
            event.deferReply().queue();
            String response = sheetData.getAlldata(Objects.requireNonNull(event.getOption("team")).getAsInt());
            event.getHook().sendMessage(response).queue();
        }
        else if(event.getName().equals("gemini")) {
            //System.out.println("gemini");
            event.deferReply().queue();
            String response = APIUtil.requestGemini(Objects.requireNonNull(event.getOption("text")).getAsString());
            event.getHook().sendMessage(response).queue();
        }
        else if (event.getName().equals("team evaluation")){
            event.deferReply().queue();
            //String response = APIUtil.requestGemini();
           // event.getHook().sendMessage(response).queue();
        } else {
            event.reply("Unknown command").setEphemeral(true).queue();
        }
    }

}
