package evaluate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cloneMatchingAlgorithms.CloneMatcher;
import database.Clone;
import database.BigCloneBenchDB;

public class EvaluateTools {
	
	public static int INTRA_PROJECT_CLONES = 1;
	public static int INTER_PROJECT_CLONES = 2;
	
	private static String addConditions(String sql, Long functionality_id, Integer type, Integer project_granularity,
			   							Double line_gt_similarity,  Double token_gt_similarity,  Double avg_gt_similarity, Double both_gt_similarity,
			   							Double line_gte_similarity, Double token_gte_similarity, Double avg_gte_similarity, Double both_gte_similarity,
			   							Double line_lt_similarity,  Double token_lt_similarity,  Double avg_lt_similarity, Double both_lt_similarity,
			   							Double line_lte_similarity, Double token_lte_similarity, Double avg_lte_similarity, Double both_lte_similarity,
			   							Integer min_size, Integer max_size, Integer min_pretty_size, Integer max_pretty_size, Integer min_tokens, Integer max_tokens,
			   							Integer min_judges, Integer min_confidence, boolean include_internal) {
		if(!include_internal)
			sql += " AND internal = FALSE ";
		if(functionality_id != null)
			sql += " AND functionality_id = " + functionality_id;
		if(type != null)
			sql += " AND syntactic_type = " + type;
		if(line_gt_similarity != null)
			sql += " AND similarity_line > " + line_gt_similarity;
		if(token_gt_similarity != null)
			sql += " AND similarity_token > " + token_gt_similarity;
		if(avg_gt_similarity != null)
			sql += " AND (1.0*similarity_line + 1.0*similarity_token)/2.0 > " + avg_gt_similarity;
		if(both_gt_similarity != null)
			sql += " AND least(similarity_line, similarity_token) > " + both_gt_similarity;
		if(line_gte_similarity != null)
			sql += " AND similarity_line >= " + line_gte_similarity;
		if(token_gte_similarity != null)
			sql += " AND similarity_token >= " + token_gte_similarity;
		if(avg_gte_similarity != null)
			sql += " AND (1.0*similarity_line + 1.0*similarity_token)/2.0 >= " + avg_gte_similarity;
		if(both_gte_similarity != null)
			sql += " AND least(similarity_line, similarity_token) >= " + both_gte_similarity;
		if(line_lt_similarity != null)
			sql += " AND similarity_line < " + line_lt_similarity;
		if(token_lt_similarity != null)
			sql += " AND similarity_token < " + token_lt_similarity;
		if(avg_lt_similarity != null)
			sql += " AND (1.0*similarity_line + 1.0*similarity_token)/2.0 < " + avg_lt_similarity;
		if(both_lt_similarity != null)
			sql += " AND least(similarity_line, similarity_token) < " + both_lt_similarity;
		if(line_lte_similarity != null)
			sql += " AND similarity_line <= " + line_lte_similarity;
		if(token_lte_similarity != null)
			sql += " AND similarity_token <= " + token_lte_similarity;
		if(avg_lte_similarity != null)
			sql += " AND (1.0*similarity_line + 1.0*similarity_token)/2.0 <= " + avg_lte_similarity;
		if(both_lte_similarity != null)
			sql += " AND least(similarity_line, similarity_token) <= " + both_lte_similarity;
		if(min_size != null)
			sql += " AND min_size >= " + min_size;
		if(max_size != null)
			sql += " AND max_size <= " + max_size;
		if(min_pretty_size != null)
			sql += " AND min_pretty_size >= " + min_pretty_size;
		if(max_pretty_size != null)
			sql += " AND max_pretty_size <= " + max_pretty_size;
		if(min_tokens != null)
			sql += " AND min_tokens >= " + min_tokens;
		if(max_tokens != null)
			sql += " AND max_tokens <= " + max_tokens;
		if(min_judges != null)
			sql += " AND min_judges >= " + min_judges;
		if(max_tokens != null)
			sql += " AND max_tokens <= " + max_tokens;
		if (project_granularity != null) {
			if(project_granularity == EvaluateTools.INTER_PROJECT_CLONES) {
			sql += " AND ((SELECT project FROM functions WHERE id = function_id_one) != (SELECT project FROM functions WHERE id = function_id_two))";
			} else if (project_granularity == EvaluateTools.INTRA_PROJECT_CLONES) {
			sql += " AND ((SELECT project FROM functions WHERE id = function_id_one) = (SELECT project FROM functions WHERE id = function_id_two))";
			}
		}
		return sql;
	}
	
