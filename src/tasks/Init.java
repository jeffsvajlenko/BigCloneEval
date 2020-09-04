package tasks;

import java.sql.SQLException;
import java.util.concurrent.Callable;

import database.Tools;
import picocli.CommandLine;

@CommandLine.Command(
		name = "init",
		description = "This command initializes the tools database. It is used on first-time setup. " +
				"It can also be used to restore the tools database to its original condition. " +
				"This will delete any tools, and their clones, from the database, and restart the ID increment to 1.%n" +
				"This may take some time to execute as the database is compacted.",
		mixinStandardHelpOptions = true,
		versionProvider = util.Version.class)
public class Init implements Callable<Void> {
	public static void main(String[] args)  {
		new CommandLine(new Init()).execute(args);
	}

	@Override
	public Void call()throws SQLException {
		Tools.init();
		return null;
	}
}
