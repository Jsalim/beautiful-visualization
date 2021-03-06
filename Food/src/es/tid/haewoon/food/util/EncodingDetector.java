/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is mozilla.org code.
 *
 * The Initial Developer of the Original Code is
 * Netscape Communications Corporation.
 * Portions created by the Initial Developer are Copyright (C) 1998
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Kohei TAKETA <k-tak@void.in>
 *   lnezda
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */
package es.tid.haewoon.food.util;

import java.io.File;
import java.io.FileInputStream;

import org.apache.log4j.Logger;
import org.mozilla.universalchardet.UniversalDetector;


public class EncodingDetector
{
    static Logger logger = Logger.getLogger(EncodingDetector.class);
    public static void main(String[] args) throws java.io.IOException
    {

        byte[] buf = new byte[4096];
        
        // UTF-8
//        java.io.FileInputStream fis = new java.io.FileInputStream(Constants.CD2_PREPARATION_SWEET_PATH + File.separator + "1.dat");

        // UTF-8
//        java.io.FileInputStream fis = new java.io.FileInputStream(Constants.CD2_PREPARATION_SAVOURY_PATH + File.separator + "1.dat");
  
        // UTF-16LE
//        java.io.FileInputStream fis = new java.io.FileInputStream(Constants.CD2_STYLE_SWEET_PATH + File.separator + "1.dat");

        // UTF-16LE
//        java.io.FileInputStream fis = new java.io.FileInputStream(Constants.CD2_STYLE_SAVOURY_PATH + File.separator + "1.dat");

        // WINDOWS-1252
//        java.io.FileInputStream fis = new java.io.FileInputStream(Constants.CD3_PREPARATION_PATH + File.separator + "1.txt");
        
        // UTF-16LE
//        java.io.FileInputStream fis = new java.io.FileInputStream(Constants.CD3_STYLE_SWEET_PATH + File.separator + "1.dat");
        
        FileInputStream fis = new FileInputStream(Constants.CD3_STYLE_SWEET_PATH + File.separator + "1.dat");
        
        // (1)
        UniversalDetector detector = new UniversalDetector(null);

        // (2)
        int nread;
        while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        fis.close();
        // (3)
        detector.dataEnd();

        // (4)
        String encoding = detector.getDetectedCharset();
        if (encoding != null) {
            System.out.println("Detected encoding = " + encoding);
        } else {
            System.out.println("No encoding detected.");
        }

        // (5)
        detector.reset();
        
//        BufferedReader br = new BufferedReader(new InputStreamReader(
//                        new FileInputStream(Constants.CD2_RECIPES_PATH + File.separator + "277.dat"), "UTF-8"));
//        String line = null;
//        while ((line = br.readLine()) != null) {
//            
//            logger.debug(line);
//            logger.debug(Arrays.asList(line.split("<br>")));
//            
//        }
    }
}