	public static int numTrueDetected (Long tool_id, CloneMatcher matcher, Long functionality_id, Integer type, Integer project_granularity,
									   Double line_gt_similarity,  Double token_gt_similarity,  Double avg_gt_similarity, Double both_gt_similarity,
									   Double line_gte_similarity, Double token_gte_similarity, Double avg_gte_similarity, Double both_gte_similarity,
									   Double line_lt_similarity,  Double token_lt_similarity,  Double avg_lt_similarity, Double both_lt_similarity,
									   Double line_lte_similarity, Double token_lte_similarity, Double avg_lte_similarity, Double both_lte_similarity, 
									   Integer min_size, Integer max_size, Integer min_pretty_size, Integer max_pretty_size, Integer min_tokens, Integer max_tokens,
									   Integer min_judges, Integer min_confidence, boolean includeInternal) throws SQLException {
		
		
		String sql = "SELECT function_id_one, function_id_two FROM clones WHERE 1 = 1";
		sql = addConditions(sql, functionality_id, type, project_granularity,
					line_gt_similarity,  token_gt_similarity,  avg_gt_similarity, both_gt_similarity,
					line_gte_similarity, token_gte_similarity, avg_gte_similarity, both_gte_similarity, 
					line_lt_similarity,  token_lt_similarity,  avg_lt_similarity, both_lt_similarity,
					line_lte_similarity, token_lte_similarity, avg_lte_similarity, both_lte_similarity,
					min_size, max_size, min_pretty_size, max_pretty_size, min_tokens, max_tokens,
					min_judges, min_confidence, includeInternal);
		
		Connection conn = BigCloneBenchDB.getConnection();
		conn.setAutoCommit(false);
		Statement stmt = conn.createStatement();
		stmt.setFetchSize(1000);
		ResultSet rs = stmt.executeQuery(sql);
		
		int numDetected = 0;
		while(rs.next()) {
			long f1_id = rs.getLong(1);
			long f2_id = rs.getLong(2);
			
			Clone clone1 = new Clone(f1_id, f2_id);
			Clone clone2 = new Clone(f2_id, f1_id);
			
			if(matcher.isDetected(clone1) || matcher.isDetected(clone2)) {
				numDetected++;
			}
		}
		
		rs.close();
		stmt.close();
		conn.setAutoCommit(true);
		conn.close();
		
		return numDetected;
	}
	
	public static int numClones (Long functionality_id, Integer type, Integer project_granularity,
			 					 	Double line_gt_similarity,  Double token_gt_similarity,  Double avg_gt_similarity, Double both_gt_similarity,
			 					 	Double line_gte_similarity, Double token_gte_similarity, Double avg_gte_similarity, Double both_gte_similarity,
			 					 	Double line_lt_similarity,  Double token_lt_similarity,  Double avg_lt_similarity, Double both_lt_similarity,
			 					 	Double line_lte_similarity, Double token_lte_similarity, Double avg_lte_similarity, Double both_lte_similarity,
			 					 	Integer min_size, Integer max_size, Integer min_pretty_size, Integer max_pretty_size, Integer min_tokens, Integer max_tokens,
			 					 	Integer min_judges, Integer min_confidence, boolean includeInternal) throws SQLException {

		String sql = "SELECT count(1) FROM clones WHERE 1 = 1";
		sql = addConditions(sql, functionality_id, type, project_granularity,
				line_gt_similarity,  token_gt_similarity,  avg_gt_similarity, both_gt_similarity,
				line_gte_similarity, token_gte_similarity, avg_gte_similarity, both_gte_similarity, 
				line_lt_similarity,  token_lt_similarity,  avg_lt_similarity, both_lt_similarity,
				line_lte_similarity, token_lte_similarity, avg_lte_similarity, both_lte_similarity,
				min_size, max_size, min_pretty_size, max_pretty_size, min_tokens, max_tokens,
				min_judges, min_confidence, includeInternal);
		//System.out.println(sql);
		Connection conn = BigCloneBenchDB.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		
		rs.next();
		int numClones = rs.getInt(1);
		
		rs.close();
		stmt.close();
		conn.setAutoCommit(true);
		conn.close();
		
		return numClones;
	}
	
