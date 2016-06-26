package tasks;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import cloneMatchingAlgorithms.CloneMatcher;
import cloneMatchingAlgorithms.CoverageMatcher;
import database.BigCloneBenchDB;
import database.Clones;
import database.Functionalities;
import database.Functionality;
import database.Tool;
import database.Tools;
import evaluate.ToolEvaluator;
import util.BigCloneEvalVersion;
import util.FixPath;

public class EvaluateTool {
	
	private static Options options;
	private static HelpFormatter formatter;
	
	public static void panic(String error, int exitval) {
		System.err.println(error);
		System.exit(exitval);
		return;
	}
	
	public static void panic(int exitval) {
		formatter.printHelp(200, "evaluateTool", "BigCloneEval-EvaluateTool", options, "", true);
		System.exit(exitval);
		return;
	}
	
	public static void main(String args[]) {
		options = new Options();
		
// Essential Configurations
		
		options.addOption(Option.builder("t")
								.longOpt("tool")
								.hasArg()
								.argName("id")
								.desc("The ID of the tool to evaluate.")
								.build()
		);
		
		options.addOption(Option.builder("o")
								.longOpt("output")
								.hasArg()
								.argName("path")
								.desc("A file to output ")
								.build()
		);
		
		options.addOption(Option.builder("st")
								.longOpt("similarity-type")
								.hasArg()
								.argName("type")
								.desc("How reference clone similarity should be measured: (both/line/token/avg).  Default both.")
								.build()
		);
		
		options.addOption(Option.builder("m")
								.longOpt("matcher")
								.desc("Specify the clone matcher.  See documentation for configuration strings.\n"
									+ "Default is coverage-matcher with 70% coverage threshold.")
								.hasArg()
								.argName("matcher")
								.build()
		);
		
// Extent
		
		options.addOption(Option.builder("mis")
				.longOpt("minimum-similarity")
				.hasArg()
				.argName("int")
				.desc("Minimum clone similarity to evaluate for.  In range [0,100], must be a multiple of 5.")
				.build()
		);
		
// Constraints
		
		options.addOption(Option.builder("mil")
								.longOpt("minimum-lines")
								.desc("Adds a minimum clone size, in lines, constraint to the reference clones.")
								.hasArg()
								.argName("int")
								.build()
		);
		
		options.addOption(Option.builder("mal")
								.longOpt("maximum-lines")
								.desc("Adds a maximum clone size, in lines, constraint to the reference clones.")
								.hasArg()
								.argName("int")
								.build()
		);
		
		options.addOption(Option.builder("mip")
								.longOpt("minimum-pretty")
								.desc("Adds a minimum clone size, in pretty-printed lines, constraint to the reference clones.")
								.hasArg()
								.argName("int")
								.build()
		);
		
		options.addOption(Option.builder("map")
								.longOpt("maximum-pretty")
								.desc("Adds a maximum clone size, in pretty-printed lines, constraint to the reference clones.")
								.hasArg()
								.argName("int")
								.build()
		);

		options.addOption(Option.builder("mit")
								.longOpt("minimum-tokens")
								.desc("Adds a minimum clone size, in tokens, constraint to the reference clones.")
								.hasArg()
								.argName("int")
								.build()
		);
		
		options.addOption(Option.builder("mat")
								.longOpt("maximum-tokens")
								.desc("Adds a maximum clone size, in tokens, constraint to the reference clones.")
								.hasArg()
								.argName("int")
								.build()
		);
		
		options.addOption(Option.builder("mij")
								.longOpt("minimum-judges")
								.desc("Adds a minimum judges constraint to the reference clones.")
								.hasArg()
								.argName("int")
								.build()
		);
		
		options.addOption(Option.builder("mic")
								.longOpt("minimum-confidence")
								.desc("Adds a minimum confidence constraint to the reference clones.")
								.hasArg()
								.argName("int")
								.build()
		);
		
// Support Options
		options.addOption(Option.builder("i")
				                .longOpt("interactive")
				                .desc("Enables interactive mode for using evaluate tool.")
				                .build()
		);
		
		options.addOption(Option.builder("h")
				                .longOpt("help")
				                .desc("Prints the usage information.")
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
		//} else if (line.hasOption("i")) {
		//	//interactive();
		} else if (line.hasOption("t")) {
			try{
				String sline;
				long id;
				Tool tool;
				Path output;
				int simtype;
				CloneMatcher matcher;
				
			// Get Tool
				try{
					id = Long.parseLong(line.getOptionValue("t"));
				} catch (NumberFormatException e) {
					panic("Invalid tool ID value.",-1);
					return;
				}
				
				tool = Tools.getTool(id);
				if(tool == null) {
					panic("There is no such tool with ID " + id + ".",-1);
					return;
				}
				
			// Get Similarity Type
				if(!line.hasOption("st")) {
					simtype = ToolEvaluator.SIMILARITY_TYPE_BOTH;
				} else {
					sline = line.getOptionValue("st");
					if(sline.equals("both")) {
						simtype = ToolEvaluator.SIMILARITY_TYPE_BOTH;
					} else if (sline.equals("line")) {
						simtype = ToolEvaluator.SIMILARITY_TYPE_LINE;
					} else if (sline.equals("token")) {
						simtype = ToolEvaluator.SIMILARITY_TYPE_TOKEN;
					} else if (sline.equals("avg")) {
						simtype = ToolEvaluator.SIMILARITY_TYPE_AVG;
					} else {
						panic("Invalid similarity type: " + sline,-1);
						return;
					}
				}
				
			// Get MatchergetMatcher()
				if(!line.hasOption("m")) {
					matcher = new CoverageMatcher(id, 0.7, null, null);
				} else {
					sline = line.getOptionValue("m");
					String [] parts = sline.split("\\s+",2);
					try {
						matcher = CloneMatcher.load(id, parts[0], parts[1]);
					} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
							| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						System.err.println("Error loading clone matcher.  Please see the exception for details: ");
						e.printStackTrace(System.err);
						panic(-1);
						return;
					}
				}	
				
			// Min Lines
				Integer min_lines;
				if(!line.hasOption("mil")) {
					min_lines = null;
				} else {
					sline = line.getOptionValue("mil");
					try {
						min_lines = Integer.parseInt(sline);
					} catch (NumberFormatException e) {
						panic("Invalid value for mil: " + sline,-1);
						return;
					}
				}
				
			// Max Lines
				Integer max_lines;
				if(!line.hasOption("mal")) {
					max_lines = null;
				} else {
					sline = line.getOptionValue("mal");
					try {
						max_lines = Integer.parseInt(sline);
					} catch (NumberFormatException e) {
						panic("Invalid value for mal: " + sline,-1);
						return;
					}
				}
				
			// Min Tokens
				Integer min_tokens;
				if(!line.hasOption("mit")) {
					min_tokens = null;
				} else {
					sline = line.getOptionValue("mit");
					try {
						min_tokens = Integer.parseInt(sline);
					} catch (NumberFormatException e) {
						panic("Invalid value for mit: " + sline,-1);
						return;
					}
				}
				
			// Max Tokens
				Integer max_tokens;
				if(!line.hasOption("mat")) {
					max_tokens = null;
				} else {
					sline = line.getOptionValue("mat");
					try {
						max_tokens = Integer.parseInt(sline);
					} catch (NumberFormatException e) {
						panic("Invalid value for mat: " + sline,-1);
						return;
					}
				}
				
			// Min Pretty
				Integer min_pretty;
				if(!line.hasOption("mip")) {
					min_pretty = null;
				} else {
					sline = line.getOptionValue("mip");
					try {
						min_pretty = Integer.parseInt(sline);
					} catch (NumberFormatException e) {
						panic("Invalid value for mip: " + sline,-1);
						return;
					}
				}
				
			// Max Pretty
				Integer max_pretty;
				if(!line.hasOption("map")) {
					max_pretty = null;
				} else {
					sline = line.getOptionValue("map");
					try {
						max_pretty = Integer.parseInt(sline);
					} catch (NumberFormatException e) {
						panic("Invalid value for map: " + sline,-1);
						return;
					}
				}
				
			// Min Judges
				Integer min_judges;
				if(!line.hasOption("mij")) {
					min_judges = null;
				} else {
					sline = line.getOptionValue("mij");
					try {
						min_judges = Integer.parseInt(sline);
					} catch (NumberFormatException e) {
						panic("Invalid value for mij: " + sline,-1);
						return;
					}
				}
				
			// Min Confidence
				Integer min_confidence;
				if(!line.hasOption("mic")) {
					min_confidence = null;
				} else {
					sline = line.getOptionValue("mic");
					try {
						min_confidence = Integer.parseInt(sline);
					} catch (NumberFormatException e) {
						panic("Invalid value for mic: " + sline,-1);
						return;
					}
				}
				
			// Min Confidence
				Integer min_similarity;
				if(!line.hasOption("mis")) {
					min_similarity = 0;
				} else {
					sline = line.getOptionValue("mis");
					try {
						min_similarity = Integer.parseInt(sline);
					} catch (NumberFormatException e) {
						panic("Invalid value for mis: " + sline,-1);
						return;
					}
				}
				
			// Get Output
				if(!line.hasOption("o")) {
					panic("Missing the output file, option -o.",-1);
					return;
				}
				try {
					output = Paths.get(line.getOptionValue("o"));
				} catch (InvalidPathException e) {
					panic("Invalid output path: " + line.getOptionValue("o"),-1);
					return;
				}
				
				output = FixPath.getAbsolutePath(output);
				
				if(Files.exists(output) && Files.isDirectory(output)) {
					panic("Output path already exists as a directory, and won't be over-written.",-1);
					return;
				}
				if(Files.exists(output) && !Files.isWritable(output)) {
					panic("Output file already exists, but is not over-writable.",-1);
					return;
				}
				if(!Files.exists(output)) {
					try {
						Files.createDirectories(output.getParent());
						Files.createFile(output);
					} catch (IOException e) {
						panic("Could not create output file: " + output,-1);
						return;
					}
				}
				
				ToolEvaluator te;
				te = new ToolEvaluator(
			                     /*tool_id*/ id,
			                     /*matcher*/ matcher,
			             /*similarity_type*/ simtype,
			                    /*min_size*/ min_lines,
			 	                /*max_size*/ max_lines,
			 	         /*min_pretty_size*/ min_pretty,
			 	         /*max_pretty_size*/ max_pretty,
			 	              /*min_tokens*/ min_tokens,
			 	              /*max_tokens*/ max_tokens,
			 	              /*min_judges*/ min_judges,
			 	       	  /*min_confidence*/ min_confidence,
			 	   		/*include_internal*/ false);
				
				try {
					PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(output.toFile())));
					long time = System.currentTimeMillis();
					EvaluateTool.writeReport(pw, tool, te, min_similarity);
					pw.flush();
					pw.close();
					time = System.currentTimeMillis() - time;
					System.err.println("\tElapsed Time: " + time/1000.0 + "s");
				} catch (IOException e) {
					System.err.println("IOException while writing output file.  See exception:");
					e.printStackTrace(System.err);
					panic(-1);
				}
				
			} catch (SQLException e) {
				System.err.println("There is some error with the database.  Try a new copy of the database, or report the error:");
				e.printStackTrace(System.err);
				System.exit(-1);
			}
		} else {
			panic(-1);
		}
		
	}
	
