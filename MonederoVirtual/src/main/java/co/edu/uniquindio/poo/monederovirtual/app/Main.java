package co.edu.uniquindio.poo.monederovirtual.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;

import static javafx.application.Application.launch;

public class Main {
    Override
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
