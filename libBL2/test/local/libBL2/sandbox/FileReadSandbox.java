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
        // TODO code application logic here
        File file = chooseFile();
        System.out.println(file.getPath());
        
    }
    
    public static File chooseFile() {
        //Create the file chooser
        JFileChooser fileChooser = new JFileChooser();
        
        //Prompt the user to select the file
        int returnVal = fileChooser.showOpenDialog(null);
        
        //Create the output file
        File file = null;
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            //If the file is selected
            file = fileChooser.getSelectedFile();
        } 
        
        return file;
    }
    
}
