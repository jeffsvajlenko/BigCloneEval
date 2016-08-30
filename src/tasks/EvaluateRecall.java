package tasks;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.UUID;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import cloneMatchingAlgorithms.CloneMatcher;
import cloneMatchingAlgorithms.CoverageMatcher;
import database.Clones;
import database.Tool;
import database.Tools;
import evaluate.ToolEvaluator;
import tasks.EvaluateTool;
import util.FixPath;

public class EvaluateRecall {

	private static Options options;
	private static HelpFormatter formatter;
	
	public static void validate(Path clones,
								Path report,
								Integer simtype,
								Double matcher_coverage,
								Integer minlines,
								Integer maxlines,
								Integer minpretty,
								Integer maxpretty,
								Integer mintokens,
								Integer maxtokens,
								Integer minjudges,
								Integer minconfidence,
								Integer minsim
	) {
		
//		throw new IllegalArgumentException("");
		
		// Clones
		if(!Files.exists(clones)) {
			throw new IllegalArgumentException("Input clones file does not exist.");
		}
		if(!Files.isRegularFile(clones)) {
			throw new IllegalArgumentException("Input clones file is not a regular file");
		}
		if(!Files.isReadable(clones)) {
			throw new IllegalArgumentException("Input clones file is not readable.");
		}
		
		// Report
		if(Files.isDirectory(report)) {
			throw new IllegalArgumentException("Specified output report file is an existing directory.");
		}
		
		// SimType
		if(matcher_coverage < 0.0 && matcher_coverage > 1.0) {
			throw new IllegalArgumentException("Clone matcher coverage must be in range [0.0,1.0].");
		}
		
		// Lines
		if(minlines != null) {
			if(minlines < 0) {
				throw new IllegalArgumentException("Minimum lines must be >= 0.");
			}
		}
		if(maxlines != null) {
			if(maxlines < 0) {
				throw new IllegalArgumentException("Maximum lines must be >= 0.");
			}
		}
		if(minlines != null && maxlines != null) {
			if(minlines > maxlines) {
				throw new IllegalArgumentException("Minimum lines must be less than maximum lines.");
			}
		}
		
		// Pretty Lines
		if(minpretty != null) {
			if(minpretty < 0) {
				throw new IllegalArgumentException("Minimum pretty lines must be >= 0.");
			}
		}
		if(maxpretty != null) {
			if(maxpretty < 0) {
				throw new IllegalArgumentException("Maximum pretty lines must be >= 0.");
			}
		}
		if(minpretty != null && maxpretty != null) {
			if(minpretty > maxpretty) {
				throw new IllegalArgumentException("Minimum pretty lines must be less than maximum lines.");
			}
		}
		
		// Lines
		if(minlines != null) {
			if(minlines < 0) {
				throw new IllegalArgumentException("Minimum lines must be >= 0.");
			}
		}
		if(maxlines != null) {
			if(maxlines < 0) {
				throw new IllegalArgumentException("Maximum lines must be >= 0.");
			}
		}
		if(minlines != null && maxlines != null) {
			if(minlines > maxlines) {
				throw new IllegalArgumentException("Minimum lines must be less than maximum lines.");
			}
		}
		
		// Tokens
		if(mintokens != null) {
			if(mintokens < 0) {
				throw new IllegalArgumentException("Minimum tokens must be >= 0.");
			}
		}
		if(maxtokens != null) {
			if(maxtokens < 0) {
				throw new IllegalArgumentException("Maximum tokens must be >= 0.");
			}
		}
		if(mintokens != null && maxtokens != null) {
			if(mintokens > maxtokens) {
				throw new IllegalArgumentException("Minimum tokens must be less than maximum lines.");
			}
		}
		
		// Min Confidence
		if(minconfidence != null) {
			if(minconfidence < 0) {
				throw new IllegalArgumentException("Minimum confidence must be >= 0.");
			}
		}
		
		// MinSim
		if(minsim != null) {
			if(minsim < 0 || minsim > 100) {
				throw new IllegalArgumentException("Minimum similarity must be a multiple of 5 in range [0,100].");
			}
			if(minsim % 5 != 0) {
				throw new IllegalArgumentException("Minimum similarity must be a multiple of 5.");
			}
		}
	}
	
