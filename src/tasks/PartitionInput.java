package tasks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import util.FixPath;

@CommandLine.Command(
        name = "partitionInput",
        description = "partitions the files from the input directory to the output directory.",
        mixinStandardHelpOptions = true
)
public class PartitionInput implements Callable<Void> {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    @CommandLine.Option(
            names = {"-i", "--input"},
            description = "The directory of source code to partition.",
            required = true,
            paramLabel = "<PATH>"
    )
    private void setInput(Path input) {
        if (!Files.exists(input))
            throw new CommandLine.ParameterException(spec.commandLine(), "Input directory does not exist.");
        if (!Files.isDirectory(input))
            throw new CommandLine.ParameterException(spec.commandLine(), "Input must be a directory.");
        this.input = input;
    }

    private Path input;

    @CommandLine.Option(
            names = {"-o", "--output"},
            description = "The directory to write the subsets to (each pair of partitions).",
            paramLabel = "<PATH>",
            required = true
    )
    private void setOutput(Path output) {
        output = FixPath.getAbsolutePath(output);

        if (Files.exists(output))
            throw new CommandLine.ParameterException(spec.commandLine(), "Output already exists.");
        try {
            Files.createDirectories(output);
        } catch (IOException e) {
            throw new CommandLine.ParameterException(spec.commandLine(), "Output directory could not be created.");
        }
        this.output = output;
    }

    private Path output;

    @CommandLine.Mixin
    private MixinOptions.MaxFiles maxFiles;

    public static void main(String[] args) {
        new CommandLine(new PartitionInput()).execute(args);
    }

    public Void call() {
        try {
            DetectClones.partition(input, output, maxFiles.maxFiles);
        } catch (IOException e1) {
            System.err.println("An exeception occured during partitioning:");
            e1.printStackTrace(System.err);
            System.exit(-1);
        }
        return null;
    }

}
