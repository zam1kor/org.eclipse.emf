/**
 * <copyright>
 *
 * Copyright (c) 2002-2004 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 *
 * </copyright>
 *
 * $Id: JETCompileTemplateOperation.java,v 1.3 2005/04/18 12:07:09 emerks Exp $
 */
package org.eclipse.emf.codegen.jet;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;

import org.eclipse.emf.codegen.CodeGenPlugin;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.URI;


public class JETCompileTemplateOperation implements IWorkspaceRunnable 
{
  protected static final String JET_EXTENSION = "jet";

  protected IProject project;
  protected Collection containers;
  protected List files = new ArrayList();
  protected boolean inBuild;

  /**
   * Creates an instance given the collection of resources.
   */
  public JETCompileTemplateOperation(IProject project, Collection containers) throws CoreException
  {
    this.project = project;
    this.containers = containers;
    for (Iterator i = containers.iterator(); i.hasNext(); )
    {
      Object container = i.next();
      if (container instanceof IContainer)
      {
        consider((IContainer)container);
      }
      else 
      {
        consider(container.toString());
      }
    }
  }

  /**
   * Creates an instance given the collection of resources.
   */
  public JETCompileTemplateOperation(IProject project, Collection containers, Collection resources) throws CoreException
  {
    super();

    this.project = project;
    this.containers = containers;
    for (Iterator i = resources.iterator(); i.hasNext(); )
    {
      Object object = i.next();
      if (object instanceof IFile)
      {
        IFile file = (IFile)object;
        for (IContainer container = file.getParent(); container != null; container = container.getParent())
        {
          if (containers.contains(container))
          {
            consider(file);
            break;
          }
        }
      }
      else if (object instanceof IContainer) 
      {
        for (IContainer container = (IContainer)object; container != null; container = container.getParent())
        {
          if (containers.contains(container))
          {
            consider(container);
            break;
          }
        }
      }
    }
  }
  
  /**
   * Returns true if there are files to compile.
   */
  public boolean shouldCompile()
  {
    return !files.isEmpty();
  }

  /**
   * Adds the URI.
   */
  protected void consider(String uri) 
  {
    URI baseURI = URI.createURI(uri);
    URI localURI = CommonPlugin.asLocalURI(baseURI);
    if (localURI.isFile() && !localURI.isRelative())
    {
      File file = new File(localURI.toFileString());
      if (file.isDirectory() && !uri.endsWith("/"))
      {
        baseURI = URI.createURI(uri + "/");
      }
      consider(baseURI, localURI, new File(localURI.toFileString()));
    }
  }

  protected void consider(URI baseURI, URI localURI, File file) 
  {
    if (file.isDirectory())
    {
      File [] files = file.listFiles();
      for (int i = 0; i < files.length; ++i)
      {
        consider(baseURI, localURI, files[i]);
      }
    }
    else if (file.isFile() && file.getName().endsWith(JET_EXTENSION) && file.getName().indexOf('.') != -1)
    {
      files.add(URI.createFileURI(file.getAbsolutePath()).deresolve(localURI).resolve(baseURI));
    }
  }

  /**
   * Adds the file to {@link #files} the file ends with the {@link #JET_EXTENSION} extension.
   */
  protected void consider(IFile file) 
  {
    if (file.getFileExtension() != null && file.getFileExtension().endsWith(JET_EXTENSION)) 
    {
      files.add(file);
    }
  }

  /**
   * Considers all the files of a container and all it's subcontainer.
   */
  protected void consider(IContainer container) throws CoreException 
  {
    IResource[] children = container.members();
    if (children != null) 
    {
      for (int i = 0; i < children.length; ++i) 
      {
        IResource resource = children[i];
        if (resource instanceof IFile) 
        {
          consider((IFile)resource);
        }
        else if (resource instanceof IContainer) 
        {
          consider((IContainer)resource);
        }
      }
    }
  }

