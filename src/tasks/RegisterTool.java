package tasks;

import java.sql.SQLException;
import java.util.concurrent.Callable;
//import java.util.Scanner;

import database.Tools;
import picocli.CommandLine;

@CommandLine.Command(
        name = "registerTool",
        description = "Registers a clone detection tool with the framework. " +
                "Requires a name and description of the tool, which is stored in the tools database. " +
                "Returns a unique identifier for the tool for indicating the target tool for the other commands. " +
                "Name and description are for reference by the user.",
        mixinStandardHelpOptions = true,
        versionProvider = util.Version.class)
public class RegisterTool implements Callable<Void> {

    @CommandLine.Option(
            names = {"-n", "--name"},
            required = true,
            description = "A name for the tool.  Use quotes to allow spaces and special characters.",
            paramLabel = "<string>"
    )
    private String name;

    @CommandLine.Option(
            names = {"-d", "--description"},
            required = true,
            description = "A description for the tool.  Use quotes to allow spaces and special characters.",
            paramLabel = "<string>"
    )
    private String description;

    public static void main(String[] args) {
        new CommandLine(new RegisterTool()).execute(args);
    }

    public Void call() {
        try {
            long id = Tools.addTool(name, description);
            System.out.println(id);
        } catch (SQLException e) {
            System.err.println("\tSome error occured with the database connection or interaction.");
            System.err.println("\tPlease try a fresh copy of the datbase, and report the error to.");
            System.err.println("\tthe developers.");
            e.printStackTrace(System.err);
            System.exit(-1);
        }
        return null;
    }

//	public static void interactive() {
//		Scanner in = new Scanner(System.in);
//		
//		System.out.println();
//		System.out.println("::::::::::::::::::::::::::: BigCloneBench - Add Tool ::::::::::::::::::::::::::");
//		System.out.println(" Provide a name and description for this tool.  A newline ends the input.");
//		System.out.println(" Provide a blank response for either to cancel.");
//		System.out.println();
//		System.out.print(" Name: ");
//		String name = in.nextLine();
//		if(name.equals("")) {
//			System.out.println();
//			System.out.println("    Add tool has been canceled.");
//			System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
//			System.out.println();
//			in.close();
//			return;
//		}
//			
//		System.out.println();
//		System.out.print(" Description: ");
//		String description = in.nextLine();
//		if(description.equals("")) {
//			System.out.println();
//			System.out.println("    Add tool has been canceled.");
//			System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
//			System.out.println();
//			in.close();
//			return;
//		}
//		
//		System.out.println();
//			
//		long id;
//		try {
//			id = Tools.addTool(name, description);
//		} catch (SQLException e) {
//			System.err.println("ERROR: Problem with database connection or schema.");
//			e.printStackTrace();
//			in.close();
//			System.exit(-1);
//			return;
//		}
//		
//		System.out.println("        Tool was added with ID: " + id);
//		System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
//		
//		in.close();
//	}

}
