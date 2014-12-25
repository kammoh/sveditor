package net.sf.sveditor.ui.text.hover;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.internal.text.html.HTMLPrinter;
import org.osgi.framework.Bundle;

import net.sf.sveditor.core.db.SVDBDocComment;
import net.sf.sveditor.core.docs.DocCommentParser;
import net.sf.sveditor.core.docs.DocTopicManager;
import net.sf.sveditor.core.docs.IDocCommentParser;
import net.sf.sveditor.core.docs.IDocTopicManager;
import net.sf.sveditor.core.docs.html.HTMLFromNDMarkup;
import net.sf.sveditor.core.docs.model.DocTopic;
import net.sf.sveditor.core.log.ILogLevel;
import net.sf.sveditor.core.log.LogFactory;
import net.sf.sveditor.core.log.LogHandle;
import net.sf.sveditor.ui.SVUiPlugin;

public class SVNaturalDocHoverContentProvider extends SVHoverContentProvider {
	private SVDBDocComment			fComment;
	private LogHandle				fLog;
	/**
	 * The style sheet (css).
	 */
	private static String fgStyleSheet;	
	
	public SVNaturalDocHoverContentProvider(SVDBDocComment comment) {
		super(null);
		fComment = comment;
		fLog = LogFactory.getLogHandle("SVNaturalDocHoverContentProvider");
	}

	@Override
	public String getContent(SVHoverInformationControlInput input) {
		StringBuffer buffer= new StringBuffer();
		if (fContent != null) {
			return fContent;
		}

		List<DocTopic> docTopics = new ArrayList<DocTopic>() ;
		
		IDocTopicManager topicMgr = new DocTopicManager() ;
		
		IDocCommentParser docCommentParser = new DocCommentParser(topicMgr) ;
		
		fLog.debug(ILogLevel.LEVEL_MID, 
				"+------------------------------------------------------------------") ;
		fLog.debug(ILogLevel.LEVEL_MID, 
				"| Raw Comment") ;
		fLog.debug(ILogLevel.LEVEL_MID,
				"| " + fComment.getRawComment()) ;
		fLog.debug(ILogLevel.LEVEL_MID, 
				"+------------------------------------------------------------------") ;
			
		docCommentParser.parse(fComment.getRawComment(), docTopics) ;
		
		for (DocTopic t : docTopics) {
			System.out.println("Topic: " + t);
		}
		
		buffer.append(genContent(docTopics)) ;

		if (buffer.length() > 0) {
			HTMLPrinter.insertPageProlog(buffer, 0, getStyleSheet());
			HTMLPrinter.addPageEpilog(buffer);
			
			fLog.debug(ILogLevel.LEVEL_MID, 
					"+------------------------------------------------------------------") ;
			fLog.debug(ILogLevel.LEVEL_MID, 
					"| HTML dump") ;
			fLog.debug(ILogLevel.LEVEL_MID,
					buffer.toString()) ;
			fLog.debug(ILogLevel.LEVEL_MID, 
					"+------------------------------------------------------------------") ;
			fLog.debug(ILogLevel.LEVEL_MID, 
					"+------------------------------------------------------------------") ;
		}
		fContent = buffer.toString();
		System.out.println("Doc Content: " + fContent);

//			SVHoverInformationControlInput ret = new SVHoverInformationControlInput(previousInput, target, buffer.toString(), 0);
//			ret.setContentProvider(SVHoverInformationControlInput.CONTENT_DOC, 
//					new SVHoverContentProvider(buffer.toString()));
//			
//			return ret;		
		// TODO Auto-generated method stub
		return fContent;
	}

	/**
	 * Returns the SVDoc hover style sheet 
	 * @return the updated style sheet
	 */
	private String getStyleSheet() {
		if (fgStyleSheet == null)
			fgStyleSheet= loadStyleSheet();
		String css= fgStyleSheet;
		return css;
	}

	/**
	 * Loads and returns the SVDoc hover style sheet.
	 * @return the style sheet, or <code>null</code> if unable to load
	 */
	private String loadStyleSheet() {
		Bundle bundle= Platform.getBundle(SVUiPlugin.PLUGIN_ID) ;
		URL styleSheetURL= bundle.getEntry("/SVDocHoverStyleSheet.css"); //$NON-NLS-1$
		if (styleSheetURL != null) {
			BufferedReader reader= null;
			try {
				reader= new BufferedReader(new InputStreamReader(styleSheetURL.openStream()));
				StringBuffer buffer= new StringBuffer(1500);
				String line= reader.readLine();
				while (line != null) {
					buffer.append(line);
					buffer.append('\n');
					line= reader.readLine();
				}
				return buffer.toString();
			} catch (IOException ex) {
				fLog.error("Exception while loading style sheet", ex) ;
				return ""; //$NON-NLS-1$
			} finally {
				try {
					if (reader != null)
						reader.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}
	
	private String genContent(List<DocTopic> topics) {
		String res = "" ;
		HTMLFromNDMarkup markupConverter = new HTMLFromNDMarkup() ;
		for(DocTopic topic: topics) {
			String html = "" ;
			html = genContentForTopic(topic) ;
			html = markupConverter.convertNDMarkupToHTML(null, topic, html, HTMLFromNDMarkup.NDMarkupToHTMLStyle.Tooltip) ;
			res += html ;
		}
		return res ;
	}		

	private String genContentForTopic(DocTopic topic) {
		String res = "" ;
		res += "<h4>" ;
		res += topic.getTitle() ;
		res += "</h4>" ;
		res += topic.getBody() ;
		for(DocTopic childTopic: topic.getChildren()) {
			res += genContentForTopic(childTopic) ;
		}
		return res ;
	}


}
