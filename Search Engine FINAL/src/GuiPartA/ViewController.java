package GuiPartA;

import PartB.Query;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import PartA.*;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * The class is responsible on the GUI
 */

public class ViewController implements Observer {

    private Model model;
    public String CorPath ="-1";
    public String filePath= "-1";
    public String queriesPath ="-1";
    private Stage stage;
    public CheckBox isStemmer;
    public CheckBox isSemmanitc;
    public Label disCor;
    public Label disFile;
    public TextField query;
    public static int counter = 0;
    private boolean isStartedOrLoad = false;
    private boolean isDeleted = false;
    private boolean isSearched = false;
//    public TableView<TermData> dictionary;


    /**
     * each function is related to another bottom
     *
     * @param m
     */
    public void setModel(Model m){
        this.model=m;
    }

    @Override
    public void update(Observable o, Object arg) {
    }


    public void setStage(Stage s){
        this.stage = s;
    }

    public void selectCorPath(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectDirectory = directoryChooser.showDialog(stage);
        try {
            CorPath = selectDirectory.getPath();
            disCor.setText(CorPath);
        }
        catch (NullPointerException e){

        }
    }


    public void selectFilePath(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectDirectory = directoryChooser.showDialog(stage);
        try {
            filePath = selectDirectory.getPath();
            disFile.setText(filePath);
        }catch (NullPointerException e){

        }
    }

    public void selectQueriesPath(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectDirectory = directoryChooser.showDialog(stage);
        try {
            queriesPath = selectDirectory.getPath();
            disFile.setText(queriesPath);
        }
        catch (NullPointerException e){
            ShowAlert("No queries were found.");
        }
    }


    public void selectRunQueries() {
        if (isStartedOrLoad) {
            if (!query.getText().isEmpty()) {
                int id = counter++;
                String idS = "" + id;
                Query tmpQ = new Query(idS, query.getText().toLowerCase(), null);
                try {
                    model.selectRunByQuery(tmpQ);
                } catch (IOException e) {

                }
            }

//        model.initSrc(CorPath,isStemmer.isSelected());
            else {
                if (!queriesPath.equals("-1")) {
                    try {

                        model.setRankIfSemmantic(isSemmanitc.isSelected());
                        model.setRankIfStemmer(isStemmer.isSelected());
                        model.selectRunByPath(queriesPath);
                    } catch (IOException e) {
                        ShowAlert("Invalid");
                    }
                }
            }
            isSearched = true;
            ShowAlert("Search Done");


        }
        else {
            ShowAlert("Please run the indexer first");
        }
    }

    public void selectStart() throws InterruptedException, IOException {

        if(CorPath.equals("-1") || filePath.equals("-1")){
            ShowAlert("One or both of the path are invalid.");
        }
        else {
            if(!model.FolderIsEmpty(filePath,isStemmer.isSelected())){
                ShowAlert("There is parsing files located in the file path, to start the indexer please make sure you are deleting the old files by pressing Delete All");
            }else {
                model.SetIdx(new Indexer(new ReadFile(CorPath), filePath, new Parse(isStemmer.isSelected(), CorPath + "/stop_words.txt")));
                model.selectBuildIndexer();
                model.initSrc(CorPath,isStemmer.isSelected());
                ShowAlert(model.printDetails());
                isStartedOrLoad = true;
                isDeleted = false;

            }
        }
    }

    public void selectDeleteAll(){
        if(filePath.equals("-1")){
            ShowAlert("The posting file path is empty, add a path and try again.");
        }
        else {
            if(!isDeleted) {
                model.selectDeleteAll(filePath);
                isStartedOrLoad = false;
                isDeleted = true;
                ShowAlert("The files were deleted successfully");
            }
            else {
                ShowAlert("There is no file to delete.");
            }
        }
    }

