package database;

public class Functionality {
	private long id;
	private String name;
	private String desc;
	private String heuristic;
	
	public Functionality(long id, String name, String desc, String heuristic) {
		super();
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.heuristic = heuristic;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public String getHeuristic() {
		return heuristic;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((desc == null) ? 0 : desc.hashCode());
		result = prime * result
				+ ((heuristic == null) ? 0 : heuristic.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Functionality other = (Functionality) obj;
		if (desc == null) {
			if (other.desc != null)
				return false;
		} else if (!desc.equals(other.desc))
			return false;
		if (heuristic == null) {
			if (other.heuristic != null)
				return false;
		} else if (!heuristic.equals(other.heuristic))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}
