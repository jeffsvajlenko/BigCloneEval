package evaluate;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import cloneMatchingAlgorithms.CloneMatcher;
import database.Functionalities;

public class ToolEvaluator implements Serializable {

	public void close() throws SQLException {
		this.matcher.close();
	}
	
	public String toString() {
		String str;
		str  = "       Tool ID: " + tool_id;
		str += "\n";
		str += "       Matcher: " + matcher.toString();
		str += "\n";
		str += "       MinSize: " + min_size;
		str += "\n";
		str += "       MaxSize: " + max_size;
		str += "\n";
		str += " MinPrettySize: " + min_pretty_size;
		str += "\n";
		str += " MaxPrettySize: " + max_pretty_size;
		str += "\n";
		str += "     MinTokens: " + min_tokens;
		str += "\n";
		str += "     MaxTokens: " + max_tokens;
		str += "\n";
		str += "     MinJudges: " + min_judges;
		str += "\n";
		str += " MinConfidence: " + min_confidence;
		str += "\n";
		if(similarity_type == ToolEvaluator.SIMILARITY_TYPE_AVG) {
		str += "SimilarityType: SIMILARITY_TYPE_AVG";
		} else if (similarity_type == ToolEvaluator.SIMILARITY_TYPE_BOTH) {
		str += "SimilarityType: SIMILARITY_TYPE_BOTH";
		} else if (similarity_type == ToolEvaluator.SIMILARITY_TYPE_LINE) {
		str += "SimilarityType: SIMILARITY_TYPE_LINE";
		} else if (similarity_type == ToolEvaluator.SIMILARITY_TYPE_TOKEN) {
		str += "SimilarityType: SIMILARITY_TYPE_TOKEN";
		}
		str += "\n";
		return str;
	}
	
	private static final long serialVersionUID = 1L;
	
	private Long tool_id;
	private CloneMatcher matcher;
	private Integer min_size;
	private Integer max_size;
	private Integer min_pretty_size;
	private Integer max_pretty_size;
	private Integer min_tokens;
	private Integer max_tokens;
	private Integer min_judges;
	private Integer min_confidence;
	private int similarity_type;
	private boolean include_internal = false;
	
	private Set<Long> functionality_ids;
	
	HashMap<Long,Integer> numClones_type1_inter;
	HashMap<Long,Integer> numDetected_type1_inter;
	
	HashMap<Long,Integer> numClones_type1_intra;
	HashMap<Long,Integer> numDetected_type1_intra;
	
	HashMap<Long,Integer> numClones_type2c_inter;
	HashMap<Long,Integer> numDetected_type2c_inter;
	
	HashMap<Long,Integer> numClones_type2c_intra;
	HashMap<Long,Integer> numDetected_type2c_intra;
	
	HashMap<Long,Integer> numClones_type2b_inter;
	HashMap<Long,Integer> numDetected_type2b_inter;
	
	HashMap<Long,Integer> numClones_type2b_intra;
	HashMap<Long,Integer> numDetected_type2b_intra;
	
	HashMap<Long,Integer>[] numClones_type3_inter;
	HashMap<Long,Integer>[] numDetected_type3_inter; // index, similarity ranges: [0-5), [5,10), [10,15), ..., [95-100]... on indexies 0 through 18
	
	HashMap<Long,Integer>[] numClones_type3_intra;
	HashMap<Long,Integer>[] numDetected_type3_intra;
	
	HashMap<Long, Integer> numClones_false_inter;
	HashMap<Long, Integer> numDetected_false_inter;
	
	HashMap<Long,Integer> numClones_false_intra;
	HashMap<Long,Integer> numDetected_false_intra;
	
	public static final int SIMILARITY_TYPE_TOKEN = 0;
	public static final int SIMILARITY_TYPE_LINE = 1;
	public static final int SIMILARITY_TYPE_BOTH = 2;
	public static final int SIMILARITY_TYPE_AVG = 3;

// All Clones
	
	public int getNumClones_inter(long functionality_id) throws SQLException {
		int numClones = 0;
		numClones += getNumClones_type1_inter(functionality_id);
		numClones += getNumClones_type2c_inter(functionality_id);
		numClones += getNumClones_type2b_inter(functionality_id);
		numClones += getNumClones_type3_inter(functionality_id);
		return numClones;
	}
	
	public int getNumClones_intra(long functionality_id) throws SQLException {
		int numClones = 0;
		numClones += getNumClones_type1_intra(functionality_id);
		numClones += getNumClones_type2c_intra(functionality_id);
		numClones += getNumClones_type2b_intra(functionality_id);
		numClones += getNumClones_type3_intra(functionality_id);
		return numClones;
	}
	
	public int getNumClones(long functionality_id) throws SQLException {
		int numClones = 0;
		numClones += getNumClones_type1(functionality_id);
		numClones += getNumClones_type2c(functionality_id);
		numClones += getNumClones_type2b(functionality_id);
		numClones += getNumClones_type3(functionality_id);
		return numClones;
	}
		
	
	public int getNumDetected_inter(long functionality_id) throws SQLException {
		int numDetected = 0;
		numDetected += this.getNumDetected_type1_inter(functionality_id);
		numDetected += this.getNumDetected_type2_inter(functionality_id);
		numDetected += this.getNumDetected_type3_inter(functionality_id);
		return numDetected;
	}
	
	public int getNumDetected_intra(long functionality_id) throws SQLException {
		int numDetected = 0;
		numDetected += this.getNumDetected_type1_intra(functionality_id);
		numDetected += this.getNumDetected_type2_intra(functionality_id);
		numDetected += this.getNumDetected_type3_intra(functionality_id);
		return numDetected;
	}
	