    public void selectLoad() {
        if(CorPath.equals("-1")){
            ShowAlert("Please Select stop words path.");
        }

        if (filePath.equals("-1")) {
            ShowAlert("The posting file path is empty.");
        } else {
            model.SetIdx(new Indexer(filePath));
            boolean isLoaded = model.selectLoad(isStemmer.isSelected());
            if(!isLoaded){
                ShowAlert("The Folder Is Empty.");
            }else{
                isStartedOrLoad = true;
            }
            if(CorPath != null) {
                model.initSrc(CorPath, isStemmer.isSelected());
            }
            else {
                ShowAlert("Please enter stop words path.");
            }
        }
    }

    public void selectQueries(){
        if(isSearched) {
            Stage stage = new Stage();
            stage.setTitle("Display Queries");

            Map<String, Map<String, Double>> rankingMap = model.getRankingMap();

            TableColumn<Map.Entry<String, Map<String, Double>>, String> column1 = new TableColumn<>("Q ID");
            column1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Map<String, Double>>, String>, ObservableValue<String>>() {

                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, Map<String, Double>>, String> p) {
                    // this callback returns property for just one cell, you can't use a loop here
                    // for first column we use key
                    return new SimpleObjectProperty<String>(p.getValue().getKey());
                }
            });

            TableColumn<Map.Entry<String, Map<String, Double>>, Void> colBtn = new TableColumn("Button Column");

