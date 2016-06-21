package database;

public class Clone {
	
	long functionality_id;
	
	long function_id_one;
	
	long function_id_two;
	
	String type;
	
	int syntactic_type;
	
	double similarity_line;
	
	double similarity_token;
	
	int min_size;
	
	int max_size;
	
	int min_pretty_size;
	
	int max_pretty_size;
	
	int min_judges;
	
	int min_confidence;

	public Clone(long function_id_one, long function_id_two) {
		this.function_id_one = function_id_one;
		this.function_id_two = function_id_two;
	}
	
	public Clone(long functionality_id, long function_id_one,
			long function_id_two, String type, int syntactic_type,
			double similarity_line, double similarity_token, int min_size,
			int max_size, int min_pretty_size, int max_pretty_size,
			int min_judges, int min_confidence) {
		super();
		this.functionality_id = functionality_id;
		this.function_id_one = function_id_one;
		this.function_id_two = function_id_two;
		this.type = type;
		this.syntactic_type = syntactic_type;
		this.similarity_line = similarity_line;
		this.similarity_token = similarity_token;
		this.min_size = min_size;
		this.max_size = max_size;
		this.min_pretty_size = min_pretty_size;
		this.max_pretty_size = max_pretty_size;
		this.min_judges = min_judges;
		this.min_confidence = min_confidence;
	}

	public long getFunctionality_id() {
		return functionality_id;
	}

	public long getFunction_id_one() {
		return function_id_one;
	}

	public long getFunction_id_two() {
		return function_id_two;
	}

	public String getType() {
		return type;
	}

	public int getSyntactic_type() {
		return syntactic_type;
	}

	public double getSimilarity_line() {
		return similarity_line;
	}

	public double getSimilarity_token() {
		return similarity_token;
	}

	public int getMin_size() {
		return min_size;
	}

	public int getMax_size() {
		return max_size;
	}

	public int getMin_pretty_size() {
		return min_pretty_size;
	}

	public int getMax_pretty_size() {
		return max_pretty_size;
	}

	public int getMin_judges() {
		return min_judges;
	}

	public int getMin_confidence() {
		return min_confidence;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (int) (function_id_one ^ (function_id_one >>> 32));
		result = prime * result
				+ (int) (function_id_two ^ (function_id_two >>> 32));
		result = prime * result
				+ (int) (functionality_id ^ (functionality_id >>> 32));
		result = prime * result + max_pretty_size;
		result = prime * result + max_size;
		result = prime * result + min_confidence;
		result = prime * result + min_judges;
		result = prime * result + min_pretty_size;
		result = prime * result + min_size;
		long temp;
		temp = Double.doubleToLongBits(similarity_line);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(similarity_token);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + syntactic_type;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Clone other = (Clone) obj;
		if (function_id_one != other.function_id_one)
			return false;
		if (function_id_two != other.function_id_two)
			return false;
		if (functionality_id != other.functionality_id)
			return false;
		if (max_pretty_size != other.max_pretty_size)
			return false;
		if (max_size != other.max_size)
			return false;
		if (min_confidence != other.min_confidence)
			return false;
		if (min_judges != other.min_judges)
			return false;
		if (min_pretty_size != other.min_pretty_size)
			return false;
		if (min_size != other.min_size)
			return false;
		if (Double.doubleToLongBits(similarity_line) != Double
				.doubleToLongBits(other.similarity_line))
			return false;
		if (Double.doubleToLongBits(similarity_token) != Double
				.doubleToLongBits(other.similarity_token))
			return false;
		if (syntactic_type != other.syntactic_type)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Clone [functionality_id=" + functionality_id
				+ ", function_id_one=" + function_id_one + ", function_id_two="
				+ function_id_two + ", type=" + type + ", syntactic_type="
				+ syntactic_type + ", similarity_line=" + similarity_line
				+ ", similarity_token=" + similarity_token + ", min_size="
				+ min_size + ", max_size=" + max_size + ", min_pretty_size="
				+ min_pretty_size + ", max_pretty_size=" + max_pretty_size
				+ ", min_judges=" + min_judges + ", min_confidence="
				+ min_confidence + "]";
	}
	
}
