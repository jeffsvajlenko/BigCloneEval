package tasks;

import java.sql.SQLException;
//import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import database.Clones;
import database.Tool;
import database.Tools;

public class CountClones {

	private static Options options;
	private static HelpFormatter formatter;
	
	public static void panic(int exitval) {
		formatter.printHelp(200, "countClones", "BigCloneEval-CountClones", options, "", true);
		System.exit(exitval);
		return;
	}	
	
	public static void main(String args[]) {
		options = new Options();
		
		//options.addOption(Option.builder("i")
		//						.longOpt("interactive")
		//						.desc("Enables interactive mode for using delete tool.")
		//				        .build()
		//);
		
		options.addOption(Option.builder("t")
								.longOpt("tool")
								.hasArg()
								.argName("ID")
								.desc("The ID of the tool to delete.")
								.required()
								.build()
		);
		
		options.addOption(Option.builder("h")
				.longOpt("help")
				.desc("Prints this usage information.")
				.build()
		);
		
		formatter = new HelpFormatter();
		formatter.setOptionComparator(null);
		CommandLineParser parser = new DefaultParser();
		CommandLine line;
		try {
			line = parser.parse(options, args);
		} catch(Exception e) {
			System.err.println(e.getMessage());
			panic(-1);
			return;
		}
		
		if(line.hasOption("h")) {
			panic(0);
		//} else if(line.hasOption("i")) {
		//	interactive();
		} else if (line.hasOption("t")) {
			String sid = line.getOptionValue("t");

			long id=-1;
			try {
				id = Long.parseLong(sid);
				Tool tool = Tools.getTool(id);
				if(tool == null) throw new IllegalArgumentException();
				long num = Clones.numClones(id);
				System.out.println(num);
			} catch (SQLException e) {
				System.err.println("\tSome error occured with the database connection or interaction.");
				System.err.println("\tPlease try a fresh copy of the datbase, and report the error to.");
				System.err.println("\tthe developers.");
				e.printStackTrace(System.err);
				System.exit(-1);
			} catch (NumberFormatException e) {
				System.err.println("\tInvalid tool identifier value.");
				System.exit(-1);
			} catch (IllegalArgumentException e) {
				System.err.println("\tNo tool exists with the ID " + id + " .");
				System.exit(-1);
			}
		} else {
			panic(-1);
		}
		
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
