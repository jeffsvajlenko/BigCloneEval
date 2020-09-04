# BigCloneEval: Evaluating Clone Detection Tools with BigCloneBench

BigCloneEval is a framework for performing clone detection tool evaluation experiments
with the BigCloneBench clone benchmark.

Database and database files available at:  http://jeff.svajlenko.com/bigcloneeval.

<details><summary>Table of Contents</summary>

  * [Contact Information](#contact-information)
  * [Installation and Setup](#installation-and-setup)
     * [Step 1: Get the latest version of BigCloneEval](#step-1-get-the-latest-version-of-bigcloneeval)
     * [Step 2: Get the latest version of BigCloneBench](#step-2-get-the-latest-version-of-bigclonebench)
     * [Step 3: Get the latest version of IJaDataset](#step-3-get-the-latest-version-of-ijadataset)
     * [Step 4: Build the source code.](#step-4-build-the-source-code)
     * [Step 5: Initialize the tools database](#step-5-initialize-the-tools-database)
  * [Using BigCloneEval](#using-bigcloneeval)
     * [Experimental Process](#experimental-process)
     * [Commands Summary](#commands-summary)
     * [Step 1: Register Tool](#step-1-register-tool)
     * [Step 2: Clone Detection](#step-2-clone-detection)
     * [Step 3: Import Clones](#step-3-import-clones)
     * [Step 4: Execute evaluation](#step-4-execute-evaluation)
  * [Command Documentation](#command-documentation)
     * [init](#init)
     * [registerTool](#registertool)
     * [listTools](#listtools)
     * [detectClones](#detectclones)
     * [importClones](#importclones)
     * [deleteTool](#deletetool)
     * [clearClones](#clearclones)
     * [evaluate](#evaluate)
     
</details>

## Contact Information

We are happy to answer any of your questions regarding BigCloneEval or BigCloneBench.
Please report any bugs you encounter so we can address them promptly.
Feel free to contact us:

- Jeff Svajlenko (jeff.svajlenko@gmail.com)
- Chanchal K. Roy (chanchal.roy@usask.ca)

## Installation and Setup

Complete the following steps to install the setup BigCloneEval.

Alternatively, download the VM version of BigCloneEval to have a pre-configured
environment. Username: `bce`, password: `clones`.

VM available in VMWare format at: https://1drv.ms/u/s!AhXbM6MKt_yLj_42w4Y-l5isPPiOOw?e=SIS5yW

The VM was created with VMWare Player Workstation, which is free for personal and educational
purposes: https://www.vmware.com/products/workstation-player.html.

### Step 1: Get the latest version of BigCloneEval

BigCloneEval is available as a github repository.  The latest version can be retrieved
using the following git command:
```
git clone https://github.com/jeffsvajlenko/BigCloneEval
```

### Step 2: Get the latest version of BigCloneBench

Downloaded the latest version of BigCloneBench (as specially packaged for BigCloneEval) 
from the following webpage:

http://jeff.svajlenko.com/bigcloneeval

Direct link: https://www.dropbox.com/s/z2k8r78l63r68os/BigCloneBench_BCEvalVersion.tar.gz?dl=0

Extract the contents of BigCloneBench (BigCloneBench_BCEvalVersion.tar.gz) into the
'bigclonebenchdb' directory of the BigCloneEval distribution.

To manually view this database, use h2database: http://h2database.com/html/main.html.

### Step 3: Get the latest version of IJaDataset

Download the latest version of IJaDataset (as specially packaged for BigCloneEval) from
the following webpage:

http://jeff.svajlenko.com/bigcloneeval

Direct link: https://www.dropbox.com/s/xdio16u396imz29/IJaDataset_BCEvalVersion.tar.gz?dl=0

Extract the contents of IJaDataset (IJaDataset_BCEvalVersion.tar.gz) into the 'ijadataset'
directory of the BigCloneEval distribution.

This should create a directory 'ijadataset/bcb_reduced/' which contains one sub-directory
per functionality in BigCloneBench.

### Step 4: Build the source code.

From thje root directory, run `make`.

### Step 5: Initialize the tools database

From the `commands/` directory, execute the `init` script.  This will initialize the tools
database.


## Using BigCloneEval

The following documents the usage of BigCloneEval.  Please also see the demonstration
video on the BigCloneEval webpage.

### Experimental Process

To evaluate the recall of a clone detection tool you must complete the following steps:

1. Register the clone detection tool with BigCloneEval.
2. Detect clones in IJaDataset using the clone detection tool.
3. Import the detected clones into BigCloneEval.
4. Configure and execution the evaluation experiment.

These steps are performed using BigCloneEval's commands, which are located in the command/
directory as scripts.  These scripts must be executed from within the command directory
(the command directory must be the working directory).

In the next section we outline the available commands.  Then we discuss how each step can
be performed in detail.  Then we include specific documentation for each command.

### Commands Summary

The commands are available in the `commands/` directory, and should be executed from that
directory.  Executing the commands with the `-h` flag will show their parameters.
The special `bcb` command can execute all other commands and provide a usage help. Run
`./bcb help` to get the following overview:

```
Usage: bcb COMMAND
The big clone bench tool
Commands:
  clearClones     Removes the imported clones for the specified registered tool.
  countClones     Count the number of clones that have been imported for the
                    tool.
  deleteTool      Deletes a tool, specified by its ID, from the framework. Also
                    removes any imported clones for this tool.
  detectClones    Executes the clone detection tool for IJaDataset in an
                    automated procedure. Requires a script that configures and
                    executes the tool, and the scalability limits of the tool
                    in terms of the maximum input size measured in source
                    files. Used deterministic input partitioning to overcome
                    scalability limits. Optional, clone detection can be
                    performed manually if desired.
  evaluateRecall  Measures the recall of the clones given. Highly configureable.
                    Summarizes recall per clone type, per inter vs
                    intra-project clones, per functionality in BigCloneBench
                    and for different syntactical similarity regions in the
                    output tool evaluation report.
  evaluateTool    Measures the recall of the specific tool based on the clones
                    imported for it. Highly configureable, including using
                    custom clone-matching algorithms. Summarizes recall per
                    clone type, per inter vs intra-project clones, per
                    functionality in BigCloneBenchand for different syntactical
                    similarity regions in the output tool evaluation report.
  importClones    Imports the clones detected by a tool in IJaDataset into the
                    framework for evaluation. Clones are provided as clone
                    pairs in a simple CSV file. See documentation below for the
                    expected format.
  init            This command initializes the tools database. It is used on
                    first-time setup. It can also be used to restore the tools
                    database to its original condition. This will delete any
                    tools, and their clones, from the database, and restart the
                    ID increment to 1.
                  This may take some time to execute as the database is
                    compacted.
  listTools       Lists the tools registered in the database. Including their
                    ID, name and description.
  partitionInput  partitions the files from the input directory to the output
                    directory.
  registerTool    Registers a clone detection tool with the framework. Requires
                    a name and description of the tool, which is stored in the
                    tools database. Returns a unique identifier for the tool
                    for indicating the target tool for the other commands. Name
                    and description are for reference by the user.
  help            Displays help information about the specified command
```
		
### Step 1: Register Tool

First the tool must be registered to the framework.  This is done by the registerTool
command, which requires a name and description of the tool.  The intention is for the user
to use the name field to record the name and version of the tool, and the description to
denote the configuration used in the detection experiment.  These are stored for later
reference by the user.  A unique identifier is output for the user to refer to this tool
in the latter steps.


### Step 2: Clone Detection

Next the tool must be executed for IJaDataset.  This can be done manually or using our 
detectClones command.  Use the method which is easiest for your tool.

#### Manually
You must execute the tool for each subdirectory in `ijadataset/bcb_sample`.  For example,
you must execute the tool for `ijadataset/bcb_sample/2` and `ijadataset/bcb_sample/3`,
etc.  Each sub-directory is the files from the full IJaDataset that contain clones of one
of the functionalities in BigCloneBench.

Some of these sub-directories may contain too many source files for the scalability
constraints of some tools, particularly those that require significant memory.  In this
case the partitionInput command can be used to split these inputs into a number of smaller
inputs given a maximum number of files the tool can reliably handle.

#### Automatically
To use the automatic clone detection procedure, a script must be provided that configures
and executes the tool.  On Linux and OSX this is expected to be a bash script, and it
executed using `bash -c /path/to/script/ /path/to/clone/detection/input/`.  On Windows,
this is expected to be a command script, and is executed using
`cmd.exe /c C:\path\to\script\ C:\path\to\clone\detection\input\`.

The script is expected to take as a parameter a directory which contains source code
(possibly in sub-directories) for clone detection to be executed on.  The script should
configure and execute the tool, and output the detected clones to stdout.  The script
should terminate with an exit code of 0 if it was successful, or non-zero if it failed.
The tool should not output anything to stdout except for the detected clones.  Ideally,
the clones should be output in the format expected by the `importClones` command (see the
next step), although any output is supported.  

The `sample/` directory contains the `nicadRunner` as an example bash script targeting
Linux/OSX, and and `iclonesRunner.cmd` command script targeting Windows.  Note that these
scripts won't run as is, and will require downloading and installing the clone detection
tools, and modifying the runner to work with your computer's environment.

The `detectClones` command takes this script as input, the maximum files the tool can handle
without scalability issues on the available hardware, and a scratch directory to use in
case input partitioning is required for scalability issues (otherwise the OS tmp directory
is used).

`detectClones` then executes the script with each sub-directory in `ijadatset/bcb_sample/`
as input, using input partitioning on the sub-directories that exceed the tool's indicated
scalability constraints.  The output from each execution is concatenated into a final
output file.

Note that due to the nature of how we reduced IJaDataset, and how input partitioning
works, the final output may contain some duplicated clones (not the fault of the tool
itself).  These can be trimmed from the output if desired, but will not affect the
evaluation results.

### Step 3: Import Clones

The next step is to import the clones, which is done using the `importClones` command.  This
takes the ID of the registered tool, and a file containing the clones.  The `importClones`
command expects the clones to be formatted in a simple CSV file, where each line specifies
one clone pair in the following format:

```
cf1_subdirectory,cf1_filename,cf1_startline,cf1_endline,cf2_subdirectory,cf2_filename,cf2_endline
```

For example:

```
selected,102353.java,10,20,default,356923.java,20,30
sample,DownloadWebpage.java,20,50,selected,10234,356123.java,30,50
```

Where `cf#_subdirectory,cf#_filename,cf#_startline,cf#_endline` specifies one of the code
fragments.  And the order of the code fragments in the clone pair does not matter.

`cf#_subdirectory` is the sub-directory of IJaDataset the source file is found in.
IJaDataset has three subdirectories: `selected`, `default` and `sample`.  For example,
sub-input `ijadataset/bcb_sample/2` contains a `selected`, `default` and `sample`
directory.  The meaning behind the sub-directories is not important, but it needs to be
specified to fully resolve the source file.

If your tool outputs clone classes, they need to be converted into clone pairs for this
format.

### Step 4: Execute evaluation.

The evaluate command is used to measure the recall of the clone detection tool for
BigCloneBench based on its imported clones.  This takes the ID of the tool, a file to
output the tool evaluation report to, and a number of configuration options for
customizing the recall evaluation experiment.

#### Clone Matcher
A clone matcher must be specified.  This is the algorithm used to determine if a reference
clone in the benchmark is sufficiently captured by a candidate clone reported by the tool
for the reference clone to be considered as detected by the candidate tool.

BigCloneEval includes our coverage-based clone matching algorithm.  More details are
provided below in the Command Documentation section.  This is implemented in a plugin
architecture such that users can implement their own clone matching algorithm using our
existing algorithm as a template.

The clone matcher to be used is specified by name, including a parameter string, when 
the evaluate command is used.

#### Reference Clone Constraints
A number of configuration parameters place constraints on which reference clones are
considered when measuring recall.  The following constraints are available:

 - minimum and/or maximum clone size in tokens, original source lines, and/or pretty-printed source lines
 - minimum confidence
 - minimum judges

The clone size constraints makes it easy to set a standard clone size for the experiment
and appropriately and fairly configure your candidate tools for their benchmark
experiment.  Clone size constraints are available in the major ways in which clone size
is typically measured.

The minimum judges constraint selects only those clones whose code fragments (functions)
have been examined by a minimum number of judges.  Each code fragment has been inspected
for the functionality of the clone by at least one judge.  Some have been examined by
multiple judges in order to measure the accuracy of the judging.  This way you can select
just those examined by some number of judges.  Only a small portion has been examined by
multiple judges currently.  In future we hope to have the entire benchmark examined by
multiple judges.

Similar to the minimum judges, the minimum confidence is the minimum agreement when
multiple judges are used.  This is, for both code fragments, the minimum difference in
true positive vs false positive votes by the judges.  Currently a small portion of the
data has been seen by more than two judges, where this constraint is relevant.

#### Similarity Type
The user can specify how the syntactical similarity of the reference clones should be
measured.  BigCloneBench includes syntactical similarity measured both by line and by
token after Type-1 and Type-2 normalizations.  This includes a strict pretty-printing,
the removal of comments, blind identifier renaming and literal abstraction.  Similarity
is measured as the minimum ratio of the lines or tokens one code fragment in the clone
pair shares with the other.  Shared lines or tokens are identified with the diff algorithm
which considers the order of the lines or tokens.

Syntactical similarity for the experimetn can then be measured by line, by token, by the
average of the line and token measures, or by both (using the smaller of the two).

We reccomend using `BOTH` as it is the most fair option when comparing token and
line-based tools without bias.  This is the setting we use in our studies, and when
reporting the clone demographics of BigCloneBench

#### Minimum Similarity
You can also specify the minimum syntactical clone similarity of clones to consider.  By
default, the evaluation considers the full scope of syntactical similarity, 0% to 100%.
However, most tools are syntax-based, and will have none or near-zero detection much
below their configured detection threshold.  So some execution time can be saved by
omitting the reporting of recall for these clones.  The minimum similarity to measure
recall to must be specified as a multiple of 5.

BigCloneBench divides Type-3 and Type-4 clones by their syntactical similarity into four
categories:

Very-Strongly Type-3   : Clones with 90% (inclsuive) to 100% (exclusive) shared syntax.
Strongly Type-3        : Clones with 70% (inclsuive) to  90% (exclusive) shared syntax.
Moderately Type-3      : Clones with 50% (inclsuive) to  70% (exclusive) shared syntax.
Weakly Type-3 / Type-4 : Clones with  0% (inclsuive) to  50% (exclusive) shared syntax.

These are canonical categories for BigCloneBench, but evaluate also reports recall for
every lower threshold and range of syntactical similarity (in 5% granularity).

Most syntax-based Type-3 tools operate mostly in the Very-Stringly and Strongly
categories.  AST and PDG type tools may have some recall in the moderately Type-3 category
as well, but not usually significant.

If time allows, it is best to run for full range.  However, executing for a minimum
similarity of 50% is sufficient for most tools, as they have 0% or near-0% for the Weakly
Type-3 / Type-4 category, and often similar for the Moderately Type-3.

#### Reccomended Settings
The reccomended settings are as follows:

similarity type = both line and token (smaller measure)
minimum clone size in original source lines = 6 lines
minimum clone size in pretty-printed source lines = 6 lines
minimum clone size in tokens = 50 tokens
clone-matcher: Coverage clone matcher with 70% coverage threshold

These correspond to the settings we have used in our previous studies.

#### Tool Evaluation Report

The tool evaluation report sumamrizes recall from a number of perspectives.

Recall is reported per clone type.  Including Type-1, Type-2, Type-3 and Type-4.  The
recall for Type-2 is reported for different common definitions of Type-2.  Since there is
no agreed upon minimum similarity of a Type-3 clone, the Type-3 and Type-4 clones are
split into a number of categories.  Note that sine all of the clones are semantic clones
(implement a shared functionality), those which do not meet the Type-3 definition are 
Type-4 clones.  We summarize the per-clone-type categories below:

 - **Type-1**: Recall for the Type-1 clones.
 - **Type-2**: Recall for all Type-2 clones (most generous definition).
 - **Type-2 (blind)**: Type-2 clones which don't have a 1:1 mapping of identifier names.
 - **Type-2 (consistent)**: Type-2 clones which have a 1:1 mapping of identifier names.
 - **Very-Strongly Type-3**: Clones with sytnactical similarity in range [90,100).
 - **Strongly Type-3**: Clones with sytnactical similarity in range [70,90)
 - **Moderately Type-3**: Clones with syntactical similarity in range [50,70)
 - **Weakly Type-3 or Type-4**: Clones with syntactical similarity in range [0,50)
	
The categories assume that Type-4 clones share less than 50% of their syntax.  Most Type-3
clone detectors should have good recall in the Very-Strongly Type-3 range.  Very good
Type-3 detectors also have good recall in the Strongly Type-3 while maintaining good
precision.  It is expected that Moderately Type-3 clones are outside the scope of syntax
based clone detection approaches, unless precision is significantly compromised.  Other
techniques that do not rely so much on the syntax of the clone may perform better in this
range.  The weakly Type-3/Type-4 clones are probably outside the scope of most or all
clone detection techniques.

Recall is also reported for Type-3/Type-4 clones in different syntactical similarity
ranges.  It is reported for each range at a granularity of 5%: [0,5), [5,10), etc.  It is
also reported for each minimum syntactical similarity at a granularity fo 5%: [0,100),
[5,100), [10,100), etc.

Recall is reported for all clones, for just the inter-project clones, and for just the
intra-project clones.  Recall can then be compared for these different contexts.

Recall is also reported for all clones, and then also the clones of each individual
functionality in BigCloneBench.

The evaluation report summarizes the number of reference clones considered in each
measurement.

The evaluation report documents the version of BigCloneEval and BigCloneBench used to
create it for future reference.

## Command Documentation

### init
```
Usage: bcb init [-hV]
This command initializes the tools database. It is used on first-time setup. It
can also be used to restore the tools database to its original condition. This
will delete any tools, and their clones, from the database, and restart the ID
increment to 1.
This may take some time to execute as the database is compacted.
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
```
Example:
```
  $ ./init
```

### registerTool
```
Usage: bcb registerTool [-hV] -d=<string> -n=<string>
Registers a clone detection tool with the framework. Requires a name and
description of the tool, which is stored in the tools database. Returns a
unique identifier for the tool for indicating the target tool for the other
commands. Name and description are for reference by the user.
  -d, --description=<string>
                        A description for the tool.  Use quotes to allow spaces
                          and special characters.
  -h, --help            Show this help message and exit.
  -n, --name=<string>   A name for the tool.  Use quotes to allow spaces and
                          special characters.
  -V, --version         Print version information and exit.
```
Example:
```
  $ ./registerTool -n "NiCad" -d "Default configuration"
  2
```

### listTools
```
Usage: bcb listTools [-hV]
Lists the tools registered in the database. Including their ID, name and
description.
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
```
Example:
```
  $ ./listTools
  1
  NiCad
  MyNiCadExecution

  2
  NiCad
  Default
```

### detectClones
```
Usage: bcb detectClones [-hnV] -m=<int> -o=<PATH> -r=<PATH> [-s=<PATH>]
Executes the clone detection tool for IJaDataset in an automated procedure.
Requires a script that configures and executes the tool, and the scalability
limits of the tool in terms of the maximum input size measured in source files.
Used deterministic input partitioning to overcome scalability limits. Optional,
clone detection can be performed manually if desired.
  -h, --help             Show this help message and exit.
  -m, --mf, --max-files=<int>
                         Maximum files in each output subset (pair of
                           partitions).
  -n, --nc, --no-clean   Does not clean up scratch data. For
                           diagnosis/correction. See documentation. Need to
                           specify custom scratch directory for this.
  -o, --output=<PATH>    File to write the report to.
  -r, --tr, --tool-runner=<PATH>
                         Path to the tool runner executable.
  -s, --sd, --scratch-directory=<PATH>
                         Directory to be used as scratch space. Default is
                           system tmp directory. Can not already exist.
  -V, --version          Print version information and exit.
```
Example:
```
  $ ./detectClones -tr ~/NiCadRunner -o ~/clones -mf 10000
```

### importClones
```
Usage: bcb importClones [-hV] -c=<FILE> -t=<ID>
Imports the clones detected by a tool in IJaDataset into the framework for
evaluation. Clones are provided as clone pairs in a simple CSV file. See
documentation below for the expected format.
  -c, --clones=<FILE>   File containing the detected clones.
  -h, --help            Show this help message and exit.
  -t, --tool=<ID>       The ID of the tool.
  -V, --version         Print version information and exit.
```
Example:
```
  $ ./importClones -t 2  -c ~/clones
```

### deleteTool
```
Usage: bcb deleteTool [-hV] -t=<ID>
Deletes a tool, specified by its ID, from the framework. Also removes any
imported clones for this tool.
  -h, --help        Show this help message and exit.
  -t, --tool=<ID>   The ID of the tool.
  -V, --version     Print version information and exit.
```
Example:
```
  $ ./deleteTool -t 2
```

### clearClones
```
Usage: bcb clearClones [-hV] -t=<ID>
Removes the imported clones for the specified registered tool.
  -h, --help        Show this help message and exit.
  -t, --tool=<ID>   The ID of the tool.
  -V, --version     Print version information and exit.
```
Example:
```
  $ ./clearClones -t 2
```

### evaluate
```
Usage: bcb evaluateTool [-hV] [-j=<int>] [-m=<MATCHER>] [--mal=<int>]
                        [--map=<int>] [--mat=<int>] [--mic=<int>] [--mil=<int>]
                        [--mip=<int>] [--mit=<int>] -o=<PATH> [-s=<int>]
                        [--st=<STR>] -t=<ID>
Measures the recall of the specific tool based on the clones imported for it.
Highly configureable, including using custom clone-matching algorithms.
Summarizes recall per clone type, per inter vs intra-project clones, per
functionality in BigCloneBenchand for different syntactical similarity regions
in the output tool evaluation report.
  -h, --help                Show this help message and exit.
  -j, --mij, --minimum-judges=<int>
                            Minimum number of judges.
  -m, --matcher=<MATCHER>   Specify the clone matcher. See documentation for
                              configuration strings. Default is
                              coverage-matcher with 70% coverage threshold.
      --mal, --max-lines, --maximum-lines=<int>
                            Maximum clone size in original lines.
      --map, --max-pretty, --maximum-pretty=<int>
                            Maximum clone size in pretty-printed lines.
      --mat, --max-tokens, --maximum-tokens=<int>
                            Maximum clone size in tokens.
      --mic, --minimum-confidence=<int>
                            Minimum confidence.
      --mil, --min-lines, --minimum-lines=<int>
                            Minimum clone size in original lines.
      --mip, --min-pretty, --minimum-pretty=<int>
                            Minimum clone size in pretty-printed lines.
      --mit, --min-tokens, --minimum-tokens=<int>
                            Minimum clone size in tokens.
  -o, --output=<PATH>       File to write the report to.
  -s, --mis, --minimum-similarity=<int>
                            Minimum clone similarity to evaluate down to.
      --st, --similarity-type=<STR>
                            How to measure similarity. One of TOKEN, LINE,
                              BOTH, AVG. Defaults to BOTH
  -t, --tool=<ID>           The ID of the tool.
  -V, --version             Print version information and exit.
```

#### Clone Matchers:

##### Coverage Matcher
Requires the tool to report a candidate clone that covers a certain ratio of the
reference clone for the reference clone to be considered detected.  Takes one or three
parameters.  The first parameter is the coverage ratio.  We recommend a 0.70 coverage
ratio.
	
Optionally, the user can put a constraint on how far outside of the clone the
candidate clone can extend while still being considered a match.  Since BigCloneBench
has function clones, it is not desirable for the tool to report clones that extend 
significant beyond the boundary of the clone.  The user can specify a tolerance on
the extension beyond the boundary of the reference clone by a number of original
source lines, or a ratio of the size of the clone itself.
	
Examples:
	
 - `CoverageMatcher 0.7`:
			The candidate clone must cover 70% of the reference clone by line.  The
			candidate clone can extend indefinitely beyond the boundaries of the reference
			clone without punishment.
- `CoverageMatcher 0.7 line 2`:
			The candidate clone must cover 70% of the reference clone by line.  If the
			candidate clone extends 2 line beyond the boundary of the reference clone (by
			either code fragment) then the candidate clone is rejected as a match.
- `CoverageMatcher 0.7 ratio 0.20`
			The candidate clone must cover 70% of the reference clone by line.  If the
			candidate clone extends 20% beyond the boundary of the reference clone, by
			each code fragment's size in lines, then the candidate clone is rejected as a
			match.
			
See the source file `src/cloneMatchingAlgorithm/CoverageMatcher.java` for more details.


##### Custom Clone Matcher
To provide your own clone matcher, create a class implementing the CloneMatcher
	interface.  Your custom clone matcher must be in the 'cloneMatchingAlgorithms'
	package, and on the classpath.  Ideally, install your compiled .class in the
	'bin/cloneMatchingAlgorithms' directory.  The clone matchers are discovered by
	reflection at runtime.  Your custom clone matcher must have a constructor which takes
	an long as its first parameter and a string as it second parameter.  The long is the
	ID of the tool being evaluated, and the string is the configuration string specified
	in the execution of evaluate.  You specify your clone matcher as such when executing
	the evaluate command:
	
`-m "MyCloneMatcher arg1 arg2 arg3"`
	
The constructor of your clone matcher will be called with the ID of the tool, and then
	"arg1 arg2 arg3" as the configuration string.
	
Your clone matcher implements the `isDetected(Clone clone)` method.  It should check if
	the specified reference clone was detected using a query over the tool's clone table.
	
The tool's clone table will be named: `tool_$ID$_clones`, where `$ID$` is the ID of the
	tool.  The clone table has fields: `type1`, `name1`, `startline1`, `endline1`, `type2`, `name2`,
	`startline2`, `endline2`.  Where the start/end lines are integers, and the other
	parameters are strings (varchar).
	
This is in the tools DB, which a connection to this database is available by the
	`ToolsDB` class.
	
Your clone matcher should check both orderings of the reference clone's code fragments
	as they may be reversed in the tool's detected clones.
	
See the existing `CoverageMatcher` as a template.
	
BigCloneEval is distributed as an eclipse project.  So the easiest way to develop
	your own clone matcher is to open BigCloneEval as an eclipse Java project and
	develop your code from there.

Example:
```
  $ ./evaluate -t 3 -o ~/report -st both -m Coverage "CoverageMatcher 0.7 ratio 0.2" -mis 50 -mil 10 -mip 10 -mit 50
```
