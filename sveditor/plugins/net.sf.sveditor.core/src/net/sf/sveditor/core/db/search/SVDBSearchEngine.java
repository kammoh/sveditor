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


package net.sf.sveditor.core.db.search;

import java.util.ArrayList;
import java.util.List;

import net.sf.sveditor.core.db.ISVDBChildItem;
import net.sf.sveditor.core.db.ISVDBChildParent;
import net.sf.sveditor.core.db.ISVDBItemBase;
import net.sf.sveditor.core.db.SVDBItem;
import net.sf.sveditor.core.db.SVDBItemType;
import net.sf.sveditor.core.db.index.ISVDBIndexIterator;
import net.sf.sveditor.core.db.index.SVDBDeclCacheItem;
import net.sf.sveditor.core.db.stmt.SVDBTypedefStmt;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * 
 * @author ballance
 *
 * Searches through the database 
 */
public class SVDBSearchEngine {
	private ISVDBIndexIterator					fSearchContext;
	private SVDBSearchSpecification				fSearchSpec;
	private IProgressMonitor					fProgressMonitor;
	
	public SVDBSearchEngine(ISVDBIndexIterator search_ctxt) {
		fSearchContext = search_ctxt;
	}

	/**
	 * Searches for elements based on the search specification. Returns a list of 
	 * matching elements. The returned list contains ISVDBItemBase and/or 
	 * SVDBDeclCacheItem elements.
	 * 
	 * @param spec
	 * @param monitor
	 * @return
	 */
	public synchronized List<Object> find(SVDBSearchSpecification spec, IProgressMonitor monitor) {
		List<Object> ret = new ArrayList<Object>();
		
		fProgressMonitor = monitor;
		
		fSearchSpec = spec;
		
		switch (spec.getSearchType()) {
			case Package:
				if (spec.getSearchUsage() == SVDBSearchUsage.Declaration ||
						spec.getSearchUsage() == SVDBSearchUsage.All) {
					find_package_decl(ret);
				}
				if (spec.getSearchUsage() == SVDBSearchUsage.Reference ||
						spec.getSearchUsage() == SVDBSearchUsage.All) {
					find_package_refs(ret);
				}
				break;
				
			case Method:
				if (spec.getSearchUsage() == SVDBSearchUsage.Declaration ||
						spec.getSearchUsage() == SVDBSearchUsage.All) {
					find_method_decl(ret);
				}
				if (spec.getSearchUsage() == SVDBSearchUsage.Reference ||
						spec.getSearchUsage() == SVDBSearchUsage.All) {
					find_method_refs(ret);
				}
				break;
				
			case Type:
				if (spec.getSearchUsage() == SVDBSearchUsage.Declaration ||
						spec.getSearchUsage() == SVDBSearchUsage.All) {
					find_type_decl(ret);
				}
				if (spec.getSearchUsage() == SVDBSearchUsage.Reference ||
						spec.getSearchUsage() == SVDBSearchUsage.All) {
					find_type_refs(ret);
				}
				break;
				
			case Field:
				if (spec.getSearchUsage() == SVDBSearchUsage.Declaration ||
						spec.getSearchUsage() == SVDBSearchUsage.All) {
					find_field_decl(ret);
				}
				if (spec.getSearchUsage() == SVDBSearchUsage.Reference ||
						spec.getSearchUsage() == SVDBSearchUsage.All) {
					find_field_refs(ret);
				}
				break;
		}

		return ret;
	}

	private void find_package_decl(List<Object> items) {
		List<SVDBDeclCacheItem> found = fSearchContext.findGlobalScopeDecl(
				new NullProgressMonitor(), 
				null, new SVDBFindByTypeMatcher(SVDBItemType.PackageDecl));

		for (SVDBDeclCacheItem it : found) {
			items.add(it);
		}
	}
	
	private void find_package_refs(List<Object> items) {
//		ISVDBItemIterator iterator = fSearchContext.getItemIterator(fProgressMonitor);

		System.out.println("[ERROR] find_package_refs not supported");
	}
	
