import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ActivityToday extends Scene
{
    double height;
    double width;
    BorderPane bp;
    VBox vb = new VBox();
    ScrollPane scrollPane = new ScrollPane(vb);
    ArrayList<String> procs = new ArrayList<String>();
    public ActivityToday(Parent root, double width, double height)
    {
        super(root, width, height);
        this.width = width;
        this.height = height;
        bp = (BorderPane) root;
        start();
    }
    public void start()
    {
        bp.setLeft(scrollPane);
        bp.requestFocus();
        getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        Rectangle r = new Rectangle((int) width, 100);
        Label toplbl = new Label("Activity Today");
        toplbl.setPadding(new Insets(10));
        StackPane sp = new StackPane();
        sp.getChildren().addAll(r, toplbl);
        sp.setAlignment(Pos.CENTER_LEFT);
        bp.setTop(sp);

    }
    public void update(ArrayList<String> procs)
    {
        for(int i = 0; i < procs.size(); i++)
        {
            Button b = new Button(procs.get(i));
            b.setPrefHeight(50);
            if (!this.procs.contains(procs.get(i)))
            {
                Label l = new Label("45 hours and 4 minutes");
                HBox hb = new HBox(b, l);
                hb.setSpacing(10);
                hb.setPadding(new Insets(5));
                hb.setAlignment(Pos.CENTER_LEFT);
                vb.getChildren().add(hb);
            }
            else
            {
                this.procs.remove(procs.get(i));
            }
        }
        for(int i = 0; i < this.procs.size(); i++)
        {
            ObservableList<Node> vblist = vb.getChildren();
            for (int j = 0; j < vblist.size(); j++)
            {
                if (((Button)((HBox)vblist.get(j)).getChildren().get(0)).getText().equals(this.procs.get(i)))
                {
                    vblist.remove(vblist.get(j));
                }
            }
        }
        this.procs = (ArrayList<String>) procs.clone();
    }
}