//	public static void interactive() {
//		Thread canceled = new Thread() {
//			public void run() {
//				System.out.println();
//				System.out.println();
//				System.out.println("    Tool evaluation canceled.");
//				System.out.println();
//				System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
//			}
//		};
//		Runtime.getRuntime().addShutdownHook(canceled);
//		
//		Scanner scanner = new Scanner(System.in);
//		String inline;
//		
//		long id;
//		Integer min_lines;
//		Integer max_lines;
//		Integer min_tokens;
//		Integer max_tokens;
//		Integer min_pretty;
//		Integer max_pretty;
//		Integer min_judges;
//		Integer min_confidence;
//		
//		int  min_similarity;
//		int similarity_type;
//		Path outfile;
//		CloneMatcher matcher;
//		
//		try {
//			System.out.println(":::::::::::::::::::::::: BigCloneBench - Evaluate Tool ::::::::::::::::::::::::");
//			System.out.println(" Specify the tool to evaluate, configure the evaluation configuration, and a");
//			System.out.println(" file to save the evaluation to.");
//			System.out.println(" Use CTRL-C to cancel.");
//	
//	// Get Tool ID
//			System.out.println();
//			System.out.println("Specify the tool to evaluate.");
//			System.out.println();
//			id = getToolID(scanner);
//			
//	// Get File
//			System.out.println();
//			System.out.println("Specify the file to write the evaluation resuls to.");
//			System.out.println();
//			outfile = getOutputPath(scanner);
//			
//	// Setup Clone Selection
//			System.out.println();
//			System.out.println("Specify the constraints on the selected reference clones for evaluation.");
//			System.out.println("This defines the clones selected for evaluation.");
//			System.out.println();
//			similarity_type = getSimilarityType(scanner);
//			System.out.println();
//			min_lines = getInteger(" Minimum Clone Size in Lines (or blank for none): ", scanner);
//			System.out.println();
//			max_lines = getInteger(" Maximum Clone Size in Lines (or blank for none): ", scanner);
//			System.out.println();
//			min_pretty = getInteger(" Minimum Clone Size in Pretty-Printed Lines (or blank for none): ", scanner);
//			System.out.println();
//			max_pretty = getInteger(" Maximum Clone Size in Pretty-Printed Lines (or blank for none): ", scanner);
//			System.out.println();
//			min_tokens = getInteger(" Minimum Clone Size in Tokens (or blank for none): ", scanner);
//			System.out.println();
//			max_tokens = getInteger(" Maximum Clone Size in Tokens (or blank for none): ", scanner);
//			System.out.println();
////			min_judges = getInteger(" Minimum Number of Judges (or blank for none): ", scanner);
////			System.out.println();
////			min_confidence = getInteger(" Minimum Clone Confidence (or blank for none): ", scanner);
//			min_judges = null;
//			min_confidence = null;
//			
//	// Setup Clone Matcher
//			System.out.println();
//			System.out.println("Select and setup the clone matching algorithm.");
//			System.out.println("This determines how recall is measured.");
//			System.out.println();
//			matcher = setupCloneMatcher(scanner, id);
//			
//			
//	// How low to evaluate
//			System.out.println();
//			System.out.println("Specify the minimum clone similarity to measure recall for.  Setting to 0");
//			System.out.println("will evaluate for all clones, but evaluation can be very time-consuming.");
//			System.out.println("Setting slightly below the expected capabilities of the tool can save");
//			System.out.println("significant evaluation time.");
//			while(true) {
//				System.out.println();
//				System.out.print  (" Minimum clone similarity [0-100, increment of 5]: ");
//				inline = scanner.nextLine();
//				
//				try {
//					min_similarity = Integer.parseInt(inline);
//				} catch (NumberFormatException e) {
//					System.out.println("    Invalid value.");
//					continue;
//				}
//				
//				if(min_similarity < 0 || min_similarity > 100) {
//					System.out.println("    Value is out of range.");
//					continue;
//				}
//				
//				if(min_similarity % 5 != 0) {
//					System.out.println("    Value needs to be a multiple of 5.");
//					continue;
//				}
//				break;
//			}
//			
//			System.out.println();
//			System.out.println("Evaluation has begun.  This may take some time!");
//			System.out.println();
//			
////			while(true) {
////				System.out.println();
////				System.out.println("Specify partial or full evaluation.  Partial considers only clones with >= 50%.");
////				System.out.println("If you don't expect your tool to have recall below 50% syntactical similarity");
////				System.out.println("then this is a significant evaluation speedup.");
////				System.out.println("");
////				System.out.print  (" Full or partial evaluation [partial/full]: ");
////				inline = scanner.nextLine();
////				if(inline.toLowerCase().equals("partial")) {
////					full = false;
////				} else if (inline.toLowerCase().equals("full")) {
////					full = true;
////				} else {
////					System.out.println("    Invalid selection.");
////					continue;
////				}
////				break;
////			}
//			
//			ToolEvaluator te;
//			te = new ToolEvaluator(
//		                     /*tool_id*/ id,
//		                     /*matcher*/ matcher,
//		             /*similarity_type*/ similarity_type,
//		                    /*min_size*/ min_lines,
//		 	                /*max_size*/ max_lines,
//		 	         /*min_pretty_size*/ min_pretty,
//		 	         /*max_pretty_size*/ max_pretty,
//		 	              /*min_tokens*/ min_tokens,
//		 	              /*max_tokens*/ max_tokens,
//		 	              /*min_judges*/ min_judges,
//		 	       	  /*min_confidence*/ min_confidence,
//		 	   		/*include_internal*/ false);
//			
//			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outfile.toFile())));
//			
//			//long id;
//			//Integer min_lines;
//			//Integer max_lines;
//			//Integer min_tokens;
//			//Integer max_tokens;
//			//Integer min_pretty;
//			//Integer max_pretty;
//			//Integer min_judges;
//			//Integer min_confidence;
//			//int  min_similarity;
//			//int similarity_type;
//			//Path outfile;
//			//IsDetected matcher;
//			
//			Tool tool = Tools.getTool(id);
//			
//			long time = System.currentTimeMillis();
//			EvaluateTool.writeReport(pw, tool, te, min_similarity);
//			time = System.currentTimeMillis() - time;
//			System.err.println("\tElapsed Time: " + time/1000.0 + "s");
//			
//			Runtime.getRuntime().removeShutdownHook(canceled);
//			System.out.println();
//			System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
//		} catch (SQLException e) {
//			
//		} catch (IOException e) {
//			
//		}
//	}
	
	public static void writeReport(PrintWriter pw, Tool tool, ToolEvaluator te, int min_similarity) throws SQLException {

		pw.println("-- Tool --");
		pw.println("       Tool: " + tool.getId() + " - " + tool.getName());
		pw.println("Description: " + tool.getDescription());
		pw.println("    #Clones: " + Clones.numClones(tool.getId()));
		pw.println();
		pw.flush();
		
		pw.println("-- Versioning --");
		pw.println(" BigCloneEval: " + BigCloneEvalVersion.getVersion());
		pw.println("BigCloneBench: " + BigCloneBenchDB.getVersion());

		pw.println("-- Selected Clones --");
		pw.println("         Min Lines: " + te.getMin_size());
		pw.println("         Max Lines: " + te.getMax_size());
		pw.println("        Min Tokens: " + te.getMin_tokens());
		pw.println("        Max Tokens: " + te.getMax_tokens());
		pw.println("  Min Pretty Lines: " + te.getMin_pretty_size());
		pw.println("  Max Pretty Lines: " + te.getMax_pretty_size());
		pw.println("        Min Judges: " + te.getMin_judges());
		pw.println("    Min Confidence: " + te.getMin_confidence());
		pw.println("          Sim Type: " + te.getSimilarity_type_string());
		pw.println("Minimum Similarity:");
		pw.println();
		pw.flush();

		pw.println("-- Clone Matcher --");
		pw.println(te.getMatcher().toString());
		pw.println();
		pw.flush();
		
		pw.println("-- Clone Types --");
		pw.println("Type-1");
		pw.println("Type-2");
		pw.println("Very-Strongly Type-3: Clone similarity in range [90,100) after pretty-printing and identifier/literal normalization.");
		pw.println("     Strongly Type-3: Clone similarity in range [70, 90) after pretty-printing and identifier/literal normalization.");
		pw.println("   Moderately Type-3: Clone similarity in range [50, 70) after pretty-printing and identifier/literal normalization.");
		pw.println("Weakly Type-3/Type-4: Clone similarity in range [ 0, 50) after pretty-printing and identifier/literal normalization.");
		pw.println();
		pw.flush();

		pw.println("================================================================================");
		pw.println("\tAll Functionalities");
		pw.println("================================================================================");
		
		pw.println("-- Recall Per Clone Type (type: numDetected / numClones = recall) --");
		pw.println("              Type-1: " + te.getNumDetected_type1()        + " / " + te.getNumClones_type1()        + " = " + te.getRecall_type1());
		pw.flush();
		pw.println("              Type-2: " + te.getNumDetected_type2()        + " / " + te.getNumClones_type2()        + " = " + te.getRecall_type2());
		pw.flush();
		pw.println("      Type-2 (blind): " + te.getNumDetected_type2b()       + " / " + te.getNumClones_type2b()       + " = " + te.getRecall_type2b());
		pw.println(" Type-2 (consistent): " + te.getNumDetected_type2c()       + " / " + te.getNumClones_type2c()       + " = " + te.getRecall_type2c());
		if(min_similarity <= 90)
		pw.println("Very-Strongly Type-3: " + te.getNumDetected_type3(90, 100) + " / " + te.getNumClones_type3(90, 100) + " = " + te.getRecall_type3(90, 100));
		if(min_similarity <= 70)
		pw.println("     Strongly Type-3: " + te.getNumDetected_type3(70, 90)  + " / " + te.getNumClones_type3(70, 90)  + " = " + te.getRecall_type3(70, 90));
		if(min_similarity <= 50)
		pw.println("    Moderatly Type-3: " + te.getNumDetected_type3(50, 70)  + " / " + te.getNumClones_type3(50, 70)  + " = " + te.getRecall_type3(50, 70));
		if(min_similarity <= 0)
		pw.println("Weakly Type-3/Type-4: " + te.getNumDetected_type3(0, 50)   + " / " + te.getNumClones_type3(0, 50)   + " = " + te.getRecall_type3(0, 50));
		pw.println();
			
			
		pw.println("-- Inter-Project Recall Per Clone Type (type: numDetected / numClones = recall)  --");
		pw.println("              Type-1: " + te.getNumDetected_type1_inter()        + " / " + te.getNumClones_type1_inter()        + " = " + te.getRecall_type1_inter());
		pw.println("              Type-2: " + te.getNumDetected_type2_inter()        + " / " + te.getNumClones_type2_inter()        + " = " + te.getRecall_type2_inter());
		pw.println("      Type-2 (blind): " + te.getNumDetected_type2b_inter()       + " / " + te.getNumClones_type2b_inter()       + " = " + te.getRecall_type2b_inter());
		pw.println(" Type-2 (consistent): " + te.getNumDetected_type2c_inter()       + " / " + te.getNumClones_type2c_inter()       + " = " + te.getRecall_type2c_inter());
		if(min_similarity <= 90) {
		pw.println("Very-Strongly Type-3: " + te.getNumDetected_type3_inter(90, 100) + " / " + te.getNumClones_type3_inter(90, 100) + " = " + te.getRecall_type3_inter(90, 100));}
		if(min_similarity <= 70) {
		pw.println("     Strongly Type-3: " + te.getNumDetected_type3_inter(70, 90)  + " / " + te.getNumClones_type3_inter(70, 90)  + " = " + te.getRecall_type3_inter(70, 90));}
		if(min_similarity <= 50) {
		pw.println("    Moderatly Type-3: " + te.getNumDetected_type3_inter(50, 70)  + " / " + te.getNumClones_type3_inter(50, 70)  + " = " + te.getRecall_type3_inter(50, 70));}
		if(min_similarity <= 0) {
		pw.println("Weakly Type-3/Type-4: " + te.getNumDetected_type3_inter(0, 50)   + " / " + te.getNumClones_type3_inter(0, 50)   + " = " + te.getRecall_type3_inter(0, 50));}
		pw.println();
			
		pw.println("-- Intra-Project Recall Per Clone Type (type: numDetected / numClones = recall) --");
		pw.println("-- Recall Per Clone Type --");
		pw.println("              Type-1: " + te.getNumDetected_type1_intra()        + " / " + te.getNumClones_type1_intra()        + " = " + te.getRecall_type1_intra());
		pw.println("              Type-2: " + te.getNumDetected_type2_intra()        + " / " + te.getNumClones_type2_intra()        + " = " + te.getRecall_type2_intra());
		pw.println("      Type-2 (blind): " + te.getNumDetected_type2b_intra()       + " / " + te.getNumClones_type2b_intra()       + " = " + te.getRecall_type2b_intra());
		pw.println(" Type-2 (consistent): " + te.getNumDetected_type2c_intra()       + " / " + te.getNumClones_type2c_intra()       + " = " + te.getRecall_type2c_intra());
		if(min_similarity <= 90)
		pw.println("Very-Strongly Type-3: " + te.getNumDetected_type3_intra(90, 100) + " / " + te.getNumClones_type3_intra(90, 100) + " = " + te.getRecall_type3_intra(90, 100));
		if(min_similarity <= 70)
		pw.println("     Strongly Type-3: " + te.getNumDetected_type3_intra(70, 90)  + " / " + te.getNumClones_type3_intra(70, 90)  + " = " + te.getRecall_type3_intra(70, 90));
		if(min_similarity <= 50)
		pw.println("    Moderatly Type-3: " + te.getNumDetected_type3_intra(50, 70)  + " / " + te.getNumClones_type3_intra(50, 70)  + " = " + te.getRecall_type3_intra(50, 70));
		if(min_similarity <= 0)
		pw.println("Weakly Type-3/Type-4: " + te.getNumDetected_type3_intra(0, 50)   + " / " + te.getNumClones_type3_intra(0, 50)   + " = " + te.getRecall_type3_intra(0, 50));
		pw.println();
			
		int base = min_similarity;
		
		pw.println("-- Type-3 Recall per 5% Region ([start,end]: numDetected / numClones = recall)  --");
		for(int start = base; start <= 95; start+=5) {
			int end = start+5;
			pw.println("[" + start + "," + end + "]: " + te.getNumDetected_type3(start, end) + " / " + te.getNumClones_type3(start,end) + " = " + te.getRecall_type3(start, end));
		}
		pw.println();
		pw.flush();
		
		pw.println("-- Type-3 Inter-Project Recall per 5% Region--");
		for(int start = base; start <= 95; start+=5) {
			int end = start+5;
			pw.println("[" + start + "," + end + "]: " + te.getNumDetected_type3_inter(start, end) + " / " + te.getNumClones_type3_inter(start,end) + " = " + te.getRecall_type3_inter(start, end));
		}
		pw.println();
		pw.flush();
		
		pw.println("-- Type-3 Intra-Project Recall per 5% Region--");
		for(int start = base; start <= 95; start+=5) {
			int end = start+5;
			pw.println("[" + start + "," + end + "]: " + te.getNumDetected_type3_intra(start, end) + " / " + te.getNumClones_type3_intra(start,end) + " = " + te.getRecall_type3_intra(start, end));
		}
		pw.println();
		pw.flush();
		
		pw.println("-- Type-3 Recall Per Minimum Similarity --");
		for(int start = base; start <= 95; start+=5) {
			pw.println("[" + start + "," + 100 + "]: " + te.getNumDetected_type3(start, 100) + " / " + te.getNumClones_type3(start,100) + " = " + te.getRecall_type3(start, 100));
		}
		pw.println();
		pw.flush();
		
		pw.println("-- Type-3 Inter-Project Recall Per Minimum Similarity --");
		for(int start = base; start <= 95; start+=5) {
			pw.println("[" + start + "," + 100 + "]: " + te.getNumDetected_type3_inter(start, 100) + " / " + te.getNumClones_type3_inter(start,100) + " = " + te.getRecall_type3_inter(start, 100));
		}
		pw.println();
		pw.flush();
		
		pw.println("-- Type-3 Intra-Project Recall Per Minimum Similarity --");
		for(int start = base; start <= 95; start+=5) {
			pw.println("[" + start + "," + 100 + "]: " + te.getNumDetected_type3_intra(start, 100) + " / " + te.getNumClones_type3_intra(start,100) + " = " + te.getRecall_type3_intra(start, 100));
		}
		pw.println();
		pw.flush();
		
		for(long fid : Functionalities.getFunctionalityIds()) {
		Functionality f = Functionalities.getFunctinality(fid);
		pw.println("================================================================================");
		pw.println("Functionality");
		pw.println("  id: " + fid);
		pw.println("name: " + f.getName());
		pw.println("desc: " + f.getDesc());
		pw.println("================================================================================");
			
		pw.println("-- Recall Per Clone Type (type: numDetected / numClones = recall) --");
		pw.println("              Type-1: " + te.getNumDetected_type1(fid)        + " / " + te.getNumClones_type1(fid)        + " = " + te.getRecall_type1(fid));
		pw.flush();
		pw.println("              Type-2: " + te.getNumDetected_type2(fid)        + " / " + te.getNumClones_type2(fid)        + " = " + te.getRecall_type2(fid));
		pw.flush();
		pw.println("      Type-2 (blind): " + te.getNumDetected_type2b(fid)       + " / " + te.getNumClones_type2b(fid)       + " = " + te.getRecall_type2b(fid));
		pw.println(" Type-2 (consistent): " + te.getNumDetected_type2c(fid)       + " / " + te.getNumClones_type2c(fid)       + " = " + te.getRecall_type2c(fid));
		if(min_similarity <= 90)
		pw.println("Very-Strongly Type-3: " + te.getNumDetected_type3(fid, 90, 100) + " / " + te.getNumClones_type3(fid, 90, 100) + " = " + te.getRecall_type3(fid, 90, 100));
		if(min_similarity <= 70)
		pw.println("     Strongly Type-3: " + te.getNumDetected_type3(fid, 70, 90)  + " / " + te.getNumClones_type3(fid, 70, 90)  + " = " + te.getRecall_type3(fid, 70, 90));
		if(min_similarity <= 50)
		pw.println("    Moderatly Type-3: " + te.getNumDetected_type3(fid, 50, 70)  + " / " + te.getNumClones_type3(fid, 50, 70)  + " = " + te.getRecall_type3(fid, 50, 70));
		if(min_similarity <= 0)
		pw.println("Weakly Type-3/Type-4: " + te.getNumDetected_type3(fid, 0, 50)   + " / " + te.getNumClones_type3(fid, 0, 50)   + " = " + te.getRecall_type3(fid, 0, 50));
		pw.println();
				
				
		pw.println("-- Inter-Project Recall Per Clone Type (type: numDetected / numClones = recall)  --");
		pw.println("              Type-1: " + te.getNumDetected_type1_inter(fid)        + " / " + te.getNumClones_type1_inter(fid)        + " = " + te.getRecall_type1_inter(fid));
		pw.println("              Type-2: " + te.getNumDetected_type2_inter(fid)        + " / " + te.getNumClones_type2_inter(fid)        + " = " + te.getRecall_type2_inter(fid));
		pw.println("      Type-2 (blind): " + te.getNumDetected_type2b_inter(fid)       + " / " + te.getNumClones_type2b_inter(fid)       + " = " + te.getRecall_type2b_inter(fid));
		pw.println(" Type-2 (consistent): " + te.getNumDetected_type2c_inter(fid)       + " / " + te.getNumClones_type2c_inter(fid)       + " = " + te.getRecall_type2c_inter(fid));
		if(min_similarity <= 90) {
		pw.println("Very-Strongly Type-3: " + te.getNumDetected_type3_inter(fid, 90, 100) + " / " + te.getNumClones_type3_inter(fid, 90, 100) + " = " + te.getRecall_type3_inter(fid, 90, 100));}
		if(min_similarity <= 70) {
		pw.println("     Strongly Type-3: " + te.getNumDetected_type3_inter(fid, 70, 90)  + " / " + te.getNumClones_type3_inter(fid, 70, 90)  + " = " + te.getRecall_type3_inter(fid, 70, 90));}
		if(min_similarity <= 50) {
		pw.println("    Moderatly Type-3: " + te.getNumDetected_type3_inter(fid, 50, 70)  + " / " + te.getNumClones_type3_inter(fid, 50, 70)  + " = " + te.getRecall_type3_inter(fid, 50, 70));}
		if(min_similarity <= 0) {
		pw.println("Weakly Type-3/Type-4: " + te.getNumDetected_type3_inter(fid, 0, 50)   + " / " + te.getNumClones_type3_inter(fid, 0, 50)   + " = " + te.getRecall_type3_inter(fid, 0, 50));}
		pw.println();
				
		pw.println("-- Intra-Project Recall Per Clone Type (type: numDetected / numClones = recall) --");
		pw.println("-- Recall Per Clone Type --");
		pw.println("              Type-1: " + te.getNumDetected_type1_intra(fid)        + " / " + te.getNumClones_type1_intra(fid)        + " = " + te.getRecall_type1_intra(fid));
		pw.println("              Type-2: " + te.getNumDetected_type2_intra(fid)        + " / " + te.getNumClones_type2_intra(fid)        + " = " + te.getRecall_type2_intra(fid));
		pw.println("      Type-2 (blind): " + te.getNumDetected_type2b_intra(fid)       + " / " + te.getNumClones_type2b_intra(fid)       + " = " + te.getRecall_type2b_intra(fid));
		pw.println(" Type-2 (consistent): " + te.getNumDetected_type2c_intra(fid)       + " / " + te.getNumClones_type2c_intra(fid)       + " = " + te.getRecall_type2c_intra(fid));
		if(min_similarity <= 90)
		pw.println("Very-Strongly Type-3: " + te.getNumDetected_type3_intra(fid, 90, 100) + " / " + te.getNumClones_type3_intra(fid, 90, 100) + " = " + te.getRecall_type3_intra(fid, 90, 100));
		if(min_similarity <= 70)
		pw.println("     Strongly Type-3: " + te.getNumDetected_type3_intra(fid, 70, 90)  + " / " + te.getNumClones_type3_intra(fid, 70, 90)  + " = " + te.getRecall_type3_intra(fid, 70, 90));
		if(min_similarity <= 50)
		pw.println("    Moderatly Type-3: " + te.getNumDetected_type3_intra(fid, 50, 70)  + " / " + te.getNumClones_type3_intra(fid, 50, 70)  + " = " + te.getRecall_type3_intra(fid, 50, 70));
		if(min_similarity <= 0)
		pw.println("Weakly Type-3/Type-4: " + te.getNumDetected_type3_intra(fid, 0, 50)   + " / " + te.getNumClones_type3_intra(fid, 0, 50)   + " = " + te.getRecall_type3_intra(fid, 0, 50));
		pw.println();
		
		pw.println("-- Type-3 Recall per 5% Region ([start,end]: numDetected / numClones = recall)  --");
		for(int start = base; start <= 95; start+=5) {
			int end = start+5;
			pw.println("[" + start + "," + end + "]: " + te.getNumDetected_type3(fid, start, end) + " / " + te.getNumClones_type3(fid, start,end) + " = " + te.getRecall_type3(fid, start, end));
		}
		pw.println();
		pw.flush();
		
		pw.println("-- Type-3 Inter-Project Recall per 5% Region--");
		for(int start = base; start <= 95; start+=5) {
			int end = start+5;
			pw.println("[" + start + "," + end + "]: " + te.getNumDetected_type3_inter(fid, start, end) + " / " + te.getNumClones_type3_inter(fid, start,end) + " = " + te.getRecall_type3_inter(fid, start, end));
		}
		pw.println();
		pw.flush();
		
		pw.println("-- Type-3 Intra-Project Recall per 5% Region--");
		for(int start = base; start <= 95; start+=5) {
			int end = start+5;
			pw.println("[" + start + "," + end + "]: " + te.getNumDetected_type3_intra(fid, start, end) + " / " + te.getNumClones_type3_intra(fid, start,end) + " = " + te.getRecall_type3_intra(fid, start, end));
		}
		pw.println();
		pw.flush();
			
		pw.println("-- Type-3 Recall Per Minimum Similarity --");
		for(int start = base; start <= 95; start+=5) {
			pw.println("[" + start + "," + 100 + "]: " + te.getNumDetected_type3(fid, start, 100) + " / " + te.getNumClones_type3(fid, start,100) + " = " + te.getRecall_type3(fid, start, 100));
		}
		pw.println();
		pw.flush();
		
		pw.println("-- Type-3 Inter-Project Recall Per Minimum Similarity --");
		for(int start = base; start <= 95; start+=5) {
			pw.println("[" + start + "," + 100 + "]: " + te.getNumDetected_type3_inter(fid, start, 100) + " / " + te.getNumClones_type3_inter(fid, start,100) + " = " + te.getRecall_type3_inter(fid, start, 100));
		}
		pw.println();
		pw.flush();
		
		pw.println("-- Type-3 Intra-Project Recall Per Minimum Similarity --");
		for(int start = base; start <= 95; start+=5) {
			pw.println("[" + start + "," + 100 + "]: " + te.getNumDetected_type3_intra(fid, start, 100) + " / " + te.getNumClones_type3_intra(fid, start,100) + " = " + te.getRecall_type3_intra(fid, start, 100));
		}
		pw.println();
		pw.flush();
		}
		
	}
	