	public static void run(	Path clones,
							Path report,
							Integer simtype,
							Double matcher_coverage,
							Integer minlines,
							Integer maxlines,
							Integer minpretty,
							Integer maxpretty,
							Integer mintokens,
							Integer maxtokens,
							Integer minjudges,
							Integer minconfidence,
							Integer minsim
	) throws ClassNotFoundException, IOException, SQLException {
		
		// Create Temporary Tool
		String name = UUID.randomUUID().toString();
		String desc = UUID.randomUUID().toString();
		Long toolID = null;
		
		try {
			// Create Tool
			toolID = Tools.addTool(name, desc);
			
			// Import Clones
			System.out.println("Importing clones...");
			Clones.importClones(toolID, clones);
			
			// Tool Evaluator
			CloneMatcher matcher = new CoverageMatcher(toolID, matcher_coverage, null, null); 
			ToolEvaluator te = new ToolEvaluator(toolID,
					                             matcher,
					                             simtype,
		   			                             minlines,
		   			                             maxlines,
		   			                             minpretty,
		   			                             maxpretty,
		   			                             mintokens,
		   			                             maxtokens,
		   			                             minjudges,
		   			                             minconfidence,
		   			                             false
		    );
			
			if(minsim == null) minsim = 0;
			
			// Echo Tool Evaluator
			System.out.println("toolid=" + te.getTool_id());
			System.out.println("minsimilarity=" + minsim);
			System.out.println("matcher=" + te.getMatcher().toString());
			System.out.println("simtype=" + te.getSimilarity_type());
			System.out.println("minlines=" + te.getMin_size());
			System.out.println("maxlines=" + te.getMax_size());
			System.out.println("minpretty=" + te.getMin_pretty_size());
			System.out.println("maxpretty=" + te.getMax_pretty_size());
			System.out.println("mintokens=" + te.getMin_tokens());
			System.out.println("maxtokens=" + te.getMax_tokens());
			System.out.println("minjudges=" + te.getMin_judges());
			System.out.println("minconfidence=" + te.getMin_confidence());
			
			// Evaluate
			System.out.println("Evaluating...");
			Tool tool = Tools.getTool(toolID);
			PrintWriter pw = new PrintWriter(new FileWriter(report.toFile()));
			EvaluateTool.writeReport(pw, tool, te, minsim);
			pw.close();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			Tools.deleteToolAndData(toolID);
		}
		
	}
	
	private static void setup() {
		options = new Options();
		
		options.addOption(Option.builder("i")
				                .longOpt("input")
				                .desc("File containing the detected clones.")
				                .required()
				                .hasArg()
				                .argName("file")
				                .build()
		);
		
		options.addOption(Option.builder("o")
				                .longOpt("output")
				                .desc("File to write evaluation report to.")
				                .required()
				                .hasArg()
				                .argName("file")
				                .build()
		);
		
		options.addOption(Option.builder("st")
								.longOpt("similarity-type")
								.desc("How to measure similarity: {line, token, both, avg}.")
								.required()
								.hasArg()
								.argName("str")
								.build()
		);
		
		options.addOption(Option.builder("c")
								.longOpt("matcher-coverage")
								.desc("Minimum coverage of clone-matcher: [0.0,1.0].")
								.hasArg()
								.argName("double")
								.required()
								.build()
		);
		
		options.addOption(Option.builder("mil")
								.longOpt("minimum-lines")
				                .desc("Minimum clone size in original lines.")
								.hasArg()
				                .argName("int")
				                .build()
		);
		
		options.addOption(Option.builder("mal")
								.longOpt("maximum-lines")
								.desc("Maximum clone size in lines.")
								.hasArg()
								.argName("int")
								.build()
		);
		
		options.addOption(Option.builder("mip")
								.longOpt("minimum-pretty-lines")
								.desc("Minimum clone size in pretty lines.")
								.hasArg()
								.argName("int")
								.build()
		);
		
		options.addOption(Option.builder("map")
								.longOpt("maximum-pretty-lines")
								.desc("Maximum clone size in pretty lines.")
								.hasArg()
								.argName("int")
								.build()
		);
		
		options.addOption(Option.builder("mit")
								.longOpt("minimum-tokens")
								.desc("Minimum clone size in tokens.")
								.hasArg()
								.argName("int")
								.build()
		);

		options.addOption(Option.builder("mat")
								.longOpt("maximum-tokens")
								.desc("Maximum clone size in tokens.")
								.hasArg()
								.argName("int")
								.build()
		);
		
		options.addOption(Option.builder("mij")
								.longOpt("minimum-judges")
								.desc("Minimum number of judges.")
								.hasArg()
								.argName("int")
								.build()
		);
		
		options.addOption(Option.builder("mic")
								.longOpt("minimum-confidence")
								.desc("Minimum confidence.")
								.hasArg()
								.argName("int")
								.build()
		);
		
		options.addOption(Option.builder("mij")
								.longOpt("minimum-judges")
								.desc("Minimum number of judges.")
								.hasArg()
								.argName("int")
								.build()
		);
		
		options.addOption(Option.builder("mis")
								.longOpt("minimum-similarity")
								.desc("Minimum clone similarity to evaluate down to.")
								.hasArg()
								.argName("int")
								.build()
		);
		
		formatter = new HelpFormatter();
		formatter.setOptionComparator(null);
	}
	
