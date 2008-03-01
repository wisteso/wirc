package SortedListModel;

public class StringIRC implements Comparable<StringIRC>
{
	protected String s;
	
	public StringIRC(String string)
	{
		this.set(string);
	}
	
	public int compareTo(StringIRC other)
	{		
		String t1 = this.s.replace('@', '\001').replace('%', '\002').replace('+', '\003').toUpperCase();
		String t2 = other.s.replace('@', '\001').replace('%', '\002').replace('+', '\003').toUpperCase();
		
		return t1.compareTo(t2);
	}
	
	public String toString()
	{
		return s;
	}
	
	public void set(String s)
	{
		this.s = s;
	}
}
