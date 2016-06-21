package tasks;

import java.sql.SQLException;
//import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import database.Tools;

public class RegisterTool {
	
	private static Options options;
	private static HelpFormatter formatter;
	
	public static void panic(int exitval) {
		formatter.printHelp(200, "addTool", "BigCloneEval-AddTool", options, "", true);
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
		
		options.addOption(Option.builder("n")
				                .longOpt("name")
				                .hasArg()
				                .argName("string")
				                .desc("A name for the tool.  Use quotes to allow spaces and special characters.")
				                .required()
				                .build()
		);
		
		options.addOption(Option.builder("d")
								.required()
							    .longOpt("description")
                                .hasArg()
                                .argName("string")
                                .desc("A description for the tool.  Use quotes to allow spaces and special characters.")
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
		} else if (line.hasOption("n") && line.hasOption("d")) {
			String desc = line.getOptionValue("d");
			String name = line.getOptionValue("n");
			
			long id = -1;
			try {
				id = Tools.addTool(name, desc);
				System.out.println(id);
			} catch (SQLException e) {
				System.err.println("\tSome error occured with the database connection or interaction.");
				System.err.println("\tPlease try a fresh copy of the datbase, and report the error to.");
				System.err.println("\tthe developers.");
				e.printStackTrace(System.err);
				System.exit(-1);
			}
		} else {
			panic(-1);
		}
		
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
