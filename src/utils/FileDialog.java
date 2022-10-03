package utils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FilenameFilter;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.Pointer;
import org.lwjgl.util.nfd.*;

public class FileDialog {

    public static String showFileDialog(String description, String filter){
/*        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e){
            e.printStackTrace();
        }

        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setFileFilter(new FileNameExtensionFilter(description, filter));
        int result = jfc.showOpenDialog(null);
        if(result == JFileChooser.APPROVE_OPTION){
            File selectedFile = jfc.getSelectedFile();
            return selectedFile.getPath();
        }*/

/*        java.awt.FileDialog fileDialog = new java.awt.FileDialog((java.awt.Frame)null);
        fileDialog.setFilenameFilter(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                for (String s : filter) {
                    if (name.toLowerCase().endsWith(s))
                        return true;
                }
                return false;
            }
        });

        fileDialog.setVisible(true);
        String file = fileDialog.getFile();
        if(file != null)
            return file;

        return "";*/
        PointerBuffer pointerBuffer = PointerBuffer.allocateDirect(512);
        int result = NativeFileDialog.NFD_OpenDialog(filter, null, pointerBuffer);
        if(result == NativeFileDialog.NFD_OKAY) {
            return pointerBuffer.getStringUTF8();
        }

        return "";
    }
}
