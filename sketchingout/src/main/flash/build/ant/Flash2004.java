/*
 * The Apache Software License, Version 1.1
 * 
 * Copyright (c) 2000-2002 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  3. The end-user documentation included with the redistribution, if any,
 * must include the following acknowlegement: "This product includes software
 * developed by the Apache Software Foundation (http://www.apache.org/)."
 * Alternately, this acknowlegement may appear in the software itself, if and
 * wherever such third-party acknowlegements normally appear.
 *  4. The names "The Jakarta Project", "Ant", and "Apache Software Foundation"
 * must not be used to endorse or promote products derived from this software
 * without prior written permission. For written permission, please contact
 * apache@apache.org.
 *  5. Products derived from this software may not be called "Apache" nor may
 * "Apache" appear in their names without prior written permission of the
 * Apache Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the Apache Software Foundation. For more information on the
 * Apache Software Foundation, please see <http://www.apache.org/> .
 */

import org.apache.tools.ant.taskdefs.*;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FlatFileNameMapper;
import org.apache.tools.ant.util.GlobPatternMapper;
import org.apache.tools.ant.util.SourceFileScanner;
import org.apache.tools.ant.taskdefs.ExecuteOn;

import java.io.*;
import java.util.Date;
import java.util.Vector;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.TreeMap;
import java.net.URL;

/**
 * Using an external compiler, builds Flash FLA files into their compiled SWF
 * files, and copies the SWF files, preserving file system layout, to the
 * output directory.
 * 
 * To build: copy this task implementation to org/apache/tools/ant/taskdefs and
 * build it into your ant.jar. Add the <taskdef>tag to your build.xml to link
 * the task to this implementation.
 * 
 * <p>
 * This implementation is based on the copy task.
 * </p>
 * 
 * @author Ethan Malasky <a href="mailto:emalasky@macromedia.com">
 *         emalasky@macromedia.com</a>
 * 
 * @version $Revision$
 * 
 * @since Ant 1.5.1+
 * 
 * @ant.task category="filesystem"
 */
public class Flash2004 extends Task
{
	protected File file = null; // the source file
	protected File destFile = null; // the destination file
	protected File destDir = null; // the destination directory
	protected Vector filesets = new Vector();

	protected boolean filtering = false;
	protected boolean preserveLastModified = false;
	protected boolean forceOverwrite = false;
	protected boolean flatten = false;
	protected int verbosity = Project.MSG_VERBOSE;
	protected boolean includeEmpty = true;
	private boolean failonerror = true;
	private boolean omitTraceActions = false;
	private boolean quitAuthoring = false;
	protected Map fileCopyMap = new TreeMap();
	protected Hashtable dirCopyMap = new Hashtable();

	protected Mapper mapperElement = null;
	private Vector filterChains = new Vector();
	private Vector filterSets = new Vector();
	private FileUtils fileUtils;
	private String encoding = null;

	protected String compiler = null;
	protected File buildDir = null;
	protected File outputDir = null;
	protected Long timeout = null;
	protected File output = new File("MX2004_ErrorOutput");

	protected File cfgFile = null;
	protected FileWriter cfgWriter = null;
	protected String cfgFilePath = null;
	
	protected File theLastSWF = null;	// aral: holds reference to last swf compiled
	protected Sleep sleepTask = null; 	// aral: sleep task
	protected int numSWFs = 0;
	

	/**
	 * Copy task constructor.
	 */
	public Flash2004()
	{
		fileUtils = FileUtils.newFileUtils();
	}

	public void setQuitAuthoring(boolean quit)
	{
		this.quitAuthoring = quit;
	}

	public void setCompiler(String compiler)
	{
		this.compiler = compiler;
	}
	public void setOmitTraceActions(boolean omit)
	{
		this.omitTraceActions = omit;
	}
	public void setBuildDir(File file)
	{
		this.buildDir = file;
	}
	public void setOutputDir(File file)
	{
		this.outputDir = file;
	}
	public void setTimeout(Long timeout)
	{
		this.timeout = timeout;
	}
	public void setOutput(File output)
	{
		this.output = output;
	}
	
