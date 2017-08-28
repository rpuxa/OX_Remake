package Oxpos;

import Editor.Sandbox;
import Editor.Tree.MovesTree;
import Jogl.JavaRenderer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;

public class SaveAndLoad {

    public static void save() {
        setNames();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.oxpos", "oxpos");
        JFileChooser fileSave = new JFileChooser();
        fileSave.setFileFilter(filter);
        fileSave.setCurrentDirectory(new File("SavedGames"));
        if ( fileSave.showSaveDialog(null) == JFileChooser.APPROVE_OPTION ) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileSave.getSelectedFile().getAbsolutePath() + ".oxpos"))) {
                oos.writeObject(Sandbox.tree);
                JOptionPane.showMessageDialog(null , "Сохранение произошло успешно!", "Сохранить", JOptionPane.INFORMATION_MESSAGE);
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(null , "Ошибка в сохранении файла", "Сохранить", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void load(){
        setNames();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.oxpos", "oxpos");
        JFileChooser fileOpen = new JFileChooser();
        fileOpen.setFileFilter(filter);
        fileOpen.setCurrentDirectory(new File("SavedGames"));
        int ret = fileOpen.showDialog(null, "Открыть файл");
        if (ret == JFileChooser.APPROVE_OPTION) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileOpen.getSelectedFile()))) {
                MovesTree movesTree = (MovesTree) ois.readObject();
                movesTree.update();
                JOptionPane.showMessageDialog(null , "Загрузка прошла успешно!", "Загрузить", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e1){
                JOptionPane.showMessageDialog(null , "Ошибка в загрузке файла", "Загрузить", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void setNames(){
        UIManager.put("FileChooser.saveButtonText", "Сохранить");
        UIManager.put("FileChooser.cancelButtonText", "Отмена");
        UIManager.put("FileChooser.fileNameLabelText", "Наименование файла");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Типы файлов");
        UIManager.put("FileChooser.lookInLabelText", "Директория");
        UIManager.put("FileChooser.saveInLabelText", "Сохранить в директории");
        UIManager.put("FileChooser.folderNameLabelText", "Путь директории");
    }


}
