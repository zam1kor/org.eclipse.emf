/**
 * <copyright>
 *
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 *
 * </copyright>
 *
 * $Id: ModelImporterPackagePage.java,v 1.2 2006/04/18 17:01:34 marcelop Exp $
 */
package org.eclipse.emf.importer.ui.contribution.base;


import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.converter.ui.contribution.base.ModelConverterPackagePage;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.importer.ImporterPlugin;
import org.eclipse.emf.importer.ModelImporter;


/**
 * @since 2.2.0
 */
public class ModelImporterPackagePage extends ModelConverterPackagePage implements IModelImporterPage
{
  public ModelImporterPackagePage(ModelImporter modelImporter, String pageName)
  {
    super(modelImporter, pageName);
    setDescription(ImporterPlugin.INSTANCE.getString("_UI_PackageSelection_description"));
  }

  public ModelImporter getModelImporter()
  {
    return (ModelImporter)getModelConverter();
  }

  protected void adjustEPackagesTableViewer(CheckboxTableViewer ePackagesTableViewer)
  {
    super.adjustEPackagesTableViewer(ePackagesTableViewer);
    createEPackageDataColumnTableEditor();
  }

  protected boolean validateEPackageData(EPackage ePackage, String data)
  {
    return validateEcoreModelFileName(data, null);
  }
  
  protected void setEPackageData(EPackage ePackage, String data)
  {
    ModelImporter.EPackageImportInfo ePackageInfo = getModelImporter().getEPackageImportInfo(ePackage);
    ePackageInfo.setEcoreFileName(data);
  }
  
  protected String getEPackageData(EPackage ePackage)
  {
    return getModelImporter().getEPackageImportInfo(ePackage).getEcoreFileName();
  }

  protected String getLabel(EPackage ePackage)
  {
    String result = super.getLabel(ePackage);
    String basePackage = getModelImporter().getEPackageImportInfo(ePackage).getBasePackage();
    if (basePackage != null)
    {
      result = basePackage + "." + result;
    }
    return result;
  }

  protected String getEPackageDataColumnLabel()
  {
    return ImporterPlugin.INSTANCE.getString("_UI_EcoreFileName_label");
  }

  protected void validate()
  {
    super.validate();
    
    if (getErrorMessage() == null)
    {
      List tableCheckedEPackages = getCheckedEPackages();
      for (Iterator i = tableCheckedEPackages.iterator(); i.hasNext();)
      {
        EPackage ePackage = (EPackage)i.next();
        String fileName = getModelImporter().getEPackageImportInfo(ePackage).getEcoreFileName();
        if (!validateEcoreModelFileName(fileName, ePackage.getName()))
        {
          return;
        }
      }

      setErrorMessage(checkEcoreFileNames());
    }
  }
  
  protected String getPackagesLabel()
  {
    return ImporterPlugin.INSTANCE.getString("_UI_RootPackages_label");
  }  

  protected boolean validateEcoreModelFileName(String fileName, String packageName)
  {
    Diagnostic diagnostic = getModelImporter().checkEcoreModelFileName(fileName, packageName);
    handleDiagnostic(diagnostic);
    return diagnostic.getSeverity() == Diagnostic.OK;
  }

  /**
   * Checks the ecore file names and returns null if everything is
   * OK or the error message otherwise.
   * @return String
   */
  protected String checkEcoreFileNames()
  {
    Set fileNames = new HashSet();
    int checkedCount = 0;
    Table table = ePackagesCheckboxTableViewer.getTable();
    TableItem[] tableItems = table.getItems();
    for (int i = 0; i < tableItems.length; i++)
    {
      if (tableItems[i].getChecked())
      {
        checkedCount++;
        fileNames.add(tableItems[i].getText(1));
      }
    }
    return fileNames.size() < checkedCount ? ImporterPlugin.INSTANCE.getString("_UI_DuplicateEcoreNames_message") : null;
  }
}
