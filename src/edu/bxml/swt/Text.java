package edu.bxml.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.javalobby.tnt.annotation.attribute;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;

/**
 * Instances of this class are selectable user interface objects that allow the user to enter and modify text.
 * See also Control Object 
 */
@attribute(value = "", required = false)
public class Text extends ControlObject {
	private org.eclipse.swt.widgets.Composite GUIParent = null;
	private org.eclipse.swt.widgets.Text text = null;
	private XMask mask = null;
	private String value = null;
	private boolean selectAllOnFocus = true;
	private boolean setFromText = false;
	
	public boolean processRawAttributes(org.xml.sax.Attributes attrs) {
		String style = attrs.getValue("style");
		this.style = XmlParser.getFieldValues(SWT.class, style);
		text = new org.eclipse.swt.widgets.Text(getGUIParent(), this.style);
		return true;
	}
	
	/**
	 *  CENTER, LEFT, MULTI, PASSWORD, SINGLE, RIGHT, READ_ONLY, WRAP 
	 *  Events: DefaultSelection, Modify, Verify 
	 */
	@attribute(value = "", required = false)
	public void setStyle(String style) {
		//dummy 
	}
	
	public org.eclipse.swt.widgets.Text getGUIObject() {
		return text;
	}
	
	public void setFromTextContent(String text) {
		if (!setFromText && text != null && !"".equals(text.trim())) {
			setText(text);
		}
		setFromText = false;
	}
	@Override
	public void execute() throws XMLBuildException {
	}
	@Override
	public void check() throws XMLBuildException {
		if (mask != null && !mask.isMaskApplied(value)) {
			value = this.mask.applyMask(value);
		}
		if (value != null) {
			text.setText(value);
		}
		if (selectAllOnFocus) {
			this.text.addListener(SWT.FocusIn, new Listener() {
				public void handleEvent(Event e) {
					((org.eclipse.swt.widgets.Text) e.widget).selectAll();
				}
			});
		}
	}

	public org.eclipse.swt.widgets.Composite getGUIParent() {
		return GUIParent;
	}

	public void setGUIParent(org.eclipse.swt.widgets.Widget parent) {
		GUIParent = (org.eclipse.swt.widgets.Composite) parent;
	}
	
	/**
	 * Sets the maximum number of characters that the receiver is capable of holding to be the argument
	 */
	@attribute(value = "", required = false)
	public void setTextLimit(Integer limit) {
		text.setTextLimit(limit);
	}
	public void setTextLimit(String limit) {
		text.setTextLimit(Integer.parseInt(limit));
	}
	
	/**
	 * Sets the echo character. 
	 */
	@attribute(value = "", required = false)
	public void setEchoChar(Character echoChar) {
		text.setEchoChar(echoChar);
	}
	public void setEchoChar(String echoChar) throws XMLParseException {
		setEchoChar(echoChar.charAt(0));
	}
	
	/**
	 * Sets a mask.  # represents a digit (0-9).  Any other character is taken as a literal.
	 * A date string is the most common use: ##/##/#### 
	 */
	@attribute(value = "", required = false)
	public void setMask(String mask) {
		if (this.mask == null) {
			org.eclipse.swt.graphics.Color color = text.getBackground();
			
			text.setEditable(false);
	
			text.addListener(SWT.MouseDown, new Listener() {
				public void handleEvent(Event e) {
					XMask.mouseDown(e);
				}
			});
			text.addListener(SWT.KeyDown, new Listener() {
				public void handleEvent(Event e) {
					XMask.textMaskGeneric(e);
				}
			});
	
			text.setBackground(color);
		}
		this.mask = new XMask(mask);
		text.setData("mask", this.mask);
		value = this.mask.applyMask(value);
	}
	
	/**
	 * Sets the text
	 */
	@attribute(value = "", required = false)
	public void setText(String text) {
		setFromText = true;
		this.value = text;
		if (mask != null && !this.mask.isMaskApplied(value)) {
			value = this.mask.applyMask(value);
			this.text.setText(value);
		}
	}
	
	/**
	 * If true will select all the text when selected so that previously 
	 * existing text in the text box can be easily overwritten.
	 */
	@attribute(value = "", required = false, defaultValue="true")
	public void setSelectAllOnFocus(Boolean text) {
		selectAllOnFocus = text;
	}
	public void setSelectAllOnFocus(String text) {
		setSelectAllOnFocus(Boolean.parseBoolean(text));
	}
	
	public String toString() {
		if (text != null)
			return text.getText();
		return "";
	}
}

class XMask {
	String mask = "";
	int maskPos = 0;
	int maxLocation = 0;
	
	static String spaceString = "_";
	static char spaceChar = spaceString.charAt(0);
	
	public XMask() {
	}
	
	public XMask(String mask) {
		setMask(mask);
	}

	 public void setMask(String mask) {
	 	this.mask = mask;
	 }
	 
	 public void setMaskPos(int pos) {
	 	if (pos < mask.length())
	 		maskPos = pos;
	 }
	 
