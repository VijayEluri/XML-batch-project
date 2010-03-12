/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/*
Copyright (c) 2002-2008 ymnk, JCraft,Inc. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

  1. Redistributions of source code must retain the above copyright notice,
     this list of conditions and the following disclaimer.

  2. Redistributions in binary form must reproduce the above copyright 
     notice, this list of conditions and the following disclaimer in 
     the documentation and/or other materials provided with the distribution.

  3. The names of the authors may not be used to endorse or promote products
     derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JCRAFT,
INC. OR ANY CONTRIBUTORS TO THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.jcraft.jsch;

import java.io.*;

import java.util.Vector;

public class MockChannelSftp {

  private static final String file_separator=java.io.File.separator;
  private static final char file_separatorc=java.io.File.separatorChar;
  private static boolean fs_is_bs=(byte)java.io.File.separatorChar == '\\';

  private String cwd;
  private String home;
  private String lcwd;

  private static final String UTF8="UTF-8";
  private String fEncoding=UTF8;
  private boolean fEncoding_is_utf8=true;


  public void start() throws JSchException{

  }
  public void put(String src, String dst) throws SftpException{
	   
	  }
  public void put(InputStream src, String dst) throws SftpException{
    put(src, dst, null, 0);
  }
  public void put(InputStream src, String dst, int mode) throws SftpException{
    put(src, dst, null, mode);
  }
  public void put(InputStream src, String dst, 
		  SftpProgressMonitor monitor) throws SftpException{
    put(src, dst, monitor, 0);
  }
  public void put(InputStream src, String dst, 
		  SftpProgressMonitor monitor, int mode) throws SftpException{
  
  }

  public OutputStream put(String dst) throws SftpException{
    return put(dst, (SftpProgressMonitor)null, 0);
  }
  public OutputStream put(String dst, final int mode) throws SftpException{
    return put(dst, (SftpProgressMonitor)null, mode);
  }
  public OutputStream put(String dst, final SftpProgressMonitor monitor, final int mode) throws SftpException{
    return put(dst, monitor, mode, 0);
  }
  public OutputStream put(String dst, final SftpProgressMonitor monitor, final int mode, long offset) throws SftpException{
	  return null;
  }

  public void get(String src, String dst) throws SftpException{
    get(src, dst, null, 0);
  }
  public void get(String src, String dst,
		  SftpProgressMonitor monitor) throws SftpException{
    get(src, dst, monitor, 0);
  }
  public void get(String src, String dst,
		  SftpProgressMonitor monitor, int mode) throws SftpException{
    // System.out.println("get: "+src+" "+dst);

   
  }
  
  public void exit() {
	  
  }
  public void cd(String path) throws SftpException {
	  
  }
  public void get(String src, OutputStream dst) throws SftpException{
    get(src, dst, null, 0, 0);
  }
  public void get(String src, OutputStream dst,
		  SftpProgressMonitor monitor) throws SftpException{
    get(src, dst, monitor, 0, 0);
  }
  public void get(String src, OutputStream dst,
		   SftpProgressMonitor monitor, int mode, long skip) throws SftpException{
  }

  private void _get(String src, OutputStream dst,
                    SftpProgressMonitor monitor, int mode, long skip) throws SftpException{

  }

  public InputStream get(String src) throws SftpException{
    return get(src, null, 0L);
  }
  public InputStream get(String src, SftpProgressMonitor monitor) throws SftpException{
    return get(src, monitor, 0L);
  }

  /**
   * @deprecated  This method will be deleted in the future.
   */
  public InputStream get(String src, int mode) throws SftpException{
    return get(src, null, 0L);
  }
  /**
   * @deprecated  This method will be deleted in the future.
   */
  public InputStream get(String src, final SftpProgressMonitor monitor, final int mode) throws SftpException{
    return get(src, monitor, 0L);
  }
  public InputStream get(String src, final SftpProgressMonitor monitor, final long skip) throws SftpException{
	  return null;
   }

   public java.util.Vector ls(String path) throws SftpException{
       return null;
   }

   public void rename(String oldpath, String newpath) throws SftpException{
    
  }
  public void rm(String path) throws SftpException{

  }

  private boolean isRemoteDir(String path){
    return false;
  }

  public void chgrp(int gid, String path) throws SftpException{
  
  }

  public void chown(int uid, String path) throws SftpException{
 
  }

  public void chmod(int permissions, String path) throws SftpException{
  
  }

  public void setMtime(String path, int mtime) throws SftpException{
  
  }

  public void rmdir(String path) throws SftpException{
  
  }

  public void mkdir(String path) throws SftpException{
   
  }

  public SftpATTRS stat(String path) throws SftpException{
    return null;
  }

  private SftpATTRS _stat(byte[] path) throws SftpException{
   return null;
  }

  private SftpATTRS _stat(String path) throws SftpException{
    return null;
  }

  public SftpATTRS lstat(String path) throws SftpException{
   return null;
  }

  private SftpATTRS _lstat(String path) throws SftpException{
	  return null;
  }

  private byte[] _realpath(String path) throws SftpException, IOException, Exception{
	  return null;
  }

  public void setStat(String path, SftpATTRS attr) throws SftpException{
    
  }
  private void _setStat(String path, SftpATTRS attr) throws SftpException{
 
  }

  public String lpwd(){ return lcwd; }
  public String version(){ return "version"; }
  public String getHome() throws SftpException {
    return null; 
  }

  private String getCwd() throws SftpException{
	  return null;
  }

  private void setCwd(String cwd){
    this.cwd=cwd;
  }

  private void read(byte[] buf, int s, int l) throws IOException, SftpException{

  }

  private boolean checkStatus(int[] ackid, Header header) throws IOException, SftpException{
 return true;
  }
  private boolean _sendCLOSE(byte[] handle, Header header) throws Exception{
    return true;
  }

  private void sendINIT() throws Exception{
   
  }

  private void sendREALPATH(byte[] path) throws Exception{
    
  }
  private void sendSTAT(byte[] path) throws Exception{
    
  }
  private void sendLSTAT(byte[] path) throws Exception{
    
  }
  private void sendFSTAT(byte[] handle) throws Exception{
    
  }
  private void sendSETSTAT(byte[] path, SftpATTRS attr) throws Exception{
   
  }
  private void sendREMOVE(byte[] path) throws Exception{
    
  }
  private void sendMKDIR(byte[] path, SftpATTRS attr) throws Exception{
    
  }
  private void sendRMDIR(byte[] path) throws Exception{
    
  }
  private void sendSYMLINK(byte[] p1, byte[] p2) throws Exception{
    
  }
  private void sendREADLINK(byte[] path) throws Exception{
    
  }
  private void sendOPENDIR(byte[] path) throws Exception{
    
  }
  private void sendREADDIR(byte[] path) throws Exception{
    
  }
  private void sendRENAME(byte[] p1, byte[] p2) throws Exception{
    
  }
  private void sendCLOSE(byte[] path) throws Exception{
    
  }
  private void sendOPENR(byte[] path) throws Exception{
   
  }
  private void sendOPENW(byte[] path) throws Exception{
    
  }
  private void sendOPENA(byte[] path) throws Exception{
    
  }
  private void sendOPEN(byte[] path, int mode) throws Exception{
    
  }
  private void sendPacketPath(byte fxp, byte[] path) throws Exception{
    
  }
  private void sendPacketPath(byte fxp, byte[] p1, byte[] p2) throws Exception{
    
  }

  private int sendWRITE(byte[] handle, long offset, 
                        byte[] data, int start, int length) throws Exception{
    
	  return 0;
  }

  private void sendREAD(byte[] handle, long offset, int length) throws Exception{

  }

  private void putHEAD(byte type, int length) throws Exception{

  }

  private Vector glob_remote(String _path) throws Exception{

    
    return null;
  }

  private boolean isPattern(byte[] path){
   return true;
  }

  private Vector glob_local(String _path) throws Exception{
	  return null;
  }

  private void throwStatusError(Buffer buf, int i) throws SftpException{

  }

  private static boolean isLocalAbsolutePath(String path){
    return (new File(path)).isAbsolute();
  }

  public void disconnect(){
   
  }

  private boolean isPattern(String path, byte[][] utf8){
   return true;
  }

  private boolean isPattern(String path){
    return isPattern(path, null);
  }

  private void fill(Buffer buf, int len)  throws IOException{
    buf.reset();
    fill(buf.buffer, 0, len);
    buf.skip(len);
  }

  private int fill(byte[] buf, int s, int len) throws IOException{
   return 0;
  }
  private void skip(long foo) throws IOException{
   
  }

  class Header{
    int length;
    int type;
    int rid;
  }
  private Header header(Buffer buf, Header header) throws IOException{
    buf.rewind();
    int i=fill(buf.buffer, 0, 9);
    header.length=buf.getInt()-5;
    header.type=buf.getByte()&0xff;
    header.rid=buf.getInt();  
    return header;
  }

  private String remoteAbsolutePath(String path) throws SftpException{
    if(path.charAt(0)=='/') return path;
    String cwd=getCwd();
    if(cwd.endsWith("/")) return cwd+path;
    return cwd+"/"+path;
  }

  private String localAbsolutePath(String path){
    if(isLocalAbsolutePath(path)) return path;
    if(lcwd.endsWith(file_separator)) return lcwd+path;
    return lcwd+file_separator+path;
  }

  /**
   * This method will check if the given string can be expanded to the
   * unique string.  If it can be expanded to mutiple files, SftpException
   * will be thrown.
   * @return the returned string is unquoted.
   */
  private String isUnique(String path) throws SftpException, Exception{
  return "";
  }

  public int getServerVersion() throws SftpException{
  return 0;
  }

  public void setFilenameEncoding(String encoding) throws SftpException{

  }

  public String getExtension(String key){
    return "";
  }

  public String realpath(String path) throws SftpException{
    return "";
  }

  public class LsEntry implements Comparable{
    private  String filename;
    private  String longname;
    private  SftpATTRS attrs;
    LsEntry(String filename, String longname, SftpATTRS attrs){
      setFilename(filename);
      setLongname(longname);
      setAttrs(attrs);
    }
    public String getFilename(){return filename;};
    void setFilename(String filename){this.filename = filename;};
    public String getLongname(){return longname;};
    void setLongname(String longname){this.longname = longname;};
    public SftpATTRS getAttrs(){return attrs;};
    void setAttrs(SftpATTRS attrs) {this.attrs = attrs;};
    public String toString(){ return longname; }
    public int compareTo(Object o) throws ClassCastException{
      if(o instanceof LsEntry){
        return filename.compareTo(((LsEntry)o).getFilename());
      }
      throw new ClassCastException("a decendent of LsEntry must be given.");
    }
  }
}
