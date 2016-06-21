package tasks;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.text.WordUtils;

import database.Tool;
import database.Tools;

public class ListTools {
	
	private static Options options;
	private static HelpFormatter formatter;
	
	public static void panic(int exitval) {
		formatter.printHelp(200, "addTool", "BigCloneEval-ListTools", options, "", true);
		System.exit(exitval);
		return;
	}
	
	public static void main(String args[]) {
		options = new Options();
		
//		options.addOption(Option.builder("i")
//								.longOpt("interactive")
//								.desc("Enables interactive mode for list tools.")
//								.build()
//		);
		
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
		} catch (Exception e) {
			panic(-1);
			return;
		}
		
		if(line.hasOption("h")) {
			panic(0);
			return;
		} else if(line.hasOption("i")) {
			interactive();
			return;
		} else {
			noninteractive();
			return;
		}
		
	}
	
	public static void noninteractive() {
		try {
			List<Tool> tools = Tools.getTools();
			for(Tool tool : tools) {
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
	
	public static void interactive() {
		try {
			System.out.println();
			System.out.println(":::::::::::::::::::::::::: BigCloneEval - List Tools :::::::::::::::::::::::::");
			System.out.println();
			List<Tool> tools = Tools.getTools();
			if(tools.size() != 0) {
				for(Tool tool : tools) {
					String description = WordUtils.wrap(tool.getDescription(), 75, "\n    ", true);
					System.out.println("[" + tool.getId() + "] - " + tool.getName());
					System.out.println("    " + description);
					System.out.println("");
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
