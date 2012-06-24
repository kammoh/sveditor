/****************************************************************************
 * Copyright (c) 2008-2010 Matthew Ballance and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Ballance - initial implementation
 *     Armond Paiva - repurposed from UI for use in HTML doc generation
 ****************************************************************************/


package net.sf.sveditor.core.docs.html;

import java.util.HashMap;
import java.util.Map;

import net.sf.sveditor.core.db.IFieldItemAttr;
import net.sf.sveditor.core.db.ISVDBItemBase;
import net.sf.sveditor.core.db.SVDBItemType;
import net.sf.sveditor.core.db.stmt.SVDBStmt;
import net.sf.sveditor.core.db.stmt.SVDBTypedefStmt;
import net.sf.sveditor.core.db.stmt.SVDBVarDeclItem;
import net.sf.sveditor.core.db.stmt.SVDBVarDeclStmt;
import net.sf.sveditor.core.docs.model.DocItem;
import net.sf.sveditor.core.docs.model.DocItemType;
import net.sf.sveditor.core.log.LogFactory;
import net.sf.sveditor.core.log.LogHandle;

public class HTMLIconUtils implements IHTMLIcons {
	
	private static final Map<DocItemType, String>	fImgDescMap ;
	
	static {
		fImgDescMap = new HashMap<DocItemType, String>();

//		fImgDescMap.put(DocItemType.File, FILE_OBJ);
//		fImgDescMap.put(DocItemType.ModuleDecl, MODULE_OBJ);
//		fImgDescMap.put(DocItemType.InterfaceDecl, INT_OBJ);
//		fImgDescMap.put(DocItemType.ConfigDecl, CONFIG_OBJ);
		fImgDescMap.put(DocItemType.ClassDoc, CLASS_OBJ);
//		fImgDescMap.put(DocItemType.MacroDef, DEFINE_OBJ);
//		fImgDescMap.put(DocItemType.Include, INCLUDE_OBJ);
		fImgDescMap.put(DocItemType.PackageDoc, PACKAGE_OBJ);
//		fImgDescMap.put(DocItemType.TypeInfoStruct, STRUCT_OBJ);
//		fImgDescMap.put(DocItemType.Covergroup, COVERGROUP_OBJ);
//		fImgDescMap.put(DocItemType.Coverpoint, COVERPOINT_OBJ);
//		fImgDescMap.put(DocItemType.CoverpointCross, COVERPOINT_CROSS_OBJ);
//		fImgDescMap.put(DocItemType.Sequence, SEQUENCE_OBJ);
//		fImgDescMap.put(DocItemType.Property, PROPERTY_OBJ);
//		fImgDescMap.put(DocItemType.Constraint, CONSTRAINT_OBJ);
//		fImgDescMap.put(DocItemType.AlwaysStmt, ALWAYS_BLOCK_OBJ);
//		fImgDescMap.put(DocItemType.InitialStmt, INITIAL_OBJ);
//		fImgDescMap.put(DocItemType.Assign, ASSIGN_OBJ);
//		fImgDescMap.put(DocItemType.GenerateBlock, GENERATE_OBJ);
//		fImgDescMap.put(DocItemType.ClockingBlock, CLOCKING_OBJ);
//		fImgDescMap.put(DocItemType.ImportItem, IMPORT_OBJ);
//		fImgDescMap.put(DocItemType.ModIfcInst, MOD_IFC_INST_OBJ);
//		fImgDescMap.put(DocItemType.ModIfcInstItem, MOD_IFC_INST_OBJ);
//		fImgDescMap.put(DocItemType.VarDeclItem, FIELD_PUB_OBJ);
		fImgDescMap.put(DocItemType.TaskDoc, TASK_PUB_OBJ);
		fImgDescMap.put(DocItemType.FuncDoc, TASK_PUB_OBJ); // FIXME: image for func?
	}
	
//	public static Image getIcon(String key) {
//		return SVUiPlugin.getImage(key);
//	}
//	
//	public static Image getIcon(SVDBItemType type) {
//		if (fImgDescMap.containsKey(type))  {
//			return SVUiPlugin.getImage(fImgDescMap.get(type)); 
//		}
//		return null;
//	}
	
	private static LogHandle log ;
	
	public static LogHandle getLog() {
		if(log == null) {
			log = LogFactory.getLogHandle("HTMLIconUtils") ;
		}
		return log ;
	}
	
	@SuppressWarnings("unused")
	public static String getImagePath(DocItem docItem) {
		if(false){
//		if (docItemType == SVDBItemType.VarDeclDoc) {
//			SVDBVarDeclItem decl = (SVDBVarDeclItem)docItemType;
//			SVDBVarDeclStmt decl_p = decl.getParent();
//			
//			if (decl_p == null) {
//				HTMLIconUtils.getLog().error("Parent of " + decl.getName() + " @ " + decl.getLocation().getLine() + " is NULL") ;
//			}
//			int attr = decl_p.getAttr();
//			if (decl_p.getParent() != null && 
//					(decl_p.getParent().getType() == SVDBItemType.Task ||
//							decl_p.getParent().getType() == SVDBItemType.Function)) {
//				return LOCAL_OBJ ;
//			} else {
//				if ((attr & IFieldItemAttr.FieldAttr_Local) != 0) {
//					return FIELD_PRIV_OBJ ;
//				} else if ((attr & IFieldItemAttr.FieldAttr_Protected) != 0) {
//					return FIELD_PROT_OBJ ;
//				} else {
//					return FIELD_PUB_OBJ ;
//				}
//			}
//		} else if (docItemType instanceof IFieldItemAttr) {
//			int            attr = ((IFieldItemAttr)docItemType).getAttr();
//			SVDBItemType   type = docItemType.getType();
//			
//			if (type == SVDBItemType.ModIfcInstItem) {
//				return MOD_IFC_INST_OBJ ;
//			} else if (type == SVDBItemType.Task || 
//					type == SVDBItemType.Function) {
//				if ((attr & IFieldItemAttr.FieldAttr_Local) != 0) {
//					return TASK_PRIV_OBJ ;
//				} else if ((attr & IFieldItemAttr.FieldAttr_Protected) != 0) {
//					return TASK_PROT_OBJ ;
//				} else {
//					return TASK_PUB_OBJ ;
//				}
//			} else if (SVDBStmt.isType(docItemType, SVDBItemType.ParamPortDecl)) {
//				return LOCAL_OBJ ;
//			}
		} else { 
			DocItemType type = docItem.getType() ; 
			if (fImgDescMap.containsKey(type)) {
				return fImgDescMap.get(type) ;
			}
		}
		
		return null;
	}
	
//    public static ImageDescriptor getImageDescriptor(SVDBItemType it) {
//		if (fImgDescMap.containsKey(it))  {
//			return SVUiPlugin.getImageDescriptor(fImgDescMap.get(it));
//		}
//		
//		return null;
//    }
}
