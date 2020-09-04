package tasks;

import java.sql.SQLException;
import java.util.concurrent.Callable;
//import java.util.Scanner;

import picocli.CommandLine;
import picocli.CommandLine.Mixin;
import tasks.MixinOptions.*;

import database.Tool;
import database.Tools;

@CommandLine.Command(
        name = "deleteTool",
        description = "Deletes a tool, specified by its ID, from the framework. Also removes any imported clones for this tool.",
        mixinStandardHelpOptions = true,
        versionProvider = util.Version.class)
public class DeleteTool implements Callable<Void> {
    @Mixin
    private ToolId toolId;

    public static void main(String[] args) {
        new CommandLine(new DeleteTool()).execute(args);
    }

    public Void call() {
        try {
            Tool tool = Tools.getTool(toolId.id);
            if (tool == null) throw new IllegalArgumentException();
            Tools.deleteToolAndData(toolId.id);
        } catch (SQLException e) {
            System.err.println("\tSome error occured with the database connection or interaction.");
            System.err.println("\n\tPlease try a fresh copy of the datbase, and report the error to.");
            System.err.println("\n\tthe developers.");
            e.printStackTrace(System.err);
            System.exit(-1);
        } catch (IllegalArgumentException e) {
            System.err.println("\tNo tool exists with the ID" + toolId.id + " .");
            System.exit(-1);
        }
        return null;
    }

//	public static void interactive() {
//		Scanner scanner = new Scanner(System.in);
//		long id = -1;
//		try {
//			System.out.println();
//			System.out.println("::::::::::::::::::::::::: BigCloneBench - Delete Tool :::::::::::::::::::::::::");
//			System.out.println(" Specify the ID of the tool to be deleted.");
//			System.out.println(" This will also delete its imported clones and evaluation data.");
//			System.out.println(" Provide a blank response to cancel.");
//			
//			while(true) {
//				System.out.println();
//				System.out.print(" ID: ");
//				try {
//					String line = scanner.nextLine();
//					if(line.equals("")) {
//						System.out.println();
//						System.out.println("    Delete tool has been canceled.");
//						System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
//						System.out.println();
//						return;
//					}
//					id = Long.parseLong(line);
//				} catch (NumberFormatException e) {
//					System.out.println();
//					System.out.println("    Invalid ID value.");
//					continue;
//				}
//				Tool tool = Tools.getTool(id);
//				if(tool == null) {
//					System.out.println();
//					System.out.println("    Tool with ID " + id + " does not exist.");
//					continue;
//				}
//				break;
//			}
//			
//			Tools.deleteToolAndData(id);
//			
//			
//			System.out.println();
//			System.out.println("    The tool with ID " + id + " has been deleted.");
//			System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
//			System.out.println();
//			
//			scanner.close();
//		} catch (SQLException e) {
//			System.out.println("    ERROR: Problem with database connection or schema.");
//			System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
//			System.out.println();
//			e.printStackTrace();
//			scanner.close();
//			System.exit(-1);
//		}
//	}

}