	private static void panic() {
		formatter.printHelp(200, "bcbeval", "Evaluate clone detection tool with BigCloneBench.", options, "", true);
		System.exit(-1);
		return;
	}
	
	public static void main(String args[]) throws SQLException, ClassNotFoundException, IOException {
		
		// Setup Options
		EvaluateRecall.setup();
		
		// Parse
		CommandLineParser parser = new DefaultParser();
		CommandLine line;
		try {
			line = parser.parse(options, args);
		} catch (Exception e) {
			panic();
			return;
		}
		
		Path    input = null;
		Path    output = null;
		Integer simtype = null;
		Double  matcher_coverage = null;
		Integer minlines = null;
		Integer maxlines = null;
		Integer minpretty = null;
		Integer maxpretty = null;
		Integer mintok = null;
		Integer maxtok = null;
		Integer minjudges = null;
		Integer minconf = null;
		Integer minsim = null;
		
		// Get Options
		try {
			input = Paths.get(line.getOptionValue("i"));
			input = FixPath.getAbsolutePath(input);
			
			output = Paths.get(line.getOptionValue("o"));
			output = FixPath.getAbsolutePath(output);
			
			String type = line.getOptionValue("st").toLowerCase();
			if(type.equals("both")) simtype = ToolEvaluator.SIMILARITY_TYPE_BOTH;
			else if (type.equals("line")) simtype = ToolEvaluator.SIMILARITY_TYPE_LINE;
			else if (type.equals("token")) simtype = ToolEvaluator.SIMILARITY_TYPE_TOKEN;
			else if (type.equals("avg")) simtype = ToolEvaluator.SIMILARITY_TYPE_AVG;
			else throw new Exception("");
			
			matcher_coverage = Double.parseDouble(line.getOptionValue("c"));
			
			if(line.hasOption("mil")) {
				minlines = Integer.parseInt(line.getOptionValue("mil"));
			}
			
			if(line.hasOption("mal")) {
				maxlines = Integer.parseInt(line.getOptionValue("mal"));
			}
			
			if(line.hasOption("mip")) {
				minpretty = Integer.parseInt(line.getOptionValue("mip"));
			}
			
			if(line.hasOption("map")) {
				maxpretty = Integer.parseInt(line.getOptionValue("map"));
			}
			
			if(line.hasOption("mit")) {
				mintok = Integer.parseInt(line.getOptionValue("mit"));
			}
			
			if(line.hasOption("mat")) {
				maxtok = Integer.parseInt(line.getOptionValue("mt"));
			}
			
			if(line.hasOption("mij")) {
				minjudges = Integer.parseInt(line.getOptionValue("mij"));
			}
			
			if(line.hasOption("mic")) {
				minconf = Integer.parseInt(line.getOptionValue("mic"));
			}
			
			if(line.hasOption("mis")) {
				minsim = Integer.parseInt(line.getOptionValue("mis"));
			}
		} catch (Exception e) {
			panic();
			e.printStackTrace();
			return;
		}
		
		// Validate
		try {
			validate(input, output, simtype, matcher_coverage, minlines, maxlines, minpretty, maxpretty, mintok, maxtok, minjudges, minconf, minsim);
		} catch (Exception e) {
			panic();
			return;
		}
		
		// Run
		try {
			run(input, output, simtype, matcher_coverage, minlines, maxlines, minpretty, maxpretty, mintok, maxtok, minjudges, minconf, minsim);
		} catch (Exception e) {
			System.out.println("Failed with excpetion: " + e.getMessage());
			e.printStackTrace();
			return;
		}
		
	}
	
}
