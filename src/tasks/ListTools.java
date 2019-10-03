package tasks;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.text.WordUtils;

import database.Tool;
import database.Tools;
import picocli.CommandLine;

@CommandLine.Command(
        name = "listTools",
        description = "Lists the tools registered in the database. " +
                "Including their ID, name and description.",
        mixinStandardHelpOptions = true,
        versionProvider = util.Version.class)
public class ListTools implements Callable<Void> {

    public static void main(String[] args) {
        new CommandLine(new ListTools()).execute(args);
    }

    public Void call() {
        noninteractive();
        return null;
    }

    private void noninteractive() {
        try {
            List<Tool> tools = Tools.getTools();
            for (Tool tool : tools) {
                System.out.println(tool.getId());
                System.out.println(tool.getName());
                System.out.println(tool.getDescription());
                System.out.println();
            }
        } catch (SQLException e) {
            System.err.println("Some problem with the database!");
            e.printStackTrace(System.err);
            System.exit(-1);
        }
    }

    private void interactive() {
        try {
            System.out.println();
            System.out.println(":::::::::::::::::::::::::: BigCloneEval - List Tools :::::::::::::::::::::::::");
            System.out.println();
            List<Tool> tools = Tools.getTools();
            if (tools.size() != 0) {
                for (Tool tool : tools) {
                    String description = WordUtils.wrap(tool.getDescription(), 75, "\n    ", true);
                    System.out.println("[" + tool.getId() + "] - " + tool.getName());
                    System.out.println("    " + description);
                    System.out.println();
                }
            } else {
                System.out.println("\tNo tools have been added.");
                System.out.println("");
            }
            System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
            System.out.println();
        } catch (SQLException e) {
            System.out.println("\tERROR: Problem with database connection or schema.");
            System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
            System.out.println();
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