//	private static CloneMatcher setupCloneMatcher(Scanner scanner, long id) throws SQLException {
//		CloneMatcher retval;
//		while(true) {
//			System.out.println(" Select a clone matcher:");
//			System.out.println("-------------------------------------------------------------------------------");
//			System.out.println("    [1] - Coverage - For a reference clone pair to be considered detected, the");
//			System.out.println("                     tool must report a clone pair that covers a given ratio of");
//			System.out.println("                     the reference clone pair.  An optional ratio can be");
//			System.out.println("                     specified as the minimum ratio of the detected clone (in");
//			System.out.println("                     lines) that must be from the reference clone.");
//			System.out.println();
//			System.out.println("    [[ Additional clone matchers to be added. ]]");
//			//System.out.println("                     optional maximimum number of lines the detected clone can");
//			//System.out.println("                     contain that are external to the reference clone.");
//			System.out.println("-------------------------------------------------------------------------------");
//			System.out.print  (":::: ");
//			
//			String inline = scanner.nextLine();
//			if(inline.equals("1")) {
//				// Get Coverage
//				double coverage;
//				while(true) {
//					System.out.println("");
//					System.out.print  (" Specify coverage in range [0.0,1.0]: ");
//					inline = scanner.nextLine();
//					try {
//						coverage = Double.parseDouble(inline);
//					} catch (NumberFormatException e) {
//						System.out.println("    Invalid value.");
//						continue;
//					}
//					if(coverage < 0 || coverage > 1) {
//						System.out.println("    Value out of range.");
//						continue;
//					}
//					break;
//				}
//				
//				// Get Tolerence
//				Double tolerence;
//				while(true) {
//					System.out.println("");
//					System.out.print  (" Specify minimum ratio is reference clone (or blank to omit) in range [0.0,1.0]: ");
//					inline = scanner.nextLine();
//					if(inline.equals("")) {
//						tolerence = null;
//						break;
//					}
//					try {
//						tolerence = Double.parseDouble(inline);
//					} catch (NumberFormatException e) {
//						System.out.println("    Invalid value.");
//						continue;
//					}
//					if(tolerence < 0 || tolerence > 1) {
//						System.out.println("    Value out of range.");
//						continue;
//					}
//					break;
//				}
//				
//				retval = new CoverageMatcher(id, coverage, null, tolerence);
//				break;
//			} else {
//				System.out.println("    Invalid selection.");
//				System.out.println();
//				continue;
//			}
//		}
//		
//		return retval;
//	}
//	
//	private static int getSimilarityType(Scanner scanner) {
//		while(true) {
//			System.out.print(" How should reference clone similarity be measured? (both/line/token/avg): ");
//			String inline = scanner.nextLine();
//			if(inline.toLowerCase().equals("token")) {
//				return ToolEvaluator.SIMILARITY_TYPE_TOKEN;
//			} else if (inline.toLowerCase().equals("line")) {
//				return ToolEvaluator.SIMILARITY_TYPE_LINE;
//			} else if (inline.toLowerCase().equals("both")) {
//				return ToolEvaluator.SIMILARITY_TYPE_BOTH;
//			} else if (inline.toLowerCase().equals("avg")) {
//				return ToolEvaluator.SIMILARITY_TYPE_AVG;
//			} else {
//				System.out.println("    Invalid selection.");
//			}
//		}
//	}
//
//	private static Path getOutputPath(Scanner scanner) {
//		Path outfile;
//		
//		while(true) {
//			System.out.print  (" Output File: ");
//			String inline = scanner.nextLine();
//			
//			try {
//				outfile = Paths.get(inline);
//			} catch (InvalidPathException e) {
//				System.out.println("    Invalid path.");
//				continue;
//			}
//			
//			if(Files.exists(outfile)) {
//				System.out.println("    This file already exists.  Please delete it or specify a new file.");
//				continue;
//			}
//			
//			try {
//				Files.createFile(outfile);
//			} catch (IOException e) {
//				System.out.println("    Failed to create the output file.  Do you have permission?");
//				continue;
//			}
//			
//			break;
//		}
//		
//		return outfile;
//	}
//	
//	private static Integer getInteger(String query, Scanner scanner) {
//		Integer in;
//		String inline;
//		
//		while(true) {
//			System.out.print(query);
//			inline = scanner.nextLine();
//			if(inline.equals("")) {
//				in = null;
//			} else {
//				try {
//					in = Integer.parseInt(inline);
//				} catch (NumberFormatException e) {
//					System.out.println("    Invalid value.");
//					System.out.println();
//					continue;
//				}
//			}
//			break;
//		}
//		return in;
//	}
//	
//	public static Long getToolID(Scanner scanner) throws SQLException {
//		long id;
//		String inline;
//		while(true) {
//			System.out.println();
//			System.out.print(" Tool ID: ");
//			inline = scanner.nextLine();
//			try {
//				id = Long.parseLong(inline);
//			} catch (NumberFormatException e) {
//				System.out.println("    Invalid ID value.");
//				continue;
//			}
//			Tool tool = Tools.getTool(id);
//			if(tool == null) {
//				System.out.println("    No tool exists with this ID.");
//				continue;
//			}
//			return id;
//		}
//	}
	
	// Output Dataset Stats (based on selection)
	// Output full statistics
	
}
