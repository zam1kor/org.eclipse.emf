package org.eclipse.emf.codegen.ecore.templates.model.tests;

import java.util.*;
import org.eclipse.emf.codegen.ecore.genmodel.*;

public class PackageTestSuite
{
  protected final String NL = System.getProperties().getProperty("line.separator");
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = "/**" + NL + " * <copyright>" + NL + " * </copyright>" + NL + " *" + NL + " * ";
  protected final String TEXT_3 = "Id";
  protected final String TEXT_4 = NL + " */" + NL + "package ";
  protected final String TEXT_5 = ";" + NL;
  protected final String TEXT_6 = NL + NL + "/**" + NL + " * <!-- begin-user-doc -->" + NL + " * A test suite for the '<em><b>";
  protected final String TEXT_7 = "</b></em>' package." + NL + " * <!-- end-user-doc -->" + NL + " * @generated" + NL + " */" + NL + "public class ";
  protected final String TEXT_8 = " extends ";
  protected final String TEXT_9 = NL + "{";
  protected final String TEXT_10 = NL + NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */" + NL + "\tpublic static final ";
  protected final String TEXT_11 = " copyright = \"";
  protected final String TEXT_12 = "\";";
  protected final String TEXT_13 = NL + NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */" + NL + "\tpublic static void main(String[] args)" + NL + "\t{" + NL + "\t\t";
  protected final String TEXT_14 = ".run(suite());" + NL + "\t}" + NL + "" + NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */" + NL + "\tpublic static ";
  protected final String TEXT_15 = " suite()" + NL + "\t{" + NL + "\t\t";
  protected final String TEXT_16 = " suite = new ";
  protected final String TEXT_17 = "(\"";
  protected final String TEXT_18 = " Tests\");";
  protected final String TEXT_19 = NL + "\t\tsuite.addTestSuite(";
  protected final String TEXT_20 = ".class);";
  protected final String TEXT_21 = NL + "\t\treturn suite;" + NL + "\t}" + NL + "" + NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */" + NL + "\tpublic ";
  protected final String TEXT_22 = "(String name)" + NL + "\t{" + NL + "\t\tsuper(name);" + NL + "\t}" + NL + "" + NL + "} //";
  protected final String TEXT_23 = NL;

  public String generate(Object argument)
  {
    StringBuffer stringBuffer = new StringBuffer();
    
/**
 * <copyright>
 *
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 *
 * </copyright>
 */

    GenPackage genPackage = (GenPackage)argument; GenModel genModel = genPackage.getGenModel();
    stringBuffer.append(TEXT_1);
    stringBuffer.append(TEXT_2);
    stringBuffer.append("$");
    stringBuffer.append(TEXT_3);
    stringBuffer.append("$");
    stringBuffer.append(TEXT_4);
    stringBuffer.append(genPackage.getTestsPackageName());
    stringBuffer.append(TEXT_5);
    genModel.markImportLocation(stringBuffer);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(genPackage.getPackageName());
    stringBuffer.append(TEXT_7);
    stringBuffer.append(genPackage.getTestSuiteClassName());
    stringBuffer.append(TEXT_8);
    stringBuffer.append(genModel.getImportedName("junit.framework.TestSuite"));
    stringBuffer.append(TEXT_9);
    if (genModel.getCopyrightText() != null) {
    stringBuffer.append(TEXT_10);
    stringBuffer.append(genModel.getImportedName("java.lang.String"));
    stringBuffer.append(TEXT_11);
    stringBuffer.append(genModel.getCopyrightText());
    stringBuffer.append(TEXT_12);
    stringBuffer.append(genModel.getNonNLS());
    }
    stringBuffer.append(TEXT_13);
    stringBuffer.append(genModel.getImportedName("junit.textui.TestRunner"));
    stringBuffer.append(TEXT_14);
    stringBuffer.append(genModel.getImportedName("junit.framework.Test"));
    stringBuffer.append(TEXT_15);
    stringBuffer.append(genModel.getImportedName("junit.framework.TestSuite"));
    stringBuffer.append(TEXT_16);
    stringBuffer.append(genPackage.getTestSuiteClassName());
    stringBuffer.append(TEXT_17);
    stringBuffer.append(genPackage.getPackageName());
    stringBuffer.append(TEXT_18);
    stringBuffer.append(genModel.getNonNLS());
    for (Iterator genClasses = genPackage.getGenClasses().iterator(); genClasses.hasNext();) { GenClass genClass = (GenClass)genClasses.next();
    if (!genClass.isExternalInterface() && !genClass.isAbstract()) {
    stringBuffer.append(TEXT_19);
    stringBuffer.append(genClass.getImportedTestCaseClassName());
    stringBuffer.append(TEXT_20);
    }
    }
    stringBuffer.append(TEXT_21);
    stringBuffer.append(genPackage.getTestSuiteClassName());
    stringBuffer.append(TEXT_22);
    stringBuffer.append(genPackage.getTestSuiteClassName());
    genModel.emitSortedImports();
    stringBuffer.append(TEXT_23);
    return stringBuffer.toString();
  }
}