            Callback<TableColumn<Map.Entry<String, Map<String, Double>>, Void>, TableCell<Map.Entry<String, Map<String, Double>>, Void>> cellFactory = new Callback<TableColumn<Map.Entry<String, Map<String, Double>>, Void>, TableCell<Map.Entry<String, Map<String, Double>>, Void>>() {
                @Override
                public TableCell<Map.Entry<String, Map<String, Double>>, Void> call(final TableColumn<Map.Entry<String, Map<String, Double>>, Void> param) {
                    final TableCell<Map.Entry<String, Map<String, Double>>, Void> cell = new TableCell<Map.Entry<String, Map<String, Double>>, Void>() {

                        private final Button btn = new Button("show rating");

                        {
                            btn.setOnAction((ActionEvent event) -> {
                                Map.Entry<String, Map<String, Double>> data = getTableView().getItems().get(getIndex());
                                selectDisplayRankQueries(data.getValue());
                            });
                        }

                        @Override
                        public void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(btn);
                            }
                        }
                    };
                    return cell;
                }
            };

            colBtn.setCellFactory(cellFactory);

            TableView<Map.Entry<String, Map<String, Double>>> dictionary = new TableView<>();
            ObservableList<Map.Entry<String, Map<String, Double>>> items = FXCollections.observableArrayList(rankingMap.entrySet());

            dictionary.setItems(items);
            dictionary.getColumns().setAll(column1, colBtn);

            Scene scene = new Scene(dictionary);
            stage.setScene(scene);
            stage.show();
        }
        else {
            ShowAlert("Search First");
        }
    }


    private void selectDisplayRankQueries(Map<String,Double> rankingMap){
        Stage stage = new Stage();
        stage.setTitle("Q Rank");


        TableColumn<Map.Entry<String, Double>, String> column1 = new TableColumn<>("Doc Number");
        column1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Double>, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, Double>, String> p) {
                // this callback returns property for just one cell, you can't use a loop here
                // for first column we use key
                return new SimpleObjectProperty<String>(p.getValue().getKey());
            }
        });

        TableColumn<Map.Entry<String, Double>,Double> column2 = new TableColumn<>("Rank");
        column2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Double>, Double>, ObservableValue<Double>>() {

            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<Map.Entry<String, Double>, Double> p) {
                // for second column we use value
                return new SimpleObjectProperty<Double>(p.getValue().getValue());
            }
        });

        TableColumn<Map.Entry<String, Double>, Void> colBtn = new TableColumn("Button Column");
        Callback<TableColumn<Map.Entry<String, Double>, Void>, TableCell<Map.Entry<String, Double>, Void>> cellFactory = new Callback<TableColumn<Map.Entry<String, Double>, Void>, TableCell<Map.Entry<String, Double>, Void>>() {
            @Override
            public TableCell<Map.Entry<String, Double>, Void> call(final TableColumn<Map.Entry<String, Double>, Void> param) {
                final TableCell<Map.Entry<String, Double>, Void> cell = new TableCell<Map.Entry<String, Double>, Void>() {

                    private final Button btn = new Button("Entities");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Map.Entry<String, Double> data = getTableView().getItems().get(getIndex());
                            selectDisplayEntities(model.getEntitiesMap(data.getKey()));
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        colBtn.setCellFactory(cellFactory);

        ObservableList<Map.Entry<String, Double>> items = FXCollections.observableArrayList(rankingMap.entrySet());

        TableView<Map.Entry<String,Double>> dictionary = new TableView<>();

        dictionary.setItems(items);
        dictionary.getColumns().setAll(column1,column2,colBtn);

        Scene scene = new Scene(dictionary);
        stage.setScene(scene);
        stage.show();
    }


    private void selectDisplayEntities(Map<String,Integer> rankingMap){
        Stage stage = new Stage();
        stage.setTitle("Entities Rank");


        TableColumn<Map.Entry<String, Integer>, String> column1 = new TableColumn<>("Entity");
        column1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Integer>, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, Integer>, String> p) {
                // this callback returns property for just one cell, you can't use a loop here
                // for first column we use key
                return new SimpleObjectProperty<String>(p.getValue().getKey());
            }
        });

        TableColumn<Map.Entry<String, Integer>,Integer> column2 = new TableColumn<>("Rank");
        column2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Integer>, Integer>, ObservableValue<Integer>>() {

            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Map.Entry<String, Integer>, Integer> p) {
                // for second column we use value
                return new SimpleObjectProperty<Integer>(p.getValue().getValue());
            }
        });

        ObservableList<Map.Entry<String, Integer>> items = FXCollections.observableArrayList(rankingMap.entrySet());

        TableView<Map.Entry<String,Integer>> dictionary = new TableView<>();

        dictionary.setItems(items);
        dictionary.getColumns().setAll(column1,column2);

        Scene scene = new Scene(dictionary);
        stage.setScene(scene);
        stage.show();
    }


    public void selectDisplayDic() {

        Stage stage = new Stage();
        stage.setTitle("DisplayDic");

        TableColumn<TermData,String> termsNames = new TableColumn<>("Term");
        termsNames.setCellValueFactory(new PropertyValueFactory<TermData,String>("term"));
        Comparator<String> c  = new Comparator<String>() {
            @Override
            public int compare(String term1, String term2) {
                for(int i=0 ; i<term1.length() && i<term2.length() ; i++){
                    if(term1.charAt(i)>term2.charAt(i)){
                        return 1;
                    }
                    if(term1.charAt(i)<term2.charAt(i)){
                        return -1;
                    }
                }
                return 0;
            }
        };
        termsNames.setComparator(c);

        TableColumn<TermData,String> termsCounter = new TableColumn<>("Total Appearance");
        termsCounter.setCellValueFactory(new PropertyValueFactory<>("TotalAppearance"));


        TableView<TermData> dictionary = new TableView<>();
        ObservableList<TermData> terms = model.getTermslist();
        SortedList<TermData> sortedData = new SortedList<>(terms);
        sortedData.comparatorProperty().bind(dictionary.comparatorProperty());


        dictionary.setItems(sortedData);
        dictionary.getColumns().addAll(termsNames,termsCounter);
        dictionary.getSortOrder().add(termsNames);



        Scene scene = new Scene(dictionary);
        stage.setScene(scene);
        stage.show();

    }
    private void ShowAlert(String alertMessage){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }

    public boolean getIsSemmantic() {
        return isSemmanitc.isSelected();
    }

    public void selectSearchByEntities() {
        if(isStartedOrLoad){

        }
        else {
            ShowAlert("Please run the indexer first");
        }
    }


}
