package edu.misc.report;

//import static com.browsexml.objects.ParseStyle.parse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlParser;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.javalobby.tnt.annotation.attribute;


public class Td extends ReportObject {
	private static Log log = LogFactory.getLog(Td.class);
	com.itextpdf.text.pdf.PdfPTable table = null;

	Phrase phrase = null;
	PdfPCell cell = null;

	@Override
	public boolean processRawAttributes(Attributes attrs)
			throws XMLBuildException {
		Table t = getAncestorOfType(Table.class);
		
		cell = new PdfPCell(t.getTable().getDefaultCell());
		log.debug("default cell border = " + t.getTable().getDefaultCell() + "   " + t.getTable().getDefaultCell().getBorder());
		return true;
	}
	
	public PdfPCell getCell() {
		//return new PdfPCell(new com.itextpdf.text.Phrase("PPP"));
		return cell;
	}
	
	@Override
	public void check() throws XMLBuildException {
	}

/*
			datatable.addCell(currentCell);
 */
	int colSpan = 1;
	String text = null;
	int border = 0;
	
	public void setText(String text) {
		this.text = text;
	}
	
	public Td() {
		setName(null);
	}
	
	public Td(String name) {
		setName(name);
	}
	
	public void setAlignment(String alignment) {
		log.debug("ALIGN = " + alignment);

		try {
			int align = XmlParser.getFieldValues(PdfPCell.class, alignment);
			cell.setHorizontalAlignment(align);
			log.debug("currentCell.setHorizontalAlignment("
					+ align + ");");
		} catch (Exception e) {

		};
	}
	
	@attribute(value = "", required = false)
	public void setBorder(Integer border) {
		cell.setBorder(border);
	}
	public void setBorder(String strBorder) {
		try {
			setBorder(Integer.parseInt(strBorder));
		}catch (NumberFormatException nfe) {
			int border = XmlParser.getFieldValues(PdfPCell.class, strBorder);
			cell.setBorder(border);
		}
	}
	/**
	 * How many columns this element spans.
	 * @param colSpan
	 * @throws XMLBuildException
	 */
	@attribute(value = "", required = false, defaultValue="1")
	public void setColSpan(Integer span) {
		log.debug("set col span to " + span);
		this.colSpan = span;
		cell.setColspan(colSpan);
	}
	public void setColSpan(String span) throws XMLBuildException {
		try {
			setColSpan(Integer.parseInt(span));
			
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		};
	}
	
	public int getSpan() {
		return colSpan;
	}
	
	/**
	 * This table cell contains text
	 * @param colSpan
	 * @throws XMLBuildException
	 */
	@attribute(value = "", required = false)
	public void addPhrase(Phrase phrase) {
		
	}
	
	public void addPhraseEnd(Phrase phrase) throws XMLBuildException {
		log.debug("Cell.addElement(" + phrase);
		this.phrase = phrase;

	}
	
	@Override
	public void execute() {
		log.debug("Cell.addElement(" + phrase);
		phrase.execute();// do macro substitutions
		cell.setPhrase(phrase.getPhrase());
	}
	
	
	@attribute(value = "", required = false)
	public void setNoWrap(Boolean noWrap) {
		cell.setNoWrap(noWrap);
	}
	public void setNoWrap(String noWrap) {
		setNoWrap(Boolean.parseBoolean(noWrap));
	}
	
	@attribute(value = "", required = false)
	public void setPadding(Float padding) {
		cell.setPadding(padding);
	}
	public void setPadding(String padding) {
		try {
			setPadding(Float.parseFloat(padding));
		}catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}
	}
	
	public void setBorderWidth(Integer borderWidth) {
		cell.setBorderWidth(borderWidth);
	}
	
	public void setBorderWidth(String width) {
		try {
			setBorderWidth(Integer.parseInt(width));
		}catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}
	}
	
	public void setBorderWidthRight(Integer borderWidth) {
		cell.setBorderWidthRight(borderWidth);
	}
	
	public void setBorderWidthRight(String width) {
		try {
			setBorderWidthRight(Integer.parseInt(width));
		}catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}
	}
	
	public void setBorderWidthLeft(Integer borderWidth) {
		cell.setBorderWidthLeft(borderWidth);
	}
	
	public void setBorderWidthLeft(String width) {
		try {
			setBorderWidthLeft(Integer.parseInt(width));
		}catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}
	}
	
	public void setBorderWidthTop(Integer borderWidth) {
		cell.setBorderWidthTop(borderWidth);


	}
	
	public BaseColor getColor(String color) {
		java.lang.reflect.Field f = null;
		try {
			f = BaseColor.class.getField(color);
		} catch (SecurityException e) {
			e.printStackTrace();
			return BaseColor.BLACK;
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			return BaseColor.BLACK;
		}
		BaseColor value = null;
		try {
			value = (BaseColor) f.get(BaseColor.class);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return BaseColor.BLACK;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return BaseColor.BLACK;
		}
		return value;
	}
	
	public void setBorderColorTop(String color) {
		cell.setBorderColorTop(getColor(color));
	}
	public void setBorderColorLeft(String color) {
		cell.setBorderColorLeft(getColor(color));
	}
	public void setBorderColorBottom(String color) {
		cell.setBorderColorBottom(getColor(color));
	}
	public void setBorderColorRight(String color) {
		cell.setBorderColorRight(getColor(color));
	}
	
	public void setBorderWidthTop(String width) {
		try {
			setBorderWidthTop(Integer.parseInt(width));
		}catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}
	}
	
	public void setBorderWidthBottom(Integer borderWidth) {
		cell.setBorderWidthBottom(borderWidth);
	}
	
	public void setBorderWidthBottom(String width) {
		try {
			setBorderWidthBottom(Integer.parseInt(width));
		}catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}
	}
	
	@attribute(value = "", required = false)
	public void setPadBottom(Float pad) {
		cell.setPaddingBottom(pad);
	}
	public void setPadBottom(String padding) {
		try {
			setPadBottom(Float.parseFloat(padding));
		}catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}
	}
	
	@attribute(value = "", required = false)
	public void setPadTop(Float pad) {
		cell.setPaddingTop(pad);
	}
	public void setPadTop(String padding) {
		try {
			setPadTop(Float.parseFloat(padding));
		}catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}
	}
	
	@attribute(value = "", required = false)
	public void setPadLeft(Float pad) {
		cell.setPaddingLeft(pad);
	}
	public void setPadLeft(String padding) {
		try {
			setPadLeft(Float.parseFloat(padding));
			
		}catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}
	}	
	
	@attribute(value = "", required = false)
	public void setPadRight(Float pad) {
		cell.setPaddingRight(pad);
	}
	public void setPadRight(String padding) {
		try {
			setPadRight(Float.parseFloat(padding));
		}catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}
	}
	
	@attribute(value = "", required = false)
	public void setHeight(Integer height) {
		cell.setFixedHeight(height);
	}
	public void setHeight(String height) {
		int intHeight = 0;
		try {
			setHeight(Integer.parseInt(height));
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	@attribute(value = "", required = false)
	public void setImage(String image) {
		System.err.println("image begin set to " + image);
		Image i = (Image) this.getSymbolTable().get(image);
		if (i != null)
			cell.setImage(i);
	}
}