	private void find_type_decl(List<Object> items) {
		List<SVDBDeclCacheItem> found;
		
		if (!fSearchSpec.isRegExp()) {
			found = fSearchContext.findGlobalScopeDecl(
					new NullProgressMonitor(), 
					fSearchSpec.getExpr(), 
					new SVDBFindByNameMatcher(SVDBItemType.ClassDecl, 
						SVDBItemType.TypedefStmt, SVDBItemType.ModuleDecl));
			
			for (SVDBDeclCacheItem it : found) {
				if (it.getType() == SVDBItemType.TypedefStmt) {
					ISVDBItemBase item = it.getSVDBItem();
					SVDBTypedefStmt td = (SVDBTypedefStmt)item;
					if (td.getTypeInfo().getType() == SVDBItemType.TypeInfoStruct) {
						continue;
					}
				} else {
					items.add(it);
				}
			}
		} else {
			found = fSearchContext.findGlobalScopeDecl(
					new NullProgressMonitor(), 
					null,
					new SVDBFindByTypeMatcher(SVDBItemType.ClassDecl, 
						SVDBItemType.TypedefStmt, SVDBItemType.ModuleDecl));
			
			for (SVDBDeclCacheItem it : found) {
				if (fSearchSpec.match(it.getName())) {
					
					if (it.getType() == SVDBItemType.TypedefStmt) {
						ISVDBItemBase item = it.getSVDBItem();
						if (item == null) {
							continue;
						}
						SVDBTypedefStmt td = (SVDBTypedefStmt)item;
						if (td.getTypeInfo().getType() == SVDBItemType.TypeInfoStruct) {
							continue;
						}
					}
					items.add(it);
				}
			}			
		}
	}
	
	private void find_type_refs(List<Object> items) {
		/** TODO:
		ISVDBItemIterator iterator = fSearchContext.getItemIterator(fProgressMonitor);
		SVDBItemType types[] = new SVDBItemType[] {SVDBItemType.VarDeclStmt, SVDBItemType.ModIfcInst}; 
		
		while (iterator.hasNext(types)) {
			ISVDBItemBase item = iterator.nextItem(types);
			String match_name = "";
			if (item.getType() == SVDBItemType.VarDeclStmt) {
				SVDBVarDeclStmt decl = (SVDBVarDeclStmt)item;
				match_name = decl.getTypeInfo().getName();
			} else if (item.getType() == SVDBItemType.ModIfcInst) {
				SVDBModIfcInst inst = (SVDBModIfcInst)item;
				
				match_name = inst.getTypeName(); 
			}
			if (fSearchSpec.match(match_name)) {
				items.add(item);
			}
		}
		 */
	}
	
	private void find_method_decl(List<Object> items) {
		List<SVDBDeclCacheItem>	method_scopes = fSearchContext.findGlobalScopeDecl(
					new NullProgressMonitor(), null,
					new SVDBFindByTypeMatcher(
							SVDBItemType.ClassDecl, 
							SVDBItemType.ModuleDecl,
							SVDBItemType.InterfaceDecl,
							SVDBItemType.ProgramDecl,
							SVDBItemType.PackageDecl));
	
		for (SVDBDeclCacheItem scope : method_scopes) {
			ISVDBItemBase it_b = scope.getSVDBItem();
			if (it_b == null) {
				continue;
			}
			
			ISVDBChildParent cp = (ISVDBChildParent)it_b;
			for (ISVDBChildItem it : cp.getChildren()) {
				if (it.getType() == SVDBItemType.Task ||
						it.getType() == SVDBItemType.Function) {
					if (fSearchSpec.match(SVDBItem.getName(it))) {
						items.add(it);
					}
				}
			}
		}
	}
	
	private void find_method_refs(List<Object> items) {
		
	}
	
	private void find_field_decl(List<Object> items) {
		/** TODO:
		ISVDBItemIterator iterator = fSearchContext.getItemIterator(fProgressMonitor);
		SVDBItemType types[] = new SVDBItemType[] {SVDBItemType.VarDeclStmt, SVDBItemType.ModIfcInst};

		while (iterator.hasNext(types)) {
			ISVDBItemBase item = iterator.nextItem();
			String name = SVDBItem.getName(item);
			
			if (fSearchSpec.match(name)) {
				items.add(item);
			}
		}
		 */
	}
	
	private void find_field_refs(List<Object> items) {
		
	}
}
