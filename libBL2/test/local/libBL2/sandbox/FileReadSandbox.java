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

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
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
            System.out.println(file.getPath());
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
    
}
