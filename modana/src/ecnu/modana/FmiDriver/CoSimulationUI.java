package ecnu.modana.FmiDriver;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import ecnu.modana.alsmc.main.State;
import ecnu.modana.model.ModelManager;
import ecnu.modana.ui.MyTextConvertor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
/**
 * @author JKQ
 *
 * 2015年11月29日下午3:41:30
 */
public class CoSimulationUI 
{
	//plotComposer window
    private Stage propertiesStage = null;
    private String prismModelPath="./fmu.pm",FMUPath="./MyBouncingBall.fmu";
    CoSimulation coSimulation=new CoSimulation("127.0.0.1", 40000);
    LineChart<Object, Number> lineChart=null;
    SplitPane rootPane;
    public ObservableList<MappingList> mappingLists=FXCollections.observableArrayList();
    public ObservableList<FMUPath> FMULists=FXCollections.observableArrayList();
    Label resultLable=null;
    String markovType="dtmc";
    Logger logger = Logger.getRootLogger();
    public void start(Stage plotComposerStage) throws Exception 
	 {
    	plotComposerStage.getIcons().add(new Image("modana-logo.png"));
    	   int width=900,height=650;
			this.propertiesStage = plotComposerStage;
			plotComposerStage.initModality(Modality.WINDOW_MODAL);
			plotComposerStage.setOpacity(0.87);
			plotComposerStage.setTitle("Co-Simulate Markov Chain with FMU");
			plotComposerStage.setWidth(width);
			plotComposerStage.setHeight(height);
			plotComposerStage.centerOnScreen();
			plotComposerStage.setResizable(true);
			
			Group root = new Group();
	        Scene scene = new Scene(root,Color.WHITE);//, 1000, 800, Color.WHITE);

	        //CREATE THE SPLITPANE
	        SplitPane splitPane = new SplitPane();
	        splitPane.setPrefSize(width,height);
	        splitPane.setOrientation(Orientation.VERTICAL);
	        splitPane.setDividerPosition(0, 0.38);
	        //splitPane.setStyle("-fx-background: rgb(0,0,0);");
	        
	        int fmuPaneWidth=420;
	        BorderPane fmuPane=initFMUTable(fmuPaneWidth);
	        fmuPane.setMinSize(fmuPaneWidth, 290);
	        BorderPane mappingPane=initReward(width-fmuPaneWidth);
	        mappingPane.setMinSize(width-fmuPaneWidth, 290);
	        Button btnWork=new Button("Co-Simulation");
	        btnWork.setMaxSize(150, 30);
	        btnWork.setMinSize(150, 30);
	        btnWork.setTranslateX(100-width);
	        btnWork.setTranslateY(295);
	        Label fmuLable=new Label("Co-simulation model table");
	        fmuLable.setMinSize(250, 30);
	        fmuLable.setTranslateX(30-width);
	        fmuLable.setTranslateY(295);
	        Label mappingLable=new Label("Congruent relationship table");
	        mappingLable.setMinSize(220, 30);
	        mappingLable.setTranslateX(250-width);
	        mappingLable.setTranslateY(295);
	        //mappingLable.setVisible(false);
	        
	        HBox hBox=new HBox(5);	        
	        hBox.setTranslateX(10);
	        hBox.setTranslateY(10);
//	        hBox.getChildren().addAll(fmuPane,mappingPane,fmuLable,btnWork,mappingLable);

	        BorderPane upPane = new BorderPane();
	        upPane.setMinHeight(350);
	        upPane.setPrefHeight(350);
	        //leftPane.setStyle("-fx-background-color: #0000AA;");
	        //upPane.getChildren().add(hBox);
//	        upPane.getChildren().addAll(btnPrism,btnFMU);

	        BorderPane downPane = new BorderPane();
	        downPane.setSnapToPixel(true);
//	        downPane.setStyle("-fx-background: rgb(10,10,10);");
	        resultLable=new Label("Co-simulation result");
	        downPane.setCenter(resultLable);
	        
	        hBox.getChildren().addAll(fmuPane,mappingPane,fmuLable,btnWork,mappingLable);
	        upPane.getChildren().addAll(hBox);

	        splitPane.getItems().addAll(upPane, downPane);

	        //ADD SPLITPANE TO ROOT
	        root.getChildren().add(splitPane);
	        
	        btnWork.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<Event>(){
					public void handle(Event e)
					{		
						if(null==coSimulation) coSimulation=new CoSimulation("127.0.0.1", 40000);
						ModelManager.getInstance().logger.error("prismModelPath:"+prismModelPath+",FMUPath:"+FMUPath);
						String modelname = prismModelPath.substring(prismModelPath.lastIndexOf("files")+6,prismModelPath.lastIndexOf(".pm"));
						if(modelname.equals("fmu"))
							lineChart=coSimulation.simulate(prismModelPath, markovType, FMUPath, 5.5, 0.01, false, ',',null,null,false);
						else {
							lineChart=coSimulation.simulate(prismModelPath, markovType, FMUPath, 15.5, 0.01, false, ',',null,null,true);
						}
						//lineChart=coSimulation.simulate(prismModelPath, markovType, FMUPath, 15.5, 0.01, false, ',',new ArrayList<State>(),null,true);
						//new CoSimulationZ("127.0.0.1", 40000).simulate(prismModelPath, "dtmc", FMUPath, 5.5, 0.01, false, ',', "./1.xml");
						try {
							//Stage stage=new Stage();
							//downPane.getChildren().add(stage);
							//myLineChart.start(new Stage());
							lineChart.getStylesheets().add("LineChart.css");
							downPane.setCenterShape(true);
							downPane.setCenter(lineChart);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					} );

	        propertiesStage.initStyle(StageStyle.DECORATED);
	        scene.getStylesheets().add("./Stage.css");
	        propertiesStage.setScene(scene);
	        propertiesStage.show();
		}
	VBox opreateVbox;
	ObservableList<String> mapNames = FXCollections.observableArrayList();
	ObservableList<String> sourceMapNames = FXCollections.observableArrayList();
	ObservableList<String> targetMapNames = FXCollections.observableArrayList();
    private BorderPane initReward(int width) 
    {
		//reward table view
    	BorderPane rewardPane = new BorderPane();
		opreateVbox = new VBox(50);
		opreateVbox.setPadding(new Insets(10));
		opreateVbox.setPrefWidth(10);
		opreateVbox.setTranslateY(90);

		TableView<MappingList> rewardTableView = new TableView<MappingList>();
		rewardTableView.setTooltip(new Tooltip("在表格空白处点击右键，设置协同仿真模型属性之间的对应关系"));
		rewardTableView.setTableMenuButtonVisible(true);
		rewardTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		rewardTableView.setEditable(true);
		// set data of table view
		rewardTableView.setItems(mappingLists);
		rewardTableView.setMinWidth(width-100);
		// add columns
		TableColumn<MappingList, String> sourceCol = new TableColumn<MappingList, String>("Source");
		sourceCol.setEditable(true);
		sourceCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MappingList, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(
							CellDataFeatures<MappingList, String> f) {
						return f.getValue().getSource();
					}
				});
//		sourceCol.setCellFactory(new Callback<TableColumn<MappingList, String>, TableCell<MappingList, String>>() {
//			@Override
//			public TableCell<MappingList, String> call(TableColumn<MappingList, String> f) {
//				TextFieldTableCell<MappingList, String> cell = new TextFieldTableCell<MappingList, String>(new MyTextConvertor());
//				cell.setAlignment(Pos.CENTER);
//				return cell;
//			}
//		});
        sourceCol.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), mapNames));
		sourceCol.setMinWidth((width)/2);
		sourceCol.setMaxWidth((width)/2);
		sourceCol.setPrefWidth(200);
		TableColumn<MappingList, String> rewardValueCol = new TableColumn<MappingList, String>(
				"Target");
		rewardValueCol.setEditable(true);
		rewardValueCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MappingList, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(
							CellDataFeatures<MappingList, String> f) {
						return f.getValue().getTarget();
					}
				});
