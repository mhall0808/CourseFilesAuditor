/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coursefilesauditor;

/**
 *
 * @author hallm8
 */
public class CourseFilesAuditor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Manifest manifest = new Manifest("C:\\Users\\Mark\\Downloads\\D2LExport_61929_201651824");

        manifest.gatherCSV();

    }

}
