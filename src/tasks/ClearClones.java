package tasks;

import java.sql.SQLException;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Mixin;
import tasks.MixinOptions.*;

import database.Clones;
import database.Tool;
import database.Tools;

@CommandLine.Command(
        name = "clearClones",
        mixinStandardHelpOptions = true,
        description = "Removes the imported clones for the specified registered tool.")
public class ClearClones implements Callable<Void> {
    @Mixin
    private ToolId toolId;

    public static void main(String[] args) {
        new CommandLine(new ClearClones()).execute(args);
    }

    public Void call() {
        try {
            Tool tool = Tools.getTool(toolId.id);
            if (tool == null) throw new IllegalArgumentException();
            int num = Clones.clearClones(toolId.id);
            System.out.println(num);
        } catch (SQLException e) {
            System.err.println("\tSome error occurred with the database connection or interaction.");
            System.err.println("\n\tPlease try a fresh copy of the database, and report the error to.");
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
//		long id;
//		Scanner scanner = new Scanner(System.in);
//		
//		try {
//			System.out.println();
//			System.out.println("::::::::::::::::::::::::: BigCloneBench - Clear Clones :::::::::::::::::::::::::");
//			System.out.println(" Specify the ID of the tool whose clones should be removed.  Or provide a ");
//			System.out.println(" blank response to cancel.");
//			
//			
//			while(true) {
//				System.out.println();
//				System.out.print(" ID: ");
//				try {
//					String line = scanner.nextLine();
//					if(line.equals("")) {
//						System.out.println();
//						System.out.println("    Delete tool has been canceled.");
//						System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
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
//			int num = Clones.clearClones(id);
//			
//			System.out.println();
//			System.out.println("    " + num + " clones have been removed.");
//			System.out.println();
//			System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
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
