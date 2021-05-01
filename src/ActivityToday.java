import javafx.application.Platform;
import javafx.collections.FXCollections;
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
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.*;

public class ActivityToday extends Scene
{
    double height;
    double width;
    BorderPane bp;
    VBox vb = new VBox();
    ScrollPane scrollPane = new ScrollPane(vb);
    HBox lefthb = new HBox(scrollPane);
    VBox centbutt = new VBox();
    HashMap<String, Label> buttlbl = new HashMap<>();
    ArrayList<String> procs = new ArrayList<>();
    String selected = "";
    Button selectbutt = null;
    Button delbutt = new Button("Ignore");
    Button addbutt = new Button("Program not on list?");
    Stage stage;
    boolean startup = true;
    class ProcSort implements Comparator<Node>
    {
        @Override
        public int compare(Node o2, Node o1)
        {
            return Info.todayProg.get(((Button) ((HBox)o1).getChildren().get(0)).getText()) - Info.todayProg.get(((Button) ((HBox)o2).getChildren().get(0)).getText());
        }
    }

    public ActivityToday(Parent root, double width, double height, Stage stage)
    {
        super(root, width, height);
        this.stage = stage;
        this.width = width;
        this.height = height;
        bp = (BorderPane) root;
        start();
    }
    public void start()
    {
        centbutt.setPadding(new Insets(10));
        centbutt.setSpacing(10);
        centbutt.getChildren().add(delbutt);
        delbutt.setDisable(true);
        Region re = new Region();
        re.setPrefSize(50, 50);
        centbutt.getChildren().add(re);
        centbutt.getChildren().add(addbutt);
        Timer t = new Timer();
        t.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                if (selectbutt == null || !selectbutt.isFocused() && !delbutt.isFocused())
                {
                    selected = "";
                }
                if (!procs.contains(selected))
                {
                    Platform.runLater(() ->
                    {
                        delbutt.setDisable(true);
                        selectbutt = null;
                        if (!stage.isFocused())
                        {
                            bp.requestFocus();
                        }
                    });
                }
            }
        }, 0, 10);
        lefthb.getChildren().add(centbutt);
        bp.setLeft(lefthb);
        bp.requestFocus();
        getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        Rectangle r = new Rectangle((int) width, 100);
        r.setId("todayrect");
        Label toplbl = new Label("Activity Today");
        r.setFill(Color.color(0.9, 0.1, 0.8));
        toplbl.setTextFill(Color.color(1, 1, 1));
        toplbl.setFont(new Font(40));
        toplbl.setPadding(new Insets(10));
        StackPane sp = new StackPane();
        sp.getChildren().addAll(r, toplbl);
        sp.setAlignment(Pos.CENTER_LEFT);
        bp.setTop(sp);
        addbutt.setOnAction(e ->
        {
            Stage addStage = new Stage();
            BorderPane bpp = new BorderPane();
            AddProcess addScene = new AddProcess(addStage, bpp, 450, 200);
            addStage.setTitle("Add Process");
            addStage.setScene(addScene);
            addStage.show();
            addStage.setAlwaysOnTop(true);
        });


    }
    public void update(ArrayList<String> procs)
    {
        for (String proc : procs)
        {
            if (Info.todayProg.containsKey(proc))
            {
                if (!startup)
                {
                    Info.todayProg.put(proc, Info.todayProg.get(proc) + 1);
                }
                else
                {
                    Info.todayProg.put(proc, Info.todayProg.get(proc));
                }
            }
            else
            {
                Info.todayProg.put(proc, 0);
            }
        }
        startup = false;
        for(String prog : Info.todayProg.keySet())
        {
            if (Info.ignoreList.contains(prog))
            {
                continue;
            }
            if (!this.procs.contains(prog))
            {
                Button b = new Button(prog);
                b.setOnAction(e ->
                {
                    selected = b.getText();
                    selectbutt = b;
                    delbutt.setDisable(false);
                    delbutt.setOnAction(ev ->
                    {
                        Info.ignoreList.add(selected);
                        selectbutt.requestFocus();
                        try
                        {
                            Info.updateJSON(Info.file);
                        }
                        catch (Exception exception)
                        {
                            exception.printStackTrace();
                        }
                        ObservableList<Node> vblist = vb.getChildren();
                        for (int i = 0; i < vblist.size(); i++)
                        {
                            if (selected.equals(((Button) ((HBox) vblist.get(i)).getChildren().get(0)).getText()))
                            {
                                vb.getChildren().remove(vblist.get(i));
                                selectbutt = null;
                                bp.requestFocus();
                                break;
                            }
                        }
                        delbutt.setDisable(true);
                    });
                });
                b.setPrefHeight(50);
                buttlbl.put(prog, new Label(String.valueOf(Info.todayProg.get(prog) / 60) + " hours " + String.valueOf(Info.todayProg.get(prog) % 60) + " minutes "));
                buttlbl.get(prog).setFont(new Font(15));
                buttlbl.get(prog).setPadding(new Insets(0, 0, 0, 20));
                HBox hb = new HBox(b, buttlbl.get(prog));
                hb.setSpacing(10);
                hb.setPadding(new Insets(5));
                hb.setAlignment(Pos.CENTER_LEFT);
                vb.getChildren().add(hb);
                this.procs.add(prog);
            }
            else
            {
                buttlbl.get(prog).setText(String.valueOf(Info.todayProg.get(prog) / 60) + " hours " + String.valueOf(Info.todayProg.get(prog) % 60) + " minutes ");
            }
        }
        ObservableList<Node> vblist = vb.getChildren();
        for (int j = 0; j < vblist.size(); j++)
        {
            if (!Info.todayProg.containsKey(((Button) ((HBox) vblist.get(j)).getChildren().get(0)).getText()))
            {
                this.procs.remove(vblist.get(j));
                vblist.remove(vblist.get(j));
            }
        }
        ObservableList<Node> ol = FXCollections.observableArrayList(vb.getChildren());
        ol.sort(new ProcSort());
        vb.getChildren().setAll(ol);
    }
}
