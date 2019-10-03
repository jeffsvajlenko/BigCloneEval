package tasks;

import database.Clones;
import database.Tool;
import database.Tools;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "importClones",
        description = "Imports the clones detected by a tool in IJaDataset into the framework for evaluation. " +
                "Clones are provided as clone pairs in a simple CSV file. " +
                "See documentation below for the expected format.",
        mixinStandardHelpOptions = true,
        versionProvider = util.Version.class)
public class ImportClones implements Callable<Void> {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    @CommandLine.Mixin
    private MixinOptions.ToolId toolId;

    @CommandLine.Mixin
    private MixinOptions.InputFile clones;

    public static void main(String[] args) {
        new CommandLine(new ImportClones()).execute(args);
    }

    @Override
    public Void call() {
        try {
            Tool tool = Tools.getTool(toolId.id);
            if (tool == null) throw new IllegalArgumentException();
            long num = Clones.importClones(toolId.id, clones.input.toPath());
            System.out.println(num);
        } catch (SQLException e) {
            System.err.println("\tSome error occured with the database connection or interaction.");
            System.err.println("\n\tPlease try a fresh copy of the datbase, and report the error to.");
            System.err.println("\n\tthe developers.");
            e.printStackTrace(System.err);
            System.exit(-1);
        } catch (NumberFormatException e) {
            System.err.println("\tInvalid tool identifier value.");
            System.exit(-1);
        } catch (IllegalArgumentException e) {
            System.err.println("\tNo tool exists with the ID " + toolId.id + " .");
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("IOException reading clone file.");
            e.printStackTrace(System.err);
            System.exit(-1);
        }
        return null;
    }

    public static void interactive() {
        Scanner scanner = new Scanner(System.in);
        long id;
        Path clones;
        long numclones;
        try {
            System.out.println();
            System.out.println(":::::::::::::::::::::::: BigCloneBench - Import CLones ::::::::::::::::::::::::");
            System.out.println(" Specify the tool and file containing the clones to import.");
            System.out.println(" Blank input cancels.");
            System.out.println();

            // Get Tool ID
            while (true) {
                System.out.print(" Tool ID: ");
                String line = scanner.nextLine();
                if (line.equals("")) {
                    scanner.close();
                    System.out.println("    Import canceled.");
                    System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                    System.out.println();
                    return;
                }
                try {
                    id = Long.parseLong(line);
                } catch (NumberFormatException e) {
                    System.out.println();
                    System.out.println("    Invalid ID.");
                    System.out.println();
                    continue;
                }
                Tool tool = Tools.getTool(id);
                if (tool == null) {
                    System.out.println();
                    System.out.println("    No tool with the given ID.");
                    System.out.println();
                    continue;
                }
                break;
            }

            System.out.println();

            // Get Clone File
            while (true) {
                System.out.print(" Clone file: ");
                String line = scanner.nextLine();
                if (line.equals("")) {
                    scanner.close();
                    System.out.println("    Import canceled.");
                    System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                    System.out.println();
                    return;
                }
                scanner.close();

                try {
                    clones = Paths.get(line);
                } catch (InvalidPathException e) {
                    System.out.println();
                    System.out.println("    Invalid path.");
                    System.out.println();
                    continue;
                }

                if (!Files.isRegularFile(clones)) {
                    System.out.println();
                    System.out.println("    Is not a regular file.");
                    System.out.println();
                    continue;
                }

                if (!Files.isReadable(clones)) {
                    System.out.println();
                    System.out.println("    Is not readable.");
                    System.out.println();
                    continue;
                }

                long time = System.currentTimeMillis();
                try {
                    numclones = Clones.importClones(id, clones);
                    time = System.currentTimeMillis() - time;
                } catch (IOException e) {
                    System.out.println("    ERROR: IOException reading clone file.");
                    e.printStackTrace();
                    System.out.println();
                    System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                    System.out.println();
                    return;
                }

                System.out.println();
                System.out.println("    Successfully imported " + numclones + " clones.");
                System.out.println();
                System.out.println("    Total time: " + time / 1000.0 + " seconds.");
                break;
            }

            System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
            System.out.println();
        } catch (SQLException e) {
            scanner.close();
            System.out.println("    ERROR: Database connection or schema error.");
            e.printStackTrace();
            System.out.println();
            System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
            System.out.println();
            return;
        }
    }

}
