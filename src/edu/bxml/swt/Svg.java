package edu.bxml.swt;

import org.apache.batik.script.Window;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.javalobby.tnt.annotation.attribute;

/**
 * Draw a Scaled Vector Graphics image
 */
/*import ;*/
@attribute(value = "", required = false)
public class Svg extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Svg.class);
	JSVGCanvas canvas = new JSVGCanvas();
	Document document;
    Window window;

	String uri = null;
	java.awt.Container GUIParent = null;


	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
//		GUIParent.add(canvas);
//		canvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
//		canvas.addSVGLoadEventDispatcherListener
//        (new SVGLoadEventDispatcherAdapter() {
//                public void svgLoadEventDispatchStarted
//                    (SVGLoadEventDispatcherEvent e) {
//                    // At this time the document is available...
//                    document = canvas.getSVGDocument();
//                    // ...and the window object too.
//                    window = canvas.getUpdateManager().
//                        getScriptingEnvironment().createWindow();
//                }
//            });

		return true;
	}
	
	
	@Override
	public void execute() throws XMLBuildException {
		
	}
	@Override
	public void check() throws XMLBuildException {
		if (uri == null) 
			throw new XMLBuildException("The uri has not been set");
	}
	
	/**
	 * Location of the svg file
	 */
	@attribute(value = "", required = false)
	public void setUri(String uri) {
		this.uri = uri;
		canvas.setURI(uri);
	}
	
	public void setGUIParent(java.awt.Container parent) {
		GUIParent = parent;
		log.debug("Swing parent at set = " + GUIParent);
	}
	
	public JSVGCanvas getCanvas() {
		return canvas;
	}
}



