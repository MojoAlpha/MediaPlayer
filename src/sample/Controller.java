package sample;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

public class Controller implements Initializable {

    // fxml file variables into controller
    @FXML
    private VBox vboxParent;

    @FXML
    private MediaView mvVideo;
    private MediaPlayer mpVideo;
    private Media mediaVideo;

    @FXML
    private HBox hboxControls;

    @FXML
    private HBox hboxVolume;

    @FXML
    private Button buttonPPR;

    @FXML
    private Label labelCurrentTime;
    @FXML
    private Label labelTotalTime;
    @FXML
    private Label labelFullScreen;
    @FXML
    private Label labelSpeed;
    @FXML
    private Label labelVolume;

    @FXML
    private Slider sliderVolume;
    @FXML
    private Slider sliderTime;

    // control variables
    private boolean atEndOfVideo = false;
    private  boolean isPlaying = true;
    private boolean isMuted = true;
    private boolean isFull = false;

    // graphic icon images
    private ImageView ivPlay;
    private ImageView ivPause;
    private ImageView ivRestart;
    private ImageView ivVolume;
    private ImageView ivFullScreen;
    private ImageView ivMute;
    private ImageView ivExit;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        final int IV_SIZE = 25;

        // generating media player to show the media
        mediaVideo = new Media(new File("src/resources/final.mp4").toURI().toString());
        mpVideo = new MediaPlayer(mediaVideo);
        mvVideo.setMediaPlayer(mpVideo);

        // generating imageview objects for dynamic labels
        Image imagePlay = new Image(new File("src/resources/play-btn.png").toURI().toString());
        ivPlay = new ImageView(imagePlay);
        ivPlay.setFitHeight(IV_SIZE);
        ivPlay.setFitWidth(IV_SIZE);

        Image imageStop = new Image(new File("src/resources/stop-btn.png").toURI().toString());
        ivPause = new ImageView(imageStop);
        ivPause.setFitHeight(IV_SIZE);
        ivPause.setFitWidth(IV_SIZE);

        Image imageRestart = new Image(new File("src/resources/restart-btn.png").toURI().toString());
        ivRestart = new ImageView(imageRestart);
        ivRestart.setFitHeight(IV_SIZE);
        ivRestart.setFitWidth(IV_SIZE);

        Image imageVol = new Image(new File("src/resources/volume.png").toURI().toString());
        ivVolume = new ImageView(imageVol);
        ivVolume.setFitHeight(IV_SIZE);
        ivVolume.setFitWidth(IV_SIZE);

        Image imageFull = new Image(new File("src/resources/fullscreen.png").toURI().toString());
        ivFullScreen = new ImageView(imageFull);
        ivFullScreen.setFitHeight(IV_SIZE);
        ivFullScreen.setFitWidth(IV_SIZE);

        Image imageMute = new Image(new File("src/resources/mute.png").toURI().toString());
        ivMute = new ImageView(imageMute);
        ivMute.setFitHeight(IV_SIZE);
        ivMute.setFitWidth(IV_SIZE);

        Image imageExit = new Image(new File("src/resources/exitscreen.png").toURI().toString());
        ivExit = new ImageView(imageExit);
        ivExit.setFitHeight(IV_SIZE);
        ivExit.setFitWidth(IV_SIZE);

        buttonPPR.setGraphic(ivPause);
        buttonPPR.setTooltip(new Tooltip("Pause"));
        labelVolume.setGraphic(ivMute);
        labelSpeed.setText("1X");
        labelFullScreen.setGraphic(ivFullScreen);
        mpVideo.play();