	protected FileUtils getFileUtils()
	{
		return fileUtils;
	}

	/**
	 * Sets a single source file to copy.
	 */
	public void setFile(File file)
	{
		this.file = file;
	}

	/**
	 * Sets the destination directory.
	 */
	public void setTodir(File destDir)
	{
		this.destDir = destDir;
	}

	/**
	 * Give the copied files the same last modified time as the original files.
	 * 
	 * @deprecated setPreserveLastModified(String) has been deprecated and
	 *             replaced with setPreserveLastModified(boolean) to
	 *             consistently let the Introspection mechanism work.
	 */
	public void setPreserveLastModified(String preserve)
	{
		setPreserveLastModified(Project.toBoolean(preserve));
	}

	/**
	 * Give the copied files the same last modified time as the original files.
	 */
	public void setPreserveLastModified(boolean preserve)
	{
		preserveLastModified = preserve;
	}

	/**
	 * Whether to give the copied files the same last modified time as the
	 * original files.
	 * 
	 * @since 1.32, Ant 1.5
	 */
	public boolean getPreserveLastModified()
	{
		return preserveLastModified;
	}

	/**
	 * Get the filtersets being applied to this operation.
	 * 
	 * @return a vector of FilterSet objects
	 */
	protected Vector getFilterSets()
	{
		return filterSets;
	}

	/**
	 * Get the filterchains being applied to this operation.
	 * 
	 * @return a vector of FilterChain objects
	 */
	protected Vector getFilterChains()
	{
		return filterChains;
	}

	/**
	 * If true, enables filtering.
	 */
	public void setFiltering(boolean filtering)
	{
		this.filtering = filtering;
	}

	/**
	 * Overwrite any existing destination file(s).
	 */
	public void setOverwrite(boolean overwrite)
	{
		this.forceOverwrite = overwrite;
	}

	/**
	 * When copying directory trees, the files can be "flattened" into a single
	 * directory. If there are multiple files with the same name in the source
	 * directory tree, only the first file will be copied into the "flattened"
	 * directory, unless the forceoverwrite attribute is true.
	 */
	public void setFlatten(boolean flatten)
	{
		this.flatten = flatten;
	}

	/**
	 * Used to force listing of all names of copied files.
	 */
	public void setVerbose(boolean verbose)
	{
		if (verbose)
		{
			this.verbosity = Project.MSG_INFO;
		}
		else
		{
			this.verbosity = Project.MSG_VERBOSE;
		}
	}

	/**
	 * Used to copy empty directories.
	 */
	public void setIncludeEmptyDirs(boolean includeEmpty)
	{
		this.includeEmpty = includeEmpty;
	}

	/**
	 * If false, note errors to the output but keep going.
	 * 
	 * @param failonerror
	 *            true or false
	 */
	public void setFailOnError(boolean failonerror)
	{
		this.failonerror = failonerror;
	}

	/**
	 * Adds a set of files to copy.
	 */
	public void addFileset(FileSet set)
	{
		filesets.addElement(set);
	}

	/**
	 * Defines the mapper to map source to destination files.
	 */
	public Mapper createMapper() throws BuildException
	{
		if (mapperElement != null)
		{
			throw new BuildException(
				"Cannot define more than one mapper",
				location);
		}
		mapperElement = new Mapper(project);
		return mapperElement;
	}

