package com.totalchange.jiffy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FlashMiddlemanServlet extends HttpServlet {
    private static final long serialVersionUID = 8592094933183420516L;

    private static final String ID_PARAM = "MNNNnnnnggggghhhhhh";
    private static final int BUFFER_SIZE = 4 * 1024;

    private static Log log = LogFactory.getLog(FlashMiddlemanServlet.class);

    private class FileUploaded {
        String contentType;
        int contentLength;
        InputStream fileIn;
    }

    private void streamThrough(InputStream in, OutputStream out)
            throws IOException {
        byte[] buf = new byte[BUFFER_SIZE];
        int read;
        while ((read = in.read(buf)) > -1) {
            out.write(buf, 0, read);
        }
    }

    /**
     * The doGet is done by the taker.
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        // Look for the id param
        String id = request.getParameter(ID_PARAM);

        if (id == null) {
            throw new ServletException("No ID - no go Joe");
        }

        // Now need to grab the file being uploaded
        FileUploaded file;
        try {
            if (log.isTraceEnabled()) {
                log.trace("New request for file with id: " + id
                        + ".  Waiting for file.");
            }

            file = (FileUploaded) Mediator.getInstance().getMate(id, this);
        } catch (MediatorException mEx) {
            log.error("Failed to fetch file being uploaded", mEx);
            throw new ServletException(mEx);
        }

        // And stream back its contents to the client
        log.trace("Got file, streaming back through to client");
        response.setContentType(file.contentType);
        if (file.contentLength > -1) {
            response.setContentLength(file.contentLength);
        }
        streamThrough(file.fileIn, response.getOutputStream());

        log.trace("Finished file download");
    }

    /**
     * The doPost is done by the giver.
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        log.trace("New POST request");

        // Whoa there - multipart forms (file uploads) need Apache Commons File
        // Smutload
        if (!ServletFileUpload.isMultipartContent(request)) {
            log.warn("Made a non-multipart request (so no file)");
            throw new ServletException("The only way is multipart, baby.  "
                    + "Just you and me, now.");
        }

        try {
            ServletFileUpload fileUp = new ServletFileUpload();
            FileItemIterator it = fileUp.getItemIterator(request);

            String id = null;
            while (it.hasNext()) {
                FileItemStream item = it.next();
                if (item.isFormField()
                        && (item.getFieldName().equals(ID_PARAM))) {
                    id = Streams.asString(item.openStream());
                    if (log.isTraceEnabled()) {
                        log.trace("Got id field with value: " + id);
                    }
                } else {
                    if (log.isTraceEnabled()) {
                        log.trace("Got a file: " + item.getName());
                    }

                    if (id == null) {
                        log.warn("Haven't got an id so cannot continue.  "
                                + "Make sure the id field comes before the "
                                + "file.");
                        throw new ServletException("Need to get an id before "
                                + "a file");
                    }

                    // Now make up a file uploaded object for this file
                    FileUploaded file = new FileUploaded();

                    // Figure out a mime type for the file
                    file.contentType = item.getContentType();
                    if (file.contentType == null) {
                        file.contentType = getServletContext().getMimeType(
                                item.getName());

                        if (log.isTraceEnabled()) {
                            log.trace("Used server to determine content "
                                    + "type of " + file.contentType);
                        }
                    } else if (log.isTraceEnabled()) {
                        log.trace("Client passed in content type of "
                                + file.contentType);
                    }

                    // The length won't be known...
                    file.contentLength = -1;

                    // Need to configure a piped input/output as this is a
                    // mediation between 2 threads
                    PipedOutputStream out = new PipedOutputStream();
                    file.fileIn = new PipedInputStream(out);

                    // Connect the file to the download process (see doGet()).
                    log.trace("Waiting for request for file");
                    Mediator.getInstance().getMate(id, file);

                    // Now start passing the file through the pipe to the other
                    // request
                    log.trace("Streaming file back through to recipient");
                    streamThrough(item.openStream(), out);

                    // Make sure notify piped input that have finished...
                    out.close();

                    // Send an OK response
                    response.getWriter().println(
                            "Transferred " + item.getName());

                    // Don't bother with any more files!
                    break;
                }
            }
        } catch (FileUploadException fEx) {
            log.error("File upload exception", fEx);
            throw new ServletException(fEx);
        } catch (MediatorException mEx) {
            log.error("Mediation exception", mEx);
            throw new ServletException(mEx);
        }

        log.trace("Finished file upload");
    }
}
