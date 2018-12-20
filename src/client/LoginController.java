package client;

import gameobjects.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import server.ServerViewController;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * 登录页面类
 */
public class LoginController implements Initializable {
    public TextField tf_serverip;
    public TextField tf_playername;
    public VBox mainPane;
    public Button bt_left;
    public Button bt_right;
    public Button bt_login;
    public ImageView iv_skin;
    public MenuItem bk_1,bk_2,bk_3,bk_4,bk_5;
    public MenuButton menubutton;
    public ImageView thumbimage;

    private int skinID = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // 按下S键启动server， 按左键显示前一个皮肤，按右键显示下一个皮肤
        mainPane.setOnKeyPressed(event -> {
            event.consume();
            changeSkin(event);
        });
        // 按下服务器IP右边的按钮，启动服务器
        tf_serverip.setOnAction(event -> startServer());

        // 按下"注册"， 进入等待连接状态
        tf_playername.setOnAction(event -> connect());

        bk_1.setOnAction((ActionEvent t) ->{
            thumbimage.setImage(new Image("images/thumbview1.png"));
            GamefieldController.background_num = 1;
            menubutton.setText("Beach");
        });

        bk_2.setOnAction((ActionEvent t) ->{
            thumbimage.setImage(new Image("images/thumbview2.png"));
            GamefieldController.background_num = 2;
            menubutton.setText("Sewer");

        });

        bk_3.setOnAction((ActionEvent t) ->{
            thumbimage.setImage(new Image("images/thumbview3.png"));
            GamefieldController.background_num = 3;
            menubutton.setText("Spooky");
        });

        bk_4.setOnAction((ActionEvent t) ->{
            thumbimage.setImage(new Image("images/thumbview4.png"));
            GamefieldController.background_num = 4;
            menubutton.setText("Farm");
        });

        bk_5.setOnAction((ActionEvent t) ->{
            thumbimage.setImage(new Image("images/thumbview5.png"));
            GamefieldController.background_num = 5;
            menubutton.setText("Junkyard");
        });
    }

    private void changeSkin(KeyEvent event) {
        if (event.getCode() == KeyCode.RIGHT) {
            nextSkin();
            bt_left.setDefaultButton(true);
        } else if (event.getCode() == KeyCode.LEFT) {
            previousSkin();
            bt_right.setDefaultButton(true);
        } else if (event.isControlDown() && event.getCode() == KeyCode.S && !event.isAltDown()) {
            Stage stage = new Stage();
            Parent root;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/server/views/ServerStat.fxml"));
                root = loader.load();
//                ServerStatController controller = loader.getController();

                StringBuilder ips = new StringBuilder();

                Enumeration e = NetworkInterface.getNetworkInterfaces();
                while (e.hasMoreElements()) {
                    NetworkInterface n = (NetworkInterface) e.nextElement();
                    Enumeration ee = n.getInetAddresses();
                    while (ee.hasMoreElements()) {
                        InetAddress i = (InetAddress) ee.nextElement();
                        if (!i.getHostAddress().contains(":"))
                            ips.append(String.format(" [%s] ", i.getHostAddress()));
                    }
                }

                stage.setTitle("Server - " + ips + "");
                stage.setScene(new Scene(root));
                stage.centerOnScreen();
                stage.setResizable(true);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (event.isControlDown() && event.getCode() == KeyCode.P) {
            skinID = 99;
            nextSkin();
        } else if (event.isAltDown() && event.getCode() == KeyCode.P) {
            connect();
        } else if (event.isAltDown() && event.isControlDown() && event.getCode() == KeyCode.S) {
            startServer();
        }

    }

    public void connect() {

        ClientModel.getInstance().setLocalPlayer(new Player(tf_playername.getText(), skinID));
        ClientModel.getInstance().setServerIP(tf_serverip.getText());

        Stage stage = new Stage();
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("view/Gamefield.fxml"));
            stage.setTitle("WORMS - " + tf_serverip.getText() + " [" + tf_playername.getText() + "]");
            stage.setScene(new Scene(root));
            stage.centerOnScreen();

            stage.setResizable(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 关闭登录窗口
        stage = (Stage) bt_login.getScene().getWindow();

        stage.close();

    }

    public void startServer() {
        Stage stage = new Stage();
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/server/views/ServerView.fxml"));
            root = loader.load();
            ServerViewController controller = loader.getController();
            stage.setTitle("Server - [" + controller.getModel().getServerIP() + "]");
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void previousSkin() {
        skinID--;
        if (skinID < 0) {
            skinID = Player.WORM_SKINS - 1;
        }
        iv_skin.setImage(new Image(String.format("/images/worms/worm%d.png", skinID)));
    }

    public void nextSkin() {
        skinID++;
        if (skinID >= Player.WORM_SKINS) {
            if (skinID != 100) {
                skinID = 0;
            }
        }
        iv_skin.setImage(new Image(String.format("/images/worms/Rworm%d.png", skinID)));
    }

}
