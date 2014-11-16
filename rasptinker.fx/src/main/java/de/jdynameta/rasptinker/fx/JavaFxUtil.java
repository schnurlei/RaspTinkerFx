/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.rasptinker.fx;

import de.jdynameta.rasptinker.model.TinkerForgeConnection;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;

/**
 *
 * @author rainer
 */
public class JavaFxUtil
{
	public static void createConnectDialog(Stage stage, TinkerForgeConnection connection)
	{
		final TextField txHost = new TextField("localhost");
		final TextField txPort = new TextField("4223");
		final Text errorTxt = new Text("");

		
		Consumer<ActionEvent> eventHandler = (ActionEvent t) ->
		{
			try {
				int port = Integer.parseInt(txPort.getText());
				Dialog d = (Dialog) t.getSource();
				connection.connect(txHost.getText(), port);
				d.hide();
			} catch(NumberFormatException ex) {
				errorTxt.setText("Ungültiger Port");
			}
			
		};

		final Action actionLogin = new Action("Connect", eventHandler);
		Dialog dlg = new Dialog(null, "Connect Dialog");
		txPort.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) ->
		{
		});

		// layout a custom GridPane containing the input fields and labels
		final GridPane content = new GridPane();
		content.setHgap(10);
		content.setVgap(10);

		content.add(new Label("Host"), 0, 0);
		content.add(txHost, 1, 0);
		GridPane.setHgrow(txHost, Priority.ALWAYS);
		content.add(new Label("Port"), 0, 1);
		content.add(txPort, 1, 1);
		GridPane.setHgrow(txPort, Priority.ALWAYS);
		content.add(errorTxt, 0, 2);
		GridPane.setHgrow(errorTxt, Priority.ALWAYS);

		dlg.setResizable(false);
		dlg.setIconifiable(false);
		dlg.setContent(content);
		dlg.getActions().addAll(actionLogin, Dialog.ACTION_CANCEL);

		Platform.runLater(new Runnable()
		{
			public void run()
			{
				txHost.requestFocus();
			}
		});

		dlg.show();
	}
	
}
