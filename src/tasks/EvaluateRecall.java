package tasks;

import cloneMatchingAlgorithms.CloneMatcher;
import cloneMatchingAlgorithms.CoverageMatcher;
import database.Clones;
import database.Tool;
import database.Tools;
import evaluate.ToolEvaluator;
import picocli.CommandLine;
import util.FixPath;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.Callable;

import static evaluate.ToolEvaluator.*;

@CommandLine.Command(
        name = "evaluateRecall",
        description = "Measures the recall of the clones given. Highly configureable." +
                "Summarizes recall per clone type, per inter vs intra-project clones, per functionality in " +
                "BigCloneBench and for different syntactical similarity regions in the output tool evaluation report.",
        mixinStandardHelpOptions = true
)
public class EvaluateRecall implements Callable<Void> {

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    @CommandLine.Mixin
    private MixinOptions.OutputFile output;

    @CommandLine.Mixin
    private MixinOptions.EvaluationOptions evaluationOptions;

    @CommandLine.Mixin
    private MixinOptions.InputFile input;

    private Double matcher_coverage = null;

    @CommandLine.Option(
            names = {"-c", "--matcher-coverage"},
            description = "Minimum coverage of clone-matcher: [0.0,1.0].",
            paramLabel = "double",
            required = true
    )
    private void setMatcherCoverage(double matcher_coverage) {
        if (matcher_coverage < 0.0 && matcher_coverage > 1.0) {
            throw new CommandLine.ParameterException(spec.commandLine(), "Clone matcher coverage must be in range [0.0,1.0].");
        }
        this.matcher_coverage = matcher_coverage;
    }

    public static void main(String[] args) {
        new CommandLine(new EvaluateRecall()).execute(args);
    }

    @Override
    public Void call() {
        // Create Temporary Tool
        String name = UUID.randomUUID().toString();
        String desc = UUID.randomUUID().toString();
        Long toolID = null;

        try {
            // Create Tool
            toolID = Tools.addTool(name, desc);

            // Import Clones
            System.out.println("Importing clones...");
            Clones.importClones(toolID, input.input.toPath());

            // Tool Evaluator
            CloneMatcher matcher = new CoverageMatcher(toolID, matcher_coverage, null, null);
            ToolEvaluator te = new ToolEvaluator(toolID,
                    matcher,
                    evaluationOptions.simtype.val,
                    evaluationOptions.lines.getMin(),
                    evaluationOptions.lines.getMax(),
                    evaluationOptions.pretty.getMin(),
                    evaluationOptions.pretty.getMax(),
                    evaluationOptions.tokens.getMin(),
                    evaluationOptions.tokens.getMax(),
                    evaluationOptions.minjudges,
                    evaluationOptions.minconfidence,
                    false
            );

            // Echo Tool Evaluator
            System.out.println("toolid=" + te.getTool_id());
            System.out.println("minsimilarity=" + evaluationOptions.minsim);
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
            PrintWriter pw = new PrintWriter(new FileWriter(output.file));
            EvaluateTool.writeReport(pw, tool, te, evaluationOptions.minsim);
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Tools.deleteToolAndData(toolID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