        // action listener on play/pause button
        buttonPPR.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Button buttonPlay = (Button) actionEvent.getSource();
                if(atEndOfVideo) {
                    sliderTime.setValue(0);
                    atEndOfVideo = false;
                    isPlaying = false;
                    buttonPPR.setTooltip(new Tooltip("Restart"));
                }
                if(isPlaying) {
                    buttonPlay.setGraphic(ivPlay);
                    buttonPPR.setTooltip(new Tooltip("Play"));
                    mpVideo.pause();
                    isPlaying = false;
                } else {
                    buttonPlay.setGraphic(ivPause);
                    buttonPPR.setTooltip(new Tooltip("Pause"));
                    mpVideo.play();
                    isPlaying = true;
                }
            }
        });


        hboxVolume.getChildren().remove(sliderVolume);

        mpVideo.volumeProperty().bindBidirectional(sliderVolume.valueProperty());

        bindCurrentTimeLabel();

        // listener for change in sliderVolume value
        sliderVolume.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mpVideo.setVolume(sliderVolume.getValue());
                if(mpVideo.getVolume() != 0.0) {
                    labelVolume.setGraphic(ivVolume);
                    labelVolume.setTooltip(new Tooltip("Mute"));
                    isMuted = false;
                } else {
                    labelVolume.setGraphic(ivMute);
                    labelVolume.setTooltip(new Tooltip("Unmute"));
                    isMuted = true;
                }
            }
        });

        // changing the rate of video playing
        labelSpeed.setTooltip(new Tooltip("Video Rate"));
        labelSpeed.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(labelSpeed.getText().equals("1X")) {
                    labelSpeed.setText("2X");
                    mpVideo.setRate(2.0);
                } else {
                    labelSpeed.setText("1X");
                    mpVideo.setRate(1.0);
                }
            }
        });

        // mute & unmute functionality
        labelVolume.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(isMuted) {
                    labelVolume.setGraphic(ivVolume);
                    sliderVolume.setValue(0.5);
                    isMuted = false;
                } else {
                    labelVolume.setGraphic(ivMute);
                    sliderVolume.setValue(0.0);
                    isMuted = true;
                }
            }
        });

        // mouse hover functionality of volume tab
        labelVolume.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(hboxVolume.lookup("#sliderVolume") == null) {
                    hboxVolume.getChildren().add(sliderVolume);
                    sliderVolume.setValue(mpVideo.getVolume());
                }
            }
        });

        hboxVolume.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                hboxVolume.getChildren().remove(sliderVolume);
            }
        });

        // full screen changing property
        vboxParent.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observableValue, Scene oldScene, Scene newScene) {
                if(oldScene == null && newScene != null) {
                    mvVideo.fitHeightProperty().bind(newScene.heightProperty().subtract(hboxControls.heightProperty().add(20)));
                }
            }
        });

        // full screen & exit full screen functionality
        labelFullScreen.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Label label = (Label) mouseEvent.getSource();
                Stage stage = (Stage) label.getScene().getWindow();
                if(stage.isFullScreen()) {
                    stage.setFullScreen(false);
                    labelFullScreen.setGraphic(ivFullScreen);
                    isFull = false;
                } else {
                    stage.setFullScreen(true);
                    labelFullScreen.setGraphic(ivExit);
                    isFull = true;
                }
                stage.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent keyEvent) {
                        if(keyEvent.getCode() == KeyCode.ESCAPE) {
                            labelFullScreen.setGraphic(ivFullScreen);
                        }
                    }
                });
            }
        });

        // setting video timer
        mpVideo.totalDurationProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration oldDuration, Duration newDuration) {
                sliderTime.setMax(newDuration.toSeconds());
                labelTotalTime.setText(getTime(newDuration));
            }
        });

        // video time changing function
        sliderTime.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean wasChanging, Boolean isChanging) {
                if(!isChanging) {
                    mpVideo.seek(Duration.seconds(sliderTime.getValue()));
                }
            }
        });

        sliderTime.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                double currentTime = mpVideo.getCurrentTime().toSeconds();
                if(Math.abs(currentTime - newValue.doubleValue()) > 0.5) {
                    mpVideo.seek(Duration.seconds(newValue.doubleValue()));
                    labelMatchEndVideo(labelCurrentTime.getText(), labelTotalTime.getText());
                }
            }
        });

        mpVideo.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration oldTime, Duration newTime) {
                if(!sliderTime.isValueChanging()) {
                    sliderTime.setValue(newTime.toSeconds());
                }
                labelMatchEndVideo(labelCurrentTime.getText(), labelTotalTime.getText());
            }
        });

        // setting end of video to change state of GUI
        mpVideo.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                buttonPPR.setGraphic(ivRestart);
                atEndOfVideo = true;
                if(!labelCurrentTime.textProperty().equals(labelTotalTime.textProperty())) {
                    labelCurrentTime.textProperty().unbind();
                    labelCurrentTime.setText(getTime(mpVideo.getTotalDuration()) + " / ");
                }
            }
        });
    }

    private void labelMatchEndVideo(String labelTime, String labelTotalTime) {
        for(int i = 0; i < labelTotalTime.length(); ++i) {
            if(labelTime.charAt(i) != labelTotalTime.charAt(i)) {
                atEndOfVideo = false;
                if (isPlaying) buttonPPR.setGraphic(ivPause);
                else buttonPPR.setGraphic(ivPlay);
                break;
            } else {
                atEndOfVideo = true;
                buttonPPR.setGraphic(ivRestart);
            }
        }
    }

    private void bindCurrentTimeLabel() {
        labelCurrentTime.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getTime(mpVideo.getCurrentTime()) + " / ";
            }
        }, mpVideo.currentTimeProperty()));
    }

    public String getTime(Duration time) {
        int hours = (int) time.toHours();
        int minutes = (int) time.toMinutes();
        int seconds = (int) time.toSeconds();

        if(seconds > 59) seconds = seconds % 60;
        if(minutes > 59) minutes = minutes % 60;
        if(hours > 59) hours = hours % 60;

        if(hours > 0) return String.format("%d:%02d:%02d", hours, minutes, seconds);
        else return String.format("%02d:%02d", minutes, seconds);
    }
}
