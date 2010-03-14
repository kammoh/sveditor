package net.sf.sveditor.core.tests.content_assist;

import java.io.InputStream;

import net.sf.sveditor.core.db.SVDBFile;
import net.sf.sveditor.core.db.index.AbstractSVDBIndex;
import net.sf.sveditor.core.db.index.ISVDBIndexChangeListener;
import net.sf.sveditor.core.db.search.SVDBSearchResult;

public class ContentAssistIndex extends AbstractSVDBIndex {
	
	public ContentAssistIndex() {
		super("GLOBAL");
	}
	
	public void setFile(SVDBFile file) {
		fIndexFileMap.remove(file.getName());
		fIndexFileMap.put(file.getName(), file);
	}

	@Override
	protected void buildIndex() {
		fIndexFileMapValid = true;
	}

	@Override
	protected void buildPreProcFileMap() {
		fPreProcFileMapValid = true;
	}

	@Override
	protected boolean isLoadUpToDate() {
		return true;
	}

	public void addChangeListener(ISVDBIndexChangeListener l) {}

	public String getBaseLocation() {
		return "";
	}

	public String getTypeID() {
		return "ContentAssistIndex";
	}
	
	public String getTypeName() {
		return "";
	}

	public void rebuildIndex() {}

	public void removeChangeListener(ISVDBIndexChangeListener l) {}

	public SVDBFile parse(InputStream in, String path) {
		return null;
	}

	public SVDBSearchResult<SVDBFile> findIncludedFile(String leaf) {
		return null;
	}

}