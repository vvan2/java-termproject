// WebcamClient.java
import com.github.sarxos.webcam.Webcam;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import javax.imageio.ImageIO;

public class WebcamClient extends JFrame {
    private Webcam webcam;
    private Socket socket;
    private DataOutputStream output;
    private DataInputStream input;
    private boolean useWebcam;
    private JLabel myVideoLabel;    // 자신의 비디오를 표시할 레이블
    private JLabel otherVideoLabel; // 상대방의 비디오를 표시할 레이블
    
    public WebcamClient(String serverAddress, int port, boolean useWebcam) {
        setTitle(useWebcam ? "내 화면" : "받은 화면");
        setSize(800, 400);
        this.useWebcam = useWebcam;
        
        if (useWebcam) {
            webcam = Webcam.getDefault();
            webcam.setViewSize(new Dimension(320, 240));
            webcam.open();
        }
        
        try {
            // 서버 연결
            socket = new Socket(serverAddress, port);
            output = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());
            
            // UI 설정
            Container container = getContentPane();
            container.setLayout(new GridLayout(1, 2, 10, 10)); // 1행 2열의 그리드 레이아웃
            
            myVideoLabel = new JLabel();
            myVideoLabel.setPreferredSize(new Dimension(320, 240));
            otherVideoLabel = new JLabel();
            otherVideoLabel.setPreferredSize(new Dimension(320, 240));
            
            // 패널에 레이블 추가
            JPanel myPanel = new JPanel();
            myPanel.add(new JLabel("내 화면"));
            myPanel.add(myVideoLabel);
            
            JPanel otherPanel = new JPanel();
            otherPanel.add(new JLabel("상대방 화면"));
            otherPanel.add(otherVideoLabel);
            
            container.add(myPanel);
            container.add(otherPanel);
            
            // 웹캠 스트리밍 전송 스레드
            if (useWebcam) {
                new Thread(this::sendVideo).start();
            }
            
            // 영상 수신 스레드
            new Thread(this::receiveVideo).start();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }
    
    private void sendVideo() {
        while (true) {
            try {
                BufferedImage image;
                if (useWebcam) {
                    image = webcam.getImage();
                } else {
                    image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_RGB);
                    Graphics2D g = image.createGraphics();
                    g.setColor(new Color((int)(Math.random() * 255), 
                                      (int)(Math.random() * 255), 
                                      (int)(Math.random() * 255)));
                    g.fillRect(0, 0, 320, 240);
                }
                
                // 자신의 화면 표시
                myVideoLabel.setIcon(new ImageIcon(image));
                
                // 서버로 전송
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", baos);
                byte[] imageData = baos.toByteArray();
                
                output.writeInt(imageData.length);
                output.write(imageData);
                output.flush();
                
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
    
    private void receiveVideo() {
        while (true) {
            try {
                // 서버로부터 이미지 수신
                int length = input.readInt();
                byte[] imageData = new byte[length];
                input.readFully(imageData);
                
                // 받은 이미지를 BufferedImage로 변환
                ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
                BufferedImage receivedImage = ImageIO.read(bais);
                
                // 상대방 화면 표시
                otherVideoLabel.setIcon(new ImageIcon(receivedImage));
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
    
    public static void main(String[] args) {
        // 테스트를 위한 메인 메서드
        new WebcamClient("192.168.40.4", 5000, true);  // 실제 웹캠 사용
    }
}