	public int getNumDetected(long functionality_id) throws SQLException {
		int numDetected = 0;
		numDetected += this.getNumDetected_type1(functionality_id);
		numDetected += this.getNumDetected_type2(functionality_id);
		numDetected += this.getNumDetected_type3(functionality_id);
		return numDetected;
	}
	
	
	public double getRecall_inter(long functionality_id) throws SQLException {
		int numClones = this.getNumClones_inter(functionality_id);
		int numDetected = this.getNumDetected_inter(functionality_id);
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_intra(long functionality_id) throws SQLException {
		int numClones = this.getNumClones_intra(functionality_id);
		int numDetected = this.getNumDetected_intra(functionality_id);
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall(long functionality_id) throws SQLException {
		int numClones = this.getNumClones(functionality_id);
		int numDetected = this.getNumDetected(functionality_id);
		return 1.0*numDetected/numClones;
	}
	
	public int getNumClones_inter() throws SQLException {
		int numClones = 0;
		for(long id : functionality_ids) {
			numClones += this.getNumClones_inter(id);
		}
		return numClones;
	}
	
	public int getNumClones_intra() throws SQLException {
		int numClones = 0;
		for(long id : functionality_ids) {
			numClones += this.getNumClones_intra(id);
		}
		return numClones;
	}
	
	public int getNumClones() throws SQLException {
		int numClones = 0;
		for(long id : functionality_ids) {
			numClones += this.getNumClones(id);
		}
		return numClones;
	}
	
	
	public int getNumDetected_inter() throws SQLException {
		int numDetected = 0;
		for(long id : functionality_ids) {
			numDetected += this.getNumDetected_inter(id);
		}
		return numDetected;
	}
	
	public int getNumDetected_intra() throws SQLException {
		int numDetected = 0;
		for(long id : functionality_ids) {
			numDetected += this.getNumDetected_intra(id);
		}
		return numDetected;
	}
	
	public int getNumDetected() throws SQLException {
		int numDetected = 0;
		for(long id : functionality_ids) {
			numDetected += this.getNumDetected(id);
		}
		return numDetected;
	}
	
	
	public double getRecall_inter() throws SQLException {
		int numClones = getNumClones_inter();
		int numDetected = getNumDetected_inter();
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_intra() throws SQLException {
		int numClones = getNumClones_intra();
		int numDetected = getNumDetected_intra();
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall() throws SQLException {
		int numClones = getNumClones();
		int numDetected = getNumDetected();
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_avg() throws SQLException {
		double avg = 0.0;
		int num = 0;
		for(long id : functionality_ids) {
			double recall = getRecall(id);
			if(!Double.isNaN(recall)) {
				avg += recall;
				num++;
			}
		}
		return avg/num;
	}
	public double getRecall_avg_inter() throws SQLException {
		double avg = 0.0;
		int num = 0;
		for(long id : functionality_ids) {
			double recall = getRecall_inter(id);
			if(!Double.isNaN(recall)) {
				avg += recall;
				num++;
			}
		}
		return avg/num;
	}
	public double getRecall_avg_intra() throws SQLException {
		double avg = 0.0;
		int num = 0;
		for(long id : functionality_ids) {
			double recall = getRecall_intra(id);
			if(!Double.isNaN(recall)) {
				avg += recall;
				num++;
			}
		}
		return avg/num;
	}
	
// False
	public int getNumClones_false_inter(long functionality_id) throws SQLException {
		if(!numClones_false_inter.containsKey(functionality_id)) {
			int numClones = EvaluateTools.numFalsePositives(functionality_id, this.min_judges, this.min_confidence, EvaluateTools.INTER_PROJECT_CLONES);
			numClones_false_inter.put(functionality_id, numClones);
		}
		return numClones_false_inter.get(functionality_id);
	}
	public int getNumClones_false_intra(long functionality_id) throws SQLException {
		if(!numClones_false_intra.containsKey(functionality_id)) {
			int numClones = EvaluateTools.numFalsePositives(functionality_id, this.min_judges, this.min_confidence, EvaluateTools.INTRA_PROJECT_CLONES);
			numClones_false_intra.put(functionality_id, numClones);
		}
		return numClones_false_intra.get(functionality_id);
	}
	public int getNumClones_false(long functionality_id) throws SQLException {
		return getNumClones_false_inter(functionality_id) + getNumClones_false_intra(functionality_id);
	}
		
	
	public int getNumDetected_false_inter(long functionality_id) throws SQLException {
		if(!numDetected_false_inter.containsKey(functionality_id)) {
			int numDetected = EvaluateTools.numFalseDetected(tool_id, functionality_id, this.min_judges, this.min_confidence, EvaluateTools.INTER_PROJECT_CLONES, this.matcher);
			numDetected_false_inter.put(functionality_id, numDetected);
		}
		return numDetected_false_inter.get(functionality_id);
	}
	public int getNumDetected_false_intra(long functionality_id) throws SQLException {
		if(!numDetected_false_intra.containsKey(functionality_id)) {
			int numDetected = EvaluateTools.numFalseDetected(tool_id, functionality_id, this.min_judges, this.min_confidence, EvaluateTools.INTRA_PROJECT_CLONES, this.matcher);
			numDetected_false_intra.put(functionality_id, numDetected);
		}
		return numDetected_false_intra.get(functionality_id);
	}
	public int getNumDetected_false(long functionality_id) throws SQLException {
		return getNumDetected_false_inter(functionality_id) + getNumDetected_false_intra(functionality_id);
	}
	
	
	public int getNumClones_false_inter() throws SQLException {
		int numFalse = 0;
		for(long id : functionality_ids) {
			numFalse += getNumClones_false_inter(id);
		}
		return numFalse;
	}
	public int getNumClones_false_intra() throws SQLException {
		int numFalse = 0;
		for(long id : functionality_ids) {
			numFalse += getNumClones_false_intra(id);
		}
		return numFalse;
	}
	public int getNumClones_false() throws SQLException {
		return getNumClones_false_inter() + getNumClones_false_intra();
	}
	
	
	public int getNumDetected_false_inter() throws SQLException {
		int numDetected = 0;
		for(long id : functionality_ids) {
			numDetected += getNumDetected_false_inter(id);
		}
		return numDetected;
	}
	public int getNumDetected_false_intra() throws SQLException {
		int numDetected = 0;
		for(long id : functionality_ids) {
			numDetected += getNumDetected_false_intra(id);
		}
		return numDetected;
	}
	public int getNumDetected_false() throws SQLException {
		return getNumDetected_false_inter() + getNumDetected_false_intra();
	}
	
	
	public double getRecall_false_inter(long functionality_id) throws SQLException {
		int numDetected_false = this.getNumDetected_false_inter(functionality_id);
		int numClones_false = this.getNumClones_false_inter(functionality_id);
		return 1.0*numDetected_false/numClones_false;
	}
	public double getRecall_false_intra(long functionality_id) throws SQLException {
		int numDetected_false = this.getNumDetected_false_intra(functionality_id);
		int numClones_false = this.getNumClones_false_intra(functionality_id);
		return 1.0*numDetected_false/numClones_false;
	}
	public double getRecall_false(long functionality_id) throws SQLException {
		int numDetected_false = this.getNumDetected_false(functionality_id);
		int numClones_false = this.getNumClones_false(functionality_id);
		return 1.0*numDetected_false/numClones_false;
	}
	
	
	public double getRecall_false_inter() throws SQLException {
		int numDetected_false = this.getNumDetected_false_inter();
		int numClones_false = this.getNumClones_false_inter();
		return 1.0*numDetected_false/numClones_false;
	}
	public double getRecall_false_intra() throws SQLException {
		int numDetected_false = this.getNumDetected_false_intra();
		int numClones_false = this.getNumClones_false_intra();
		return 1.0*numDetected_false/numClones_false;
	}
	public double getRecall_false() throws SQLException {
		int numDetected_false = this.getNumDetected_false();
		int numClones_false = this.getNumClones_false();
		return 1.0*numDetected_false/numClones_false;
	}
	
	/**
	 * Calculated as the ratio of the known clones/false detected that are true not false positives.
	 * @param functionality_id
	 * @return
	 * @throws SQLException
	 */
	public double getPrecision_inter(long functionality_id) throws SQLException {
		int numDetected_true = this.getNumDetected_inter(functionality_id);
		int numDetected_false = this.getNumDetected_false_inter(functionality_id);
		return 1.0*numDetected_true/(numDetected_true+numDetected_false);
	}
	public double getPrecision_intra(long functionality_id) throws SQLException {
		int numDetected_true = this.getNumDetected_intra(functionality_id);
		int numDetected_false = this.getNumDetected_false_intra(functionality_id);
		return 1.0*numDetected_true/(numDetected_true+numDetected_false);
	}
	public double getPrecision(long functionality_id) throws SQLException {
		int numDetected_true = this.getNumDetected(functionality_id);
		int numDetected_false = this.getNumDetected_false(functionality_id);
		return 1.0*numDetected_true/(numDetected_true+numDetected_false);
	}

	/**
	 * Calculated as the ratio of the known clones/false detected that are true not false positives.
	 * @return
	 * @throws SQLException
	 */
	public double getPrecision_inter() throws SQLException {
		int numDetected_true = this.getNumDetected_inter();
		int numDetected_false = this.getNumDetected_false_inter();
		return 1.0*numDetected_true/(numDetected_true+numDetected_false);
	}
	public double getPrecision_avg_inter() throws SQLException {
		double avg = 0.0;
		int num = 0;
		for(long id : functionality_ids) {
			double recall = getPrecision_inter(id);
			if(!Double.isNaN(recall)) {
				avg += recall;
				num++;
			}
		}
		return avg/num;
	}
	public double getPrecision_intra() throws SQLException {
		int numDetected_true = this.getNumDetected_intra();
		int numDetected_false = this.getNumDetected_false_intra();
		return 1.0*numDetected_true/(numDetected_true+numDetected_false);
	}
	public double getPrecision_avg_intra() throws SQLException {
		double avg = 0.0;
		int num = 0;
		for(long id : functionality_ids) {
			double recall = getPrecision_intra(id);
			if(!Double.isNaN(recall)) {
				avg += recall;
				num++;
			}
		}
		return avg/num;
	}
	public double getPrecision() throws SQLException {
		int numDetected_true = this.getNumDetected();
		int numDetected_false = this.getNumDetected_false();
		return 1.0*numDetected_true/(numDetected_true+numDetected_false);
	}
	
	public double getPrecision_avg() throws SQLException {
		double avg = 0.0;
		int num = 0;
		for(long id : functionality_ids) {
			double recall = getPrecision(id);
			if(!Double.isNaN(recall)) {
				avg += recall;
				num++;
			}
		}
		return avg/num;
	}
	
// Type-1
	public int getNumClones_type1_inter(long functionality_id) throws SQLException {
		if(numClones_type1_inter.get(functionality_id) == null) {
			int numClones = 
					EvaluateTools.numClones(	    /*functionality_id*/	functionality_id, 
												                /*type*/	1,
												 /*Project Granularity*/	EvaluateTools.INTER_PROJECT_CLONES,
												  /*line_gt_similarity*/	null,
												 /*token_gt_similarity*/	null, 
												   /*avg_gt_similarity*/	null,
												  /*both_gt_similarity*/	null,
												 /*line_gte_similarity*/	null, 
												/*token_gte_similarity*/	null, 
												  /*avg_gte_similarity*/	null,
												 /*both_gte_similarity*/	null,
												  /*line_lt_similarity*/	null, 
											 	 /*token_lt_similarity*/	null, 
												   /*avg_lt_similarity*/	null,
												  /*both_lt_similarity*/	null,
												 /*line_lte_similarity*/	null, 
												/*token_lte_similarity*/	null, 
												  /*avg_lte_similarity*/	null,
												 /*both_lte_similarity*/	null,
												            /*min_size*/	min_size, 
												            /*max_size*/	max_size, 
												     /*min_pretty_size*/	min_pretty_size, 
												     /*max_pretty_size*/	max_pretty_size, 
												          /*min_tokens*/	min_tokens, 
												          /*max_tokens*/	max_tokens, 
												          /*min_judges*/	min_judges, 
												      /*min_confidence*/	min_confidence,
												      						include_internal
									);
			numClones_type1_inter.put(functionality_id, numClones);
		}
		return numClones_type1_inter.get(functionality_id);
	}
	
	public int getNumClones_type1_intra(long functionality_id) throws SQLException {
		if(numClones_type1_intra.get(functionality_id) == null) {
			int numClones = 
					EvaluateTools.numClones(	    /*functionality_id*/	functionality_id, 
												                /*type*/	1,
												 /*Project Granularity*/	EvaluateTools.INTRA_PROJECT_CLONES,
												  /*line_gt_similarity*/	null,
												 /*token_gt_similarity*/	null, 
												   /*avg_gt_similarity*/	null,
												  /*both_gt_similarity*/	null,
												 /*line_gte_similarity*/	null, 
												/*token_gte_similarity*/	null, 
												  /*avg_gte_similarity*/	null,
												 /*both_gte_similarity*/	null,
												  /*line_lt_similarity*/	null, 
											 	 /*token_lt_similarity*/	null, 
												   /*avg_lt_similarity*/	null,
												  /*both_lt_similarity*/	null,
												 /*line_lte_similarity*/	null, 
												/*token_lte_similarity*/	null, 
												  /*avg_lte_similarity*/	null,
												 /*both_lte_similarity*/	null,
												            /*min_size*/	min_size, 
												            /*max_size*/	max_size, 
												     /*min_pretty_size*/	min_pretty_size, 
												     /*max_pretty_size*/	max_pretty_size, 
												          /*min_tokens*/	min_tokens, 
												          /*max_tokens*/	max_tokens, 
												          /*min_judges*/	min_judges, 
												      /*min_confidence*/	min_confidence,
							      											include_internal
									);
			numClones_type1_intra.put(functionality_id, numClones);
		}
		return numClones_type1_intra.get(functionality_id);
	}
	
	public int getNumClones_type1(long functionality_id) throws SQLException {
		return getNumClones_type1_inter(functionality_id) + getNumClones_type1_intra(functionality_id);
	}
	
	public int getNumClones_type1_inter() throws SQLException {
		//System.out.println("\tgetNumClones_type1_inter");
		int numClones = 0;
		
		for(Long fid : functionality_ids) {
			//System.out.println("\t\t"+fid);
			numClones += getNumClones_type1_inter(fid);
		}
		return numClones;
	}
	
	public int getNumClones_type1_intra() throws SQLException {
		//System.out.println("\tgetNumClones_type1_intra");
		int numClones = 0;
		for(Long fid : functionality_ids) {
			//System.out.println("\t\t" + fid);
			numClones += getNumClones_type1_intra(fid);
		}
		return numClones;
	}
	
	public int getNumClones_type1() throws SQLException {
		//System.out.println("getNumClones_type1");
		return getNumClones_type1_inter() + getNumClones_type1_intra();
	}
	
	public int getNumDetected_type1_inter(long functionality_id) throws SQLException {
		if(numDetected_type1_inter.get(functionality_id) == null) {
			int numDetected =
					EvaluateTools.numTrueDetected(    	         /*tool_id*/	this.tool_id, 
													             /*matcher*/	this.matcher, 
													    /*functionality_id*/	functionality_id, 
													                /*type*/	1,
													 /*Project Granularity*/	EvaluateTools.INTER_PROJECT_CLONES,
													  /*line_gt_similarity*/	null,
													 /*token_gt_similarity*/	null, 
													   /*avg_gt_similarity*/	null,
													  /*both_gt_similarity*/	null,
													 /*line_gte_similarity*/	null, 
													/*token_gte_similarity*/	null, 
													  /*avg_gte_similarity*/	null,
													 /*both_gte_similarity*/	null,
													  /*line_lt_similarity*/	null, 
													 /*token_lt_similarity*/	null, 
													   /*avg_lt_similarity*/	null,
													  /*both_lt_similarity*/	null,
													 /*line_lte_similarity*/	null, 
													/*token_lte_similarity*/	null, 
													  /*avg_lte_similarity*/	null,
													 /*both_lte_similarity*/	null,
													            /*min_size*/	this.min_size, 
													            /*max_size*/	this.max_size, 
													     /*min_pretty_size*/	this.min_pretty_size, 
													     /*max_pretty_size*/	this.max_pretty_size, 
													          /*min_tokens*/	this.min_tokens, 
													          /*max_tokens*/	this.max_tokens, 
													          /*min_judges*/	this.min_judges, 
													      /*min_confidence*/	this.min_confidence,
			      											include_internal
			);
			numDetected_type1_inter.put(functionality_id, numDetected);
		}
		return numDetected_type1_inter.get(functionality_id);
	}
	
	public int getNumDetected_type1_intra(long functionality_id) throws SQLException {
		if(numDetected_type1_intra.get(functionality_id) == null) {
			int numDetected =
					EvaluateTools.numTrueDetected(    	         /*tool_id*/	this.tool_id, 
													             /*matcher*/	this.matcher, 
													    /*functionality_id*/	functionality_id, 
													                /*type*/	1,
													 /*Project Granularity*/	EvaluateTools.INTRA_PROJECT_CLONES,
													  /*line_gt_similarity*/	null,
													 /*token_gt_similarity*/	null, 
													   /*avg_gt_similarity*/	null,
													  /*both_gt_similarity*/	null,
													 /*line_gte_similarity*/	null, 
													/*token_gte_similarity*/	null, 
													  /*avg_gte_similarity*/	null,
													 /*both_gte_similarity*/	null,
													  /*line_lt_similarity*/	null, 
													 /*token_lt_similarity*/	null, 
													   /*avg_lt_similarity*/	null,
													  /*both_lt_similarity*/	null,
													 /*line_lte_similarity*/	null, 
													/*token_lte_similarity*/	null, 
													  /*avg_lte_similarity*/	null,
													 /*both_lte_similarity*/	null,
													            /*min_size*/	this.min_size, 
													            /*max_size*/	this.max_size, 
													     /*min_pretty_size*/	this.min_pretty_size, 
													     /*max_pretty_size*/	this.max_pretty_size, 
													          /*min_tokens*/	this.min_tokens, 
													          /*max_tokens*/	this.max_tokens, 
													          /*min_judges*/	this.min_judges, 
													      /*min_confidence*/	this.min_confidence,
			      											include_internal
			);
			numDetected_type1_intra.put(functionality_id, numDetected);
		}
		return numDetected_type1_intra.get(functionality_id);
	}
	
	public int getNumDetected_type1(long functionality_id) throws SQLException {
		return getNumDetected_type1_inter(functionality_id) + getNumDetected_type1_intra(functionality_id);
	}
	
	public int getNumDetected_type1_inter() throws SQLException {
		//System.out.println("\tgetNumDetected_type1_inter");
		int numDetected = 0;
		for(Long fid : functionality_ids) {
			//System.out.println("\t\t" + fid);
			numDetected += getNumDetected_type1_inter(fid);
		}
		return numDetected;
	}
	
	public int getNumDetected_type1_intra() throws SQLException {
		//System.out.println("\tgetNumDetected_type1_intra");
		int numDetected = 0;
		for(Long fid : functionality_ids) {
			//System.out.println("\t\t"+fid);
			numDetected += getNumDetected_type1_intra(fid);
		}
		return numDetected;
	}
	
	public int getNumDetected_type1() throws SQLException {
		//System.out.println("getNumDetected_type1");
		return getNumDetected_type1_inter() + getNumDetected_type1_intra();
	}
	
	public double getRecall_type1_inter(long functionality_id) throws SQLException {
		int numDetected = getNumDetected_type1_inter(functionality_id);
		int numClones = getNumClones_type1_inter(functionality_id);
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type1_intra(long functionality_id) throws SQLException {
		int numDetected = getNumDetected_type1_intra(functionality_id);
		int numClones = getNumClones_type1_intra(functionality_id);
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type1(long functionality_id) throws SQLException {
		int numDetected = getNumDetected_type1(functionality_id);
		int numClones = getNumClones_type1(functionality_id);
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type1_inter() throws SQLException {
		int numDetected = getNumDetected_type1_inter();
		int numClones = getNumClones_type1_inter();
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type1_intra() throws SQLException {
		int numDetected = getNumDetected_type1_intra();
		int numClones = getNumClones_type1_intra();
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type1() throws SQLException {
		//System.out.println("getRecall_type1");
		int numDetected = getNumDetected_type1();
		int numClones = getNumClones_type1();
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type1_avg() throws SQLException {
		double avg = 0.0;
		int num = 0;
		for(long id : functionality_ids) {
			double recall = getRecall_type1(id);
			if(!Double.isNaN(recall)) {
				avg += recall;
				num++;
			}
		}
		return avg/num;
	}
	public double getRecall_type1_avg_inter() throws SQLException {
		double avg = 0.0;
		int num = 0;
		for(long id : functionality_ids) {
			double recall = getRecall_type1_inter(id);
			if(!Double.isNaN(recall)) {
				avg += recall;
				num++;
			}
		}
		return avg/num;
	}
	
	public double getRecall_type1_avg_intra() throws SQLException {
		double avg = 0.0;
		int num = 0;
		for(long id : functionality_ids) {
			double recall = getRecall_type1_intra(id);
			if(!Double.isNaN(recall)) {
				avg += recall;
				num++;
			}
		}
		return avg/num;
	}
	
// Type-2
	
	public int getNumClones_type2c_inter(long functionality_id) throws SQLException {
		if(numClones_type2c_inter.get(functionality_id) == null) {
			int numClones = 
					EvaluateTools.numClones(	    /*functionality_id*/	functionality_id, 
												                /*type*/	2,
											     /*Project Granularity*/	EvaluateTools.INTER_PROJECT_CLONES,
												  /*line_gt_similarity*/	null,
												 /*token_gt_similarity*/	null, 
												   /*avg_gt_similarity*/	null,
												  /*both_gt_similarity*/	null,
												 /*line_gte_similarity*/	null, 
												/*token_gte_similarity*/	null, 
												  /*avg_gte_similarity*/	null,
												 /*both_gte_similarity*/	null,
												  /*line_lt_similarity*/	null, 
												 /*token_lt_similarity*/	null, 
												   /*avg_lt_similarity*/	null,
												  /*both_lt_similarity*/	null,
												 /*line_lte_similarity*/	null, 
												/*token_lte_similarity*/	null, 
												  /*avg_lte_similarity*/	null,
												 /*both_lte_similarity*/	null,
												            /*min_size*/	min_size, 
												            /*max_size*/	max_size, 
												     /*min_pretty_size*/	min_pretty_size, 
												     /*max_pretty_size*/	max_pretty_size, 
												          /*min_tokens*/	min_tokens, 
												          /*max_tokens*/	max_tokens, 
												          /*min_judges*/	min_judges, 
												      /*min_confidence*/	min_confidence,
		      											include_internal
											);		
			numClones_type2c_inter.put(functionality_id, numClones);
		}
		return numClones_type2c_inter.get(functionality_id);
	}
	
	public int getNumClones_type2c_intra(long functionality_id) throws SQLException {
		if(numClones_type2c_intra.get(functionality_id) == null) {
			int numClones = 
					EvaluateTools.numClones(	    /*functionality_id*/	functionality_id, 
												                /*type*/	2,
											     /*Project Granularity*/	EvaluateTools.INTRA_PROJECT_CLONES,
												  /*line_gt_similarity*/	null,
												 /*token_gt_similarity*/	null, 
												   /*avg_gt_similarity*/	null,
												  /*both_gt_similarity*/	null,
												 /*line_gte_similarity*/	null, 
												/*token_gte_similarity*/	null, 
												  /*avg_gte_similarity*/	null,
												 /*both_gte_similarity*/	null,
												  /*line_lt_similarity*/	null, 
												 /*token_lt_similarity*/	null, 
												   /*avg_lt_similarity*/	null,
												  /*both_lt_similarity*/	null,
												 /*line_lte_similarity*/	null, 
												/*token_lte_similarity*/	null, 
												  /*avg_lte_similarity*/	null,
												 /*both_lte_similarity*/	null,
												            /*min_size*/	min_size, 
												            /*max_size*/	max_size, 
												     /*min_pretty_size*/	min_pretty_size, 
												     /*max_pretty_size*/	max_pretty_size, 
												          /*min_tokens*/	min_tokens, 
												          /*max_tokens*/	max_tokens, 
												          /*min_judges*/	min_judges, 
												      /*min_confidence*/	min_confidence,
		      											include_internal
											);		
			numClones_type2c_intra.put(functionality_id, numClones);
		}
		return numClones_type2c_intra.get(functionality_id);
	}
	
	public int getNumClones_type2c(long functionality_id) throws SQLException {
		return getNumClones_type2c_inter(functionality_id) + getNumClones_type2c_intra(functionality_id);
	}
	
	public int getNumClones_type2c_inter() throws SQLException {
		int numClones = 0;
		for(Long fid : functionality_ids) {
			numClones += getNumClones_type2c_inter(fid);
		}
		return numClones;
	}
	
	public int getNumClones_type2c_intra() throws SQLException {
		int numClones = 0;
		for(Long fid : functionality_ids) {
			numClones += getNumClones_type2c_intra(fid);
		}
		return numClones;
	}
	
	public int getNumClones_type2c() throws SQLException {
		return getNumClones_type2c_inter() + getNumClones_type2c_intra();
	}
	
	public int getNumDetected_type2c_inter(long functionality_id) throws SQLException {
		if(numDetected_type2c_inter.get(functionality_id) == null) {
			int numDetected =
					EvaluateTools.numTrueDetected(    	         /*tool_id*/	this.tool_id, 
													             /*matcher*/	this.matcher, 
													    /*functionality_id*/	functionality_id, 
													                /*type*/	2,
													 /*Project Granularity*/	EvaluateTools.INTER_PROJECT_CLONES,
													  /*line_gt_similarity*/	null,
													 /*token_gt_similarity*/	null, 
													   /*avg_gt_similarity*/	null,
													  /*both_gt_similarity*/	null,
													 /*line_gte_similarity*/	null, 
													/*token_gte_similarity*/	null, 
													  /*avg_gte_similarity*/	null,
													 /*both_gte_similarity*/	null,
													  /*line_lt_similarity*/	null, 
													 /*token_lt_similarity*/	null, 
													   /*avg_lt_similarity*/	null,
													  /*both_lt_similarity*/	null,
													 /*line_lte_similarity*/	null, 
													/*token_lte_similarity*/	null, 
													  /*avg_lte_similarity*/	null,
													 /*both_lte_similarity*/	null,
													            /*min_size*/	this.min_size, 
													            /*max_size*/	this.max_size, 
													     /*min_pretty_size*/	this.min_pretty_size, 
													     /*max_pretty_size*/	this.max_pretty_size, 
													          /*min_tokens*/	this.min_tokens, 
													          /*max_tokens*/	this.max_tokens, 
													          /*min_judges*/	this.min_judges, 
													      /*min_confidence*/	this.min_confidence,
			      											include_internal);
			numDetected_type2c_inter.put(functionality_id, numDetected);
		}
		return numDetected_type2c_inter.get(functionality_id);
	}
	
	public int getNumDetected_type2c_intra(long functionality_id) throws SQLException {
		if(numDetected_type2c_intra.get(functionality_id) == null) {
			int numDetected =
					EvaluateTools.numTrueDetected(    	         /*tool_id*/	this.tool_id, 
													             /*matcher*/	this.matcher, 
													    /*functionality_id*/	functionality_id, 
													                /*type*/	2,
													 /*Project Granularity*/	EvaluateTools.INTRA_PROJECT_CLONES,
													  /*line_gt_similarity*/	null,
													 /*token_gt_similarity*/	null, 
													   /*avg_gt_similarity*/	null,
													  /*both_gt_similarity*/	null,
													 /*line_gte_similarity*/	null, 
													/*token_gte_similarity*/	null, 
													  /*avg_gte_similarity*/	null,
													 /*both_gte_similarity*/	null,
													  /*line_lt_similarity*/	null, 
													 /*token_lt_similarity*/	null, 
													   /*avg_lt_similarity*/	null,
													  /*both_lt_similarity*/	null,
													 /*line_lte_similarity*/	null, 
													/*token_lte_similarity*/	null, 
													  /*avg_lte_similarity*/	null,
													 /*both_lte_similarity*/	null,
													            /*min_size*/	this.min_size, 
													            /*max_size*/	this.max_size, 
													     /*min_pretty_size*/	this.min_pretty_size, 
													     /*max_pretty_size*/	this.max_pretty_size, 
													          /*min_tokens*/	this.min_tokens, 
													          /*max_tokens*/	this.max_tokens, 
													          /*min_judges*/	this.min_judges, 
													      /*min_confidence*/	this.min_confidence,
			      											include_internal);
			numDetected_type2c_intra.put(functionality_id, numDetected);
		}
		return numDetected_type2c_intra.get(functionality_id);
	}
	
	public int getNumDetected_type2c(long functionality_id) throws SQLException {
		return getNumDetected_type2c_inter(functionality_id) + getNumDetected_type2c_intra(functionality_id); 
	}
	
	public int getNumDetected_type2c_inter() throws SQLException {
		int numDetected = 0;
		for(Long fid : functionality_ids)
			numDetected += getNumDetected_type2c_inter(fid);
		return numDetected;
	}
	
	public int getNumDetected_type2c_intra() throws SQLException {
		int numDetected = 0;
		for(Long fid : functionality_ids)
			numDetected += getNumDetected_type2c_intra(fid);
		return numDetected;
	}
	
	public int getNumDetected_type2c() throws SQLException {
		return getNumDetected_type2c_inter() + getNumDetected_type2c_intra();
	}
	
	public double getRecall_type2c_inter(long functionality_id) throws SQLException {
		int numDetected = getNumDetected_type2c_inter(functionality_id);
		int numClones = getNumClones_type2c_inter(functionality_id);
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type2c_intra(long functionality_id) throws SQLException {
		int numDetected = getNumDetected_type2c_intra(functionality_id);
		int numClones = getNumClones_type2c_intra(functionality_id);
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type2c(long functionality_id) throws SQLException {
		int numDetected = getNumDetected_type2c(functionality_id);
		int numClones = getNumClones_type2c(functionality_id);
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type2c_inter() throws SQLException {
		int numDetected = getNumDetected_type2c_inter();
		int numClones = getNumClones_type2c_inter();
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type2c_intra() throws SQLException {
		int numDetected = getNumDetected_type2c_intra();
		int numClones = getNumClones_type2c_intra();
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type2c() throws SQLException {
		int numDetected = getNumDetected_type2c();
		int numClones = getNumClones_type2c();
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type2c_avg() throws SQLException {
		double avg = 0.0;
		int num = 0;
		for(long id : functionality_ids) {
			double recall = getRecall_type2c(id);
			if(!Double.isNaN(recall)) {
				avg += recall;
				num++;
			}
		}
		return avg/num;
	}
	public double getRecall_type2c_avg_inter() throws SQLException {
		double avg = 0.0;
		int num = 0;
		for(long id : functionality_ids) {
			double recall = getRecall_type2c_inter(id);
			if(!Double.isNaN(recall)) {
				avg += recall;
				num++;
			}
		}
		return avg/num;
	}
	public double getRecall_type2c_avg_intra() throws SQLException {
		double avg = 0.0;
		int num = 0;
		for(long id : functionality_ids) {
			double recall = getRecall_type2c_intra(id);
			if(!Double.isNaN(recall)) {
				avg += recall;
				num++;
			}
		}
		return avg/num;
	}
	
// Type-2 Blind
	
	public int getNumClones_type2b_inter(long functionality_id) throws SQLException {
		if(numClones_type2b_inter.get(functionality_id) == null) {
			int numClones = 
					EvaluateTools.numClones(	    /*functionality_id*/	functionality_id, 
												                /*type*/	3,
												 /*Project Granularity*/	EvaluateTools.INTER_PROJECT_CLONES,
												  /*line_gt_similarity*/	null,
												 /*token_gt_similarity*/	null, 
												   /*avg_gt_similarity*/	null,
												  /*both_gt_similarity*/	null,
												 /*line_gte_similarity*/	null, 
												/*token_gte_similarity*/	null, 
												  /*avg_gte_similarity*/	null,
												 /*both_gte_similarity*/	1.0,
												  /*line_lt_similarity*/	null, 
												 /*token_lt_similarity*/	null, 
												   /*avg_lt_similarity*/	null,
												  /*both_lt_similarity*/	null,
												 /*line_lte_similarity*/	null, 
												/*token_lte_similarity*/	null, 
												  /*avg_lte_similarity*/	null,
												 /*both_lte_similarity*/	null,
												            /*min_size*/	min_size, 
												            /*max_size*/	max_size, 
												     /*min_pretty_size*/	min_pretty_size, 
												     /*max_pretty_size*/	max_pretty_size, 
												          /*min_tokens*/	min_tokens, 
												          /*max_tokens*/	max_tokens, 
												          /*min_judges*/	min_judges, 
												      /*min_confidence*/	min_confidence,
		      											include_internal
											);		
			numClones_type2b_inter.put(functionality_id, numClones);
		}
		return numClones_type2b_inter.get(functionality_id);
	}
	
	public int getNumClones_type2b_intra(long functionality_id) throws SQLException {
		if(numClones_type2b_intra.get(functionality_id) == null) {
			int numClones = 
					EvaluateTools.numClones(	    /*functionality_id*/	functionality_id, 
												                /*type*/	3,
												 /*Project Granularity*/	EvaluateTools.INTRA_PROJECT_CLONES,
												  /*line_gt_similarity*/	null,
												 /*token_gt_similarity*/	null, 
												   /*avg_gt_similarity*/	null,
												  /*both_gt_similarity*/	null,
												 /*line_gte_similarity*/	null, 
												/*token_gte_similarity*/	null, 
												  /*avg_gte_similarity*/	null,
												 /*both_gte_similarity*/	1.0,
												  /*line_lt_similarity*/	null, 
												 /*token_lt_similarity*/	null, 
												   /*avg_lt_similarity*/	null,
												  /*both_lt_similarity*/	null,
												 /*line_lte_similarity*/	null, 
												/*token_lte_similarity*/	null, 
												  /*avg_lte_similarity*/	null,
												 /*both_lte_similarity*/	null,
												            /*min_size*/	min_size, 
												            /*max_size*/	max_size, 
												     /*min_pretty_size*/	min_pretty_size, 
												     /*max_pretty_size*/	max_pretty_size, 
												          /*min_tokens*/	min_tokens, 
												          /*max_tokens*/	max_tokens, 
												          /*min_judges*/	min_judges, 
												      /*min_confidence*/	min_confidence,
		      											include_internal
											);		
			numClones_type2b_intra.put(functionality_id, numClones);
		}
		return numClones_type2b_intra.get(functionality_id);
	}
	
	public int getNumClones_type2b(long functionality_id) throws SQLException {
		return getNumClones_type2b_inter(functionality_id) + getNumClones_type2b_intra(functionality_id);
	}
	
	public int getNumClones_type2b_inter() throws SQLException {
		int numClones = 0;
		for(Long fid : functionality_ids) {
			numClones += getNumClones_type2b_inter(fid);
		}
		return numClones;
	}
	
	public int getNumClones_type2b_intra() throws SQLException {
		int numClones = 0;
		for(Long fid : functionality_ids) {
			numClones += getNumClones_type2b_intra(fid);
		}
		return numClones;
	}
	
	public int getNumClones_type2b() throws SQLException {
		return getNumClones_type2b_inter() + getNumClones_type2b_intra();
	}
	
	public int getNumDetected_type2b_inter(long functionality_id) throws SQLException {
		if(numDetected_type2b_inter.get(functionality_id) == null) {
			int numDetected =
					EvaluateTools.numTrueDetected(    	         /*tool_id*/	this.tool_id, 
													             /*matcher*/	this.matcher, 
													    /*functionality_id*/	functionality_id, 
													                /*type*/	3,
												     /*Project Granularity*/	EvaluateTools.INTER_PROJECT_CLONES,
													  /*line_gt_similarity*/	null,
													 /*token_gt_similarity*/	null, 
													   /*avg_gt_similarity*/	null,
													  /*both_gt_similarity*/	null,
													 /*line_gte_similarity*/	null, 
													/*token_gte_similarity*/	null, 
													  /*avg_gte_similarity*/	null,
													 /*both_gte_similarity*/	1.0,
													  /*line_lt_similarity*/	null, 
													 /*token_lt_similarity*/	null, 
													   /*avg_lt_similarity*/	null,
													  /*both_lt_similarity*/	null,
													 /*line_lte_similarity*/	null, 
													/*token_lte_similarity*/	null, 
													  /*avg_lte_similarity*/	null,
													 /*both_lte_similarity*/	null, 
													            /*min_size*/	this.min_size, 
													            /*max_size*/	this.max_size, 
													     /*min_pretty_size*/	this.min_pretty_size, 
													     /*max_pretty_size*/	this.max_pretty_size, 
													          /*min_tokens*/	this.min_tokens, 
													          /*max_tokens*/	this.max_tokens, 
													          /*min_judges*/	this.min_judges, 
													      /*min_confidence*/	this.min_confidence,
			      											include_internal);
			numDetected_type2b_inter.put(functionality_id, numDetected);
		}
		return numDetected_type2b_inter.get(functionality_id);
	}
	
	public int getNumDetected_type2b_intra(long functionality_id) throws SQLException {
		if(numDetected_type2b_intra.get(functionality_id) == null) {
			int numDetected =
					EvaluateTools.numTrueDetected(    	         /*tool_id*/	this.tool_id, 
													             /*matcher*/	this.matcher, 
													    /*functionality_id*/	functionality_id, 
													                /*type*/	3,
												     /*Project Granularity*/	EvaluateTools.INTRA_PROJECT_CLONES,
													  /*line_gt_similarity*/	null,
													 /*token_gt_similarity*/	null, 
													   /*avg_gt_similarity*/	null,
													  /*both_gt_similarity*/	null,
													 /*line_gte_similarity*/	null, 
													/*token_gte_similarity*/	null, 
													  /*avg_gte_similarity*/	null,
													 /*both_gte_similarity*/	1.0,
													  /*line_lt_similarity*/	null, 
													 /*token_lt_similarity*/	null, 
													   /*avg_lt_similarity*/	null,
													  /*both_lt_similarity*/	null,
													 /*line_lte_similarity*/	null, 
													/*token_lte_similarity*/	null, 
													  /*avg_lte_similarity*/	null,
													 /*both_lte_similarity*/	null,
													            /*min_size*/	this.min_size, 
													            /*max_size*/	this.max_size, 
													     /*min_pretty_size*/	this.min_pretty_size, 
													     /*max_pretty_size*/	this.max_pretty_size, 
													          /*min_tokens*/	this.min_tokens, 
													          /*max_tokens*/	this.max_tokens, 
													          /*min_judges*/	this.min_judges, 
													      /*min_confidence*/	this.min_confidence,
			      											include_internal);
			numDetected_type2b_intra.put(functionality_id, numDetected);
		}
		return numDetected_type2b_intra.get(functionality_id);
	}
	
	public int getNumDetected_type2b(long functionality_id) throws SQLException {
		return getNumDetected_type2b_inter(functionality_id) + getNumDetected_type2b_intra(functionality_id);
	}
	
	public int getNumDetected_type2b_inter() throws SQLException {
		int numDetected = 0;
		for(Long fid : functionality_ids)
			numDetected += getNumDetected_type2b_inter(fid);
		return numDetected;
	}
	
	public int getNumDetected_type2b_intra() throws SQLException {
		int numDetected = 0;
		for(Long fid : functionality_ids)
			numDetected += getNumDetected_type2b_intra(fid);
		return numDetected;
	}
	
	public int getNumDetected_type2b() throws SQLException {
		return getNumDetected_type2b_inter() + getNumDetected_type2b_intra();
	}
	
	public double getRecall_type2b_inter(long functionality_id) throws SQLException {
		int numDetected = getNumDetected_type2b_inter(functionality_id);
		int numClones = getNumClones_type2b_inter(functionality_id);
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type2b_intra(long functionality_id) throws SQLException {
		int numDetected = getNumDetected_type2b_intra(functionality_id);
		int numClones = getNumClones_type2b_intra(functionality_id);
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type2b(long functionality_id) throws SQLException {
		int numDetected = getNumDetected_type2b(functionality_id);
		int numClones = getNumClones_type2b(functionality_id);
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type2b_inter() throws SQLException {
		int numDetected = getNumDetected_type2b_inter();
		int numClones = getNumClones_type2b_inter();
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type2b_intra() throws SQLException {
		int numDetected = getNumDetected_type2b_intra();
		int numClones = getNumClones_type2b_intra();
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type2b() throws SQLException {
		int numDetected = getNumDetected_type2b();
		int numClones = getNumClones_type2b();
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type2b_avg() throws SQLException {
		double avg = 0.0;
		int num = 0;
		for(long id : functionality_ids) {
			double recall = getRecall_type2b(id);
			if(!Double.isNaN(recall)) {
				avg += recall;
				num++;
			}
		}
		return avg/num;
	}
	public double getRecall_type2b_avg_inter() throws SQLException {
		double avg = 0.0;
		int num = 0;
		for(long id : functionality_ids) {
			double recall = getRecall_type2b_inter(id);
			if(!Double.isNaN(recall)) {
				avg += recall;
				num++;
			}
		}
		return avg/num;
	}
	public double getRecall_type2b_avg_intra() throws SQLException {
		double avg = 0.0;
		int num = 0;
		for(long id : functionality_ids) {
			double recall = getRecall_type2b_intra(id);
			if(!Double.isNaN(recall)) {
				avg += recall;
				num++;
			}
		}
		return avg/num;
	}
	
// Type-2
	
	public int getNumClones_type2_inter(long functionality_id) throws SQLException {
		int numClones = 0;
		numClones += getNumClones_type2b_inter(functionality_id);
		numClones += getNumClones_type2c_inter(functionality_id);
		return numClones;
	}
	
	public int getNumClones_type2_intra(long functionality_id) throws SQLException {
		int numClones = 0;
		numClones += getNumClones_type2b_intra(functionality_id);
		numClones += getNumClones_type2c_intra(functionality_id);
		return numClones;
	}
	
	public int getNumClones_type2(long functionality_id) throws SQLException {
		return getNumClones_type2_inter(functionality_id) + getNumClones_type2_intra(functionality_id);
	}
	
	
	public int getNumClones_type2_inter() throws SQLException {
		int numClones = 0;
		for(Long fid : functionality_ids) {
			numClones += getNumClones_type2_inter(fid);
		}
		return numClones;
	}
	
	public int getNumClones_type2_intra() throws SQLException {
		int numClones = 0;
		for(Long fid : functionality_ids) {
			numClones += getNumClones_type2_intra(fid);
		}
		return numClones;
	}
	
	public int getNumClones_type2() throws SQLException {
		return getNumClones_type2_inter() + getNumClones_type2_intra();
	}
	
	
	public int getNumDetected_type2_inter(long functionality_id) throws SQLException {
		int numDetected = 0;
		numDetected += getNumDetected_type2c_inter(functionality_id);
		numDetected += getNumDetected_type2b_inter(functionality_id);
		return numDetected;
	}
	
	public int getNumDetected_type2_intra(long functionality_id) throws SQLException {
		int numDetected = 0;
		numDetected += getNumDetected_type2c_intra(functionality_id);
		numDetected += getNumDetected_type2b_intra(functionality_id);
		return numDetected;
	}
	
	public int getNumDetected_type2(long functionality_id) throws SQLException {
		return getNumDetected_type2_inter(functionality_id) + getNumDetected_type2_intra(functionality_id);
	}
	
	
	public int getNumDetected_type2_inter() throws SQLException {
		int numDetected = 0;
		for(Long fid : functionality_ids)
			numDetected += getNumDetected_type2_inter(fid);
		return numDetected;
	}
	
	public int getNumDetected_type2_intra() throws SQLException {
		int numDetected = 0;
		for(Long fid : functionality_ids)
			numDetected += getNumDetected_type2_intra(fid);
		return numDetected;
	}
	
	public int getNumDetected_type2() throws SQLException {
		return getNumDetected_type2_inter() + getNumDetected_type2_intra();
	}
	
	
	public double getRecall_type2_inter(long functionality_id) throws SQLException {
		int numDetected = getNumDetected_type2_inter(functionality_id);
		int numClones = getNumClones_type2_inter(functionality_id);
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type2_intra(long functionality_id) throws SQLException {
		int numDetected = getNumDetected_type2_intra(functionality_id);
		int numClones = getNumClones_type2_intra(functionality_id);
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type2(long functionality_id) throws SQLException {
		int numDetected = getNumDetected_type2(functionality_id);
		int numClones = getNumClones_type2(functionality_id);
		return 1.0*numDetected/numClones;
	}
	
	
	public double getRecall_type2_inter() throws SQLException {
		int numDetected = getNumDetected_type2_inter();
		int numClones = getNumClones_type2_inter();
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type2_intra() throws SQLException {
		int numDetected = getNumDetected_type2_intra();
		int numClones = getNumClones_type2_intra();
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type2() throws SQLException {
		int numDetected = getNumDetected_type2();
		int numClones = getNumClones_type2();
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type2_avg() throws SQLException {
		double avg = 0.0;
		int num = 0;
		for(long id : functionality_ids) {
			double recall = getRecall_type2b(id);
			if(!Double.isNaN(recall)) {
				avg += recall;
				num++;
			}
		}
		return avg/num;
	}
	public double getRecall_type2_avg_inter() throws SQLException {
		double avg = 0.0;
		int num = 0;
		for(long id : functionality_ids) {
			double recall = getRecall_type2b_inter(id);
			if(!Double.isNaN(recall)) {
				avg += recall;
				num++;
			}
		}
		return avg/num;
	}
	public double getRecall_type2_avg_intra() throws SQLException {
		double avg = 0.0;
		int num = 0;
		for(long id : functionality_ids) {
			double recall = getRecall_type2_intra(id);
			if(!Double.isNaN(recall)) {
				avg += recall;
				num++;
			}
		}
		return avg/num;
	}
	
// Type-3
		
	public int getNumClones_type3_inter(long functionality_id, int similarity) throws SQLException {
		if(similarity % 5 != 0 || similarity >= 100 || similarity < 0)
			throw new IllegalArgumentException("Similarity must be a multiple of 5 in range [0-100).");
		int index = similarity/5;
		double start = similarity/100.0;
		double end = (similarity+5)/100.0;
		if(numClones_type3_inter[index].get(functionality_id) == null) {
			int numClones;
			if(this.similarity_type == ToolEvaluator.SIMILARITY_TYPE_LINE) {
				numClones = 
				EvaluateTools.numClones(	    /*functionality_id*/	functionality_id, 
											                /*type*/	3,
										     /*Project Granularity*/	EvaluateTools.INTER_PROJECT_CLONES,
											  /*line_gt_similarity*/	null,
											 /*token_gt_similarity*/	null, 
											   /*avg_gt_similarity*/	null,
											  /*both_gt_similarity*/	null,
											 /*line_gte_similarity*/	start, 
											/*token_gte_similarity*/	null, 
											  /*avg_gte_similarity*/	null,
											 /*both_gte_similarity*/	null,
											  /*line_lt_similarity*/	end, 
											 /*token_lt_similarity*/	null, 
											   /*avg_lt_similarity*/	null,
											  /*both_lt_similarity*/	null,
											 /*line_lte_similarity*/	null, 
											/*token_lte_similarity*/	null, 
											  /*avg_lte_similarity*/	null,
											 /*both_lte_similarity*/	null,
											            /*min_size*/	min_size, 
											            /*max_size*/	max_size, 
											     /*min_pretty_size*/	min_pretty_size, 
											     /*max_pretty_size*/	max_pretty_size, 
											          /*min_tokens*/	min_tokens, 
											          /*max_tokens*/	max_tokens, 
											          /*min_judges*/	min_judges, 
											      /*min_confidence*/	min_confidence,
	      											include_internal
										);
			} else if(this.similarity_type == ToolEvaluator.SIMILARITY_TYPE_TOKEN) {
				numClones = 
				EvaluateTools.numClones(	    /*functionality_id*/	functionality_id, 
											                /*type*/	3,
										     /*Project Granularity*/	EvaluateTools.INTER_PROJECT_CLONES,
											  /*line_gt_similarity*/	null,
											 /*token_gt_similarity*/	null, 
											   /*avg_gt_similarity*/	null,
											  /*both_gt_similarity*/	null,
											 /*line_gte_similarity*/	null, 
											/*token_gte_similarity*/	start, 
											  /*avg_gte_similarity*/	null,
											 /*both_gte_similarity*/	null,
											  /*line_lt_similarity*/	null, 
											 /*token_lt_similarity*/	end, 
											   /*avg_lt_similarity*/	null,
											  /*both_lt_similarity*/	null,
											 /*line_lte_similarity*/	null, 
											/*token_lte_similarity*/	null, 
											  /*avg_lte_similarity*/	null,
											 /*both_lte_similarity*/	null,
											            /*min_size*/	min_size, 
											            /*max_size*/	max_size, 
											     /*min_pretty_size*/	min_pretty_size, 
											     /*max_pretty_size*/	max_pretty_size, 
											          /*min_tokens*/	min_tokens, 
											          /*max_tokens*/	max_tokens, 
											          /*min_judges*/	min_judges, 
											      /*min_confidence*/	min_confidence,
	      											include_internal
										);
			} else if(this.similarity_type == ToolEvaluator.SIMILARITY_TYPE_BOTH) {
				numClones = 
				EvaluateTools.numClones(	    /*functionality_id*/	functionality_id, 
											                /*type*/	3,
										     /*Project Granularity*/	EvaluateTools.INTER_PROJECT_CLONES,
											  /*line_gt_similarity*/	null,
											 /*token_gt_similarity*/	null, 
											   /*avg_gt_similarity*/	null,
											  /*both_gt_similarity*/	null,
											 /*line_gte_similarity*/	null, 
											/*token_gte_similarity*/	null, 
											  /*avg_gte_similarity*/	null,
											 /*both_gte_similarity*/	start,
											  /*line_lt_similarity*/	null, 
											 /*token_lt_similarity*/	null, 
											   /*avg_lt_similarity*/	null,
											  /*both_lt_similarity*/	end,
											 /*line_lte_similarity*/	null, 
											/*token_lte_similarity*/	null, 
											  /*avg_lte_similarity*/	null,
											 /*both_lte_similarity*/	null,
											            /*min_size*/	min_size, 
											            /*max_size*/	max_size, 
											     /*min_pretty_size*/	min_pretty_size, 
											     /*max_pretty_size*/	max_pretty_size, 
											          /*min_tokens*/	min_tokens, 
											          /*max_tokens*/	max_tokens, 
											          /*min_judges*/	min_judges, 
											      /*min_confidence*/	min_confidence,
	      											include_internal
										);
			} else {//if(this.similarity_type == ToolEvaluator.SIMILARITY_TYPE_AVG) {
				numClones = 
				EvaluateTools.numClones(	    /*functionality_id*/	functionality_id, 
											                /*type*/	3,
											 /*Project Granularity*/	EvaluateTools.INTER_PROJECT_CLONES,
											  /*line_gt_similarity*/	null,
											 /*token_gt_similarity*/	null, 
											   /*avg_gt_similarity*/	null,
											  /*both_gt_similarity*/	null,
											 /*line_gte_similarity*/	null, 
											/*token_gte_similarity*/	null, 
											  /*avg_gte_similarity*/	start,
											 /*both_gte_similarity*/	null,
											  /*line_lt_similarity*/	null, 
											 /*token_lt_similarity*/	null, 
											   /*avg_lt_similarity*/	end,
											  /*both_lt_similarity*/	null,
											 /*line_lte_similarity*/	null, 
											/*token_lte_similarity*/	null, 
											  /*avg_lte_similarity*/	null,
											 /*both_lte_similarity*/	null,
											            /*min_size*/	min_size, 
											            /*max_size*/	max_size, 
											     /*min_pretty_size*/	min_pretty_size, 
											     /*max_pretty_size*/	max_pretty_size, 
											          /*min_tokens*/	min_tokens, 
											          /*max_tokens*/	max_tokens, 
											          /*min_judges*/	min_judges, 
											      /*min_confidence*/	min_confidence,
	      											include_internal
										);
			}
			numClones_type3_inter[index].put(functionality_id, numClones);
		}
		return numClones_type3_inter[index].get(functionality_id);
	}
	
	public int getNumClones_type3_intra(long functionality_id, int similarity) throws SQLException {
		if(similarity % 5 != 0 || similarity >= 100 || similarity < 0)
			throw new IllegalArgumentException("Similarity must be a multiple of 5 in range [0-100).");
		int index = similarity/5;
		double start = similarity/100.0;
		double end = (similarity+5)/100.0;
		if(numClones_type3_intra[index].get(functionality_id) == null) {
			int numClones;
			if(this.similarity_type == ToolEvaluator.SIMILARITY_TYPE_LINE) {
				numClones = 
				EvaluateTools.numClones(	    /*functionality_id*/	functionality_id, 
											                /*type*/	3,
										     /*Project Granularity*/	EvaluateTools.INTRA_PROJECT_CLONES,
											  /*line_gt_similarity*/	null,
											 /*token_gt_similarity*/	null, 
											   /*avg_gt_similarity*/	null,
											  /*both_gt_similarity*/	null,
											 /*line_gte_similarity*/	start, 
											/*token_gte_similarity*/	null, 
											  /*avg_gte_similarity*/	null,
											 /*both_gte_similarity*/	null,
											  /*line_lt_similarity*/	end, 
											 /*token_lt_similarity*/	null, 
											   /*avg_lt_similarity*/	null,
											  /*both_lt_similarity*/	null,
											 /*line_lte_similarity*/	null, 
											/*token_lte_similarity*/	null, 
											  /*avg_lte_similarity*/	null,
											 /*both_lte_similarity*/	null,
											            /*min_size*/	min_size, 
											            /*max_size*/	max_size, 
											     /*min_pretty_size*/	min_pretty_size, 
											     /*max_pretty_size*/	max_pretty_size, 
											          /*min_tokens*/	min_tokens, 
											          /*max_tokens*/	max_tokens, 
											          /*min_judges*/	min_judges, 
											      /*min_confidence*/	min_confidence,
	      											include_internal
										);
			} else if(this.similarity_type == ToolEvaluator.SIMILARITY_TYPE_TOKEN) {
				numClones = 
				EvaluateTools.numClones(	    /*functionality_id*/	functionality_id, 
											                /*type*/	3,
										     /*Project Granularity*/	EvaluateTools.INTRA_PROJECT_CLONES,
											  /*line_gt_similarity*/	null,
											 /*token_gt_similarity*/	null, 
											   /*avg_gt_similarity*/	null,
											  /*both_gt_similarity*/	null,
											 /*line_gte_similarity*/	null, 
											/*token_gte_similarity*/	start, 
											  /*avg_gte_similarity*/	null,
											 /*both_gte_similarity*/	null,
											  /*line_lt_similarity*/	null, 
											 /*token_lt_similarity*/	end, 
											   /*avg_lt_similarity*/	null,
											  /*both_lt_similarity*/	null,
											 /*line_lte_similarity*/	null, 
											/*token_lte_similarity*/	null, 
											  /*avg_lte_similarity*/	null,
											 /*both_lte_similarity*/	null,
											            /*min_size*/	min_size, 
											            /*max_size*/	max_size, 
											     /*min_pretty_size*/	min_pretty_size, 
											     /*max_pretty_size*/	max_pretty_size, 
											          /*min_tokens*/	min_tokens, 
											          /*max_tokens*/	max_tokens, 
											          /*min_judges*/	min_judges, 
											      /*min_confidence*/	min_confidence,
	      											include_internal
										);
			} else if(this.similarity_type == ToolEvaluator.SIMILARITY_TYPE_BOTH) {
				numClones = 
				EvaluateTools.numClones(	    /*functionality_id*/	functionality_id, 
											                /*type*/	3,
										     /*Project Granularity*/	EvaluateTools.INTRA_PROJECT_CLONES,
											  /*line_gt_similarity*/	null,
											 /*token_gt_similarity*/	null, 
											   /*avg_gt_similarity*/	null,
											  /*both_gt_similarity*/	null,
											 /*line_gte_similarity*/	null, 
											/*token_gte_similarity*/	null, 
											  /*avg_gte_similarity*/	null,
											 /*both_gte_similarity*/	start,
											  /*line_lt_similarity*/	null, 
											 /*token_lt_similarity*/	null, 
											   /*avg_lt_similarity*/	null,
											  /*both_lt_similarity*/	end,
											 /*line_lte_similarity*/	null, 
											/*token_lte_similarity*/	null, 
											  /*avg_lte_similarity*/	null,
											 /*both_lte_similarity*/	null,
											            /*min_size*/	min_size, 
											            /*max_size*/	max_size, 
											     /*min_pretty_size*/	min_pretty_size, 
											     /*max_pretty_size*/	max_pretty_size, 
											          /*min_tokens*/	min_tokens, 
											          /*max_tokens*/	max_tokens, 
											          /*min_judges*/	min_judges, 
											      /*min_confidence*/	min_confidence,
	      											include_internal
										);
			} else {//if(this.similarity_type == ToolEvaluator.SIMILARITY_TYPE_AVG) {
				numClones = 
				EvaluateTools.numClones(	    /*functionality_id*/	functionality_id, 
											                /*type*/	3,
											 /*Project Granularity*/	EvaluateTools.INTRA_PROJECT_CLONES,
											  /*line_gt_similarity*/	null,
											 /*token_gt_similarity*/	null, 
											   /*avg_gt_similarity*/	null,
											  /*both_gt_similarity*/	null,
											 /*line_gte_similarity*/	null, 
											/*token_gte_similarity*/	null, 
											  /*avg_gte_similarity*/	null,
											 /*both_gte_similarity*/	start,
											  /*line_lt_similarity*/	null, 
											 /*token_lt_similarity*/	null, 
											   /*avg_lt_similarity*/	null,
											  /*both_lt_similarity*/	end,
											 /*line_lte_similarity*/	null, 
											/*token_lte_similarity*/	null, 
											  /*avg_lte_similarity*/	null,
											 /*both_lte_similarity*/	null,
											            /*min_size*/	min_size, 
											            /*max_size*/	max_size, 
											     /*min_pretty_size*/	min_pretty_size, 
											     /*max_pretty_size*/	max_pretty_size, 
											          /*min_tokens*/	min_tokens, 
											          /*max_tokens*/	max_tokens, 
											          /*min_judges*/	min_judges, 
											      /*min_confidence*/	min_confidence,
	      											include_internal
										);
			}
			numClones_type3_intra[index].put(functionality_id, numClones);
		}
		return numClones_type3_intra[index].get(functionality_id);
	}
	
	public int getNumClones_type3(long functionality_id, int similarity) throws SQLException {
		return getNumClones_type3_inter(functionality_id, similarity) + getNumClones_type3_intra(functionality_id, similarity); 
	}
	
			
	public int getNumClones_type3_inter(long functionality_id, int similarity_start, int similarity_end) throws SQLException {
		if(similarity_start < 0) throw new IllegalArgumentException("similarity_start must be >= 0.");
		if(similarity_end > 100) throw new IllegalArgumentException("similarity_end must be <= 100");
		if(similarity_start > similarity_end) throw new IllegalArgumentException("similarity start must be < similarity_end");
		if(similarity_start % 5 != 0) throw new IllegalArgumentException("similarity_start must be a multiple of 5");
		if(similarity_end % 5 != 0) throw new IllegalArgumentException("similarity_end must be a multiple of 5");
		
		int numClones = 0;
		for(int i = similarity_start; i < similarity_end; i += 5) {
			numClones += getNumClones_type3_inter(functionality_id, i);
		}
		return numClones;
	}
	
	public int getNumClones_type3_intra(long functionality_id, int similarity_start, int similarity_end) throws SQLException {
		if(similarity_start < 0) throw new IllegalArgumentException("similarity_start must be >= 0.");
		if(similarity_end > 100) throw new IllegalArgumentException("similarity_end must be <= 100");
		if(similarity_start > similarity_end) throw new IllegalArgumentException("similarity start must be < similarity_end");
		if(similarity_start % 5 != 0) throw new IllegalArgumentException("similarity_start must be a multiple of 5");
		if(similarity_end % 5 != 0) throw new IllegalArgumentException("similarity_end must be a multiple of 5");
		
		int numClones = 0;
		for(int i = similarity_start; i < similarity_end; i += 5) {
			numClones += getNumClones_type3_intra(functionality_id, i);
		}
		return numClones;
	}
	
	public int getNumClones_type3(long functionality_id, int similarity_start, int similarity_end) throws SQLException {
		return getNumClones_type3_intra(functionality_id, similarity_start, similarity_end) + getNumClones_type3_inter(functionality_id, similarity_start, similarity_end); 
	}
	
	
	public int getNumClones_type3_inter(int similarity_start, int similarity_end) throws SQLException {
		if(similarity_start < 0) throw new IllegalArgumentException("similarity_start must be >= 0.");
		if(similarity_end > 100) throw new IllegalArgumentException("similarity_end must be <= 100");
		if(similarity_start > similarity_end) throw new IllegalArgumentException("similarity start must be < similarity_end");
		if(similarity_start % 5 != 0) throw new IllegalArgumentException("similarity_start must be a multiple of 5");
		if(similarity_end % 5 != 0) throw new IllegalArgumentException("similarity_end must be a multiple of 5");
		
		int numClones = 0;
		for(long fid : functionality_ids) {
			numClones += getNumClones_type3_inter(fid, similarity_start, similarity_end);
		}
		return numClones;
	}
	
	public int getNumClones_type3_intra(int similarity_start, int similarity_end) throws SQLException {
		if(similarity_start < 0) throw new IllegalArgumentException("similarity_start must be >= 0.");
		if(similarity_end > 100) throw new IllegalArgumentException("similarity_end must be <= 100");
		if(similarity_start > similarity_end) throw new IllegalArgumentException("similarity start must be < similarity_end");
		if(similarity_start % 5 != 0) throw new IllegalArgumentException("similarity_start must be a multiple of 5");
		if(similarity_end % 5 != 0) throw new IllegalArgumentException("similarity_end must be a multiple of 5");
		
		int numClones = 0;
		for(long fid : functionality_ids) {
			numClones += getNumClones_type3_intra(fid, similarity_start, similarity_end);
		}
		return numClones;
	}
	
	public int getNumClones_type3(int similarity_start, int similarity_end) throws SQLException {
		return getNumClones_type3_inter(similarity_start, similarity_end) + getNumClones_type3_intra(similarity_start, similarity_end);
	}
	
	
	public int getNumClones_type3_inter(long functionality_id) throws SQLException {
		int numClones = 0;
		for(int i = 0; i < 100; i += 5) {
			numClones += getNumClones_type3_inter(functionality_id, i, i+5);
		}
		return numClones;
	}
	
	public int getNumClones_type3_intra(long functionality_id) throws SQLException {
		int numClones = 0;
		for(int i = 0; i < 100; i += 5) {
			numClones += getNumClones_type3_intra(functionality_id, i, i+5);
		}
		return numClones;
	}
	
	public int getNumClones_type3(long functionality_id) throws SQLException {
		return getNumClones_type3_inter(functionality_id) + getNumClones_type3_intra(functionality_id);
	}
	
		
	public int getNumClones_type3_inter() throws SQLException {
		int numClones = 0;
		for(int i = 0; i < 100; i = i + 5) {
			numClones += getNumClones_type3_inter(i, i+5);
		}
		return numClones;
	}
	
	public int getNumClones_type3_intra() throws SQLException {
		int numClones = 0;
		for(int i = 0; i < 100; i = i + 5) {
			numClones += getNumClones_type3_intra(i, i+5);
		}
		return numClones;
	}
	
	public int getNumClones_type3() throws SQLException {
		return getNumClones_type3_intra() + getNumClones_type3_inter();
	}
	
	
	public int getNumDetected_type3_inter(long functionality_id, int similarity) throws SQLException {
		if(similarity % 5 != 0 || similarity >= 100 || similarity < 0)
			throw new IllegalArgumentException("Similarity must be a multiple of 5 in range [0-100).");
		int index = similarity/5;
		double start = similarity/100.0;
		double end = (similarity+5)/100.0;
		
		if(numDetected_type3_inter[index].get(functionality_id) == null) {
			int numDetected;
			if(this.similarity_type == ToolEvaluator.SIMILARITY_TYPE_LINE) {
				numDetected =
				EvaluateTools.numTrueDetected(    	         /*tool_id*/	this.tool_id, 
												             /*matcher*/	this.matcher, 
												    /*functionality_id*/	functionality_id, 
												                /*type*/	3,
												 /*Project Granularity*/	EvaluateTools.INTER_PROJECT_CLONES,
												  /*line_gt_similarity*/	null,
												 /*token_gt_similarity*/	null, 
												   /*avg_gt_similarity*/	null,
												  /*both_gt_similarity*/	null,
												 /*line_gte_similarity*/	start, 
												/*token_gte_similarity*/	null, 
												  /*avg_gte_similarity*/	null,
												 /*both_gte_similarity*/	null,
												  /*line_lt_similarity*/	end, 
												 /*token_lt_similarity*/	null, 
												   /*avg_lt_similarity*/	null,
												  /*both_lt_similarity*/	null,
												 /*line_lte_similarity*/	null, 
												/*token_lte_similarity*/	null, 
												  /*avg_lte_similarity*/	null,
												 /*both_lte_similarity*/	null,
												            /*min_size*/	this.min_size, 
												            /*max_size*/	this.max_size, 
												     /*min_pretty_size*/	this.min_pretty_size, 
												     /*max_pretty_size*/	this.max_pretty_size, 
												          /*min_tokens*/	this.min_tokens, 
												          /*max_tokens*/	this.max_tokens, 
												          /*min_judges*/	this.min_judges, 
												      /*min_confidence*/	this.min_confidence,
		      											include_internal
											);
			} else if(this.similarity_type == ToolEvaluator.SIMILARITY_TYPE_TOKEN) {
				numDetected =
				EvaluateTools.numTrueDetected(    	         /*tool_id*/	this.tool_id, 
												             /*matcher*/	this.matcher, 
												    /*functionality_id*/	functionality_id, 
												                /*type*/	3,
												 /*Project Granularity*/	EvaluateTools.INTER_PROJECT_CLONES,
												  /*line_gt_similarity*/	null,
												 /*token_gt_similarity*/	null, 
												   /*avg_gt_similarity*/	null,
												  /*both_gt_similarity*/	null,
												 /*line_gte_similarity*/	null, 
												/*token_gte_similarity*/	start, 
												  /*avg_gte_similarity*/	null,
												 /*both_gte_similarity*/	null,
												  /*line_lt_similarity*/	null, 
												 /*token_lt_similarity*/	end, 
												   /*avg_lt_similarity*/	null,
												  /*both_lt_similarity*/	null,
												 /*line_lte_similarity*/	null, 
												/*token_lte_similarity*/	null, 
												  /*avg_lte_similarity*/	null,
												 /*both_lte_similarity*/	null,
												            /*min_size*/	this.min_size, 
												            /*max_size*/	this.max_size, 
												     /*min_pretty_size*/	this.min_pretty_size, 
												     /*max_pretty_size*/	this.max_pretty_size, 
												          /*min_tokens*/	this.min_tokens, 
												          /*max_tokens*/	this.max_tokens, 
												          /*min_judges*/	this.min_judges, 
												      /*min_confidence*/	this.min_confidence,
		      											include_internal
											);
			} else if(this.similarity_type == ToolEvaluator.SIMILARITY_TYPE_BOTH) {
				numDetected =
				EvaluateTools.numTrueDetected(    	         /*tool_id*/	this.tool_id, 
												             /*matcher*/	this.matcher, 
												    /*functionality_id*/	functionality_id, 
												                /*type*/	3,
												 /*Project Granularity*/	EvaluateTools.INTER_PROJECT_CLONES,
												  /*line_gt_similarity*/	null,
												 /*token_gt_similarity*/	null, 
												   /*avg_gt_similarity*/	null,
												  /*both_gt_similarity*/	null,
												 /*line_gte_similarity*/	null, 
												/*token_gte_similarity*/	null, 
												  /*avg_gte_similarity*/	null,
												 /*both_gte_similarity*/	start,
												  /*line_lt_similarity*/	null, 
												 /*token_lt_similarity*/	null, 
												   /*avg_lt_similarity*/	null,
												  /*both_lt_similarity*/	end,
												 /*line_lte_similarity*/	null, 
												/*token_lte_similarity*/	null, 
												  /*avg_lte_similarity*/	null,
												 /*both_lte_similarity*/	null,
												            /*min_size*/	this.min_size, 
												            /*max_size*/	this.max_size, 
												     /*min_pretty_size*/	this.min_pretty_size, 
												     /*max_pretty_size*/	this.max_pretty_size, 
												          /*min_tokens*/	this.min_tokens, 
												          /*max_tokens*/	this.max_tokens, 
												          /*min_judges*/	this.min_judges, 
												      /*min_confidence*/	this.min_confidence,
		      											include_internal
											);
			} else {//if(this.similarity_type == ToolEvaluator.SIMILARITY_TYPE_AVG) {
				numDetected =
				EvaluateTools.numTrueDetected(    	         /*tool_id*/	this.tool_id, 
												             /*matcher*/	this.matcher, 
												    /*functionality_id*/	functionality_id, 
												                /*type*/	3,
												 /*Project Granularity*/	EvaluateTools.INTER_PROJECT_CLONES,
												  /*line_gt_similarity*/	null,
												 /*token_gt_similarity*/	null, 
												   /*avg_gt_similarity*/	null,
												  /*both_gt_similarity*/	null,
												 /*line_gte_similarity*/	null, 
												/*token_gte_similarity*/	null, 
												  /*avg_gte_similarity*/	start,
												 /*both_gte_similarity*/	null,
												  /*line_lt_similarity*/	null, 
												 /*token_lt_similarity*/	null, 
												   /*avg_lt_similarity*/	end,
												  /*both_lt_similarity*/	null,
												 /*line_lte_similarity*/	null, 
												/*token_lte_similarity*/	null, 
												  /*avg_lte_similarity*/	null,
												 /*both_lte_similarity*/	null,
												            /*min_size*/	this.min_size, 
												            /*max_size*/	this.max_size, 
												     /*min_pretty_size*/	this.min_pretty_size, 
												     /*max_pretty_size*/	this.max_pretty_size, 
												          /*min_tokens*/	this.min_tokens, 
												          /*max_tokens*/	this.max_tokens, 
												          /*min_judges*/	this.min_judges, 
												      /*min_confidence*/	this.min_confidence,
		      											include_internal
											);
			}
			numDetected_type3_inter[index].put(functionality_id, numDetected);
		}
		return numDetected_type3_inter[index].get(functionality_id);
	}
	
	public int getNumDetected_type3_intra(long functionality_id, int similarity) throws SQLException {
		if(similarity % 5 != 0 || similarity >= 100 || similarity < 0)
			throw new IllegalArgumentException("Similarity must be a multiple of 5 in range [0-100).");
		int index = similarity/5;
		double start = similarity/100.0;
		double end = (similarity+5)/100.0;
		
		if(numDetected_type3_intra[index].get(functionality_id) == null) {
			int numDetected;
			if(this.similarity_type == ToolEvaluator.SIMILARITY_TYPE_LINE) {
				numDetected =
				EvaluateTools.numTrueDetected(    	         /*tool_id*/	this.tool_id, 
												             /*matcher*/	this.matcher, 
												    /*functionality_id*/	functionality_id, 
												                /*type*/	3,
												 /*Project Granularity*/	EvaluateTools.INTRA_PROJECT_CLONES,
												  /*line_gt_similarity*/	null,
												 /*token_gt_similarity*/	null, 
												   /*avg_gt_similarity*/	null,
												  /*both_gt_similarity*/	null,
												 /*line_gte_similarity*/	start, 
												/*token_gte_similarity*/	null, 
												  /*avg_gte_similarity*/	null,
												 /*both_gte_similarity*/	null,
												  /*line_lt_similarity*/	end, 
												 /*token_lt_similarity*/	null, 
												   /*avg_lt_similarity*/	null,
												  /*both_lt_similarity*/	null,
												 /*line_lte_similarity*/	null, 
												/*token_lte_similarity*/	null, 
												  /*avg_lte_similarity*/	null,
												 /*both_lte_similarity*/	null,
												            /*min_size*/	this.min_size, 
												            /*max_size*/	this.max_size, 
												     /*min_pretty_size*/	this.min_pretty_size, 
												     /*max_pretty_size*/	this.max_pretty_size, 
												          /*min_tokens*/	this.min_tokens, 
												          /*max_tokens*/	this.max_tokens, 
												          /*min_judges*/	this.min_judges, 
												      /*min_confidence*/	this.min_confidence,
		      											include_internal
											);
			} else if(this.similarity_type == ToolEvaluator.SIMILARITY_TYPE_TOKEN) {
				numDetected =
				EvaluateTools.numTrueDetected(    	         /*tool_id*/	this.tool_id, 
												             /*matcher*/	this.matcher, 
												    /*functionality_id*/	functionality_id, 
												                /*type*/	3,
												 /*Project Granularity*/	EvaluateTools.INTRA_PROJECT_CLONES,
												  /*line_gt_similarity*/	null,
												 /*token_gt_similarity*/	null, 
												   /*avg_gt_similarity*/	null,
												  /*both_gt_similarity*/	null,
												 /*line_gte_similarity*/	null, 
												/*token_gte_similarity*/	start, 
												  /*avg_gte_similarity*/	null,
												 /*both_gte_similarity*/	null,
												  /*line_lt_similarity*/	null, 
												 /*token_lt_similarity*/	end, 
												   /*avg_lt_similarity*/	null,
												  /*both_lt_similarity*/	null,
												 /*line_lte_similarity*/	null, 
												/*token_lte_similarity*/	null, 
												  /*avg_lte_similarity*/	null,
												 /*both_lte_similarity*/	null, 
												            /*min_size*/	this.min_size, 
												            /*max_size*/	this.max_size, 
												     /*min_pretty_size*/	this.min_pretty_size, 
												     /*max_pretty_size*/	this.max_pretty_size, 
												          /*min_tokens*/	this.min_tokens, 
												          /*max_tokens*/	this.max_tokens, 
												          /*min_judges*/	this.min_judges, 
												      /*min_confidence*/	this.min_confidence,
		      											include_internal
											);
			} else if(this.similarity_type == ToolEvaluator.SIMILARITY_TYPE_BOTH) {
				numDetected =
				EvaluateTools.numTrueDetected(    	         /*tool_id*/	this.tool_id, 
												             /*matcher*/	this.matcher, 
												    /*functionality_id*/	functionality_id, 
												                /*type*/	3,
												 /*Project Granularity*/	EvaluateTools.INTRA_PROJECT_CLONES,
												  /*line_gt_similarity*/	null,
												 /*token_gt_similarity*/	null, 
												   /*avg_gt_similarity*/	null,
												  /*both_gt_similarity*/	null,
												 /*line_gte_similarity*/	null, 
												/*token_gte_similarity*/	null, 
												  /*avg_gte_similarity*/	null,
												 /*both_gte_similarity*/	start,
												  /*line_lt_similarity*/	null, 
												 /*token_lt_similarity*/	null, 
												   /*avg_lt_similarity*/	null,
												  /*both_lt_similarity*/	end,
												 /*line_lte_similarity*/	null, 
												/*token_lte_similarity*/	null, 
												  /*avg_lte_similarity*/	null,
												 /*both_lte_similarity*/	null, 
												            /*min_size*/	this.min_size, 
												            /*max_size*/	this.max_size, 
												     /*min_pretty_size*/	this.min_pretty_size, 
												     /*max_pretty_size*/	this.max_pretty_size, 
												          /*min_tokens*/	this.min_tokens, 
												          /*max_tokens*/	this.max_tokens, 
												          /*min_judges*/	this.min_judges, 
												      /*min_confidence*/	this.min_confidence,
		      											include_internal
											);
			} else {//if(this.similarity_type == ToolEvaluator.SIMILARITY_TYPE_AVG) {
				numDetected =
				EvaluateTools.numTrueDetected(    	         /*tool_id*/	this.tool_id, 
												             /*matcher*/	this.matcher, 
												    /*functionality_id*/	functionality_id, 
												                /*type*/	3,
												 /*Project Granularity*/	EvaluateTools.INTRA_PROJECT_CLONES,
												  /*line_gt_similarity*/	null,
												 /*token_gt_similarity*/	null, 
												   /*avg_gt_similarity*/	null,
												  /*both_gt_similarity*/	null,
												 /*line_gte_similarity*/	null, 
												/*token_gte_similarity*/	null, 
												  /*avg_gte_similarity*/	start,
												 /*both_gte_similarity*/	null,
												  /*line_lt_similarity*/	null, 
												 /*token_lt_similarity*/	null, 
												   /*avg_lt_similarity*/	end,
												  /*both_lt_similarity*/	null,
												 /*line_lte_similarity*/	null, 
												/*token_lte_similarity*/	null, 
												  /*avg_lte_similarity*/	null,
												 /*both_lte_similarity*/	null,
												            /*min_size*/	this.min_size, 
												            /*max_size*/	this.max_size, 
												     /*min_pretty_size*/	this.min_pretty_size, 
												     /*max_pretty_size*/	this.max_pretty_size, 
												          /*min_tokens*/	this.min_tokens, 
												          /*max_tokens*/	this.max_tokens, 
												          /*min_judges*/	this.min_judges, 
												      /*min_confidence*/	this.min_confidence,
		      											include_internal
											);
			}
			numDetected_type3_intra[index].put(functionality_id, numDetected);
		}
		return numDetected_type3_intra[index].get(functionality_id);
	}
	
	public int getNumDetected_type3(long functionality_id, int similarity) throws SQLException {
		return getNumDetected_type3_intra(functionality_id, similarity) + getNumDetected_type3_inter(functionality_id, similarity);
	}
	
	
	public int getNumDetected_type3_inter(long functionality_id, int similarity_start, int similarity_end) throws SQLException {
		if(similarity_start < 0) throw new IllegalArgumentException("similarity_start must be >= 0.");
		if(similarity_end > 100) throw new IllegalArgumentException("similarity_end must be <= 100");
		if(similarity_start > similarity_end) throw new IllegalArgumentException("similarity start must be < similarity_end");
		if(similarity_start % 5 != 0) throw new IllegalArgumentException("similarity_start must be a multiple of 5");
		if(similarity_end % 5 != 0) throw new IllegalArgumentException("similarity_end must be a multiple of 5");
		
		int numDetected = 0;
		for(int i = similarity_start; i < similarity_end; i += 5) {
			numDetected += getNumDetected_type3_inter(functionality_id, i);
		}
		return numDetected;
	}
	
	public int getNumDetected_type3_intra(long functionality_id, int similarity_start, int similarity_end) throws SQLException {
		if(similarity_start < 0) throw new IllegalArgumentException("similarity_start must be >= 0.");
		if(similarity_end > 100) throw new IllegalArgumentException("similarity_end must be <= 100");
		if(similarity_start > similarity_end) throw new IllegalArgumentException("similarity start must be < similarity_end");
		if(similarity_start % 5 != 0) throw new IllegalArgumentException("similarity_start must be a multiple of 5");
		if(similarity_end % 5 != 0) throw new IllegalArgumentException("similarity_end must be a multiple of 5");
		
		int numDetected = 0;
		for(int i = similarity_start; i < similarity_end; i += 5) {
			numDetected += getNumDetected_type3_intra(functionality_id, i);
		}
		return numDetected;
	}
	
	public int getNumDetected_type3(long functionality_id, int similarity_start, int similarity_end) throws SQLException {
		return getNumDetected_type3_intra(functionality_id, similarity_start, similarity_end) +
			   getNumDetected_type3_inter(functionality_id, similarity_start, similarity_end);
	}
	
	
	public int getNumDetected_type3_inter(int similarity_start, int similarity_end) throws SQLException {
		if(similarity_start < 0) throw new IllegalArgumentException("similarity_start must be >= 0.");
		if(similarity_end > 100) throw new IllegalArgumentException("similarity_end must be <= 100");
		if(similarity_start > similarity_end) throw new IllegalArgumentException("similarity start must be < similarity_end");
		if(similarity_start % 5 != 0) throw new IllegalArgumentException("similarity_start must be a multiple of 5");
		if(similarity_end % 5 != 0) throw new IllegalArgumentException("similarity_end must be a multiple of 5");
		
		int numDetected = 0;
		for(long fid : functionality_ids) {
			numDetected += getNumDetected_type3_inter(fid, similarity_start, similarity_end);
		}
		return numDetected;
	}
	
	public int getNumDetected_type3_intra(int similarity_start, int similarity_end) throws SQLException {
		if(similarity_start < 0) throw new IllegalArgumentException("similarity_start must be >= 0.");
		if(similarity_end > 100) throw new IllegalArgumentException("similarity_end must be <= 100");
		if(similarity_start > similarity_end) throw new IllegalArgumentException("similarity start must be < similarity_end");
		if(similarity_start % 5 != 0) throw new IllegalArgumentException("similarity_start must be a multiple of 5");
		if(similarity_end % 5 != 0) throw new IllegalArgumentException("similarity_end must be a multiple of 5");
		
		int numDetected = 0;
		for(long fid : functionality_ids) {
			numDetected += getNumDetected_type3_intra(fid, similarity_start, similarity_end);
		}
		return numDetected;
	}
	
	public int getNumDetected_type3(int similarity_start, int similarity_end) throws SQLException {
		return getNumDetected_type3_intra(similarity_start, similarity_end) +
			   getNumDetected_type3_inter(similarity_start, similarity_end);
	}
	
	
	public int getNumDetected_type3_inter(long functionality_id) throws SQLException {
		int numDetected = 0;
		for(int i = 0; i < 100; i += 5) {
			numDetected += getNumDetected_type3_inter(functionality_id, i, i+5);
		}
		return numDetected;
	}
	
	public int getNumDetected_type3_intra(long functionality_id) throws SQLException {
		int numDetected = 0;
		for(int i = 0; i < 100; i += 5) {
			numDetected += getNumDetected_type3_intra(functionality_id, i, i+5);
		}
		return numDetected;
	}
	
	public int getNumDetected_type3(long functionality_id) throws SQLException {
		return getNumDetected_type3_intra(functionality_id) + 
			   getNumDetected_type3_inter(functionality_id);
	}
	
	
	public int getNumDetected_type3_inter() throws SQLException {
		int numDetected = 0;
		for(int i = 0; i < 100; i = i + 5) {
			numDetected += getNumDetected_type3_inter(i, i+5);
		}
		return numDetected;
	}
	
	public int getNumDetected_type3_intra() throws SQLException {
		int numDetected = 0;
		for(int i = 0; i < 100; i = i + 5) {
			numDetected += getNumDetected_type3_intra(i, i+5);
		}
		return numDetected;
	}
	
	public int getNumDetected_type3() throws SQLException {
		return getNumDetected_type3_intra() + getNumDetected_type3_inter();
	}
	
	
	public double getRecall_type3_inter(long functionality_id, int similarity_start, int similarity_end) throws SQLException {
		if(similarity_start < 0) throw new IllegalArgumentException("similarity_start must be >= 0.");
		if(similarity_end > 100) throw new IllegalArgumentException("similarity_end must be <= 100");
		if(similarity_start > similarity_end) throw new IllegalArgumentException("similarity start must be < similarity_end");
		if(similarity_start % 5 != 0) throw new IllegalArgumentException("similarity_start must be a multiple of 5");
		if(similarity_end % 5 != 0) throw new IllegalArgumentException("similarity_end must be a multiple of 5");
		
		int numDetected = getNumDetected_type3_inter(functionality_id, similarity_start, similarity_end);
		int numClones = getNumClones_type3_inter(functionality_id, similarity_start, similarity_end);
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type3_intra(long functionality_id, int similarity_start, int similarity_end) throws SQLException {
		if(similarity_start < 0) throw new IllegalArgumentException("similarity_start must be >= 0.");
		if(similarity_end > 100) throw new IllegalArgumentException("similarity_end must be <= 100");
		if(similarity_start > similarity_end) throw new IllegalArgumentException("similarity start must be < similarity_end");
		if(similarity_start % 5 != 0) throw new IllegalArgumentException("similarity_start must be a multiple of 5");
		if(similarity_end % 5 != 0) throw new IllegalArgumentException("similarity_end must be a multiple of 5");
		
		int numDetected = getNumDetected_type3_intra(functionality_id, similarity_start, similarity_end);
		int numClones = getNumClones_type3_intra(functionality_id, similarity_start, similarity_end);
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type3(long functionality_id, int similarity_start, int similarity_end) throws SQLException {
		if(similarity_start < 0) throw new IllegalArgumentException("similarity_start must be >= 0.");
		if(similarity_end > 100) throw new IllegalArgumentException("similarity_end must be <= 100");
		if(similarity_start > similarity_end) throw new IllegalArgumentException("similarity start must be < similarity_end");
		if(similarity_start % 5 != 0) throw new IllegalArgumentException("similarity_start must be a multiple of 5");
		if(similarity_end % 5 != 0) throw new IllegalArgumentException("similarity_end must be a multiple of 5");
		
		int numDetected = getNumDetected_type3(functionality_id, similarity_start, similarity_end);
		int numClones = getNumClones_type3 (functionality_id, similarity_start, similarity_end);
		return 1.0*numDetected/numClones;
	}
	
	
	public double getRecall_type3_inter(int similarity_start, int similarity_end) throws SQLException {
		if(similarity_start < 0) throw new IllegalArgumentException("similarity_start must be >= 0.");
		if(similarity_end > 100) throw new IllegalArgumentException("similarity_end must be <= 100");
		if(similarity_start > similarity_end) throw new IllegalArgumentException("similarity start must be < similarity_end");
		if(similarity_start % 5 != 0) throw new IllegalArgumentException("similarity_start must be a multiple of 5");
		if(similarity_end % 5 != 0) throw new IllegalArgumentException("similarity_end must be a multiple of 5");
		
		int numDetected = getNumDetected_type3_inter(similarity_start, similarity_end);
		int numClones = getNumClones_type3_inter(similarity_start, similarity_end);
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type3_avg_inter(int similarity_start, int similarity_end) throws SQLException {
		if(similarity_start < 0) throw new IllegalArgumentException("similarity_start must be >= 0.");
		if(similarity_end > 100) throw new IllegalArgumentException("similarity_end must be <= 100");
		if(similarity_start > similarity_end) throw new IllegalArgumentException("similarity start must be < similarity_end");
		if(similarity_start % 5 != 0) throw new IllegalArgumentException("similarity_start must be a multiple of 5");
		if(similarity_end % 5 != 0) throw new IllegalArgumentException("similarity_end must be a multiple of 5");
		
		double avg = 0.0;
		int num = 0;
		for(long id : functionality_ids) {
			double recall = this.getRecall_type3_inter(id, similarity_start, similarity_end);
			if(!Double.isNaN(recall)) {
				avg += recall;
				num++;
			}
		}
		return avg/num;
	}
	
	public double getRecall_type3_intra(int similarity_start, int similarity_end) throws SQLException {
		if(similarity_start < 0) throw new IllegalArgumentException("similarity_start must be >= 0.");
		if(similarity_end > 100) throw new IllegalArgumentException("similarity_end must be <= 100");
		if(similarity_start > similarity_end) throw new IllegalArgumentException("similarity start must be < similarity_end");
		if(similarity_start % 5 != 0) throw new IllegalArgumentException("similarity_start must be a multiple of 5");
		if(similarity_end % 5 != 0) throw new IllegalArgumentException("similarity_end must be a multiple of 5");
		
		int numDetected = getNumDetected_type3_intra(similarity_start, similarity_end);
		int numClones = getNumClones_type3_intra(similarity_start, similarity_end);
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type3_avg_intra(int similarity_start, int similarity_end) throws SQLException {
		if(similarity_start < 0) throw new IllegalArgumentException("similarity_start must be >= 0.");
		if(similarity_end > 100) throw new IllegalArgumentException("similarity_end must be <= 100");
		if(similarity_start > similarity_end) throw new IllegalArgumentException("similarity start must be < similarity_end");
		if(similarity_start % 5 != 0) throw new IllegalArgumentException("similarity_start must be a multiple of 5");
		if(similarity_end % 5 != 0) throw new IllegalArgumentException("similarity_end must be a multiple of 5");
		
		double avg = 0.0;
		int num = 0;
		for(long id : functionality_ids) {
			double recall = this.getRecall_type3_intra(id, similarity_start, similarity_end);
			if(!Double.isNaN(recall)) {
				avg += recall;
				num++;
			}
		}
		return avg/num;
	}
	
	public double getRecall_type3(int similarity_start, int similarity_end) throws SQLException {
		if(similarity_start < 0) throw new IllegalArgumentException("similarity_start must be >= 0.");
		if(similarity_end > 100) throw new IllegalArgumentException("similarity_end must be <= 100");
		if(similarity_start > similarity_end) throw new IllegalArgumentException("similarity start must be < similarity_end");
		if(similarity_start % 5 != 0) throw new IllegalArgumentException("similarity_start must be a multiple of 5");
		if(similarity_end % 5 != 0) throw new IllegalArgumentException("similarity_end must be a multiple of 5");
		
		int numDetected = getNumDetected_type3(similarity_start, similarity_end);
		int numClones = getNumClones_type3(similarity_start, similarity_end);
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type3_avg(int similarity_start, int similarity_end) throws SQLException {
		double avg = 0.0;
		int num = 0;
		for(long id : functionality_ids) {
			double recall = getRecall_type3(id, similarity_start, similarity_end);
			if(!Double.isNaN(recall)) {
				avg += recall;
				num++;
			}
		}
		return avg/num;
	}
	
		
	public double getRecall_type3_inter(long functionality_id) throws SQLException {
		int numDetected = getNumDetected_type3_inter(functionality_id);
		int numClones = getNumClones_type3_inter(functionality_id);
		//System.out.println(numDetected + " " + numClones);
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type3_intra(long functionality_id) throws SQLException {
		int numDetected = getNumDetected_type3_intra(functionality_id);
		int numClones = getNumClones_type3_intra(functionality_id);
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type3(long functionality_id) throws SQLException {
		int numDetected = getNumDetected_type3(functionality_id);
		int numClones = getNumClones_type3(functionality_id);
		return 1.0*numDetected/numClones;
	}
	
	
	public double getRecall_type3_inter() throws SQLException {
		int numDetected = getNumDetected_type3_inter();
		int numClones = getNumClones_type3_inter();
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type3_intra() throws SQLException {
		int numDetected = getNumDetected_type3_intra();
		int numClones = getNumClones_type3_intra();
		return 1.0*numDetected/numClones;
	}
	
	public double getRecall_type3() throws SQLException {
		int numDetected = getNumDetected_type3();
		int numClones = getNumClones_type3();
		return 1.0*numDetected/numClones;
	}
	
	
	public CloneMatcher getMatcher() {
		return this.matcher;
	}
	
	@SuppressWarnings("unchecked")
	public ToolEvaluator(Long tool_id, CloneMatcher matcher, int similarity_type,
			   			 Integer min_size, Integer max_size, Integer min_pretty_size, Integer max_pretty_size, Integer min_tokens, Integer max_tokens,
			   			 Integer min_judges, Integer min_confidence, boolean includeInternal) throws SQLException {
		
		this.similarity_type = similarity_type;
		this.tool_id = tool_id;
		this.matcher = matcher;
		this.min_size = min_size;
		this.max_size = max_size;
		this.min_pretty_size = min_pretty_size;
		this.max_pretty_size = max_pretty_size;
		this.min_tokens = min_tokens;
		this.max_tokens = max_tokens;
		this.min_judges = min_judges;
		this.min_confidence = min_confidence;
		this.include_internal = includeInternal;
		
		numClones_type1_inter = new HashMap<Long,Integer>();
		numClones_type1_intra = new HashMap<Long,Integer>();
		
		numDetected_type1_inter = new HashMap<Long,Integer>();
		numDetected_type1_intra = new HashMap<Long,Integer>();
		
		numClones_type2c_inter = new HashMap<Long,Integer>();
		numClones_type2c_intra = new HashMap<Long,Integer>();
		
		numDetected_type2c_inter = new HashMap<Long,Integer>();
		numDetected_type2c_intra = new HashMap<Long,Integer>();
		
		numClones_type2b_inter = new HashMap<Long,Integer>();
		numClones_type2b_intra = new HashMap<Long,Integer>();
		
		numDetected_type2b_inter = new HashMap<Long,Integer>();
		numDetected_type2b_intra = new HashMap<Long,Integer>();
		
		numClones_type3_inter = (HashMap<Long,Integer>[]) new HashMap[20];
		numClones_type3_intra = (HashMap<Long,Integer>[]) new HashMap[20];
		
		numDetected_type3_inter = (HashMap<Long,Integer>[]) new HashMap[20];
		numDetected_type3_intra = (HashMap<Long,Integer>[]) new HashMap[20];
		
		numClones_false_inter = new HashMap<Long,Integer>();
		numClones_false_intra = new HashMap<Long,Integer>();
		
		numDetected_false_inter = new HashMap<Long,Integer>();
		numDetected_false_intra = new HashMap<Long,Integer>();
		
		for(int i = 0; i < 20; i++) {
			numClones_type3_inter[i] = new HashMap<Long,Integer>();
			numClones_type3_intra[i] = new HashMap<Long,Integer>();
			numDetected_type3_inter[i] = new HashMap<Long,Integer>();
			numDetected_type3_intra[i] = new HashMap<Long,Integer>();
		}
		
		functionality_ids = Functionalities.getFunctionalityIds();
		for(long id : functionality_ids) {
			numClones_type1_inter.put(id, null);
			numDetected_type1_inter.put(id, null);
			numClones_type2c_inter.put(id, null);
			numDetected_type2c_inter.put(id, null);
			numClones_type1_intra.put(id, null);
			numDetected_type1_intra.put(id, null);
			numClones_type2c_intra.put(id, null);
			numDetected_type2c_intra.put(id, null);
			for(int i = 0; i < 19; i++) {
				numClones_type3_inter[i].put(id, null);
				numDetected_type3_inter[i].put(id, null);
				numClones_type3_intra[i].put(id, null);
				numDetected_type3_intra[i].put(id, null);
			}
		}
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((functionality_ids == null) ? 0 : functionality_ids
						.hashCode());
		result = prime * result + ((matcher == null) ? 0 : matcher.hashCode());
		result = prime * result
				+ ((max_pretty_size == null) ? 0 : max_pretty_size.hashCode());
		result = prime * result
				+ ((max_size == null) ? 0 : max_size.hashCode());
		result = prime * result
				+ ((max_tokens == null) ? 0 : max_tokens.hashCode());
		result = prime * result
				+ ((min_confidence == null) ? 0 : min_confidence.hashCode());
		result = prime * result
				+ ((min_judges == null) ? 0 : min_judges.hashCode());
		result = prime * result
				+ ((min_pretty_size == null) ? 0 : min_pretty_size.hashCode());
		result = prime * result
				+ ((min_size == null) ? 0 : min_size.hashCode());
		result = prime * result
				+ ((min_tokens == null) ? 0 : min_tokens.hashCode());
		result = prime
				* result
				+ ((numClones_false_inter == null) ? 0 : numClones_false_inter
						.hashCode());
		result = prime
				* result
				+ ((numClones_false_intra == null) ? 0 : numClones_false_intra
						.hashCode());
		result = prime
				* result
				+ ((numClones_type1_inter == null) ? 0 : numClones_type1_inter
						.hashCode());
		result = prime
				* result
				+ ((numClones_type1_intra == null) ? 0 : numClones_type1_intra
						.hashCode());
		result = prime
				* result
				+ ((numClones_type2b_inter == null) ? 0
						: numClones_type2b_inter.hashCode());
		result = prime
				* result
				+ ((numClones_type2b_intra == null) ? 0
						: numClones_type2b_intra.hashCode());
		result = prime
				* result
				+ ((numClones_type2c_inter == null) ? 0
						: numClones_type2c_inter.hashCode());
		result = prime
				* result
				+ ((numClones_type2c_intra == null) ? 0
						: numClones_type2c_intra.hashCode());
		result = prime * result + Arrays.hashCode(numClones_type3_inter);
		result = prime * result + Arrays.hashCode(numClones_type3_intra);
		result = prime
				* result
				+ ((numDetected_false_inter == null) ? 0
						: numDetected_false_inter.hashCode());
		result = prime
				* result
				+ ((numDetected_false_intra == null) ? 0
						: numDetected_false_intra.hashCode());
		result = prime
				* result
				+ ((numDetected_type1_inter == null) ? 0
						: numDetected_type1_inter.hashCode());
		result = prime
				* result
				+ ((numDetected_type1_intra == null) ? 0
						: numDetected_type1_intra.hashCode());
		result = prime
				* result
				+ ((numDetected_type2b_inter == null) ? 0
						: numDetected_type2b_inter.hashCode());
		result = prime
				* result
				+ ((numDetected_type2b_intra == null) ? 0
						: numDetected_type2b_intra.hashCode());
		result = prime
				* result
				+ ((numDetected_type2c_inter == null) ? 0
						: numDetected_type2c_inter.hashCode());
		result = prime
				* result
				+ ((numDetected_type2c_intra == null) ? 0
						: numDetected_type2c_intra.hashCode());
		result = prime * result + Arrays.hashCode(numDetected_type3_inter);
		result = prime * result + Arrays.hashCode(numDetected_type3_intra);
		result = prime * result + similarity_type;
		result = prime * result + ((tool_id == null) ? 0 : tool_id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ToolEvaluator other = (ToolEvaluator) obj;
		if (functionality_ids == null) {
			if (other.functionality_ids != null)
				return false;
		} else if (!functionality_ids.equals(other.functionality_ids))
			return false;
		if (matcher == null) {
			if (other.matcher != null)
				return false;
		} else if (!matcher.equals(other.matcher))
			return false;
		if (max_pretty_size == null) {
			if (other.max_pretty_size != null)
				return false;
		} else if (!max_pretty_size.equals(other.max_pretty_size))
			return false;
		if (max_size == null) {
			if (other.max_size != null)
				return false;
		} else if (!max_size.equals(other.max_size))
			return false;
		if (max_tokens == null) {
			if (other.max_tokens != null)
				return false;
		} else if (!max_tokens.equals(other.max_tokens))
			return false;
		if (min_confidence == null) {
			if (other.min_confidence != null)
				return false;
		} else if (!min_confidence.equals(other.min_confidence))
			return false;
		if (min_judges == null) {
			if (other.min_judges != null)
				return false;
		} else if (!min_judges.equals(other.min_judges))
			return false;
		if (min_pretty_size == null) {
			if (other.min_pretty_size != null)
				return false;
		} else if (!min_pretty_size.equals(other.min_pretty_size))
			return false;
		if (min_size == null) {
			if (other.min_size != null)
				return false;
		} else if (!min_size.equals(other.min_size))
			return false;
		if (min_tokens == null) {
			if (other.min_tokens != null)
				return false;
		} else if (!min_tokens.equals(other.min_tokens))
			return false;
		if (numClones_false_inter == null) {
			if (other.numClones_false_inter != null)
				return false;
		} else if (!numClones_false_inter.equals(other.numClones_false_inter))
			return false;
		if (numClones_false_intra == null) {
			if (other.numClones_false_intra != null)
				return false;
		} else if (!numClones_false_intra.equals(other.numClones_false_intra))
			return false;
		if (numClones_type1_inter == null) {
			if (other.numClones_type1_inter != null)
				return false;
		} else if (!numClones_type1_inter.equals(other.numClones_type1_inter))
			return false;
		if (numClones_type1_intra == null) {
			if (other.numClones_type1_intra != null)
				return false;
		} else if (!numClones_type1_intra.equals(other.numClones_type1_intra))
			return false;
		if (numClones_type2b_inter == null) {
			if (other.numClones_type2b_inter != null)
				return false;
		} else if (!numClones_type2b_inter.equals(other.numClones_type2b_inter))
			return false;
		if (numClones_type2b_intra == null) {
			if (other.numClones_type2b_intra != null)
				return false;
		} else if (!numClones_type2b_intra.equals(other.numClones_type2b_intra))
			return false;
		if (numClones_type2c_inter == null) {
			if (other.numClones_type2c_inter != null)
				return false;
		} else if (!numClones_type2c_inter.equals(other.numClones_type2c_inter))
			return false;
		if (numClones_type2c_intra == null) {
			if (other.numClones_type2c_intra != null)
				return false;
		} else if (!numClones_type2c_intra.equals(other.numClones_type2c_intra))
			return false;
		if (!Arrays.equals(numClones_type3_inter, other.numClones_type3_inter))
			return false;
		if (!Arrays.equals(numClones_type3_intra, other.numClones_type3_intra))
			return false;
		if (numDetected_false_inter == null) {
			if (other.numDetected_false_inter != null)
				return false;
		} else if (!numDetected_false_inter
				.equals(other.numDetected_false_inter))
			return false;
		if (numDetected_false_intra == null) {
			if (other.numDetected_false_intra != null)
				return false;
		} else if (!numDetected_false_intra
				.equals(other.numDetected_false_intra))
			return false;
		if (numDetected_type1_inter == null) {
			if (other.numDetected_type1_inter != null)
				return false;
		} else if (!numDetected_type1_inter
				.equals(other.numDetected_type1_inter))
			return false;
		if (numDetected_type1_intra == null) {
			if (other.numDetected_type1_intra != null)
				return false;
		} else if (!numDetected_type1_intra
				.equals(other.numDetected_type1_intra))
			return false;
		if (numDetected_type2b_inter == null) {
			if (other.numDetected_type2b_inter != null)
				return false;
		} else if (!numDetected_type2b_inter
				.equals(other.numDetected_type2b_inter))
			return false;
		if (numDetected_type2b_intra == null) {
			if (other.numDetected_type2b_intra != null)
				return false;
		} else if (!numDetected_type2b_intra
				.equals(other.numDetected_type2b_intra))
			return false;
		if (numDetected_type2c_inter == null) {
			if (other.numDetected_type2c_inter != null)
				return false;
		} else if (!numDetected_type2c_inter
				.equals(other.numDetected_type2c_inter))
			return false;
		if (numDetected_type2c_intra == null) {
			if (other.numDetected_type2c_intra != null)
				return false;
		} else if (!numDetected_type2c_intra
				.equals(other.numDetected_type2c_intra))
			return false;
		if (!Arrays.equals(numDetected_type3_inter,
				other.numDetected_type3_inter))
			return false;
		if (!Arrays.equals(numDetected_type3_intra,
				other.numDetected_type3_intra))
			return false;
		if (similarity_type != other.similarity_type)
			return false;
		if (tool_id == null) {
			if (other.tool_id != null)
				return false;
		} else if (!tool_id.equals(other.tool_id))
			return false;
		return true;
	}	
	
//	public static boolean exists(ToolEvaluator te, Path dir) throws SQLException {
//		Path pserialized = dir.resolve(te.getSerializedFileName());
//		return Files.exists(pserialized);
//	}
//	
//	public static ToolEvaluator load(ToolEvaluator te, Path dir) throws FileNotFoundException, IOException, ClassNotFoundException, SQLException {
//		Path pserialized = dir.resolve(te.getSerializedFileName());
//		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(pserialized.toFile()));
//		ToolEvaluator check = (ToolEvaluator) ois.readObject();
//		ois.close();
//		return check;
//	}
//	
//	public static void save(ToolEvaluator te, Path dir) throws FileNotFoundException, IOException, SQLException {
//		Files.createDirectories(dir);
//		Path pserialized = dir.resolve(te.getSerializedFileName());
//		if(Files.exists(pserialized))
//			Files.delete(pserialized);
//		Files.createFile(pserialized);
//		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(pserialized.toFile()));
//		oos.writeObject(te);
//		oos.close();
//	}

	public Long getTool_id() {
		return tool_id;
	}

	public Integer getMin_size() {
		return min_size;
	}

	public Integer getMax_size() {
		return max_size;
	}

	public Integer getMin_pretty_size() {
		return min_pretty_size;
	}

	public Integer getMax_pretty_size() {
		return max_pretty_size;
	}

	public Integer getMin_tokens() {
		return min_tokens;
	}

	public Integer getMax_tokens() {
		return max_tokens;
	}

	public Integer getMin_judges() {
		return min_judges;
	}

	public Integer getMin_confidence() {
		return min_confidence;
	}

	public int getSimilarity_type() {
		return similarity_type;
	}

	public String getSimilarity_type_string() {
		if(this.similarity_type == ToolEvaluator.SIMILARITY_TYPE_AVG) {
			return "AVG";
		} else if (this.similarity_type == ToolEvaluator.SIMILARITY_TYPE_BOTH) {
			return "BOTH";
		} else if (this.similarity_type == ToolEvaluator.SIMILARITY_TYPE_LINE) {
			return "LINE";
		} else if (this.similarity_type == ToolEvaluator.SIMILARITY_TYPE_TOKEN) {
			return "TOKEN";
		} else {
			return "UNKNOWN";
		}
	}
	
	public boolean isInclude_internal() {
		return include_internal;
	}

	public Set<Long> getFunctionality_ids() {
		return functionality_ids;
	}
	
}
