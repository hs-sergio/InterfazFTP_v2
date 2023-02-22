package com.example.interfazftp_v2;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.net.ftp.*;


import java.net.URL;
import java.util.ResourceBundle;

public class ControllerFTP implements Initializable {
    @FXML
    private TreeView TreeViewRemote;
    @FXML
    private Button btnConnect;
    @FXML
    private TextField txtServer;
    @FXML
    private TextField txtUser;
    @FXML
    private TextField txtPass;

    @FXML
    private TreeView TreeViewLocal;
    @FXML
    private Button btnLocalDirectory;
    @FXML
    private Button btnUpload;
    @FXML
    private Button btnDeleteRemoteFile;
    @FXML
    private Button btnDeleteLocalFile;

    // create a alert
    Alert alert = new Alert(Alert.AlertType.NONE);

    String folder = "C:\\Users\\gilni\\Desktop\\CLASE\\Interfaces_JavaFX\\InterfazFTP_v2\\src\\main\\resources\\com\\example\\interfazftp_v2\\img\\folder.png";
    String image = "C:\\Users\\gilni\\Desktop\\CLASE\\Interfaces_JavaFX\\InterfazFTP_v2\\src\\main\\resources\\com\\example\\interfazftp_v2\\img\\image.png";
    String pdf = "C:\\Users\\gilni\\Desktop\\CLASE\\Interfaces_JavaFX\\InterfazFTP_v2\\src\\main\\resources\\com\\example\\interfazftp_v2\\img\\pdf.png";
    String video = "C:\\Users\\gilni\\Desktop\\CLASE\\Interfaces_JavaFX\\InterfazFTP_v2\\src\\main\\resources\\com\\example\\interfazftp_v2\\img\\video.png";
    String txt = "C:\\Users\\gilni\\Desktop\\CLASE\\Interfaces_JavaFX\\InterfazFTP_v2\\src\\main\\resources\\com\\example\\interfazftp_v2\\img\\txt.png";
    String unknown = "C:\\Users\\gilni\\Desktop\\CLASE\\Interfaces_JavaFX\\InterfazFTP_v2\\src\\main\\resources\\com\\example\\interfazftp_v2\\img\\unknown.png";


    String localPath = "";

    FTPClient cliente = new FTPClient(); // Creamos el cliente FTP


