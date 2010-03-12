package edu.bxml.swt;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Panel;

import javax.swing.JRootPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.awt.SWT_AWT;

import com.browsexml.core.XMLBuildException;
import com.javalobby.tnt.annotation.attribute;

/**
 * Allow swing components to be embedded in SWT composite
 */
@attribute(value = "", required = false)
public class Swing extends GUIObject {
	private static Log log = LogFactory.getLog(Swing.class);
	Frame frame = null;
	java.awt.Container contentPane = null;
	org.eclipse.swt.widgets.Composite GUIParent = null;
	
	public org.eclipse.swt.widgets.Composite getGUIObject() {
		return GUIParent;
	}
	
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		log.debug("Swing parent at process = " + GUIParent);
		frame = SWT_AWT.new_Frame(getGUIParent());
		Panel panel = new Panel(new BorderLayout()) {
			public void update(java.awt.Graphics g) {
				/* Do not erase the background */ 
				paint(g);
			}
		};
		frame.add(panel);
		JRootPane root = new JRootPane();
		panel.add(root);
		contentPane = root.getContentPane();

		return true;
	}
	
	
	@Override
	public void execute() throws XMLBuildException {
		
	}
	@Override
	public void check() throws XMLBuildException {
	}
	
	public void setGUIParent(org.eclipse.swt.widgets.Widget parent) {
		GUIParent = (org.eclipse.swt.widgets.Composite) parent;
		log.debug("Swing parent at set = " + GUIParent);
	}
	
	public org.eclipse.swt.widgets.Composite getGUIParent() {
		return GUIParent;
	}
	
	/**
	 * Add a scaled vector graphic drawing
	 */
	@attribute(value = "", required = false)
	public void addSvg(Svg svg) {
		svg.setGUIParent(contentPane);
	}
}
