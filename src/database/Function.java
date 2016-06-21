package database;

public class Function {
	@Override
	public String toString() {
		return "Function [id=" + id + ", name=" + name + ", type=" + type
				+ ", startline=" + startline + ", endline=" + endline
				+ ", normalized_size=" + normalized_size + "]";
	}

	private long id;
	private String name;
	private String type;
	private int startline;
	private int endline;
	private int normalized_size;
	
	public Function(long id, String name, String type, int startline,
			int endline, int normalized_size) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.startline = startline;
		this.endline = endline;
		this.normalized_size = normalized_size;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public int getStartline() {
		return startline;
	}

	public int getEndline() {
		return endline;
	}

	public int getNormalized_size() {
		return normalized_size;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + endline;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + normalized_size;
		result = prime * result + startline;
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
		Function other = (Function) obj;
		if (endline != other.endline)
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (normalized_size != other.normalized_size)
			return false;
		if (startline != other.startline)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
	
	
}
