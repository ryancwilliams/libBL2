/*
 * Copyright 2014 ryanwilliams.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package local.libBL2.sandbox;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import javax.swing.JFileChooser;

/**
 *
 * @author ryanwilliams
 */
public class FileReadSandbox {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // TODO code application logic here
            File file = chooseFile();
            readFile(file);
        } catch (Throwable ex) {
            Logger.getLogger(FileReadSandbox.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
    }
    
    /**
     * Prompts the user to select a file.
     * the user will NOT be prompted for the file again 
     * if a file is not selected.
     * @return The file selected by the user
     * @throws Throwable Thrown if a file is not selected
     */
    public static File chooseFile() throws Throwable {
        return chooseFile(false);
    }
    
    /**
     * The file chooser for the choseFile functions. 
     * Required for the choseFile functions to function.
     */
    private static final JFileChooser fileChooser = new JFileChooser();
    
    /**
     * Prompts the user to select a file
     * @param retry if TRUE then the user will be prompted for the file again 
     * if a file is not selected. 
     * @return The file selected by the user
     * @throws Throwable Thrown if a file is not selected
     */
    public static File chooseFile(boolean retry) throws Throwable {
        //Create the file chooser
        //done at class level
        
        //Prompt the user to select the file
        int returnVal = fileChooser.showOpenDialog(null);
        
        //Create the output file
        File file = null;
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            //If the file is selected
            file = fileChooser.getSelectedFile();
        } else if (retry == true) {
            //If the file is not selected and retry is true
            file = chooseFile(retry);
        } else {
            throw new Throwable("File was not selected.");
        }
        
        return file;
    }
    
    public static void readFile(File file) {
        System.out.println("Path: " + file.getPath());
        System.out.println("File size: " + file.length() + " bytes");

        InputStream input = null;
        try {
            //Create the input stream
            input = new BufferedInputStream(new FileInputStream(file));
            
            //Read the first 20 bytes
            byte[] checksum = read(input, 20);  
            //print the first 20 bytes
            System.out.println("Checksum: " + toHexString(checksum));
            
            //Do the real hash
            byte[] hash = computeHash(input);
            //Print thr hash
            System.out.println("SHA-1 Hash: " + toHexString(hash));
            
            //Read the next 4 bytes
            byte[] uncompressedLength = read(input, 4);
            //Print the next 4 bytes
            System.out.println("uncompressedLength: " + toHexString(uncompressedLength));
            
            //Read the rest of the file
            //FIXME fix the issue with LZO not working.
            InputStream uncompressedInput = new InflaterInputStream(input);
            byte[] payload = read(uncompressedInput, uncompressedInput.available());
            //Print the payload
            System.out.println("ADDRESS -------------------------------- PAYLOAD ---------------------------------");
            System.out.println(toHexString(payload, true));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileReadSandbox.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileReadSandbox.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            //Close the input stream
            try {
                input.close();
            } catch (IOException ex) {
                Logger.getLogger(FileReadSandbox.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static byte[] read(InputStream input, int length) throws IOException {
        byte[] data = new byte[length];
        input.read(data);
        return data;
    }
    
    /**
     * Converts the provided byte array to a readable string
     * @param data the byte array to read
     * @return the byte array as a string
     */
    public static String toHexString(byte[] data) {
        return toHexString(data, false);
    } 
    
    /**
     * Converts the provided byte array to a readable string
     * @param data the byte array to read
     * @param showAddress TRUE if addresses should be shown on the left side
     * @return the byte array as a string
     */
    public static String toHexString(byte[] data, boolean showAddress) {
        String output = "";
        int colCount = 0;
        int rowCount = 0;
        final int LINE_WIDTH = 24;
        for(byte fragment : data) {
            //Print address if first col
            if (colCount == 0 && showAddress) {
                //Create the address string
                String address = String.format("%02X", LINE_WIDTH * rowCount);
                //Pad the address string with 0
                address = "0000".substring(address.length()) + address;
                //Add the address to the output
                output = output + "0x" + address + " ";
            }
            
            //Add the byte to the string
            output = output + " " + String.format("%02X", fragment);
            
            //Manage counter
            colCount++;
            if (colCount > LINE_WIDTH) {
                //if LINE_WIDTH bytes on a line create a new line
                output = output + "\n";
                colCount = 0;
                rowCount++;
            }
        }
        return output;
    }
    
    public static byte[] computeHash(InputStream input) throws IOException {
        
        //Mark the input stream
        input.mark(Integer.MAX_VALUE);
        
        //Read the input stream
        byte[] data = read(input, input.available());
        
        //compute the sha1 hash
        byte[] hash = null;
        try {
            //Create the digest
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            //reset the digest
            sha1.reset();
            //load the data
            sha1.update(data);
            //Get the hash
            hash = sha1.digest();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(FileReadSandbox.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //reset the inputstream
        input.reset();
        
        //Return the hash
        return hash;
    }
}
