package edu.bxml.swt;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;

import com.browsexml.core.XMLBuildException;
import com.browsexml.core.XmlObject;
import com.browsexml.core.XmlObjectImpl;
import com.javalobby.tnt.annotation.attribute;

import edu.bxml.http.Get;

/**
 * 
 */
@attribute(value = "", required = false)
public class Timer extends XmlObjectImpl implements XmlObject {
	private static Log log = LogFactory.getLog(Timer.class);
	int time = -1;
	Vector<XmlObject>actions = new Vector<XmlObject>();

	org.eclipse.swt.widgets.Display display = Display.getDefault();
	
	@Override
	public void execute() throws XMLBuildException {

	}
	
	public void runTimer() {
		log.debug("Timer executed");
		for (XmlObject action: actions) {
			try {
				action.execute();
			} catch (XMLBuildException e) {
				e.printStackTrace();
				break;
			} 
		}
	}
	
	@Override
	public void check() throws XMLBuildException {

	}
	
	
	@attribute(value = "", required = true)
	public void setTime(Integer time) {
		this.time = time;
		display.timerExec (this.time, timer);
	}
	public void setTime(String time) {
		setTime(Integer.parseInt(time));
	}
	
	/**
	 * Run an html:get operation on listener.
	 */
	@attribute(value = "", required = false)
	public void addGet(Get get) {
		actions.add(get);
	}
	
	public void addApply(Apply a) {
		actions.add(a);
	}


	final Runnable timer = new Runnable () {
		public void run () {
			runTimer();
			display.timerExec (time, this);
		}
	};

}
