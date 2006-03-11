package com.totalchange.sketchingout.mavenxml2swfplugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.compiler.util.scan.InclusionScanException;
import org.codehaus.plexus.compiler.util.scan.StaleSourceScanner;
import org.codehaus.plexus.compiler.util.scan.mapping.SuffixMapping;

import com.totalchange.sketchingout.ImageParser;
import com.totalchange.sketchingout.SketchingoutSettings;
import com.totalchange.sketchingout.imageparsers.SketchingoutImageParserException;
import com.totalchange.sketchingout.imageparsers.SwfAnimatedImageParser;

/**
 * 
 * @author RalphJones
 * @goal compile
 * @description Compiles .xml Sketching Out Drawing files to .swf's
 * @phase compile
 */
public class CompileMojo extends AbstractMojo {
    /**
     * Where to find the source xml/configs('s)
     * 
     * @parameter expression="${basedir}/src/main/sod"
     * @required
     */
    private String flaSourceDirectory;

    /**
     * Where to create the swf('s)
     * 
     * @parameter expression="${project.build.directory}/${project.build.finalName}"
     * @required
     */
    private String outputDirectory;

    /**
     * @parameter expression="300"
     */
    private int defaultWidth = 300;

    /**
     * @parameter expression="200"
     */
    private int defaultHeight = 200;

    /**
     * @parameter expression="100"
     */
    private int defaultScale = 100;

    /**
     * @parameter expression="0"
     */
    private int defaultLoss = 0;

    public void execute() throws MojoExecutionException, MojoFailureException {
        File srcDir = new File(flaSourceDirectory);
        File destDir = new File(outputDirectory);

        if (!srcDir.exists()) {
            getLog().info("No sod (SketchingOut Drawing) sources");
            return;
        }

        // Use a stale source scanner to find source fla's
        StaleSourceScanner scanner = new StaleSourceScanner(0);
        scanner.addSourceMapping(new SuffixMapping(".xml", ".swf"));
        scanner.addSourceMapping(new SuffixMapping(".cfg", ".swf"));
        Set sources = null;

        try {
            sources = scanner.getIncludedSources(srcDir, destDir);
        } catch (InclusionScanException inEx) {
            throw new MojoExecutionException("An error occurred looking for "
                    + "stale sources", inEx);
        }

        if (sources.size() <= 0) {
            getLog().info("No sod's to compile - all swf's are up to date");
            return;
        }

        getLog().info("Compiling " + sources.size() + " sod's");
        if (!destDir.exists())
            destDir.mkdirs();

        for (Iterator it = sources.iterator(); it.hasNext();) {
            File sod = (File) it.next();
            File swf = makeDestFile(sod, srcDir, destDir);

            // Make sure the directory exists for destination
            File swfDir = swf.getParentFile();
            if (!swfDir.exists())
                swfDir.mkdirs();

            // Need to find an xml file and associated config file
            String fileName = sod.getName();
            fileName = fileName.substring(0, fileName.lastIndexOf("."));

            File sodDir = sod.getParentFile();
            File xmlSod = new File(sodDir, fileName + ".xml");
            File cfgSod = new File(sodDir, fileName + ".cfg");

            // Need to get some properties - and override from config file if
            // defined.
            int width = defaultWidth;
            int height = defaultHeight;
            int scale = defaultScale;
            int loss = defaultLoss;
            if (cfgSod.exists()) {
                Properties props = new Properties();

                try {
                    props.load(new FileInputStream(cfgSod));
                } catch (IOException ioEx) {
                    throw new MojoExecutionException("cfg sod " + cfgSod
                            + "could not be read", ioEx);
                }

                width = Integer.parseInt(props.getProperty("width", String
                        .valueOf(width)));
                height = Integer.parseInt(props.getProperty("height", String
                        .valueOf(height)));
                scale = Integer.parseInt(props.getProperty("scale", String
                        .valueOf(scale)));
                loss = Integer.parseInt(props.getProperty("loss", String
                        .valueOf(loss)));
            }

            try {
                OutputStream out = new FileOutputStream(swf);
                ImageParser parse = new ImageParser(
                        SketchingoutSettings.PRESENT_DRAWING_VERSION, width,
                        height, scale, loss, out, new SwfAnimatedImageParser());

                parse.addStage(new FileReader(xmlSod));
                parse.close();
                out.close();
            } catch (SketchingoutImageParserException spEx) {
                throw new MojoFailureException("Invalid drawing " + xmlSod
                        + ": " + spEx.getMessage());
            } catch (Exception ex) {
                throw new MojoExecutionException("Failed to compile " + xmlSod,
                        ex);
            }
        }
    }

    /**
     * Makes a destination file from a source file
     * 
     * @param srcFile
     * @param srcDir
     * @param destDir
     * @return
     */
    private File makeDestFile(File srcFile, File srcDir, File destDir) {
        String fols = "";
        File srcFileFol = srcFile.getParentFile();

        while (!srcFileFol.equals(srcDir)) {
            fols = srcFileFol.getName() + "/" + fols;
            srcFileFol = srcFileFol.getParentFile();
        }

        String fileName = srcFile.getName();
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        fileName += ".swf";

        return new File(destDir, fols + fileName);
    }
}
