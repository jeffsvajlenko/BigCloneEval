package tasks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import database.Clones;
import database.Tool;
import database.Tools;
import util.FixPath;

public class ImportClones {
	
	private static Options options;
	private static HelpFormatter formatter;
	
	public static void panic(int exitval) {
		formatter.printHelp(200, "importClones", "BigCloneEval-ImportClones", options, "", true);
		System.exit(exitval);
		return;
	}	
	
	public static void main(String args[]) {
		options = new Options();
		
		options.addOption(Option.builder("t")
								.longOpt("tool")
								.hasArg()
								.argName("ID")
								.desc("The ID of the tool to import clones for.")
								.build()
		);
		
		options.addOption(Option.builder("c")
								.longOpt("clones")
								.hasArg()
								.argName("FILE")
								.desc("A file with clones to import.")
								.build()
		);
		
		//options.addOption(Option.builder("i")
		//		.longOpt("interactive")
		//		.desc("Enables interactive mode for using delete tool.")
		//        .build()
		//);
		
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
			panic(-1);
			return;
		}
		
		if(line.hasOption("h")) {
			panic(0);
		//if(line.hasOption("i")) {
		//	interactive();
		//} else 
	    } else if (line.hasOption("t") && line.hasOption("c")) {
			String sid = line.getOptionValue("t");
			String sfile = line.getOptionValue("c");
			Path cfile = null;
			
			try {
				cfile = Paths.get(sfile);
				
				// get absolute path
				cfile = FixPath.getAbsolutePath(cfile);
				
				if(!Files.exists(cfile)) {
					System.err.println("Clone file does not exist.");
					System.exit(-1);
					return;
				}
				
				if(!Files.isRegularFile(cfile)) {
					System.err.println("Specified clone file is not a regular file.");
					System.exit(-1);
					return;
				}
				
				if(!Files.isReadable(cfile)) {
					System.err.println("Specified clone file is not readable.");
					System.exit(-1);
					return;
				}
				
			} catch(InvalidPathException e) {
				System.err.println("Invalid clone file path.");
				System.exit(-1);
				return;
			}
			
			long id=-1;
			try {
				id = Long.parseLong(sid);
				Tool tool = Tools.getTool(id);
				if(tool == null) throw new IllegalArgumentException();
				long num = Clones.importClones(id, cfile);
				System.out.println(num);
			} catch (SQLException e) {
				System.err.println("\tSome error occured with the database connection or interaction.");
				System.err.println("\n\tPlease try a fresh copy of the datbase, and report the error to.");
				System.err.println("\n\tthe developers.");
				e.printStackTrace(System.err);
				System.exit(-1);
				return;
			} catch (NumberFormatException e) {
				System.err.println("\tInvalid tool identifier value.");
				System.exit(-1);
				return;
			} catch (IllegalArgumentException e) {
				System.err.println("\tNo tool exists with the ID" + id + " .");
				System.exit(-1);
				return;
			} catch (IOException e) {
				System.err.println("IOException reading clone file.");
				e.printStackTrace(System.err);
				System.exit(-1);
				return;
			}
		} else {
			panic(0);
		}
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
			while(true) {
				System.out.print(" Tool ID: ");
				String line = scanner.nextLine();
				if(line.equals("")) {
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
				if(tool == null) {
					System.out.println();
					System.out.println("    No tool with the given ID.");
					System.out.println();
					continue;
				}
				break;
			}
			
			System.out.println();
			
			// Get Clone File
			while(true) {
				System.out.print(" Clone file: ");
				String line = scanner.nextLine();
				if(line.equals("")) {
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
				
				if(!Files.isRegularFile(clones)) {
					System.out.println();
					System.out.println("    Is not a regular file.");
					System.out.println();
					continue;
				}
				
				if(!Files.isReadable(clones)) {
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
				System.out.println("    Total time: " + time/1000.0 + " seconds.");
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