    @Override
    public void initialize(URL location, ResourceBundle resources) {


        btnConnect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Connect();
            }
        });

        btnLocalDirectory.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getLocalFilesAndFill();
            }
        });

        btnUpload.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    UploadLocalToRemote();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btnDeleteRemoteFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                deleteRemoteFile();
            }
        });

        btnDeleteLocalFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                deleteLocalFiles();
            }
        });



    }

    public void SelectItemRemote() {

        TreeItem<String> item = (TreeItem<String>) TreeViewRemote.getSelectionModel().getSelectedItem();

        if (item != null) {
            System.out.println(item.getValue());
        }
    }

    public void SelectItemLocal(){
        TreeItem<String> item = (TreeItem<String>) TreeViewLocal.getSelectionModel().getSelectedItem();

        if (item != null) {
            System.out.println(item.getValue());
            System.out.println(localPath+"\\"+item.getValue());
        }
    }

    public void UploadLocalToRemote() throws IOException {

        alert.setAlertType(Alert.AlertType.CONFIRMATION);
        alert.setTitle("CONFIRMACIÓN SUBIDA ARCHIVO");
        alert.setContentText("Estas seguro que quieres subir el archivo seleccionado?");
        Optional<ButtonType> action = alert.showAndWait();

        if(action.get() == ButtonType.OK){
            String archivo = "";

            TreeItem<String> item = (TreeItem<String>) TreeViewLocal.getSelectionModel().getSelectedItem();
            if (item != null) {
                System.out.println(item.getValue());
                archivo = localPath+"\\"+item.getValue();
                System.out.println(archivo);
            }

            cliente.setFileType(FTP.BINARY_FILE_TYPE);

            String archivo_a_subir = archivo;

            // Creamos un BufferedInputStream ya que tenemos que subir un binario
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(archivo_a_subir));
            cliente.storeFile("REMOTO_"+item.getValue(), in);



            getRemoteFilesAndFill();
        }
    }

    public void deleteRemoteFile(){

        alert.setAlertType(Alert.AlertType.WARNING);
        alert.setTitle("BORRAR ARCHIVO REMOTO");
        alert.setHeaderText("SE BORRARÁ EL ARCHIVO DEL DIRECTORIO REMOTO");
        alert.setContentText("Estas seguro que quieres BORRAR el archivo seleccionado?");
        Optional<ButtonType> action = alert.showAndWait();

        if(action.get() == ButtonType.OK){
            String archivo = "";
            TreeItem<String> item = (TreeItem<String>) TreeViewRemote.getSelectionModel().getSelectedItem();

            if (item != null) {
                System.out.println(item.getValue());
                archivo = item.getValue();
                System.out.println(archivo);

                try {
                    cliente.deleteFile(archivo);
                    getRemoteFilesAndFill();
                } catch (IOException e) {
                    System.out.println("ERROR AL BORRAR EL ARCHIVO");
                    e.printStackTrace();
                }
            }
        }
    }

    public void deleteLocalFiles(){
        alert.setAlertType(Alert.AlertType.WARNING);
        alert.setTitle("BORRAR ARCHIVO LOCAL");
        alert.setHeaderText("SE BORRARÁ EL ARCHIVO DEL DIRECTORIO LOCAL");
        alert.setContentText("Estas seguro que quieres BORRAR el archivo seleccionado?");
        Optional<ButtonType> action = alert.showAndWait();

        if(action.get() == ButtonType.OK){
            String archivo = "";
            TreeItem<String> item = (TreeItem<String>) TreeViewLocal.getSelectionModel().getSelectedItem();
            if (item != null) {
                System.out.println(item.getValue());
                archivo = localPath+"\\"+item.getValue();
                System.out.println(archivo);

                File localFile = new File(archivo);
                if(localFile.delete()){
                    System.out.println("Archivo borrado correctamente");
                    getLocalFilesAndFill();
                }else{
                    System.out.println("NO SE PUDO BORRAR EL ARCHIVO LOCAL");
                }

            }
        }


    }

    public void Connect() {

        try {
            cliente.connect(txtServer.getText());
            cliente.enterLocalPassiveMode(); // Entramos en modo pasivo

            //  boolean login = cliente.login(txtUser.getText(), txtPass.getText());
            // Voy a falsear la conexión porque me da pereza estar escribiendo
            boolean login = cliente.login("alumno", "alumno");

            if (login) {
                // Si nos hemos conectado
                System.out.println("Estamos dentro!");

                getRemoteFilesAndFill(); // Llamamos al método para obtener los archivos del servidor FTP
            } else {
                // Si no nos hemos conectado
                System.out.println("No te has podido conectar!");
            }


        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void getRemoteFilesAndFill() {

        try {
            FTPFile[] file = cliente.listFiles();
            FTPFile[] directory = cliente.listDirectories();


            String DirectorioRemoto = cliente.printWorkingDirectory();

            System.out.println(cliente.getRemoteAddress());
            TreeItem<String> rootItem = new TreeItem<>(DirectorioRemoto, new ImageView(new Image(folder)));

            System.out.println("============= REMOTO =============");
            System.out.println("Numero de carpetas: " + directory.length);
            System.out.println("Numero de archivos: " + directory.length);

            for (FTPFile d : directory) {
                System.out.println("Nombre: " + d.getName());
                System.out.println("\tTamaño directorio: " + d.getSize());
                System.out.println("\tFecha: " + d.getTimestamp().getTime());
                System.out.println(" "); // Separador
                System.out.println("");

                // Creamos dinámicamente branchItems del TreeItem para añadir los directorios
                TreeItem<String> branchItem = new TreeItem<>(d.getName(), new ImageView(new Image(folder)));
                rootItem.getChildren().add(branchItem);
            }

            for (FTPFile a : file) {

                String fileName = a.getName();
                String extension = "";
                System.out.println("Nombre: " + a.getName());
                System.out.println("\tTamaño: " + a.getSize());
                System.out.println("\tFecha: " + a.getTimestamp().getTime());
                System.out.println(" "); // Separador

                int i = fileName.lastIndexOf(".");
                if (i > 0) {
                    extension = fileName.substring(i + 1);
                }

                System.out.println("EXTENSION: " + extension);

                if (extension.equals("pdf")) {
                    TreeItem<String> leafItem = new TreeItem<>(a.getName(), new ImageView(new Image(pdf)));
                    rootItem.getChildren().add(leafItem);
                } else if (extension.equals("png") || extension.equals("jpg")) {
                    TreeItem<String> leafItem = new TreeItem<>(a.getName(), new ImageView(new Image(image)));
                    rootItem.getChildren().add(leafItem);
                } else if (extension.equals("mp4")) {
                    TreeItem<String> leafItem = new TreeItem<>(a.getName(), new ImageView(new Image(video)));
                    rootItem.getChildren().add(leafItem);
                } else if (extension.equals("txt")) {
                    TreeItem<String> leafItem = new TreeItem<>(a.getName(), new ImageView(new Image(txt)));
                    rootItem.getChildren().add(leafItem);
                } else {
                    // Creamos dinámicamente leafItems del TreeItem para añadir los archivos
                    TreeItem<String> leafItem = new TreeItem<>(a.getName(), new ImageView(new Image(unknown)));
                    rootItem.getChildren().add(leafItem);
                }



            }

//            TreeViewRemote.setShowRoot(false);  Esto para quitar el primer directorio y que salgan directamente los archivos
            TreeViewRemote.setRoot(rootItem);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        System.out.println("============= REMOTO =============");
    }

    public void getLocalFilesAndFill() {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("ELEGIR DIRECTORIO LOCAL");
        File newDirectory = chooser.showOpenDialog(Stage.getWindows().get(0));
        String path = newDirectory.getParent();
        System.out.println(path);
        localPath = path;



        TreeItem<String> root = new TreeItem<>(new String(path));
        TreeViewLocal.setRoot(root);


        File[] fileList = new File(path).listFiles();
        for (File fileLocal : fileList) {
            String fileName = fileLocal.getName();
            String extension = "";

            System.out.println("============= LOCAL =============");
            System.out.println(fileLocal.getName());
            System.out.println(fileLocal.getPath());

            int i = fileName.lastIndexOf(".");
            if (i > 0) {
                extension = fileName.substring(i + 1);
            }

            System.out.println("EXTENSION: " + extension);

            if (extension.equals("pdf")) {
                TreeItem<String> leafItem = new TreeItem<>(fileLocal.getName(), new ImageView(new Image(pdf)));
                root.getChildren().add(leafItem);
            } else if (extension.equals("png") || extension.equals("jpg")) {
                TreeItem<String> leafItem = new TreeItem<>(fileLocal.getName(), new ImageView(new Image(image)));
                root.getChildren().add(leafItem);
            } else if (extension.equals("mp4")) {
                TreeItem<String> leafItem = new TreeItem<>(fileLocal.getName(), new ImageView(new Image(video)));
                root.getChildren().add(leafItem);
            } else if (extension.equals("txt")) {
                TreeItem<String> leafItem = new TreeItem<>(fileLocal.getName(), new ImageView(new Image(txt)));
                root.getChildren().add(leafItem);
            } else {
                // Creamos dinámicamente leafItems del TreeItem para añadir los archivos
                TreeItem<String> leafItem = new TreeItem<>(fileLocal.getName(), new ImageView(new Image(unknown)));
                root.getChildren().add(leafItem);
            }

            System.out.println("============= LOCAL =============");

        }

    }


}