//		rewardValueCol.setCellFactory(new Callback<TableColumn<MappingList, String>, TableCell<MappingList, String>>() {
//			@Override
//			public TableCell<MappingList, String> call(TableColumn<MappingList, String> f) {
//				TextFieldTableCell<MappingList, String> cell = new TextFieldTableCell<MappingList, String>(new MyTextConvertor());
//				cell.setAlignment(Pos.CENTER);
//				return cell;
//			}
//		});
		rewardValueCol.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), mapNames));
		rewardValueCol.setMinWidth((width)/2);
		rewardValueCol.setMaxWidth((width)/2);
		rewardValueCol.setPrefWidth((width)/2);
		rewardTableView.getColumns().addAll(sourceCol,rewardValueCol);
		
		rewardPane.setCenter(rewardTableView);
		rewardPane.setRight(opreateVbox);
		
		rewardTableView.setTableMenuButtonVisible(true);
	    ContextMenu contextMenu=new ContextMenu();
	    MenuItem menuItem=new MenuItem("Delete");
	    menuItem.setOnAction(new EventHandler<ActionEvent>() {
	    	@Override
			public void handle(ActionEvent e) {
				mappingLists.removeAll(rewardTableView.getSelectionModel().getSelectedItems());
				e.consume();
			}
        });
	    MenuItem menuItemAdd=new MenuItem("Add");
	    menuItemAdd.setOnAction(new EventHandler<ActionEvent>() {
	    	@Override
			public void handle(ActionEvent e) {
	    		mappingLists.add(new MappingList("source", "target"));
	    		e.consume();
			}
        });
        contextMenu.getItems().addAll(menuItemAdd,menuItem);
        rewardTableView.setContextMenu(contextMenu);
        //mappingLists.add(new MappingList("source: from one model", "target: to another model"));
		
		return rewardPane;
		//return rewardTableView;
	}
    public class MappingList{
		private StringProperty source;
		private StringProperty target;
		public MappingList(String source,String target){
			this.source = new SimpleStringProperty(source);
			this.target = new SimpleStringProperty(target);
		}
		public StringProperty getSource() {
			return source;
		}
		public StringProperty getTarget() {
			return target;
		}
	}	 
    String[]fmuVariables,markovVariable;
    private void FillMappingList(){
    	if(null==fmuVariables||fmuVariables.length==0||null==markovVariable||markovVariable.length==0)
    	{
    		logger.error("lack of models or there no variables in model");
    		return;
    	}
    	int i,j;
    	String tailFix="";
    	for(i=0;i<fmuVariables.length;i++){
			if(fmuVariables[i].contains(".in_")){
				sourceMapNames.add(fmuVariables[i]);
				tailFix="out_"+fmuVariables[i].substring(fmuVariables[i].indexOf(".in_")+4);
				for(j=0;j<markovVariable.length;j++){
					if(markovVariable[j].endsWith(tailFix))
						targetMapNames.add(markovVariable[j]);
				}
			}
		}
    	for(i=0;i<markovVariable.length;i++){
			if(markovVariable[i].contains(".in_")){
				sourceMapNames.add(markovVariable[i]);
				tailFix="out_"+markovVariable[i].substring(markovVariable[i].indexOf(".in_")+4);
				for(j=0;j<fmuVariables.length;j++){
					if(fmuVariables[j].endsWith(tailFix))
						targetMapNames.add(fmuVariables[j]);
				}
			}
		}
    	if(sourceMapNames.size()!=targetMapNames.size()){
    		logger.error("model names not match");
    		return;
    	}
    	for(i=0;i<sourceMapNames.size();i++)
    		mappingLists.add(new MappingList(sourceMapNames.get(i),targetMapNames.get(i)));
    }
    private BorderPane initFMUTable(int width) 
    {
    	//reward table view
		BorderPane rewardPane = new BorderPane();
		opreateVbox = new VBox(50);
		opreateVbox.setPadding(new Insets(10));
		opreateVbox.setPrefWidth(10);
		opreateVbox.setTranslateY(90);
		//System.out.print(modelTree.getParent().getValue()+"00000000000000000000");
		//PrismModel prismModel = (PrismModel)ModelManager.getInstance().modelListMap.get(modelTree.getParent().getValue());
		// table view for showing all loaded reward
		TableView<FMUPath> pathTableView = new TableView<FMUPath>();
		pathTableView.setTooltip(new Tooltip("在表格空白处点击右键，选择需要联合仿真的模型（markov模型，modelica模型）"));
		pathTableView.setTableMenuButtonVisible(true);
		//rewardTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		pathTableView.setEditable(true);
		// set data of table view
		pathTableView.setItems(FMULists);
		pathTableView.setMinWidth(width-100);
		// add columns
		TableColumn<FMUPath, String> pathCol = new TableColumn<FMUPath, String>("Path");
		pathCol.setText("right-click table and select co-simulation model");
		pathCol.setEditable(true);
		pathCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<FMUPath, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(
							CellDataFeatures<FMUPath, String> f) {
						return f.getValue().getPath();
					}
				});
		pathCol.setCellFactory(new Callback<TableColumn<FMUPath, String>, TableCell<FMUPath, String>>() {
			@Override
			public TableCell<FMUPath, String> call(TableColumn<FMUPath, String> f) {
				TextFieldTableCell<FMUPath, String> cell = new TextFieldTableCell<FMUPath, String>(new MyTextConvertor());
				cell.setAlignment(Pos.CENTER);
				return cell;
			}
		});
		pathCol.setMinWidth((width));
		pathCol.setMaxWidth((width));
		pathCol.setPrefWidth(200);
		
		pathTableView.getColumns().addAll(pathCol);
		
		rewardPane.setCenter(pathTableView);
		rewardPane.setRight(opreateVbox);
		
		pathTableView.setTableMenuButtonVisible(true);
	    ContextMenu contextMenu=new ContextMenu();
	    MenuItem menuItem=new MenuItem("Delete");
	    menuItem.setOnAction(new EventHandler<ActionEvent>() {
	    	@Override
			public void handle(ActionEvent e) {
	    		String path=pathTableView.getSelectionModel().getSelectedItems().get(0).toString();
	    		System.err.println(path);
	    		String name=path.substring(path.lastIndexOf('/')+1);
	    		name=name.substring(0,name.lastIndexOf('.')+1);
	    		for(int i=0;i<mapNames.size();i++)
	    			if(mapNames.get(i).startsWith(name)) 
	    				mapNames.remove(i--);
				FMULists.removeAll(pathTableView.getSelectionModel().getSelectedItems());
				e.consume();
			}
        });
	    MenuItem menuItemAdd=new MenuItem("Add FMU");
	    menuItemAdd.setOnAction(new EventHandler<ActionEvent>() {
	    	@Override
			public void handle(ActionEvent e) {
	    		String file=ChooseFile("./files/","FMU(*.fmu)","*.fmu");
	    		if(null==file) return;
	    		fmuVariables=coSimulation.GetFMUVariables(file);
	    		if(null==fmuVariables||fmuVariables.length==0) return;
	    		for(int i=0;i<fmuVariables.length;i++){
	    			mapNames.add(fmuVariables[i]);
	    		}
	    		FMUPath=file;
	    		FMULists.add(new FMUPath(file));
	    		FillMappingList();
	    		e.consume();
			}
        });
	    MenuItem menuItemAddMarkov=new MenuItem("Add Markov");
	    menuItemAddMarkov.setOnAction(new EventHandler<ActionEvent>() {
	    	@Override
			public void handle(ActionEvent e) {
	    		String file=ChooseFile("./files/","Markov(*.pm|*.sm)","*.pm");
	    		if(null==file) return;
	    		markovVariable=coSimulation.GetMarkovVariables(file);
	    		if(null==markovVariable||markovVariable.length==0) return;
	    		for(int i=0;i<markovVariable.length;i++){
	    			mapNames.add(markovVariable[i]);
	    		}
	    		prismModelPath=file;
	    		FMULists.add(new FMUPath(file));
	    		FillMappingList();
	    		if(file.endsWith(".pm")) markovType="dtmc";
	    		else if(file.endsWith(".cm")) markovType="ctmc";
	    		e.consume();
			}
        });
        contextMenu.getItems().addAll(menuItemAdd,menuItemAddMarkov,menuItem);
        pathTableView.setContextMenu(contextMenu);
		
		return rewardPane;
		//return rewardTableView;
	}
    public class FMUPath
    {
		private StringProperty path;
		public FMUPath(String path){
			this.path = new SimpleStringProperty(path);
		}
		public StringProperty getPath() {
			return path;
		}
	}	 
    private String ChooseFile(String initialDirectory,String name,String extension)
    {
    	final FileChooser fileChooser = new FileChooser();
    	fileChooser.setInitialDirectory(new File(initialDirectory));
    	FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(name,extension);
		fileChooser.getExtensionFilters().add(extFilter);
    	String fp;
		File file = fileChooser.showOpenDialog(new Stage());
		fp=file.getAbsolutePath().replace("\\", "/");
//		fp="D:/WorkSpace/Java/win32/modana/fmu.xml";
		//fp="D:/WorkSpace/Java/win32/modana/CTMC.xml";
		if (file != null) {
			return fp;
			//TODO open model!!
			//new PrismModel("","./files/prism.ecore", "PrismModel").LoadFromFile(fp);
//			logger.debug("model opened!");
		}	
		return null;
    }
}
