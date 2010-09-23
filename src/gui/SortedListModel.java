package gui;
import javax.swing.DefaultListModel;

/**
 * @author	Dr. Stu Hansen
 */
public class SortedListModel extends DefaultListModel
{
	static final long serialVersionUID = 931478712398213657L;
	
	@Override
	public void addElement(Object obj)
	{
		String str = obj.toString();
    	
    	int index = getSize();
    	
    	while (index > 0 && compare(elementAt(index - 1), str) >= 0)
    	{
    		index--;
    	}
    	
    	super.add(index, obj);
    	
    	fireIntervalAdded(this, index, index);
	}
	
    public void addElement(Object... obj)
    {
    	String str;
    	
    	int index, min = getSize(), max = 0;
    	
    	for (int i = 0; i < obj.length; ++i)
    	{
    		str = obj.toString();
        	
        	index = min;
        	
        	while (index > 0 && compare(elementAt(index - 1), str) >= 0)
        	{
        		index--;
        	}
        	
        	super.add(index, obj);
        	
        	if (index < min)
        		min = index;
        	
        	if (index >= max)
        		max = index;
        	else
        		max++;
    	}
    	
    	super.fireIntervalAdded(this, min, max);
    }
    
    public static int compare(Object former, Object latter)
	{		
		String t1 = former.toString().replace('@', '\001').replace('%', '\002').replace('+', '\003').toUpperCase();
		String t2 = latter.toString().replace('@', '\001').replace('%', '\002').replace('+', '\003').toUpperCase();
		
		return t1.compareTo(t2);
	}
}