	 public int textToMaskPos(int textPos) {
	 	byte[] m = mask.getBytes();
		for (int i = 0, k = 0; i < textPos && 
					i < m.length; i++) {
			while (m[k] != '#' && k < m.length)
				k++;
			if (k < m.length)
				k++;
		}
	 	return 0;
	 }
	 
	 public int getTextPos() {
	 	int textPos = 0;
		byte[] m = mask.getBytes();
		for (int i = 0; i < maskPos && 
					i < m.length; i++) {
			if (m[i] == '#')
				textPos++;
		}
	 	return textPos;
	 }
	 
	public int getMaskPos() {
	   return maskPos;
	}
	
	public String clear(String text, int pos, int len) {
		byte[] m = mask.getBytes();
		byte[] t = text.getBytes();
		for (int i = 0; i < m.length; i++) {
			if (m[i] == '#' && i >= pos && i <(pos+len))
				t[i] = (byte)(spaceChar);
		}
		text = new String(t);
		return text;
	}
	
	public boolean isMaskApplied(String text) {
		byte[] m = mask.getBytes();
		String localText = text + "             ";
		for (int i = 0; i < m.length; i++) {
			if (m[i] != '#') {
				if (localText.charAt(i) != (char)m[i])
					return false;
			}
			else {
				if (localText.charAt(i) != spaceChar)
					if (!Character.isDigit(localText.charAt(i)))
						return false;
			}
		}
		return true;
	}

	public String applyMask(String text) {
		byte[] m = mask.getBytes();
		if (text == null) {
			text = "";
		}
		int i = 0;
		int k = 0;
		for (; i < m.length && k < text.length(); i++) {
			if (m[i] == '#') {
				if (Character.isDigit(text.charAt(k))) {
					if (k < text.length()) {
						m[i] = (byte)text.charAt(k++);
						//maxLocation = i;
					}
					else 
						m[i] = (byte)spaceChar;
				}
				else {
					maxLocation = new String(m).indexOf("#");
					return new String(m).replaceAll("#", spaceString);
				}
			}
			else if (m[i] == text.charAt(k))
				k++;
		}
		maxLocation = new String(m).indexOf("#");
		return new String(m).replaceAll("#", spaceString);
	}
		
	public String deapplyMask(String text) {
			StringBuffer s1 = new StringBuffer("");
			byte[] m = mask.getBytes();
			for (int i = 0; i < m.length && i < text.length(); i++) {
				if (m[i] == '#') {
					s1.append(text.charAt(i));
				}
			}
			return s1.toString();
	}
	
	public static void mouseDown(Event e) {
		org.eclipse.swt.widgets.Text t = (org.eclipse.swt.widgets.Text) e.widget;
		XMask m = (XMask) t.getData("mask");
		String s = m.deapplyMask(t.getText());
		//int maxlength = m.mask.length() - s.length();
		m.applyMask(s);
		if (t.getCaretPosition() > m.maxLocation)
			t.setSelection(m.maxLocation);
	}
	
	public static void textMaskGeneric(Event e) {
		org.eclipse.swt.widgets.Text t = (org.eclipse.swt.widgets.Text) e.widget;
		t.setEditable(false);
		XMask m = (XMask) t.getData("mask");
		String mask = m.mask;
		String text = t.getText();
		
		int range = t.getSelectionCount();
		int pos = t.getCaretPosition()- range;
		m.setMaskPos(pos);

		if (e.stateMask == 262144) {
			TextTransfer transfer = TextTransfer.getInstance();
			//FIX ME text = (String) XMLBrowser.cb.getContents(transfer);
			t.setText(m.applyMask(text));
			return;
		}

		if (e.character != SWT.BS
			&& ((Character.getNumericValue(e.character) == -1)
				|| (e.keyCode == 0)))
			return;
		
		boolean moveBack =
			(e.character == SWT.BS || e.keyCode == SWT.ARROW_LEFT);

		if (range != 0) {
			text = m.clear(text, pos, range);
		}
		range = 0;

		if ((mask.length() <= pos && !moveBack) || range != 0) {
			t.setText(text);
			t.setSelection(pos);
			return;
		}

		if (!m.isMaskApplied(text)) {
			text = m.applyMask(text);
		}

		byte[] b = text.getBytes();
		char replace = e.character;
		if (Character.isDigit(e.character)) {
			pos++;
			/* not over read-only character */

			if (mask.charAt(pos - 1) != '#')
				pos = mask.indexOf('#', pos) + 1;

			if (pos - 1 <= mask.length()) {
				b[pos - 1] = (byte) replace;

				/* advance past read-only characters */
				if (pos < mask.length() && mask.charAt(pos) != '#')
					pos = mask.indexOf('#', pos);
			}
		} else {
			if (moveBack) {
				pos--;
				if (pos < 0)
					pos = 0;
				if (mask.charAt(pos) != '#') {
					pos = mask.lastIndexOf('#', pos);
					if (pos < 0)
						pos = mask.indexOf('#');
				}
				if (e.character == SWT.BS)
					b[pos] = (byte) spaceChar;
			} else {
				if (pos < mask.length() && mask.charAt(pos) != '#')
					pos = mask.indexOf('#', pos);
			}
		}
		String out = new String(b);
		t.setText(out);
		t.setSelection(pos);
	}
	
}

