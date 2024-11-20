import com.github.sarxos.webcam.Webcam;

public class WebcamTest {
    public static void main(String[] args) {
        System.out.println("웹캠 테스트 시작");
        Webcam webcam = Webcam.getDefault();
        if (webcam != null) {
            System.out.println("웹캠 발견: " + webcam.getName());
        } else {
            System.out.println("웹캠을 찾을 수 없습니다");
        }
    }
}