package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Class for "gobbling" an input stream.  Used to clear a stream without regards to contents.
 * 
 * 	This is a modification of the StreamGobbler class created by Michael C. Daconta.
 *
 *	Code retrieved on Jan29, 2012 from http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html?page=4.
 *  The author is Michael C. Daconta who posted the article on December 29, 2000.
 */
public class StreamGobbler extends Thread
{
    InputStream is;
    
    /**
     * Builds a stream gobbler.
     * @param is the input stream.
     */
    public StreamGobbler(InputStream is)
    {
        this.is = is;
    }
    
    /**
     * Begins the stream gobbler.
     */
    public void run()
    {
    	try {
    		InputStreamReader isr = new InputStreamReader(is);
    		BufferedReader br = new BufferedReader(isr);
    		//String line = "";
			while ( (br.readLine()) != null) {
				//System.out.println(line);
			}
			isr.close();
			br.close();
		} catch (IOException e) {
			//e.printStackTrace();
		}
    	try {
			is.close();
		} catch (IOException e) {
			//e.printStackTrace();
		}
    }
}