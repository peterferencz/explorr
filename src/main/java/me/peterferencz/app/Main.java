package me.peterferencz.app;

import java.util.jar.JarFile;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import me.peterferencz.app.jar.JarFileHandler;
import me.peterferencz.ui.Display;

public class Main {

    private static Context globalContext = new Context();
    
    public static Context getGlobalContext() { return globalContext; }

    public static void main(String[] args) {
        new Display();

        JarFileHandler.subscribeToEvents();

        Options options = new Options(){{
            addOption("h", "help", false, "Display available flags");
            addOption("i", "input", true, "Specify the input jar file");
        }};

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                formatter.printHelp("java -jar jarexplor.jar [options]", options);
                System.exit(0);
            }

            if (cmd.hasOption("i")) {
                String input = cmd.getOptionValue("i");
                try {
                    globalContext.setJarFile(new JarFile(new File(input)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}