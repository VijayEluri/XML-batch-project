package edu.bxml.svg ;

import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.gvt.JGVTComponentListener;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.swt.Svg;

/**
 * Draw a Scaled Vector Graphics image
 */
/*import ;*/
@attribute(value = "", required = false)
public class Er extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Er.class);
	JSVGCanvas canvas = null; 
	String id = null;
		
	MovableObject ball1 = null;
	MovableObject ball2 = null;

	Element eHandle;
	Element sHandle;
	Element gStroke;
	Element eStroke;
	Element gBall;
	Element eBall;
	Element sBall;
	Element bInit;
	HashMap<String, MovableObject> items = new HashMap<String, MovableObject> ();

	int oldX     = 100;
	int oldY     = 100;


	boolean bMouseDown = false;
	String mouseDownElement = null;

	int	chainLen = 50;
	double	friction = 0.95;
	double	damping  = 50.0;
	int intervalID;
	int x = 100;
	int	y = 100;
	int count = 0;
	
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		return true;
	}

	@Override
	public void execute() throws XMLBuildException {
		
	}
	public void check() throws XMLBuildException {
		Svg parent = getAncestorOfType(Svg.class);
		if (id != null) {
			getSymbolTable().put(id, this);
		}
		
		if (parent == null) {
			throw new XMLBuildException("Svg ancestor could not be found");
		}
		canvas = parent.getCanvas();

		
		canvas.addJGVTComponentListener(new JGVTComponentListener() {
			public void componentTransformChanged(ComponentEvent e) {
				log.debug(e.getComponent().toString());
			};
		}
		);
        // Set the JSVGCanvas listeners.
		canvas.addSVGDocumentLoaderListener(new SVGDocumentLoaderAdapter() {
            public void documentLoadingStarted(SVGDocumentLoaderEvent e) {
            	log.debug("Document Loading...");
            }
            public void documentLoadingCompleted(SVGDocumentLoaderEvent e) {
            	log.debug("Document Loaded.");
            }
        });

		canvas.addGVTTreeBuilderListener(new GVTTreeBuilderAdapter() {
            public void gvtBuildStarted(GVTTreeBuilderEvent e) {
            	log.debug("Build Started...");
            }
            public void gvtBuildCompleted(GVTTreeBuilderEvent e) {
            	log.debug("Build Done.");
            	/*

        		
            	log.debug("putting balls...");
            	
            	SVGDocument doc = canvas.getSVGDocument();
            	eHandle = doc.getElementById("eHandle");
            	sHandle = doc.getElementById("sHandle");
            	gStroke = doc.getElementById("gStroke");
            	eStroke = doc.getElementById("eStroke");
            	gBall = doc.getElementById("gBall");
            	eBall = doc.getElementById("eBall");
            	sBall = doc.getElementById("sBall");
            	
            	oldX     = 100;
				oldY     = 100;

				Element stroke = doc.getElementById("eStroke");
				ball1 = new MovableObject(doc.getElementById("gHandle"));
				//ball1.addLink(stroke, LEFT);
				ball2 = new MovableObject(doc.getElementById("gBall"));
				//ball2.addLink(stroke, RIGHT);
				
        		for (Iterator s = items.entrySet().iterator();s.hasNext();) {
        			Map.Entry entry = (Map.Entry)s.next();
        			try {
        				log.debug("TABLE EXECUTE");
						((edu.bxml.svg.MovableObject) entry.getValue()).execute();
					} catch (XMLBuildException e1) {
						e1.printStackTrace();
					}
        		}
				
				items.put(ball1.getId(), ball1);
				items.put(ball2.getId(), ball2);
				
                EventTarget target = (EventTarget) doc.getDocumentElement();
               
                MovingHandler movingListener = new MovingHandler();
                DownHandler downListener = new DownHandler();
                UpHandler upListener = new UpHandler();
                
                target.addEventListener(SVGConstants.SVG_MOUSEDOWN_EVENT_TYPE,
                        downListener, true);
                target.addEventListener(SVGConstants.SVG_MOUSEUP_EVENT_TYPE,
                        upListener, true);
                target.addEventListener(SVGConstants.SVG_MOUSEMOVE_EVENT_TYPE,
                        movingListener, true);
                
                canvas.setDoubleBufferedRendering(false);
                */
            }
            
        });

		canvas.addGVTTreeRendererListener(new GVTTreeRendererAdapter() {
            public void gvtRenderingPrepare(GVTTreeRendererEvent e) {
            	log.debug("Rendering Started...");
            }
            public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
            	log.debug("Render done.");

               
            }
            
        });
		
		canvas.addMouseListener(new MouseListener() {
			public void mousePressed(MouseEvent e) {
				
			}
			public void mouseClicked(MouseEvent e1) {
				log.debug("CLICK: " + e1.paramString());

	            
			}
			public void mouseReleased(MouseEvent e) {
			}
			public void mouseEntered(MouseEvent e) {
				
			}
			public void mouseExited(MouseEvent e) {
				
			}
		});

	}

	protected class CapturingClickHandler implements EventListener {
	    public void handleEvent(Event evt) {       
            Element targetElement = (Element) evt.getTarget();
            
            Node parent = targetElement.getParentNode();
            String id = parent.getAttributes().getNamedItem("id").getNodeValue();
            log.debug("target element = " + id);
            NamedNodeMap map = targetElement.getParentNode().getAttributes();
            for(int j = 0; j < map.getLength(); j++) {
            	log.debug("Attribute: " + map.item(j).getNodeName() + " = " +
            			map.item(j).getNodeValue());
            }


	    }
	}
	
	public void doUpdate(MovableObject ball) {
		if (ball == null) {
			log.debug("null ball");
			return;
		}
		ball.move(x, y);
	}
	
	protected class MovingHandler implements EventListener {
	    public void handleEvent(Event evt) {       
	    	org.w3c.dom.events.MouseEvent m = (org.w3c.dom.events.MouseEvent)evt;
            
			if (!bMouseDown)
				return;

			x  = m.getClientX();
			y  = m.getClientY();
			doUpdate(items.get(mouseDownElement));
	    }
	}
	
	protected class DownHandler implements EventListener {
	    public void handleEvent(Event evt) {       
	    	bMouseDown = true;
	    	org.w3c.dom.events.MouseEvent me = (org.w3c.dom.events.MouseEvent) evt;
	    	 Element targetElement = (Element) evt.getTarget();
	    	mouseDownElement = targetElement.getAttribute("id");
	    	
            Node parent = targetElement.getParentNode();
        
			mouseDownElement = parent.getAttributes().getNamedItem("id").getNodeValue();

            
            log.debug("Client: " + me.getClientX() + ", " + me.getClientY());
            
            NamedNodeMap map = targetElement.getParentNode().getAttributes();
            for(int j = 0; j < map.getLength(); j++) {
            	log.debug("Attribute: " + map.item(j).getNodeName() + " = " +
            			map.item(j).getNodeValue());
            }
            
	    	log.debug("DOWN = " + mouseDownElement + "  tyep: " + 
	    			targetElement.getNodeType());
	    	
	    }
	}
	
	protected class UpHandler implements EventListener {
	    public void handleEvent(Event evt) {       
            bMouseDown = false;
            doUpdate(items.get(mouseDownElement));
            mouseDownElement = null;
	    }
	}
	
	public void setCanvas(JSVGCanvas canvas) {
		this.canvas = canvas;
	}
	
	public void setId(String id) {
		log.debug("put into symbol table " + id + "  " + this);
		this.id = id;
	}
	
	public JSVGCanvas getCanvas() {
		return canvas;
	}
	
	public void addTable(Table t) throws XMLBuildException {
		log.debug("Put table " + t.getId());
		items.put(t.getId(), t);
	}
	
}



