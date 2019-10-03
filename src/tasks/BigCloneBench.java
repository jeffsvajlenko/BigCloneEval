package tasks;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "bcb",
        description = "The big clone bench tool",
        synopsisSubcommandLabel = "COMMAND",
        subcommands = {
                ClearClones.class,
                CountClones.class,
                DeleteTool.class,
                DetectClones.class,
                EvaluateRecall.class,
                EvaluateTool.class,
                ImportClones.class,
                Init.class,
                ListTools.class,
                PartitionInput.class,
                RegisterTool.class,
                CommandLine.HelpCommand.class,
        },
        exitCodeOnSuccess = 2
)
public class BigCloneBench implements Callable<Void> {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    public static void main(String[] args) {
        System.exit(new CommandLine(new BigCloneBench()).execute(args));
    }

    @Override
    public Void call() {
        throw new CommandLine.ParameterException(spec.commandLine(), "Missing subcommand");
    }
}