	/**
	 * Sets the character encoding
	 * 
	 * @since 1.32, Ant 1.5
	 */
	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}

	/**
	 * @return the character encoding, <code>null</code> if not set.
	 * 
	 * @since 1.32, Ant 1.5
	 */
	public String getEncoding()
	{
		return encoding;
	}

	/**
	 * Property to pass to the invoked target.
	 */
	//     public Property createParam() {
	//         if (callee == null) {
	//             init();
	//         }
	//         return callee.createProperty();
	//     }

	/**
	 * Performs the copy operation.
	 */
	public void execute() throws BuildException
	{
		File savedFile = file; // may be altered in validateAttributes
		File savedDestFile = destFile;
		File savedDestDir = destDir;
		FileSet savedFileSet = null;
		if (file == null && destFile != null && filesets.size() == 1)
		{
			// will be removed in validateAttributes
			savedFileSet = (FileSet) filesets.elementAt(0);
		}

		// make sure we don't have an illegal set of options
		validateAttributes();

		try
		{

			// deal with the single file
			if (file != null)
			{
				if (file.exists())
				{
					if (destFile == null)
					{
						destFile = new File(destDir, file.getName());
					}

					if (forceOverwrite
						|| (file.lastModified() > destFile.lastModified()))
					{
						fileCopyMap.put(
							file.getAbsolutePath(),
							destFile.getAbsolutePath());
					}
					else
					{
						log(
							file
								+ " omitted as "
								+ destFile
								+ " is up to date.",
							Project.MSG_VERBOSE);
					}
				}
				else
				{
					String message =
						"Warning: Could not find file "
							+ file.getAbsolutePath()
							+ " to copy.";
					if (!failonerror)
					{
						log(message);
					}
					else
					{
						throw new BuildException(message);
					}
				}
			}

			// deal with the filesets
			for (int i = 0; i < filesets.size(); i++)
			{
				FileSet fs = (FileSet) filesets.elementAt(i);
				DirectoryScanner ds = fs.getDirectoryScanner(project);
				File fromDir = fs.getDir(project);

				String[] srcFiles = ds.getIncludedFiles();
				String[] srcDirs = ds.getIncludedDirectories();
				scan(fromDir, destDir, srcFiles, srcDirs);
			}

			// do all the copy operations now...
			doFileOperations();
		}
		finally
		{
			// clean up again, so this instance can be used a second
			// time
			file = savedFile;
			destFile = savedDestFile;
			destDir = savedDestDir;
			if (savedFileSet != null)
			{
				filesets.insertElementAt(savedFileSet, 0);
			}

			fileCopyMap.clear();
			dirCopyMap.clear();
		}
	}

	//************************************************************************
	//  protected and private methods
	//************************************************************************

	/**
	 * Ensure we have a consistent and legal set of attributes, and set any
	 * internal flags necessary based on different combinations of attributes.
	 */
	protected void validateAttributes() throws BuildException
	{
		if (compiler == null)
		{
			throw new BuildException("compiler must be set.");
		}

		if (outputDir == null)
		{
			throw new BuildException("outputDir must be set.");
		}

		if (buildDir == null)
		{
			throw new BuildException("buildDir must be set.");
		}

		if (file == null && filesets.size() == 0)
		{
			throw new BuildException(
				"Specify at least one source " + "- a file or a fileset.");
		}

	}

	/**
	 * Compares source files to destination files to see if they should be
	 * copied.
	 */
	protected void scan(
		File fromDir,
		File toDir,
		String[] files,
		String[] dirs)
	{
		FileNameMapper mapper = null;
		if (mapperElement != null)
		{
			mapper = mapperElement.getImplementation();
		}
		else
			if (flatten)
			{
				mapper = new FlatFileNameMapper();
			}
			else
			{
				mapper = new GlobPatternMapper();
				mapper.setFrom("*");
				mapper.setTo(outputDir.toString() + "/*");
			}

		buildMap(fromDir, toDir, files, mapper, fileCopyMap);

		if (includeEmpty)
		{
			buildMap(fromDir, toDir, dirs, mapper, dirCopyMap);
		}
	}

	protected void buildMap(
		File fromDir,
		File toDir,
		String[] names,
		FileNameMapper mapper,
		Map map)
	{

		String[] toCopy = null;
		if (forceOverwrite)
		{
			Vector v = new Vector();
			for (int i = 0; i < names.length; i++)
			{
				if (mapper.mapFileName(names[i]) != null)
				{
					v.addElement(names[i]);
				}
			}
			toCopy = new String[v.size()];
			v.copyInto(toCopy);
		}
		else
		{
			SourceFileScanner ds = new SourceFileScanner(this);
			toCopy = ds.restrict(names, fromDir, toDir, mapper);
		}

		for (int i = 0; i < toCopy.length; i++)
		{
			File src = new File(fromDir, toCopy[i]);
			File dest = new File(toDir, mapper.mapFileName(toCopy[i])[0]);
			map.put(src.getAbsolutePath(), dest.getAbsolutePath());
		}
	}

	/**
	 * Actually does the file (and possibly empty directory) copies. This is a
	 * good method for subclasses to override.
	 */
	protected void doFileOperations()
	{
		if (fileCopyMap.size() > 0)
		{
			Set set = fileCopyMap.keySet();
			Iterator it = set.iterator();
			//create a two dimensional array to hold fromFile and toFile for
			// copying later
			File[][] copyFileArray = new File[set.size()][2];
			boolean publishFailed = false;
			try
			{
				cfgFile = new File(buildDir, "publish_swf.jsfl");
				cfgWriter = new FileWriter(cfgFile);
			}
			catch (IOException e)
			{
				if (failonerror)
				{
					throw new BuildException("Could not create script file for Authoring tool");
				}
				else
				{
					log("Could not create script file for Authoring tool");
				}
			}

			//
			// Clear the output panel in Flash so previously existing text there
			// does not skew our results
			//
			// Aral Balkan [ab] - Added Nov. 29th, 2003
			// (aral@bitsandpixels.co.uk)
			//
			try
			{
				cfgWriter.write(
					"fl.outputPanel.clear();\r\n");
			}
			catch (IOException e)
			{
				String msg =
					"Unable to write JSFL "
						+ " due to "
						+ e.getMessage();
				throw new BuildException(msg, e, location);				
			}
			
			int fileCount = 0;
			while (it.hasNext())
			{
				String fromFile = (String) it.next();
				String toFile = (String) fileCopyMap.get(fromFile);

				if (omitTraceActions)
				{
					Flash2004.switchOmitTraceActions(fromFile, true, location);
				}

				if (fromFile.equals(toFile))
				{
					log("Skipping self-copy of " + fromFile);
					continue;
				}
				try
				{
					log("Flash compiling " + fromFile);
					File fileOrig = new File(fromFile);
					URL fromFLA = fileOrig.toURL();
					String strURL = fromFLA.toString();
					strURL = strURL.replaceFirst("file:/", "file:///");
					String toSWF =
						strURL.substring(0, strURL.lastIndexOf(".")) + ".swf";

					cfgWriter.write(
						"var doc = fl.openDocument(\"" + strURL + "\");\r\n");
					cfgWriter.write(
						"fl.outputPanel.trace(\""
							+ "Publishing SWF - "
							+ toSWF
							+ "\"); \r\n");
					cfgWriter.write(
						"doc.exportSWF(\"" + toSWF + "\", true); \r\n");
					cfgWriter.write("fl.closeDocument(doc, false); \r\n\r\n");
					//cfgWriter.close();
					cfgFilePath = cfgFile.toString();

					copyFileArray[fileCount][0] =
						new File(
							fromFile.substring(0, fromFile.length() - 3)
								+ "swf");
					copyFileArray[fileCount][1] =
						new File(
							toFile.substring(0, toFile.length() - 3) + "swf");
					fileCount++;
				}
				catch (IOException ioe)
				{
					String msg =
						"Failed to build and post "
							+ fromFile
							+ " due to "
							+ ioe.getMessage();
					throw new BuildException(msg, ioe, location);
				}
			}
			
			//
			// The Flash2004_ErrorOutput log needs to be reset each time a build
			// is made, otherwise the user will see increasingly large amounts of
			// traces as the file is appended to. Also, we are using these traces
			// from Flash to see if there was an error in the publish so this can
			// skew the results of later builds if errors existed in earlier builds.
			//
			// Aral Balkan [ab] - Added Nov. 29th, 2003
			// (aral@bitsandpixels.co.uk)
			// 	
			try
			{
				log ("Deleting "+output);
				output.delete();
			}
			catch (Exception e)
			{
				log ("Warning: Could not delete the log file MX2004_ErrorOutput prior to build: "+e.getMessage());
			}
			
			
			try
			{
				String outputURL = output.toURL().toString();
				outputURL = outputURL.replaceFirst("file:/", "file:///");
				cfgWriter.write(
					"fl.outputPanel.save(\"" + outputURL + "\");\r\n");

				if (this.quitAuthoring)
				{
					cfgWriter.write("fl.quit(false); \r\n");
				}
				cfgWriter.close();
				// RUN COMPILER
				try
				{
					ExecuteOn execTask = new ExecuteOn();
					execTask.setProject(project);
					execTask.setTaskName("flash2004");
					//execTask.setDir( cfgFile.getParentFile() );
					if (timeout != null)
					{
						execTask.setTimeout(timeout);
					}
					execTask.setFailonerror(this.failonerror);
					execTask.setExecutable(compiler);
					FileSet fileSet = new FileSet();
					fileSet.setFile(cfgFile);
					execTask.addFileset(fileSet);
					execTask.execute();
				}
				catch (Exception e)
				{
					log("Problem execing compiler: " + e.getMessage());
				}

			}
			catch (Exception e)
			{
				log("Problem writing config file: " + e.getMessage());
			}
			
			//
			// In some instances, trying to read from the MX2004_ErrorOutput file
			// used to result in the following error: "The process cannot access 
			// the file because it is being used by another process." To fix this we 
			// wait a few seconds for Flash to finish writing to the file and 
			// let go of the handle. 
			//
			// Aral Balkan [ab] - Added Nov. 29th, 2003
			// (aral@bitsandpixels.co.uk)
			// 
			log ("Waiting for Flash to release the handle on MX2004_ErrorOutput...");
			sleepTask = new Sleep();
			sleepTask.doSleep(500);
			log ("Done!");
			
			try
			{
				String execOutput = "";
				BufferedReader br = new BufferedReader(new FileReader(output));
				while ((execOutput = br.readLine()) != null)
				{
					//write the errors back out to the output stream					
					log(execOutput);
					if (execOutput.indexOf("**Error**") != -1)
					{
						publishFailed = true;
					}
				}
				br.close();
			}
			catch (FileNotFoundException e)
			{

			}
			catch (IOException ioe)
			{

			}
			

			// output.delete();

			if (omitTraceActions)
			{
				it = set.iterator();
				while (it.hasNext())
				{
					String fromFile = (String) it.next();
					Flash2004.switchOmitTraceActions(fromFile, false, location);
				}
			}

			if (publishFailed)
			{
				if (failonerror)
				{
					throw new BuildException("Errors reported while generating SWFs");
				}
			}
			
			//
			// Flash needs time to finish publishing the SWFs. The original version 
			// just set a timeout of 100000 ms and relied on quitting the Flash 
			// IDE. This is dangerous for development builds. Flash may have
			// unsaved changes which will be lost. On a less serious but important
			// note, Flash MX 2004 does take a while to start back up and that can
			// interrupt the workflow. 
			//
			// In this implementation, the build script waits until the last 
			// FLA to be published to be written out into its SWF file and only then
			// proceeds with the copy. You should still be able to use the
			// older behavior by passing in a timeout value from the build script and
			// setting the quitAuthoring flag. 
			//
			// Aral Balkan [ab] - Added Nov. 29th, 2003
			// (aral@bitsandpixels.co.uk)
			
			// reference to the last SWF to be published
			theLastSWF = copyFileArray[copyFileArray.length-1][0];
			numSWFs = copyFileArray.length;
			
			// Inner Class: Wait for the last SWF to be published
			class WaitForLastSWF 
			{
				Date startDate = new Date();	// save the start time
				boolean firstTime = true;
				
				WaitForLastSWF ()
				{
					if ( timeout == null )
					{
						// default to 100000ms
						timeout = new Long(100000);
						log ("No timeout specified, defaulting to 100000ms.");
					}
					else
					{
						log ("Build timeout set at "+timeout+"ms");
					}
				}

				
				private void doTheWait ()
				{
					// calculate start time (for timeout calculation)
					Date currentDate = new Date();
					long startTime = startDate.getTime();
					long currentTime = currentDate.getTime();
					
					// calculate the time to stop (note timeout is passed in through build.xml)
					boolean timeToStop = currentTime >= (startTime + timeout.longValue());

					// has the last SWF been published?
					if ( theLastSWF.exists() )
					{
						// display a status message
						long timeTaken = (currentTime - startTime) / 1000;
						log ("The last SWF has been published! Publish took "+timeTaken+" seconds.");

						log ("Waiting for Flash handle release on last file...");
						
						// Last SWF exists! Give Flash a little time to 
						// finish writing it out and release the file handle
						sleepTask = new Sleep();
						sleepTask.doSleep(500);
						
						log ("Done!");
						
					}	
					else if ( timeToStop )
					{
						// Error: Compile timed out.
						throw new BuildException("Publish of SWFs timed out at "+timeout+"ms. Consider raising timeout value.");
					}
					else
					{
						//
						// The last SWF has not finished publishing, wait a bit...
						//
								
						// Is this the first time we're waiting? 
						if ( firstTime )
						{
							// Inform the user that we are waiting and why.
							log ("Waiting for Flash to publish all the SWFs ("+numSWFs+")...");
							firstTime = false; // flip flag
						}
						
						// sleep a second
						sleepTask = new Sleep();
						sleepTask.doSleep(1000);
						// and lets try again (recurse)
						doTheWait();
					}
				}
			}
			// create instance of inner class
			WaitForLastSWF waitForLastSWF = new WaitForLastSWF();
			// start waiting for last SWF to be finished publishing (or time out)
			waitForLastSWF.doTheWait();
			
			for (int i = 0; i < copyFileArray.length; i++)
			{
				// COPY resulting FLA to it's place in output
				Copy copyTask = new Copy();
				copyTask.setProject(project);
				copyTask.setTaskName("flash");
				copyTask.setFile(copyFileArray[i][0]);
				copyTask.setTofile(copyFileArray[i][1]);
				copyTask.execute();
				
				//
				// Delete the original SWF so as not to clutter the source directories.
				//
				// Aral Balkan [ab] - Added Nov. 29th, 2003
				// (aral@bitsandpixels.co.uk)
				//
				Delete deleteTask = new Delete();
				deleteTask.setProject(project);
				deleteTask.setTaskName("flash");
				deleteTask.setFile(copyFileArray[i][0]);
				deleteTask.execute();
			}

		}

	}

	public static void switchOmitTraceActions(
		String fla,
		boolean to,
		Location location)
		throws BuildException
	{
		try
		{
			RandomAccessFile raf = new RandomAccessFile(fla, "rw");
			// skip until we find Omit Trace Actions
			int b = 0;
			while ((b = raf.read()) != -1)
			{
				if (b == 'O'
					&& raf.read() == 'm'
					&& raf.read() == 'i'
					&& raf.read() == 't'
					&& raf.read() == ' '
					&& raf.read() == 'T'
					&& raf.read() == 'r'
					&& raf.read() == 'a'
					&& raf.read() == 'c'
					&& raf.read() == 'e'
					&& raf.read() == ' '
					&& raf.read() == 'A'
					&& raf.read() == 'c'
					&& raf.read() == 't'
					&& raf.read() == 'i'
					&& raf.read() == 'o'
					&& raf.read() == 'n'
					&& raf.read() == 's')
				{
					// skip end of string delimiter
					raf.read();
					if (to)
						raf.write('1');
					else
						raf.write('0');
				}
			}
			raf.close();
		}
		catch (IOException ioe)
		{
			throw new BuildException(
				"Failed to toggle omit trace actions",
				ioe,
				location);
		}
	}

}
