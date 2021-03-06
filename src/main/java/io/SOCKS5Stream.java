/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//#ifdef FILE_TRANSFER

package io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author Vitaly
 */
public class SOCKS5Stream {
    
    private Socket connection;
    private InputStream inpStream = null;
    private OutputStream outStream = null;
    
    public SOCKS5Stream(Socket con) throws IOException {
        connection = con;
        inpStream = connection.getInputStream();
        outStream = connection.getOutputStream();
    }
    
    public void send(byte[] data, int ofs, int length) throws IOException {        
        synchronized (outStream) {
            outStream.write(data, ofs, length);    
        }
    }
    public void send(byte[] data) throws IOException {        
        send(data, 0, data.length);
    }
    
    public int read(byte[] buf) throws IOException {
        
        int avail = inpStream.available();        
        if (avail==0) return 0;        
        if (avail>buf.length) avail=buf.length;        
        return inpStream.read(buf, 0, avail);
    }

    public void flush() throws IOException {
        outStream.flush();
    }
    
    public void close() {
        try {
            inpStream.close(); inpStream = null;
            outStream.close(); outStream = null;
        } catch (IOException e) {
            Client.StaticData.getInstance().roster.errorLog(e.getMessage());
        }
    }

}

//#endif
