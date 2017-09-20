package com.xmlcalabash.util;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;

import javax.xml.transform.SourceLocator;

import com.xmlcalabash.core.XProcException;
import com.xmlcalabash.core.XProcMessageListener;
import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.model.RuntimeValue;
import com.xmlcalabash.runtime.XStep;
import com.xmlcalabash.util.URIUtils;

import net.sf.saxon.s9api.*;
import net.sf.saxon.Controller;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.instruct.Executable;
import net.sf.saxon.expr.instruct.LocationMap;
import net.sf.saxon.expr.parser.ExpressionVisitor;
import net.sf.saxon.functions.FunctionLibraryList;
import net.sf.saxon.functions.StandardFunction;
import net.sf.saxon.functions.SystemFunctionLibrary;
import net.sf.saxon.lib.StringCollator;
import net.sf.saxon.om.NameOfNode;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.style.AttributeValueTemplate;
import net.sf.saxon.style.ExpressionContext;
import net.sf.saxon.style.StyleElement;
import net.sf.saxon.sxpath.IndependentContext;
import net.sf.saxon.sxpath.XPathDynamicContext;
import net.sf.saxon.sxpath.XPathExpression;
import net.sf.saxon.sxpath.XPathEvaluator;
import net.sf.saxon.sxpath.XPathVariable;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.tree.linked.LinkedTreeBuilder;
import net.sf.saxon.type.Type;
import net.sf.saxon.value.DecimalValue;

public class XProcMessageListenerHelper {

	public static String evaluateExtensionAttribute(QName attName, XProcRuntime runtime, XStep step) {
		String val = step.getExtensionAttribute(attName);
		if (val != null) {
			if (val.contains("{")) {
				// may be an attribute value template
				try {
					val = evaluateAVT(val, runtime, step);
				} catch (Exception e) {
					Throwable cause; {
						if (e instanceof XPathException) {
							XPathException xe = (XPathException)e;
							final SourceLocator[] location = new SourceLocator[] {
								XProcException.prettyLocator(xe.getLocator(), attName.toString())
							};
							cause = new XProcException(xe.getMessage(), xe) {
								@Override
								public SourceLocator[] getLocator() {
									return location; }};
						} else
							cause = e;
					}
					throw new XProcException("Could not evaluate " + attName + ": " + val, cause);
				}
			}
		}
		return val;
	}

	public static String evaluateAVT(String avt, final XProcRuntime runtime, XStep step) throws XPathException {
		
		final Hashtable<QName,RuntimeValue> globals = step.getInScopeOptions();
		final String systemId = URIUtils.cwdAsURI().relativize(step.getNode().getBaseURI()).toASCIIString();
		final int lineNumber = step.getNode().getLineNumber();
		final XPathEvaluator evaluator = new XPathEvaluator(); {
			((IndependentContext)evaluator.getStaticContext()).setAllowUndeclaredVariables(true);
		}

		// AVT is a XSLT-only concept, we need to hack around that
		ExpressionContext dummyContext; {
			final StyleElement dummyXslt = new StyleElement() {
				{
					NodeInfo dummyNode; {
						Controller controller = new Controller(runtime.getProcessor().getUnderlyingConfiguration());
						LinkedTreeBuilder builder = new LinkedTreeBuilder(controller.makePipelineConfiguration());
						builder.open();
						builder.startDocument(0);
						builder.endDocument();
						builder.close();
						dummyNode = builder.getCurrentRoot();
					}
					initialise(new NameOfNode(dummyNode), dummyNode.getSchemaType(), null, dummyNode, 0);
				}
				protected void prepareAttributes() throws XPathException {}
			};
			dummyContext = new ExpressionContext(dummyXslt) {
				final LocationMap locationMap = new LocationMap();
				final Executable dummyExec = new Executable(runtime.getProcessor().getUnderlyingConfiguration()); {
					dummyExec.setLocationMap(locationMap);
				}
				final FunctionLibraryList library = new FunctionLibraryList(); {
					library.addFunctionLibrary(SystemFunctionLibrary.getSystemFunctionLibrary(
							StandardFunction.CORE | StandardFunction.XSLT));
				}
				@Override
				public DecimalValue getXPathLanguageLevel() {
					return DecimalValue.TWO;
				}
				@Override
				public LocationMap getLocationMap() {
					return locationMap;
				}
				@Override
				public Executable getExecutable() {
					return dummyExec;
				}
				@Override
				public FunctionLibraryList getFunctionLibrary() {
					return library;
				}
				@Override
				public StringCollator getCollation(String name) {
					return null;
				}
				@Override
				public String getSystemId() {
					return systemId;
				}
				@Override
				public Expression bindVariable(StructuredQName name) throws XPathException {
					if (!globals.containsKey(new QName(name.toJaxpQName())))
						throw new XPathException("Variable is not defined: $" + name) {
							@Override
							public void maybeSetLocation(SourceLocator loc) {
								final long id = ((Expression)loc).getLocationId();
								super.maybeSetLocation(new SourceLocator() {
									public String getPublicId()  { return null; }
									public String getSystemId()  { return locationMap.getSystemId(id); }
									public int getLineNumber()   { return locationMap.getLineNumber(id); }
									public int getColumnNumber() { return locationMap.getColumnNumber(id); }
								});
							}
						};
					return evaluator.getStaticContext().bindVariable(name);
				}
			};
		}

		// parse the AVT expression
		Expression expr = AttributeValueTemplate.make(avt, lineNumber, dummyContext);

		// now set the variables and evaluate
		XPathDynamicContext dynamicContext; {
			XPathExpression dummyXPath = evaluator.createExpression("()");
			dynamicContext = dummyXPath.createDynamicContext();
			Iterator<XPathVariable> variables = ((IndependentContext)evaluator.getStaticContext()).iterateExternalVariables();
			while (variables.hasNext()) {
				XPathVariable var = variables.next();
				QName name = new QName(var.getVariableQName().toJaxpQName());
				dynamicContext.setVariable(var, globals.get(name).getStringValue());
			}
		}
		expr.typeCheck(ExpressionVisitor.make(dummyContext, dummyContext.getExecutable()),
		               new ExpressionVisitor.ContextItemType(Type.ITEM_TYPE, true));
		return expr.evaluateAsString(dynamicContext.getXPathContextObject()).toString();
	}
}
