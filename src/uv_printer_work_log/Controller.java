package uv_printer_work_log;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML private TextField uiLogPathInput;
    @FXML private Button uiActionBtn;
    @FXML private Label uiTotalLabel;
    @FXML private Label uiAbortedLabel;
    @FXML private Label uiFailedLabel;
    @FXML private Label uiSuccessfullLabel;
    @FXML private Label uiUnknownLabel;
    @FXML private ProgressIndicator uiSpinner;


    private int aborted = 0, failed = 0, successful = 0, total = 0, unknown = 0;
    @Override
    public void initialize(URL url, ResourceBundle rb){

        uiActionBtn.setOnMouseClicked(ev ->{

            uiActionBtn.setDisable(true);
            uiSpinner.setVisible(true);
            uiLogPathInput.setDisable(true);
            String path = uiLogPathInput.getText();
            if( !path.equals("") ) {
                Thread th = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            File input = new File(path);
                            Document document = Jsoup.parse(input, "UTF-8", "");
                            Elements tables = document.select("table");
                            for( int k = 0; k < tables.size(); k++ ){
                                Elements rows = tables.get(k).select("tr");
                                if( rows.get(0).text().contains("RIP Job") ) continue;
                                try {
                                    total++;
                                    if( rows.get(35).select("th").text().contains("Info") ){
                                        aborted++;
                                        continue;
                                    }
                                    if( rows.get(35).select("th").text().contains("Error") ){
                                        failed++;
                                        continue;
                                    }
                                    successful++;
                                } catch( IndexOutOfBoundsException e ){
                                    unknown++;
                                    //e.printStackTrace();
                                }
                            }
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    uiActionBtn.setDisable(false);
                                    uiSpinner.setVisible(false);
                                    uiLogPathInput.setDisable(false);
                                    uiTotalLabel.setText( String.valueOf(total));
                                    uiAbortedLabel.setText( String.valueOf(aborted));
                                    uiFailedLabel.setText( String.valueOf(failed));
                                    uiSuccessfullLabel.setText( String.valueOf(successful));
                                    uiUnknownLabel.setText( String.valueOf(unknown));
                                }
                            });
                        } catch( IOException e ){
                            e.printStackTrace();
                        }
                    }
                });
                th.setDaemon(true);
                th.start();
            }


        });
    }

}