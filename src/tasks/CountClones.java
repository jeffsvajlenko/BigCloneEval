package tasks;

import java.sql.SQLException;
import java.util.concurrent.Callable;

import database.Clones;
import database.Tool;
import database.Tools;
import picocli.CommandLine;
import picocli.CommandLine.Mixin;
import tasks.MixinOptions.*;

@CommandLine.Command(
        name = "countClones",
        description = "Count the number of clones that have been imported for the tool.",
        mixinStandardHelpOptions = true,
        versionProvider = util.Version.class)
public class CountClones implements Callable<Void> {
    @Mixin
    private ToolId toolId;

    public static void main(String[] args) {
        new CommandLine(new CountClones()).execute(args);
    }

    public Void call() {
        try {
            Tool tool = Tools.getTool(toolId.id);
            if (tool == null) throw new IllegalArgumentException();
            long num = Clones.numClones(toolId.id);
            System.out.println(num);
        } catch (SQLException e) {
            System.err.println("\tSome error occured with the database connection or interaction.");
            System.err.println("\tPlease try a fresh copy of the datbase, and report the error to.");
            System.err.println("\tthe developers.");
            e.printStackTrace(System.err);
            System.exit(-1);
        } catch (IllegalArgumentException e) {
            System.err.println("\tNo tool exists with the ID " + toolId.id + " .");
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
//			System.out.println("::::::::::::::::::::::::: BigCloneBench - Count Clones :::::::::::::::::::::::::");
//			System.out.println(" Specify the ID of the tool.  Or blank to cancel.");
//
//			while(true) {
//				System.out.println();
//				System.out.print(" ID: ");
//				try {
//					String line = scanner.nextLine();
//					if(line.equals("")) {
//						System.out.println();
//						System.out.println("    Count cloneshas been canceled.");
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
//			long num = Clones.numClones(id);
//
//			System.out.println();
//			System.out.println("    There are " + num + " clones associated with this tool.");
//			System.out.println();
//			System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
//
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