  /**
   */
  public void run(IProgressMonitor progressMonitor) throws CoreException 
  {
    try 
    {
      progressMonitor.beginTask("", 3 * files.size());
      progressMonitor.subTask(CodeGenPlugin.getPlugin().getString("_UI_JETCompilingTemplates_message"));

      IWorkspaceRoot workspaceRoot =  ResourcesPlugin.getWorkspace().getRoot();
      Collection jetProjects = new HashSet();

      HashSet visitedRelativePaths = new HashSet();
      for (Iterator i = files.iterator(); i.hasNext(); )
      {
        Object file = i.next();
        String fileName = file instanceof IFile ? ((IFile)file).getName() : file.toString();

        progressMonitor.subTask(CodeGenPlugin.getPlugin().getString("_UI_JETCompile_message", new Object [] { fileName }));

        JETNature nature = JETNature.getRuntime(project);
        IContainer directory = nature.getJavaSourceContainer();

        IPath filePath = file instanceof IFile ? ((IFile)file).getFullPath() : new Path(file.toString());
        List templateContainers = nature.getTemplateContainers();
        List templateSourceContainers = nature.getTemplateSourceContainers();

        String [] containerLocations = new String [templateContainers.size()];
        for (ListIterator j = templateContainers.listIterator(); j.hasNext(); )
        {
          Object container = j.next();
          if (container instanceof IContainer)
          {
            containerLocations[j.previousIndex()] = "platform:/resource" + ((IContainer)container).getFullPath();
          }
          else
          {
            containerLocations[j.previousIndex()] = container.toString();
          }
        }

        for (Iterator j = templateSourceContainers.iterator(); j.hasNext(); )
        {
          Object container = j.next();
          IPath containerPath = container instanceof IContainer ? ((IContainer)container).getFullPath() : new Path(container.toString());
          if (containerPath.isPrefixOf(filePath))
          {
            String relativePath = filePath.removeFirstSegments(containerPath.segmentCount()).setDevice(null).toString();
            if (visitedRelativePaths.add(relativePath))
            {
              JETCompiler compiler =  new JETCompiler(containerLocations, relativePath);
              compiler.parse();
  
              ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
              compiler.generate(arrayOutputStream);
  
              JETSkeleton skeleton = compiler.getSkeleton();
              if (skeleton.getClassName().equals("")) 
              {
                skeleton.setClassName(fileName.substring(0, fileName.indexOf('.')));
              }
              if (skeleton.getPackageName() != null) 
              {
                directory = getPackageContainer(directory, skeleton.getPackageName(), new SubProgressMonitor(progressMonitor, 1));
              }
              else
              {
                progressMonitor.worked(1);
              }
  
              IFile outputFile = 
                workspaceRoot.getFile(directory.getFullPath().append(skeleton.getClassName() + ".java"));
  
              progressMonitor.subTask(CodeGenPlugin.getPlugin().getString("_UI_JETUpdate_message", new Object [] { fileName }));
  
              if (!outputFile.exists()) 
              {
                outputFile.create(new ByteArrayInputStream(arrayOutputStream.toByteArray()), true, progressMonitor);
              } 
              else 
              {
                boolean changed = true;
                byte [] newBytes = arrayOutputStream.toByteArray();
                try
                {
                  InputStream inputStream = outputFile.getContents();
                  byte [] oldBytes =  new byte[inputStream.available()];
                  inputStream.read(oldBytes);
                  inputStream.close();
                  changed = !Arrays.equals(oldBytes, newBytes);
                }
                catch (IOException exception) 
                {
                }
  
                if (changed)
                {
                  outputFile.setContents(new ByteArrayInputStream(arrayOutputStream.toByteArray()), true, true, progressMonitor);
                }
              }
  
              jetProjects.add(outputFile.getProject());
  
              progressMonitor.worked(1);
  
              break;
            }
          }
        }
      }

      // Kick of a Java build if not already in a build.
      //
      if (!isInBuild()) 
      {
        for (Iterator projects = jetProjects.iterator(); projects.hasNext(); ) 
        {
          IProject project = (IProject) projects.next();
          progressMonitor.subTask
            (CodeGenPlugin.getPlugin().getString("_UI_JETJavaCompileProject_message", new Object [] { project.getFullPath() }));
          project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new SubProgressMonitor(progressMonitor, 1));
        }
      }
    }
    finally
    {
      progressMonitor.done();
    }
  }

  protected IContainer getPackageContainer(IContainer root, String packagename, IProgressMonitor monitor) throws CoreException 
  {
    for (StringTokenizer stringTokenizer = new StringTokenizer(packagename, "."); stringTokenizer.hasMoreTokens(); ) 
    {
      IFolder newContainer = root.getFolder(new Path(stringTokenizer.nextToken()));
      if (!newContainer.exists()) 
      {
        newContainer.create(true, true, monitor);
      }
      root = newContainer;
    }

    return root;
  }

  public boolean isInBuild() 
  {
    return inBuild;
  }

  public void setInBuild(boolean build) 
  {
    inBuild = build;
  }
}
