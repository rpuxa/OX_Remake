package Oxpos;

import Jogl.JavaRenderer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;

public class SaveAndLoad {

    public static void save() {
        UIManager.put("FileChooser.saveButtonText", "Сохранить");
        UIManager.put("FileChooser.cancelButtonText", "Отмена");
        UIManager.put("FileChooser.fileNameLabelText", "Наименование файла");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Типы файлов");
        UIManager.put("FileChooser.lookInLabelText", "Директория");
        UIManager.put("FileChooser.saveInLabelText", "Сохранить в директории");
        UIManager.put("FileChooser.folderNameLabelText", "Путь директории");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.oxpos", "oxpos");
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(filter);
        if ( fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION ) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fc.getSelectedFile().getAbsolutePath() + ".oxpos"))) {
                oos.writeObject(new OxposFile(JavaRenderer.game,JavaRenderer.moveNumber));
                JOptionPane.showMessageDialog(null , "Сохранение произошло успешно!", "Сохранить", JOptionPane.INFORMATION_MESSAGE);
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(null , "Ошибка в сохранении файла", "Сохранить", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void load(){
        UIManager.put("FileChooser.saveButtonText", "Сохранить");
        UIManager.put("FileChooser.cancelButtonText", "Отмена");
        UIManager.put("FileChooser.fileNameLabelText", "Наименование файла");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Типы файлов");
        UIManager.put("FileChooser.lookInLabelText", "Директория");
        UIManager.put("FileChooser.saveInLabelText", "Сохранить в директории");
        UIManager.put("FileChooser.folderNameLabelText", "Путь директории");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.oxpos", "oxpos");
        JFileChooser fileOpen = new JFileChooser();
        fileOpen.setFileFilter(filter);
        int ret = fileOpen.showDialog(null, "Открыть файл");
        if (ret == JFileChooser.APPROVE_OPTION) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileOpen.getSelectedFile()))) {
                OxposFile oxposFile = (OxposFile) ois.readObject();
                JavaRenderer.game = oxposFile.getGame();
                JavaRenderer.moveNumber = oxposFile.getMoveNuber();
                JavaRenderer.position = JavaRenderer.game.get(JavaRenderer.moveNumber);
                JOptionPane.showMessageDialog(null , "Загрузка прошла успешно!", "Загрузить", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e1){
                JOptionPane.showMessageDialog(null , "Ошибка в загрузке файла", "Загрузить", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


}
