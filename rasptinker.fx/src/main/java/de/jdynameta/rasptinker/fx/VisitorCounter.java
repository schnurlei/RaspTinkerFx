/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.rasptinker.fx;

import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.BrickletMotionDetector;
import com.tinkerforge.BrickletPiezoSpeaker;
import de.jdynameta.rasptinker.model.DualButtonCounter;
import de.jdynameta.rasptinker.model.TinkerForgeConnection;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author rainer
 */
public class VisitorCounter extends Application
{
	private final TinkerForgeConnection connection;

	public VisitorCounter()
	{
		this.connection = new TinkerForgeConnection();
		BrickletLCD20x4 lcd = this.connection.addLCD20x4("oc7","Linken Knopf drücken");
		BrickletPiezoSpeaker speaker = this.connection.addPiezoSpeaker("mHP");
		BrickletMotionDetector motionDetector = this.connection.addMotionDetector("oUu");
		DualButtonCounter counter = new DualButtonCounter(connection,"mtw");
	}
	
	
	
	@Override
	public void start(Stage primaryStage)
	{
		primaryStage.setTitle("Besucherzähler");

		BorderPane border = new BorderPane();
		border.setTop(createTopPane(primaryStage));

        final VBox box = new VBox();
        final ObservableList<Node> boxChildren = box.getChildren();
		border.setCenter(box);

//		Text scenetitle = new Text("Welcome");
//		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
//		grid.add(scenetitle, 0, 0, 2, 1);


		Scene scene = new Scene(border, 300, 275);
		primaryStage.setScene(scene);

		primaryStage.show();	}

	private Pane createTopPane(Stage primaryStage) {
		
		Button connectBtn = new Button("Connect");
		connectBtn.setOnAction((ActionEvent e) ->
		{
			JavaFxUtil.createConnectDialog(primaryStage, this.connection);
		});
		Button disconnectBtn = new Button("Disconnect");
		disconnectBtn.setOnAction((ActionEvent e) ->
		{
			this.connection.disconnect();
		});
		HBox hbBtn = new HBox(10, connectBtn, disconnectBtn);
		
		return hbBtn;
	}

	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		launch(args);
	}
	
}
