package tasks;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import util.FixPath;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static evaluate.ToolEvaluator.*;

abstract class MixinOptions {
    public static class ToolId {
        @Option(
                names = {"-t", "--tool"},
                required = true,
                paramLabel = "<ID>",
                description = "The ID of the tool."
        )
        public long id;
    }

    public static class OutputFile {
        @CommandLine.Spec
        private CommandLine.Model.CommandSpec spec;

        @Option(
                names = {"-o", "--output"},
                description = "File to write the report to.",
                required = true,
                paramLabel = "<PATH>"
        )
        private void setOutput(File output) {
            output = FixPath.getAbsolutePath(output.toPath()).toFile();
            if (output.isDirectory()) {
                throw new CommandLine.ParameterException(spec.commandLine(), "Specified output report file is an existing directory.");
            } else if (output.exists() && !output.canWrite()) {
                throw new CommandLine.ParameterException(spec.commandLine(), "Output file already exists, but is not over-writable.");
            } else if (!output.exists()) {
                try {
                    output.getParentFile().mkdirs();
                    output.createNewFile();
                } catch (IOException e) {
                    throw new CommandLine.ParameterException(spec.commandLine(), "Could not create output file: " + output);
                }
            }
            file = output;
        }

        public File file;
    }

    public static class InputFile {
        @CommandLine.Spec
        private CommandLine.Model.CommandSpec spec;

        @CommandLine.Option(
                names = {"-c", "--clones"},
                required = true,
                paramLabel = "<FILE>",
                description = "File containing the detected clones."
        )
        private void setInput(File input) {
            input = FixPath.getAbsolutePath(input.toPath()).toFile();
            if (!input.exists()) {
                throw new CommandLine.ParameterException(spec.commandLine(), "Input clones file does not exist.");
            }
            if (!input.isFile()) {
                throw new CommandLine.ParameterException(spec.commandLine(), "Input clones file is not a regular file.");
            }
            if (!input.canRead()) {
                throw new CommandLine.ParameterException(spec.commandLine(), "Input clones file is not readable.");
            }
            this.input = input;
        }

        public File input;
    }

    public static class MaxFiles {
        @CommandLine.Spec
        private CommandLine.Model.CommandSpec spec;

        @CommandLine.Option(
                names = {"-m", "--mf", "--max-files"},
                description = "Maximum files in each output subset (pair of partitions).",
                paramLabel = "<int>",
                required = true
        )
        private void setMaxFiles(int maxFiles) {
            if (maxFiles <= 0) {
                throw new CommandLine.ParameterException(spec.commandLine(), "Invalid value for max-files.");
            }
            this.maxFiles = maxFiles;
        }

        public int maxFiles = Integer.MAX_VALUE;
    }

    private static abstract class MinMaxBase {
        @CommandLine.Spec
        protected CommandLine.Model.CommandSpec spec;

        protected Integer min = null;
        protected Integer max = null;

        protected abstract String getName();

        protected void validate() {
            if (min != null && min < 0)
                throw new CommandLine.ParameterException(spec.commandLine(), "Minimum " + getName() + " must be >= 0");
            if (max != null && max < 0)
                throw new CommandLine.ParameterException(spec.commandLine(), "Maximum " + getName() + " must be >= 0");
            if (min != null && max != null && min > max)
                throw new CommandLine.ParameterException(spec.commandLine(), "Minimum " + getName() + " must be less than maximum " + getName());
        }

        public Integer getMin() {
            return min;
        }

        public Integer getMax() {
            return max;
        }
    }

    public static class MinMaxLines extends MinMaxBase {
        @Option(
                names = {"--mil", "--min-lines", "--minimum-lines"},
                paramLabel = "<int>",
                description = "Minimum clone size in original lines."
        )
        private void setMin(int min) {
            this.min = min;
            validate();
        }

        @Option(
                names = {"--mal", "--max-lines", "--maximum-lines"},
                paramLabel = "<int>",
                description = "Maximum clone size in original lines."
        )
        private void setMax(int max) {
            this.max = max;
            validate();
        }

        @Override
        protected String getName() {
            return "original lines";
        }
    }

    public static class MinMaxPretty extends MinMaxBase {
        @Option(
                names = {"--mip", "--min-pretty", "--minimum-pretty"},
                paramLabel = "<int>",
                description = "Minimum clone size in pretty-printed lines."
        )
        private void setMin(int min) {
            this.min = min;
            validate();
        }

        @Option(
                names = {"--map", "--max-pretty", "--maximum-pretty"},
                paramLabel = "<int>",
                description = "Maximum clone size in pretty-printed lines."
        )
        private void setMax(int max) {
            this.max = max;
            validate();
        }

        @Override
        protected String getName() {
            return "pretty-printed";
        }
    }

    public static class MinMaxTokens extends MinMaxBase {
        @Option(
                names = {"--mit", "--min-tokens", "--minimum-tokens"},
                paramLabel = "<int>",
                description = "Minimum clone size in tokens."
        )
        private void setMin(int min) {
            this.min = min;
            validate();
        }

        @Option(
                names = {"--mat", "--max-tokens", "--maximum-tokens"},
                paramLabel = "<int>",
                description = "Maximum clone size in tokens."
        )
        private void setMax(int max) {
            this.max = max;
            validate();
        }

        @Override
        protected String getName() {
            return "tokens";
        }
    }

    public static class EvaluationOptions {

        public enum SimType {
            TOKEN(SIMILARITY_TYPE_TOKEN),
            LINE(SIMILARITY_TYPE_LINE),
            BOTH(SIMILARITY_TYPE_BOTH),
            AVG(SIMILARITY_TYPE_AVG);

            public final int val;

            SimType(int val) {
                this.val = val;
            }
        }

        @CommandLine.Spec
        private CommandLine.Model.CommandSpec spec;

        @CommandLine.Mixin
        public MinMaxLines lines;
        @CommandLine.Mixin
        public MinMaxPretty pretty;
        @CommandLine.Mixin
        public MinMaxTokens tokens;


        @CommandLine.Option(
                names = {"--st", "--similarity-type"},
                description = "How to measure similarity. One of ${COMPLETION-CANDIDATES}. Defaults to BOTH",
                paramLabel = "<STR>"
        )
        SimType simtype = SimType.BOTH;

        @CommandLine.Option(
                names = {"-j", "--mij", "--minimum-judges"},
                description = "Minimum number of judges.",
                paramLabel = "<int>"
        )
        Integer minjudges = null;

        @CommandLine.Option(
                names = {"--mic", "--minimum-confidence"},
                description = "Minimum confidence.",
                paramLabel = "<int>"
        )
        private void setMinConfidence(int minConfidence) {
            if (minconfidence < 0) {
                throw new CommandLine.ParameterException(spec.commandLine(), "Minimum confidence must be >= 0.");
            }
            this.minconfidence = minConfidence;
        }

        Integer minconfidence = null;

        @CommandLine.Option(
                names = {"-s", "--mis", "--minimum-similarity"},
                description = "Minimum clone similarity to evaluate down to.",
                paramLabel = "<int>"
        )
        private void setMinsim(int minsim) {
            if (minsim < 0 || minsim > 100) {
                throw new CommandLine.ParameterException(spec.commandLine(), "Minimum similarity must be a multiple of 5 in range [0,100].");
            }
            if (minsim % 5 != 0) {
                throw new CommandLine.ParameterException(spec.commandLine(), "Minimum similarity must be a multiple of 5.");
            }
            this.minsim = minsim;
        }

        int minsim = 0;
    }
}
