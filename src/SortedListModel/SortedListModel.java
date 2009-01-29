package SortedListModel;
import javax.swing.*;

/**
 * @author	Dr. Stu Hansen
 */
public class SortedListModel extends DefaultListModel
{
	static final long serialVersionUID = 1L;
    
    public void addElement(Object obj)
    {
    	String str = obj.toString();
    	
    	int index = getSize();
    	
    	while (index > 0 && ((String)elementAt(index - 1)).compareTo(str) >= 0)
    	{
    		index--;
    	}
    	
    	super.add(index, obj);
    }
}