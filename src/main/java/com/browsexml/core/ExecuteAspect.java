package com.browsexml.core;

import com.browsexml.core.annotation.EndDocument;
import edu.bxml.io.FilterAJ;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class ExecuteAspect {

	private static final Log log = LogFactory.getLog(Execute.class);

	@Around("@annotation(endDocument) && execution(* *(..))")
	public Object around(ProceedingJoinPoint joinPoint, EndDocument endDocument) throws Throwable {
		Object pojo = joinPoint.getTarget();
		FilterAJ wrapper = XmlParser.getWrapper(pojo);
		XmlObject x = (XmlObject) wrapper;
		Object ret = null;
		log.debug("AJ Execute");
		try {
			if (x.isIff()) {
				Set c = x.getVariableParameters().entrySet();
				log.debug("size of variable parameters = " + c.size());
				Iterator itr = c.iterator();

				while (itr.hasNext()) {
					Object[] arguments = new Object[1];
					Entry<Method, String> hmItem = (Entry<Method, String>) itr.next();

					arguments[0] = XmlParser.processMacros(x.getSymbolTable(), XmlParser.replacePoundMacros(hmItem.getValue()));
					log.debug("aspect before execute " + x.getName() + ": " + x.getClass().getName() + ": " + hmItem.getValue() + " = " + arguments[0]);
					try {
						hmItem.getKey().invoke(x, arguments);
					} catch (IllegalAccessException e) {
						log.debug(e);
					} catch (IllegalArgumentException e) {
						log.debug(e);
					} catch (InvocationTargetException e) {
						log.debug(e);
					}
				}
				if (x.isIff()) {
					joinPoint.proceed();
				} else {
					log.debug(x.getName() + ": " + x.getClass().getName() + ": NOT EXECUTED: iff is false");
				}
			}
		} catch (XMLBuildException e) {
			log.debug(e);
		}
		return ret;
	}
}
