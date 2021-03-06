/****************************************************************************
 * Copyright (c) 2008-2014 Matthew Ballance and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Ballance - initial implementation
 ****************************************************************************/


package net.sf.sveditor.core.db;

import java.io.File;

import net.sf.sveditor.core.db.attr.SVDBDoNotSaveAttr;
import net.sf.sveditor.core.db.index.ISVDBIndex;

public class SVDBFile extends SVDBScopeItem {
	@SVDBDoNotSaveAttr
	public ISVDBIndex					fIndex;
	public String						fFile;
	
	public SVDBFile() {
		super("", SVDBItemType.File);
		fFile = "";
	}
	
	public SVDBFile(String file) {
		super(file, SVDBItemType.File);
		if (file != null) {
			setName(new File(file).getName());
		} else {
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		fFile               = file;
		setLocation(new SVDBLocation(-1, -1, -1));
	}
	
	public void setIndex(ISVDBIndex index) {
		fIndex = index;
	}
	
	public ISVDBIndex getIndex() {
		return fIndex;
	}

	public String getFilePath() {
		return fFile;
	}
	
	public void setFilePath(String file) {
		if (file == null) {
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		fFile = file;
	}
	
	public void clearChildren() {
		fItems.clear();
	}
}
