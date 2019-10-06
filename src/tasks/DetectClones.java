package tasks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.SystemUtils;

import picocli.CommandLine;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import tasks.MixinOptions.*;

import util.FixPath;
import util.StreamGobbler;

@CommandLine.Command(
        name = "detectClones",
        description = "Executes the clone detection tool for IJaDataset in an automated procedure. " +
                "Requires a script that configures and executes the tool, " +
                "and the scalability limits of the tool in terms of the maximum input size measured in source files. " +
                "Used deterministic input partitioning to overcome scalability limits. " +
                "Optional, clone detection can be performed manually if desired.",
        mixinStandardHelpOptions = true,
        versionProvider = util.Version.class)
public class DetectClones implements Callable<Void> {

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    @Mixin
    private OutputFile outputFile;

    @Mixin
    private MaxFiles maxFiles;

    @Option(
            names = {"-n", "--nc", "--no-clean"},
            description = "Does not clean up scratch data. For diagnosis/correction. See documentation. Need to specify custom scratch directory for this."
    )
    private boolean noClean;

    private File toolRunner;
    private File scratchDirectory = null;
    private boolean fullclean = true;

    @Option(
            names = {"-r", "--tr", "--tool-runner"},
            description = "Path to the tool runner executable.",
            required = true,
            paramLabel = "<PATH>"
    )
    private void setToolRunner(File toolRunner) {
        toolRunner = FixPath.getAbsolutePath(toolRunner.toPath()).toFile();
        if (!toolRunner.exists()) {
            throw new CommandLine.ParameterException(spec.commandLine(), "Tool runner does not exist.");
        } else if (!Files.isExecutable(toolRunner.toPath())) {
            throw new CommandLine.ParameterException(spec.commandLine(), "Tool runner is not executable.");
        }
        this.toolRunner = toolRunner;
    }

    @Option(
            names = {"-s", "--sd", "--scratch-directory"},
            description = "Directory to be used as scratch space. Default is system tmp directory. Can not already exist.",
            paramLabel = "<PATH>"
    )
    private void setScratchDirectory(File scratchDirectory) {
        if (scratchDirectory.exists()) {
            throw new CommandLine.ParameterException(spec.commandLine(), "Scratch directory already exists. Must specify a new one (to protect against accidental data loss).");
        }
        this.scratchDirectory = scratchDirectory;
        this.fullclean = false;
    }

    private void panic() {
        new CommandLine(this).usage(System.err);
        System.exit(-1);
    }

    public static void main(String[] args) {
        new CommandLine(new DetectClones()).execute(args);
    }

    public Void call() {
        Path dataset = Paths.get("ijadataset/bcb_reduced/");

        // Setup output
        try {
            outputFile.file.delete();
            outputFile.file.getParentFile().mkdirs();
            outputFile.file.createNewFile();
        } catch (Exception e) {
            System.err.println("Failed to create output file.");
            e.printStackTrace(System.err);
            panic();
            return null;
        }

        // Setup Scratch Directory
        try {
            if (scratchDirectory != null) {
                scratchDirectory.mkdirs();
            } else {
                scratchDirectory = Files.createTempDirectory("DetectClones").toFile();
            }
        } catch (IOException e) {
            System.err.println("Failed to create scratch directory.");
            panic();
            return null;
        }

        try {
            DetectClones.detect(outputFile.file.toPath(), dataset, toolRunner.toPath(), scratchDirectory.toPath(), maxFiles.maxFiles, !noClean);
        } catch (IOException e1) {
            System.err.println("An exeception occured during detection:");
            e1.printStackTrace(System.err);
            panic();
            return null;
        }

        // Cleanup
        if (fullclean) {
            try {
                FileUtils.deleteDirectory(scratchDirectory);
            } catch (IOException e) {
                System.err.println("Failed to delete scratch (temporary) directory, please do so manually: " + scratchDirectory.toString());
            }
        }
        return null;
    }

