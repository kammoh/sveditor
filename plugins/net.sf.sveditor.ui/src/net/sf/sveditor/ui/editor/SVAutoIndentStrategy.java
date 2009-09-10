package net.sf.sveditor.ui.editor;

import java.util.ArrayList;
import java.util.List;

import net.sf.sveditor.core.parser.SVKeywords;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;

public class SVAutoIndentStrategy extends DefaultIndentLineAutoEditStrategy {
	
	private final String				fParititioning;
	private List<String>				fEndTerminatedKW;
	
	public SVAutoIndentStrategy(String p) {
		fParititioning = p;
		
		fEndTerminatedKW = new ArrayList<String>();
		
		for (String kw : SVKeywords.getKeywords()) {
			if (kw.startsWith("end") && !kw.equals("end")) {
				if (kw.equals("endgroup")) {
					fEndTerminatedKW.add("covergroup");
				} else {
					fEndTerminatedKW.add(kw.substring("end".length()));
				}
			}
		}
		// search in priority order
		fEndTerminatedKW.add("end");
	}
	
	private boolean isLineDelimiter(IDocument document, String text) {
		String[] delimiters= document.getLegalLineDelimiters();
		if (delimiters != null)
			return TextUtilities.equals(delimiters, text) > -1;
			return false;
	}
	
	private boolean containsEndTermKW(String line) {
		int idx;
		
		for (String word : fEndTerminatedKW) {
			if ((idx = line.indexOf(word)) != -1) {
				if ((idx == 0 || Character.isWhitespace(line.charAt(idx-1))) &&
						((line.length() == idx+word.length()) || 
								Character.isWhitespace(line.charAt(idx+word.length())))) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean endsWithEndTermKW(String line) {
		int idx;
		
		for (String word : fEndTerminatedKW) {
			if ((idx = line.indexOf(word)) != -1) {
				idx += word.length();
				
				if (idx == line.length() || Character.isWhitespace(line.charAt(idx))) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private String getIndentString(IDocument doc, IRegion line) throws BadLocationException {
		int ws_lim = line.getOffset();
		while (Character.isWhitespace(doc.getChar(ws_lim))) { ws_lim++; }
		
		return doc.get(line.getOffset(), (ws_lim-line.getOffset()));
	}
	
	private String getMatchingIndent(IDocument doc, int offset) throws BadLocationException {
		int level = 1;
		
		int idx = offset-3;
		
		StringBuilder sb = new StringBuilder();
		while (idx >= 0) {
			while (idx >= 0 && Character.isWhitespace(doc.getChar(idx))) {
				idx--;
			}
			
			if (idx >= 0) {
				int ch = doc.getChar(idx);
				
				if (Character.isJavaIdentifierPart(ch)) {
					sb.setLength(0);
					
					while (Character.isJavaIdentifierPart(doc.getChar(idx))) {
						sb.append((char)doc.getChar(idx));
						idx--;
					}
					
					String id = sb.reverse().toString();
					if (fEndTerminatedKW.contains(id) || id.equals("begin")) {
						level--;
					} else if (SVKeywords.isKeyword(id) && id.startsWith("end")) {
						level++;
					}
					
					if (level == 0) {
						// found place to match
						IRegion line = doc.getLineInformationOfOffset(idx+1);
						
						String indent_ref_str = getIndentString(doc, line);
						
						return indent_ref_str;
					}
				} else {
					idx--;
				}
			}
		}
		
		return "";
	}
	
	private void indentAfterNewLine(IDocument doc, DocumentCommand cmd) {
		StringBuilder sb = new StringBuilder();
		int idx;
		
		try {
			IRegion line = doc.getLineInformationOfOffset(cmd.offset);
			
			// See if the last thing on the line qualifies for an indent
			String line_t = doc.get(line.getOffset(), line.getLength());
			if (line_t.endsWith("begin") || containsEndTermKW(line_t) || line_t.endsWith("{")) {
				// okay, find how much whitespace is at the head of the line and add one tab
				sb.setLength(0);
				
				idx = line.getOffset();
				while (idx < (line.getOffset() + line.getLength()) &&
						Character.isWhitespace(doc.getChar(idx))) { idx++; }
				
				cmd.text = cmd.text + doc.get(line.getOffset(), (idx - line.getOffset())) + "\t";
			} else if (endsWithEndTermKW(line_t)) {
				String ref_indent = getIndentString(doc, line);
				String indent = getMatchingIndent(doc, cmd.offset);
				
				doc.replace(line.getOffset(), ref_indent.length(), indent);
				
				cmd.offset -= (ref_indent.length()-indent.length());
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	private void indentOnKeypress(IDocument doc, DocumentCommand cmd) {
		try {
			IRegion cmd_line = doc.getLineInformationOfOffset(cmd.offset);
			
			if (Character.isWhitespace(cmd.text.charAt(0))) {
				String line_t = doc.get(cmd_line.getOffset(), 
						cmd.offset - cmd_line.getOffset());
				if (endsWithEndTermKW(line_t)) {
					String indent = getMatchingIndent(doc, cmd.offset);
					String ref_indent = getIndentString(doc, cmd_line);

					doc.replace(cmd_line.getOffset(), 
							ref_indent.length(), indent);
					
					cmd.offset -= (ref_indent.length()-indent.length());
				}
			} else if (cmd.text.equals("}")) {
				int level = 1;
				
				int idx = cmd.offset-1;
				
				StringBuilder sb = new StringBuilder();
				while (idx >= 0) {
					while (idx >= 0 && Character.isWhitespace(doc.getChar(idx))) {
						idx--;
					}
					
					if (idx >= 0) {
						int ch = doc.getChar(idx);
						
						if (ch == '{') {
							level--;
						} else if (ch == '}') {
							level++;
						}
						
						if (level == 0) {
							// found place to match
							IRegion line = doc.getLineInformationOfOffset(idx+1);
								
							int indent_ref_idx = line.getOffset();
								
							sb.setLength(0);
							while (Character.isWhitespace(doc.getChar(indent_ref_idx))) {
								sb.append((char)doc.getChar(indent_ref_idx++));
							}
								
							IRegion target_line = doc.getLineInformationOfOffset(cmd.offset-1);
								
							// Determine the end of whitespace for the '}' line
							int ws_lim = target_line.getOffset();
							while (Character.isWhitespace(doc.getChar(ws_lim)) &&
									(ws_lim < (target_line.getOffset()+target_line.getLength()))) { ws_lim++; }
								
							
							if ((ws_lim - target_line.getOffset()) > 0) {
								
								doc.replace(target_line.getOffset(),
										(ws_lim - target_line.getOffset() - 1), sb.toString());
								cmd.offset -= 1;
							}
							break;
						}

						idx--;
					}
				}
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
    public void customizeDocumentCommand(IDocument doc, DocumentCommand cmd) {
    	
    	/*
    	if (cmd.doit == false) {
    		return;
    	}
    	 */
    	if (cmd.length == 0 && cmd.text != null && isLineDelimiter(doc, cmd.text)) {
    		indentAfterNewLine(doc, cmd);
    	} else if (cmd.length == 0) {
    		indentOnKeypress(doc, cmd);
    	}
    }
}
