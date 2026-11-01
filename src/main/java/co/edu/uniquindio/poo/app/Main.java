package co.edu.uniquindio.poo.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage ventana) throws Exception {
        Parent raiz = FXMLLoader.load(getClass().getResource("/co/edu/uniquindio/poo/view/MonederoVista.fxml"));
        ventana.setTitle("Monedero Virtual con Sistema de Puntos");
        ventana.getIcons().add(new Image(getClass().getResourceAsStream("/co/edu/uniquindio/poo/view/imagenes/icono.png")));
        ventana.setScene(new Scene(raiz));
        ventana.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

