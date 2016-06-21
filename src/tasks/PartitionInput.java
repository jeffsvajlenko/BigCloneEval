package tasks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import util.FixPath;

public class PartitionInput {


	private static Options options;
	private static HelpFormatter formatter;
	
	public static void panic(int exitval) {
		formatter.printHelp(200, "partitionInput", "BigCloneEval-PartitionInput", options, "", true);
		System.exit(exitval);
		return;
	}	
	
	public static void main(String args[]) {
		options = new Options();
		
		options.addOption(Option.builder("i")
								.longOpt("input")
								.desc("The directory of source code to partition.")
								.hasArg()
								.argName("path")
								.required()
								.build()
		);
		
		options.addOption(Option.builder("o")
								.longOpt("output")
								.desc("The directory to write the subsets to (each pair of partitions).")
								.hasArg()
								.argName("path")
								.required()
								.build()
		);
		
		options.addOption(Option.builder("mf")
								.longOpt("max-files")
								.desc("Maximum files in each output subset (pair of partitions).")
								.hasArg()
								.argName("int")
								.required()
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
		
		Path input;
		Path output;
		int maxfiles;
	
		try {
			input = Paths.get(line.getOptionValue("i"));
		} catch (Exception e) {
			System.err.println("Invalid value for 'input'.");
			panic(-1);
			return;
		}
		
		try {
			output = Paths.get(line.getOptionValue("o"));
		} catch (Exception e) {
			System.err.println("Invalid value for 'output'.");
			panic(-1);
			return;
		}
		
		output = FixPath.getAbsolutePath(output);
		
		try {
			maxfiles = Integer.parseInt(line.getOptionValue("mf"));
		} catch (Exception e) {
			System.err.println("Invalid value for 'maxfiles'.");
			panic(-1);
			return;
		}
	
		if(!Files.exists(input)) {
			System.err.println("Input directory does not exist.");
			panic(-1);
			return;
		}
		if(!Files.isDirectory(input)) {
			System.err.println("Input must be a directory.");
			panic(-1);
			return;
		}
		
		if(Files.exists(output)) {
			System.err.println("Output already exists.");
			panic(-1);
			return;
		}
		try {
			Files.createDirectories(output);
		} catch (IOException e) {
			System.err.println("Output directory could not be created.");
			panic(-1);
			return;
		}
		
		try {
			DetectClones.partition(input, output, maxfiles);
		} catch (IOException e1) {
			System.err.println("An exeception occured during partitioning:");
			e1.printStackTrace(System.err);
			System.exit(-1);
		}
		
	}
	
}
