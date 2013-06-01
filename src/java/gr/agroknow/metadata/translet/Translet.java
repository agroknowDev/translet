package gr.agroknow.metadata.translet ;

import java.io.File ;
import java.io.IOException ;

import javax.xml.transform.Transformer ;
import javax.xml.transform.TransformerConfigurationException ;
import javax.xml.transform.TransformerException ;
import javax.xml.transform.TransformerFactory ;
import javax.xml.transform.stream.StreamResult ;
import javax.xml.transform.stream.StreamSource ;

import org.apache.commons.io.FileUtils ;

public class Translet
{
	private static File xsl ;
	private static File input ;
	private static File output ;
	private static File bad ;
	
	private Transformer transformer ;
	
    public static void main(String[] args)
    {
		if (args.length != 4)
        {
            System.err.println( "Usage : " ) ;
            System.err.println( "java -jar gr.agroknow.metadata.translet.Translet <xsl file> <input folder> <output folder> <bad folder>" ) ;
            System.exit( -1 ) ;
        }

        xsl = new File( args[0] ) ;
        input = new File( args[1] ) ;
        output = new File( args[2] ) ;
        bad = new File( args[3] ) ;

        if ( !xsl.exists() || !xsl.isFile() )
        {
        	System.err.println( "Invalid xsl transformation: " + xsl.getAbsolutePath() ) ;
        	System.exit( -1 ) ;
        }
        if ( !input.exists() || !input.isDirectory() )
        {
        	System.err.println( "Invalid input directory: " + input.getAbsolutePath() ) ;
        	System.exit( -1 ) ;
        }
        if ( !output.exists() || !output.isDirectory() )
        {
        	System.err.println( "Invalid output directory: " + output.getAbsolutePath() ) ;
        	System.exit( -1 ) ;
        }
        if ( !bad.exists() || !bad.isDirectory() )
        {
        	System.err.println( "Invalid directory for bad files: " + bad.getAbsolutePath() ) ;
        	System.exit( -1 ) ;
        }
        
        Translet translet = new Translet() ;
        translet.ini() ;
        
        for ( File file: input.listFiles() )
        {
        	translet.transform( file ) ;
        }
    }
    
    private void ini()
    {
    	TransformerFactory factory = TransformerFactory.newInstance() ;
        StreamSource xslStream = new StreamSource( xsl ) ;
        try 
        {
			transformer = factory.newTransformer( xslStream ) ;
		}
        catch (TransformerConfigurationException e) 
        {
			System.err.println( "Cannot initialize transformer: " + e.getMessage() ) ;
			e.printStackTrace() ;
			System.exit( -1 ) ;
		}
    }
    
    private void transform( File source ) 
    {   
        StreamSource in = new StreamSource( source ) ;
        // System.out.println( output.getAbsolutePath() + File.separator + source.getName() ) ;
        File target = new File( output.getAbsolutePath() + File.separator + source.getName() ) ;
        StreamResult out = new StreamResult( target ) ;
        try 
        {
			transformer.transform( in, out ) ;
		} 
        catch (TransformerException te) 
        {
            try 
            {
				FileUtils.copyFileToDirectory( source, bad ) ;
				System.err.println( "Cannot convert file: " + source.getName() ) ;
			} 
            catch (IOException ioe) 
            {
				System.err.println( "Cannot copy file " + source.getName() + " to " + bad.getAbsolutePath() ) ;
			}
        }
    }
}