    public static void detect(Path output, Path dataset, Path tool, Path scratchdir, int maxFiles, boolean cleanup) throws IOException {

        output = output.toAbsolutePath();
        dataset = dataset.toAbsolutePath();
        tool = tool.toAbsolutePath();
        scratchdir = scratchdir.toAbsolutePath();

        // Open Output for Writing
        BufferedWriter writer = new BufferedWriter(new FileWriter(output.toFile()));

        // Build list of inputs
        List<Path> inputs = new ArrayList<Path>();
        for (File file : dataset.toFile().listFiles(File::isDirectory)) {//FileUtils.listFilesAndDirs(dataset.toFile(), DirectoryFileFilter.INSTANCE, null)) {
            inputs.add(file.toPath());
        }
        Collections.sort(inputs);
        Collections.reverse(inputs);

        // Run Detection
        for (Path input : inputs) {
            System.out.println("Detecting clones in: " + input.toAbsolutePath());
            if (FileUtils.listFiles(input.toFile(), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).size() > maxFiles) {
                detect(tool, input, writer, scratchdir, maxFiles, cleanup);
            } else {
                try {
                    int retval = detect(tool, input, writer);
                    if (0 != retval) {
                        System.err.println("Execution for input: " + input + " had a non-zero return value: " + retval + ".");
                    }
                } catch (InterruptedException | IOException e) {
                    System.err.println("Execution for input: " + input + " failed with exception: ");
                    e.printStackTrace(System.err);
                }
            }
        }

        // Close Output
        writer.flush();
        writer.close();
    }

    public static int detect(Path tool, Path input, Writer out) throws IOException, InterruptedException {
        ProcessBuilder pb;
        if (SystemUtils.IS_OS_WINDOWS) {
            String[] exec = {"cmd.exe", "/c", tool.toString() + " " + input.toString()};
            pb = new ProcessBuilder(exec);
        } else {
            String[] exec = {"bash", "-c", "\"" + tool.toString() + "\" \"" + input.toString() + "\""};
            pb = new ProcessBuilder(exec);
        }

        Process p = pb.start();
        new StreamGobbler(p.getErrorStream()).start();
        String line = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (!line.equals("")) {
                out.write(line + "\n");
            }
        }
        br.close();
        int retval = p.waitFor();
        return retval;
    }

    public static void detect(Path tool, Path input, Writer out, Path scratchdir, int maxFiles, boolean cleanup) throws IOException {
        // Partition
        Path tmpdir = Files.createDirectories(scratchdir.resolve(input.getFileName().toString() + "_partition"));
        DetectClones.partition(input, tmpdir, maxFiles);

        // Run for each partition
        for (File partition : tmpdir.toFile().listFiles()) {
            System.out.println("\tExecuting for partition: " + partition.getAbsolutePath());
            try {
                int retval = detect(tool, partition.toPath(), out);
                if (0 != retval) {
                    System.err.println("Execution for input: " + input + " partition: " + partition.getName() + " had non-zero return value.");
                }
            } catch (Exception e) {
                System.err.println("Execution for input: " + input + " partition: " + partition.getName() + " failed with exception:");
                e.printStackTrace(System.err);
            }
        }

        // Cleanup
        if (cleanup) {
            try {
                FileUtils.deleteDirectory(tmpdir.toFile());
            } catch (Exception e) {
                System.err.println("Failed to delete a temporary directory, please do so manually: " + tmpdir.toAbsolutePath());
            }
        }
    }

    public static void partition(Path dir, Path split, int maxfiles) throws IOException {
        // Get and Shuffle the files
        List<File> files = new ArrayList<File>(FileUtils.listFiles(dir.toFile(), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE));
        Collections.shuffle(files);

        // halve the maxfiles to get the max size of each partition
        maxfiles = Math.max((int) Math.ceil(1.0 * maxfiles / 2), 1);

        // Number of partitions
        int numpartitions = (int) Math.ceil(1.0 * files.size() / maxfiles);

        // Split files into the partitions
        List<List<File>> partitions = new ArrayList<List<File>>(numpartitions);
        int count = 0;
        for (int i = 0; i < numpartitions; i++) {
            partitions.add(new ArrayList<File>(maxfiles));
            List<File> partition = partitions.get(i);
            for (int j = 0; j < maxfiles && count < files.size(); j++) {
                partition.add(files.get(count));
                count++;
            }
        }

        // Build each pair of partitions on disk, considering symmetry
        for (int i = 0; i < partitions.size(); i++) {
            List<File> partition1 = partitions.get(i);
            for (int j = i + 1; j < partitions.size(); j++) {
                List<File> partition2 = partitions.get(j);

                Path idir = split.resolve(i + "-" + j);
                Files.createDirectories(idir);

                for (int k = 0; k < partition1.size(); k++) {
                    Path ofile = partition1.get(k).getCanonicalFile().toPath();
                    Path nfile = idir.resolve(dir.relativize(ofile));
                    Files.createDirectories(nfile.getParent());
                    Files.copy(ofile, nfile);
                }

                for (int k = 0; k < partition2.size(); k++) {
                    Path ofile = partition2.get(k).getCanonicalFile().toPath();
                    Path nfile = idir.resolve(dir.relativize(ofile));
                    Files.createDirectories(nfile.getParent());
                    Files.copy(ofile, nfile);
                }

            }
        }
    }

}