	public static int numFalsePositives(Long functionality_id, Integer min_judges, Integer min_confidence, Integer project_granularity) throws SQLException {
		String sql = "SELECT count(1) FROM false_positives WHERE 1 = 1 ";
		if(functionality_id != null)
			 sql += " AND functionality_id = " + functionality_id;
		if(min_judges != null)
			sql += " AND min_judges >= " + min_judges;
		if(min_confidence != null)
			sql += " AND min_confidence >= " + min_confidence;
		if (project_granularity != null) {
			if(project_granularity == EvaluateTools.INTER_PROJECT_CLONES) {
			sql += " AND ((SELECT project FROM functions WHERE id = function_id_one) != (SELECT project FROM functions WHERE id = function_id_two))";
			} else if (project_granularity == EvaluateTools.INTRA_PROJECT_CLONES) {
			sql += " AND ((SELECT project FROM functions WHERE id = function_id_one) = (SELECT project FROM functions WHERE id = function_id_two))";
			}
		}
		Connection conn = BigCloneBenchDB.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		rs.next();
		int num = rs.getInt(1);
		rs.close();
		stmt.close();
		conn.close();
		return num;
	}
	
	public static int numFalseDetected(Long tool_id, Long functionality_id, Integer min_judges, Integer min_confidence, Integer project_granularity, CloneMatcher matching_algorithm) throws SQLException {
		Connection conn;
		Statement stmt;
		ResultSet rs;
		String sql;
		int numDetected = 0;
		
		sql = "SELECT function_id_one, function_id_two from false_positives WHERE 1 = 1 ";
		if(functionality_id != null)
			sql += " AND functionality_id = " + functionality_id;
		if(min_judges != null)
			sql += " AND min_judges >= " + min_judges;
		if(min_confidence != null)
			sql += " AND min_confidence >= " + min_confidence;
		if (project_granularity != null) {
			if(project_granularity == EvaluateTools.INTER_PROJECT_CLONES) {
			sql += " AND ((SELECT project FROM functions WHERE id = function_id_one) != (SELECT project FROM functions WHERE id = function_id_two))";
			} else if (project_granularity == EvaluateTools.INTRA_PROJECT_CLONES) {
			sql += " AND ((SELECT project FROM functions WHERE id = function_id_one) = (SELECT project FROM functions WHERE id = function_id_two))";
			}
		}
			
		conn = BigCloneBenchDB.getConnection();
		conn.setAutoCommit(false);
		stmt = conn.createStatement();
		stmt.setFetchSize(1000);
		rs = stmt.executeQuery(sql);
		
		while(rs.next()) {
			long f1_id = rs.getLong(1);
			long f2_id = rs.getLong(2);
			
			Clone clone1 = new Clone(f1_id ,f2_id);
			Clone clone2 = new Clone(f2_id, f1_id);
			
			
			if(matching_algorithm.isDetected(clone1) || matching_algorithm.isDetected(clone2)) {
				numDetected++;
			}
		}
		
		rs.close();
		stmt.close();
		conn.setAutoCommit(true);
		conn.close();
		
		return numDetected;
	}
	
}
