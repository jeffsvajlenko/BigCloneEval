package tasks;

import java.sql.SQLException;

import database.Tools;

public class Init {
	public static void main(String args[]) throws SQLException {
		Tools.init();
	}
}
